package com.github.lorenj.wordtint.ui.adapter.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.lorenj.wordtint.context.factory.StaticFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * 手写输入视图，捕获触摸笔画后交给 ML Kit Digital Ink Recognition 引擎识别单词。
 * 首次使用需联网下载模型（约 20MB），之后纯离线运行。
 */
public class HandwritingView extends View {

    private final Paint strokePaint;
    private final List<StrokePoint> currentStroke = new ArrayList<>();
    private final List<List<StrokePoint>> allStrokes = new ArrayList<>();
    private final Path currentPath = new Path();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Consumer<String> recognitionListener;
    private boolean enabled = false;

    /** 提笔后等待识别延迟（毫秒） */
    private static final long RECOGNITION_DELAY_MS = 600;
    /** 默认笔画宽度 */
    private static final float STROKE_WIDTH = 5f;

    /** 识别进行中标记，防止并发识别 */
    private volatile boolean recognizing = false;
    /** 待处理识别任务的 Future，用户再次下笔时取消 */
    private Future<?> pendingRecognition;

    private final Runnable recognitionRunnable = () -> {
        if (!allStrokes.isEmpty() && !recognizing) {
            doRecognition();
        }
    };

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
        strokePaint.setDither(true);
    }

    public void setOnRecognitionListener(Consumer<String> listener) {
        this.recognitionListener = listener;
    }

    public void setHandwritingEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            // 异步下载手写识别模型并初始化（首次需联网，约 20MB，之后离线）
            LetterRecognizer.initAsync();
            setVisibility(VISIBLE);
        } else {
            cancelPendingRecognition();
            clear();
            setVisibility(GONE);
        }
    }

    public boolean isHandwritingEnabled() {
        return enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enabled) return false;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 用户再次下笔，取消待执行的延迟识别和正在进行的后台识别
                cancelPendingRecognition();
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (List<StrokePoint> stroke : allStrokes) drawStroke(canvas, stroke);
        drawStroke(canvas, currentStroke);
    }

    private void drawStroke(Canvas canvas, List<StrokePoint> stroke) {
        if (stroke.isEmpty()) return;
        Path path = new Path();
        path.moveTo(stroke.get(0).x, stroke.get(0).y);
        for (int i = 1; i < stroke.size(); i++) {
            StrokePoint prev = stroke.get(i - 1);
            StrokePoint curr = stroke.get(i);
            float midX = (prev.x + curr.x) / 2;
            float midY = (prev.y + curr.y) / 2;
            path.quadTo(prev.x, prev.y, midX, midY);
        }
        path.lineTo(stroke.get(stroke.size() - 1).x, stroke.get(stroke.size() - 1).y);
        canvas.drawPath(path, strokePaint);
    }

    public void clear() {
        mainHandler.removeCallbacks(recognitionRunnable);
        allStrokes.clear();
        currentStroke.clear();
        currentPath.reset();
        invalidate();
    }

    // ---- 识别 ----

    /** 取消所有待处理和进行中的识别任务 */
    private void cancelPendingRecognition() {
        mainHandler.removeCallbacks(recognitionRunnable);
        if (pendingRecognition != null) {
            pendingRecognition.cancel(true);
            pendingRecognition = null;
        }
        recognizing = false;
    }

    /** 在后台线程执行手写识别，结果通过主线程回调 */
    private void doRecognition() {
        // ML Kit 尚未初始化完成，保持笔迹等待下次重试
        if (!LetterRecognizer.isReady()) {
            recognizing = false;
            return;
        }
        recognizing = true;
        // 复制笔画数据（避免在后台线程访问被修改的集合）
        List<List<StrokePoint>> strokesCopy = new ArrayList<>();
        for (var stroke : allStrokes) {
            strokesCopy.add(new ArrayList<>(stroke));
        }

        pendingRecognition = StaticFactory.getExecutorService().submit(() -> {
            if (Thread.currentThread().isInterrupted()) return;
            String result = LetterRecognizer.recognizeWord(strokesCopy);
            if (Thread.currentThread().isInterrupted()) return;
            mainHandler.post(() -> {
                recognizing = false;
                pendingRecognition = null;
                clear();
                notifyResult(result);
            });
        });
    }

    private void notifyResult(String text) {
        if (recognitionListener != null) {
            recognitionListener.accept(text);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelPendingRecognition();
        mainHandler.removeCallbacks(recognitionRunnable);
    }

    // ---- StrokePoint ----

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
