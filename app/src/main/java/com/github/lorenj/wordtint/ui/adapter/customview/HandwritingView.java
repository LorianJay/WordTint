package com.github.lorenj.wordtint.ui.adapter.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.WordFunctionHandler;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.common.RecognitionCandidate;
import com.google.mlkit.vision.digitalink.common.RecognitionResult;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizerOptions;
import com.google.mlkit.vision.digitalink.recognition.Ink;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * 手写输入视图，集成 ML Kit Digital Ink Recognition 进行英语单词识别。
 * 首次使用需联网下载模型（约 20MB），之后完全离线运行。
 *
 * <p>通过 {@link #setWordFunctionHandler(WordFunctionHandler)} 绑定业务处理器，
 * 识别结果写入 {@code WordFunctionHandler.getHandwritingMatch()}。
 * 启用/禁用状态由 {@code WordFunctionHandlerState.getEnableHandwriting()} LiveData 驱动。
 */
public class HandwritingView extends View {

    private static final float STROKE_WIDTH = 5f;
    private static final long RECOGNITION_DELAY_MS = 600;

    private final Paint strokePaint;
    private final List<StrokePoint> currentStroke = new ArrayList<>();
    private final List<List<StrokePoint>> allStrokes = new ArrayList<>();
    private final Path currentPath = new Path();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 后台识别是否正在进行中
     */
    private volatile boolean recognizing;
    /**
     * 后台识别任务的 Future，用于取消
     */
    private Future<?> pendingRecognition;

    private volatile DigitalInkRecognizer recognizer;
    private volatile boolean mlKitReady;
    private final Object MLKIT_LOCK = new Object();

    private WordFunctionHandler wordFunctionHandler;
    /**
     * 提笔延迟后触发的识别任务
     */
    private final Runnable recognitionRunnable = () -> {
        if (!allStrokes.isEmpty() && !recognizing) doRecognition();
    };
    /**
     * 识别的文本
     */
    private String recognizedText = "";

    public HandwritingView(Context context) {
        this(context, null);
    }

    public HandwritingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandwritingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(STROKE_WIDTH);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setAntiAlias(true);
    }

    /**
     * 绑定 {@link WordFunctionHandler} 并观察手写开关状态。
     * 当状态变为启用时自动触发 ML Kit 模型初始化，变为禁用时取消待处理识别并清屏。
     *
     * @param wordFunctionHandler 单词功能处理器
     */
    public void setWordFunctionHandler(WordFunctionHandler wordFunctionHandler) {
        this.wordFunctionHandler = wordFunctionHandler;
        this.wordFunctionHandler.getWordFunctionHandlerState().getEnableHandwriting()
                .observe((LifecycleOwner) getContext(), enabled -> {
                    if (enabled) {
                        initMlKit();
                    } else {
                        cancelPending();
                        clear();
                    }
                    setVisibility(enabled ? VISIBLE : GONE);
                });
    }

    /**
     * 处理手指触摸事件，采集笔迹坐标和时间戳。
     * 提笔后延迟 {@link #RECOGNITION_DELAY_MS} 毫秒触发识别。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (Boolean.FALSE.equals(wordFunctionHandler.getWordFunctionHandlerState()
                .getEnableHandwriting().getValue()))
            return false;
        float x = event.getX(), y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelPending();
                currentStroke.clear();
                currentPath.reset();
                currentPath.moveTo(x, y);
                currentStroke.add(new StrokePoint(x, y));
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                currentStroke.add(new StrokePoint(x, y));
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                if (!currentStroke.isEmpty()) {
                    allStrokes.add(new ArrayList<>(currentStroke));
                    currentStroke.clear();
                }
                // 延迟一段时间触发识别任务
                mainHandler.postDelayed(recognitionRunnable, RECOGNITION_DELAY_MS);
                return true;
        }
        return false;
    }

    /**
     * 绘制所有已完成的笔画和当前正在进行的笔画。
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (List<StrokePoint> stroke : allStrokes) drawStroke(canvas, stroke);
        drawStroke(canvas, currentStroke);
    }

    /**
     * View 从窗口分离时取消所有待处理任务，防止内存泄漏。
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelPending();
    }

    /**
     * 清除所有笔迹并取消待执行的识别任务。
     */
    public void clear() {
        mainHandler.removeCallbacks(recognitionRunnable);
        allStrokes.clear();
        currentStroke.clear();
        currentPath.reset();
        invalidate();
    }

    /**
     * 将单个笔画绘制为二次贝塞尔曲线，实现平滑笔迹效果。
     */
    private void drawStroke(Canvas canvas, List<StrokePoint> stroke) {
        if (stroke.isEmpty()) return;
        Path path = new Path();
        path.moveTo(stroke.get(0).x, stroke.get(0).y);
        for (int i = 1; i < stroke.size(); i++) {
            StrokePoint prev = stroke.get(i - 1);
            StrokePoint curr = stroke.get(i);
            path.quadTo(prev.x, prev.y, (prev.x + curr.x) / 2f, (prev.y + curr.y) / 2f);
        }
        StrokePoint last = stroke.get(stroke.size() - 1);
        path.lineTo(last.x, last.y);
        canvas.drawPath(path, strokePaint);
    }

    /**
     * 取消所有待处理的后台识别任务（延迟定时器 + 正在执行中的 Future）。
     */
    private void cancelPending() {
        mainHandler.removeCallbacks(recognitionRunnable);
        if (pendingRecognition != null) {
            pendingRecognition.cancel(true);
            pendingRecognition = null;
        }
        recognizing = false;
    }

    /**
     * 在后台线程执行手写识别，将结果与当前单词原文比对，
     * 匹配结果写入 {@code WordFunctionHandler.getHandwritingMatch()}。
     */
    private void doRecognition() {
        if (!mlKitReady || recognizer == null) {
            recognizing = false;
            return;
        }
        recognizing = true;

        ArrayList<List<StrokePoint>> strokesCopy = new ArrayList<>();
        for (List<StrokePoint> s : allStrokes) strokesCopy.add(new ArrayList<>(s));

        pendingRecognition = StaticFactory.getExecutorService().submit(() -> {
            if (Thread.currentThread().isInterrupted()) return;
            // 构建 Ink 并识别
            Ink.Builder inkBuilder = Ink.builder();
            for (List<StrokePoint> stroke : strokesCopy) {
                if (stroke.isEmpty()) continue;
                Ink.Stroke.Builder sb = Ink.Stroke.builder();
                for (StrokePoint pt : stroke)
                    sb.addPoint(Ink.Point.create(pt.x, pt.y, pt.timestamp));
                inkBuilder.addStroke(sb.build());
            }
            try {
                RecognitionResult result = Tasks.await(recognizer.recognize(inkBuilder.build()));
                List<RecognitionCandidate> candidates = result.getCandidates();
                if (!candidates.isEmpty()) {
                    String text = candidates.get(0).getText();
                    recognizedText = text.replaceAll("\\s+", "");
                }
            } catch (Exception ignored) {
            }
            if (Thread.currentThread().isInterrupted()) return;
            // 获取原单词
            FunctionWordVO currentFocusWord = wordFunctionHandler.getCurrentFocusWord();
            String wordOrigin = Optional.ofNullable(currentFocusWord)
                    .map(FunctionWordVO::getValue)
                    .map(map -> map.get(WordStructure.WORD_ORIGIN))
                    .map(wordOriginEntity -> wordOriginEntity.value)
                    .orElse("");
            if (Thread.currentThread().isInterrupted()) return;
            mainHandler.post(() -> {
                recognizing = false;
                pendingRecognition = null;
                clear();
                if (TextUtils.isEmpty(recognizedText)) return;
                wordFunctionHandler.getHandwritingMatch()
                        .setValue(wordOrigin.trim().equalsIgnoreCase(recognizedText.trim()));
            });
        });
    }

    /**
     * 异步初始化 ML Kit 手写识别引擎。
     * 首次调用时自动下载英语手写模型（需联网，约 20MB），之后从缓存加载。
     * 线程安全——多次调用不会重复初始化。
     */
    private void initMlKit() {
        if (mlKitReady) return;
        StaticFactory.getExecutorService().execute(() -> {
            synchronized (MLKIT_LOCK) {
                if (mlKitReady) return;
                try {
                    DigitalInkRecognitionModelIdentifier modelId =
                            DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");
                    DigitalInkRecognitionModel model =
                            DigitalInkRecognitionModel.builder(modelId).build();
                    RemoteModelManager mgr = RemoteModelManager.getInstance();
                    if (!Tasks.await(mgr.isModelDownloaded(model))) {
                        Tasks.await(mgr.download(model,
                                new DownloadConditions.Builder().requireWifi().build()));
                    }
                    recognizer = DigitalInkRecognition.getClient(
                            DigitalInkRecognizerOptions.builder(model).build());
                    mlKitReady = true;
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * 单点笔迹，包含坐标和时间戳。
     */
    static class StrokePoint {
        final float x, y;
        final long timestamp;

        StrokePoint(float x, float y) {
            this.x = x;
            this.y = y;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
