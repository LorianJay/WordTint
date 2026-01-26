package com.github.lorenj.wordtint.ui.markdown.plugin;

import android.graphics.Paint;

import androidx.annotation.NonNull;

import io.noties.markwon.core.MarkwonTheme;

/**
 * @author cnsukidayo
 * @date 2023/2/7 11:26
 */
public class MarkwonThemeAdapter extends MarkwonTheme {

    private static MarkwonThemeAdapter markwonTheme;
    private int betweenHeadingHeight = 0;

    /**
     * 获取到已经初始化的MarkwonTheme
     *
     * @return 返回单利Bean
     */
    public static MarkwonThemeAdapter getAdapterInstance() {
        if (markwonTheme == null) {
            throw new IllegalStateException("you must transfer createAdapterInstance before call current method!");
        }
        return markwonTheme;
    }

    /**
     * 注意如果要使用主题适配器则必须要调用该方法,即创建该适配器必须且只能调用该静态方法.<br>
     * 否则适配器的各种功能是无法使用的,注意该适配器不支持动态修改MarkwonTheme,也就是主题的内的各种设置在第一次调用该静态方法就会确定<br>
     * 所以如果你想继承框架本身的Builder,则你应当在{@link io.noties.markwon.MarkwonPlugin#configureTheme(Builder)}方法内部将Builder全部配置完毕后<br>
     * 调用当前方法并且将{@link io.noties.markwon.core.MarkwonTheme.Builder}对象传入作为一个初始化,这样你就可以在任何地方获取到MarkwonTheme<br>
     * 而不必担心系统没有回调参数
     *
     * @param copyBuilder 拷贝的Builder
     */
    public static void createAdapterInstance(final Builder copyBuilder) {
        if (markwonTheme == null) {
            markwonTheme = new MarkwonThemeAdapter(copyBuilder);
        }
    }

    private MarkwonThemeAdapter(final Builder builder) {
        super(builder);
    }

    @Override
    public void applyHeadingTextStyle(@NonNull Paint paint, int level) {
        if (headingTypeface == null) {
            paint.setFakeBoldText(true);
        } else {
            paint.setTypeface(headingTypeface);
        }
        if (headingTextSizeMultipliers != null && headingTextSizeMultipliers.length >= level) {
            paint.setTextSize(headingTextSizeMultipliers[level - 1]);
        }
    }

    public void setBetweenHeadingHeight(int betweenHeadingHeight) {
        this.betweenHeadingHeight = betweenHeadingHeight;
    }

    public int getBetweenHeadingHeight() {
        return betweenHeadingHeight;
    }
}
