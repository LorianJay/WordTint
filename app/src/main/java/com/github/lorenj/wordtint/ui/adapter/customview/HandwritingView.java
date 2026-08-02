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
import androidx.lifecycle.Observer;

import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.WordFunctionHandler;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
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
import java.util.function.Consumer;

public class HandwritingView extends View {

    private static final float STROKE_WIDTH = 5f;
    private static final long RECOGNITION_DELAY_MS = 600;

    private final Paint strokePaint;
    private final List<StrokePoint> currentStroke = new ArrayList<>();
    private final List<List<StrokePoint>> allStrokes = new ArrayList<>();
    private final Path currentPath = new Path();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private volatile boolean recognizing;
    private Future<?> pendingRecognition;

    // ---- ML Kit (static — 所有实例共享同一个识别器) ----

    private static volatile DigitalInkRecognizer recognizer;
    private static volatile boolean mlKitReady;
    private static final Object MLKIT_LOCK = new Object();

    private WordFunctionHandler wordFunctionHandler;

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
     * 解耦
     *
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
     * 触摸并绘制图案
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (Boolean.FALSE.equals(wordFunctionHandler.getWordFunctionHandlerState().getEnableHandwriting().getValue()))
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
                mainHandler.postDelayed(recognitionRunnable, RECOGNITION_DELAY_MS);
                return true;
        }
        return false;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (List<StrokePoint> stroke : allStrokes) drawStroke(canvas, stroke);
        drawStroke(canvas, currentStroke);
    }

    public void clear() {
        mainHandler.removeCallbacks(recognitionRunnable);
        allStrokes.clear();
        currentStroke.clear();
        currentPath.reset();
        invalidate();
    }

    private void drawStroke(Canvas canvas, List<StrokePoint> stroke) {
        if (stroke.isEmpty()) return;
        Path path = new Path();
        path.moveTo(stroke.get(0).x, stroke.get(0).y);
        for (int i = 1; i < stroke.size(); i++) {
            var prev = stroke.get(i - 1);
            var curr = stroke.get(i);
            path.quadTo(prev.x, prev.y, (prev.x + curr.x) / 2f, (prev.y + curr.y) / 2f);
        }
        var last = stroke.get(stroke.size() - 1);
        path.lineTo(last.x, last.y);
        canvas.drawPath(path, strokePaint);
    }

    // ---- 识别调度 ----

    private final Runnable recognitionRunnable = () -> {
        if (!allStrokes.isEmpty() && !recognizing) doRecognition();
    };

    private void cancelPending() {
        mainHandler.removeCallbacks(recognitionRunnable);
        if (pendingRecognition != null) {
            pendingRecognition.cancel(true);
            pendingRecognition = null;
        }
        recognizing = false;
    }

    private void doRecognition() {
        if (!mlKitReady) {
            recognizing = false;
            return;
        }
        recognizing = true;

        var strokesCopy = new ArrayList<List<StrokePoint>>();
        for (var s : allStrokes) strokesCopy.add(new ArrayList<>(s));

        pendingRecognition = StaticFactory.getExecutorService().submit(() -> {
            if (Thread.currentThread().isInterrupted()) return;
            String recognizedText = recognize(strokesCopy);
            // 查询出单词
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
                wordFunctionHandler.getHandwritingMatch().setValue(wordOrigin.trim().equalsIgnoreCase(recognizedText.trim()));
            });
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelPending();
    }

    // ---- ML Kit 初始化和识别 ----

    private static void initMlKit() {
        if (mlKitReady) return;
        StaticFactory.getExecutorService().execute(() -> {
            synchronized (MLKIT_LOCK) {
                if (mlKitReady) return;
                try {
                    var modelId = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");
                    var model = DigitalInkRecognitionModel.builder(modelId).build();
                    var mgr = RemoteModelManager.getInstance();
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

    private static String recognize(List<List<StrokePoint>> strokes) {
        var rec = recognizer;
        if (!mlKitReady || rec == null || strokes.isEmpty()) return "";

        var inkBuilder = Ink.builder();
        for (var stroke : strokes) {
            if (stroke.isEmpty()) continue;
            var sb = Ink.Stroke.builder();
            for (var pt : stroke) sb.addPoint(Ink.Point.create(pt.x, pt.y, pt.timestamp));
            inkBuilder.addStroke(sb.build());
        }

        try {
            RecognitionResult result = Tasks.await(rec.recognize(inkBuilder.build()));
            var candidates = result.getCandidates();
            if (candidates == null || candidates.isEmpty()) return "";
            String text = candidates.get(0).getText();
            return text != null ? text.replaceAll("\\s+", "") : "";
        } catch (Exception e) {
            return "";
        }
    }


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
