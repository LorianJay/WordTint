package com.github.lorenj.wordtint.ui.adapter.customview;

import android.util.Log;

import com.github.lorenj.wordtint.context.factory.StaticFactory;
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

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 手写单词识别器，基于 Google ML Kit Digital Ink Recognition API。
 * 专为手写英文字母设计，识别准确率远高于 OCR 方案。
 * 模型首次使用时自动下载（约 20MB），之后纯离线运行。
 */
public final class LetterRecognizer {

    private static final String TAG = "LetterRecognizer";
    private static final String LANGUAGE_TAG = "en-US";

    private static volatile DigitalInkRecognizer recognizer;
    private static volatile boolean ready = false;
    private static final Object LOCK = new Object();

    private LetterRecognizer() {}

    /**
     * 是否已初始化完成（模型已下载且识别器已就绪）。
     */
    public static boolean isReady() {
        return ready;
    }

    /**
     * 异步初始化：下载英语手写模型并创建识别器。
     * 首次调用需要联网（仅一次，之后缓存到设备），约需下载 20MB。
     * 多次调用安全——已就绪则直接返回。
     */
    public static void initAsync() {
        if (ready) return;
        StaticFactory.getExecutorService().execute(() -> {
            synchronized (LOCK) {
                if (ready) return;
                try {
                    // 1. 创建英语手写模型标识
                    DigitalInkRecognitionModelIdentifier modelId =
                            DigitalInkRecognitionModelIdentifier.fromLanguageTag(LANGUAGE_TAG);
                    if (modelId == null) {
                        Log.e(TAG, "Unsupported language tag: " + LANGUAGE_TAG);
                        return;
                    }

                    DigitalInkRecognitionModel model =
                            DigitalInkRecognitionModel.builder(modelId).build();

                    // 2. 下载模型（仅首次，已缓存则跳过）
                    RemoteModelManager modelManager = RemoteModelManager.getInstance();
                    if (!Tasks.await(modelManager.isModelDownloaded(model))) {
                        Log.i(TAG, "Downloading handwriting model (~20MB)...");
                        DownloadConditions conditions = new DownloadConditions.Builder()
                                .requireWifi()
                                .build();
                        Tasks.await(modelManager.download(model, conditions));
                        Log.i(TAG, "Model download complete.");
                    }

                    // 3. 创建识别器
                    DigitalInkRecognizerOptions options =
                            DigitalInkRecognizerOptions.builder(model).build();
                    recognizer = DigitalInkRecognition.getClient(options);
                    ready = true;
                    Log.i(TAG, "Handwriting recognizer ready.");
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Model download failed", e);
                } catch (Exception e) {
                    Log.e(TAG, "Recognizer init failed", e);
                }
            }
        });
    }

    /**
     * 从手写笔画识别英文单词（大小写不敏感）。
     *
     * @param strokes 所有笔画点列表（含时间戳）
     * @return 识别出的单词文本；失败时返回空字符串
     */
    public static String recognizeWord(List<List<HandwritingView.StrokePoint>> strokes) {
        DigitalInkRecognizer localRecognizer = recognizer;
        if (!ready || localRecognizer == null || strokes.isEmpty()) {
            return "";
        }

        // 1. 将笔画转换为 ML Kit Ink 格式
        Ink ink = buildInk(strokes);
        if (ink == null) return "";

        // 2. 执行识别（同步等待结果）
        try {
            RecognitionResult result = Tasks.await(localRecognizer.recognize(ink));
            List<RecognitionCandidate> candidates = result.getCandidates();
            if (candidates == null || candidates.isEmpty()) {
                return "";
            }
            // 返回最佳候选文本，去除可能存在的空格
            String text = candidates.get(0).getText();
            return text != null ? text.replaceAll("\\s+", "") : "";
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Recognition failed", e);
            return "";
        }
    }

    /**
     * 释放识别器资源。
     */
    public static void release() {
        synchronized (LOCK) {
            if (recognizer != null) {
                try {
                    recognizer.close();
                } catch (Exception e) {
                    Log.w(TAG, "Error closing recognizer", e);
                }
                recognizer = null;
                ready = false;
            }
        }
    }

    // ---- private helpers ----

    /**
     * 将手写笔画列表转换为 ML Kit {@link Ink} 对象。
     */
    private static Ink buildInk(List<List<HandwritingView.StrokePoint>> strokes) {
        Ink.Builder inkBuilder = Ink.builder();
        for (var stroke : strokes) {
            if (stroke.isEmpty()) continue;
            Ink.Stroke.Builder strokeBuilder = Ink.Stroke.builder();
            for (var pt : stroke) {
                strokeBuilder.addPoint(Ink.Point.create(pt.x, pt.y, pt.timestamp));
            }
            inkBuilder.addStroke(strokeBuilder.build());
        }
        return inkBuilder.build();
    }
}
