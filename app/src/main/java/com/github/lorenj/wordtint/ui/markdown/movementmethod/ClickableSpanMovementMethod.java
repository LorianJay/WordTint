package com.github.lorenj.wordtint.ui.markdown.movementmethod;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.textclassifier.TextLinks;
import android.widget.TextView;

/**
 * 处理长按和点击产生背景色冲突的LinkMovementMethod
 *
 * @author cnsukidayo
 * @date 2023/2/6 10:46
 */
public class ClickableSpanMovementMethod extends LinkMovementMethod {

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);

            if (links.length != 0) {
                ClickableSpan link = links[0];
                if (action == MotionEvent.ACTION_UP) {
                    if (!(link instanceof TextLinks.TextLinkSpan)) {
                        link.onClick(widget);
                    }
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }
        return false;
    }

    public static MovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new ClickableSpanMovementMethod();

        return sInstance;
    }
    private static ClickableSpanMovementMethod sInstance;
}
