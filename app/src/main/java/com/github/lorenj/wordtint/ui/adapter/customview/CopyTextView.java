package com.github.lorenj.wordtint.ui.adapter.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

import com.github.lorenj.wordtint.R;

public class CopyTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Drawable copyDrawable;
    private Rect copyRect = new Rect();

    private OnCopyClickListener listener;

    public CopyTextView(Context context) {
        super(context);
        init();
    }

    public CopyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CopyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setTextIsSelectable(true);

        copyDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_copy);

        if (copyDrawable != null) {
            int size = (int) (getTextSize() * 1.3f);
            copyDrawable.setBounds(0, 0, size, size);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (copyDrawable == null || getLayout() == null || length() == 0) {
            return;
        }

        Layout layout = getLayout();

        int lastOffset = getText().length();

        int line = layout.getLineForOffset(lastOffset);

        float x = layout.getPrimaryHorizontal(lastOffset);

        int baseline = layout.getLineBaseline(line);

        int left = (int) (getPaddingLeft() + x + dp(4));

        int top = baseline - copyDrawable.getIntrinsicHeight();

        copyDrawable.setBounds(
                left,
                top,
                left + copyDrawable.getIntrinsicWidth(),
                top + copyDrawable.getIntrinsicHeight());

        copyDrawable.draw(canvas);

        copyRect.set(copyDrawable.getBounds());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (copyRect.contains((int) event.getX(), (int) event.getY())) {

                if (listener != null) {
                    listener.onCopyClick();
                }

                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    public void setOnCopyClickListener(OnCopyClickListener listener) {
        this.listener = listener;
    }

    public interface OnCopyClickListener {
        void onCopyClick();
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics());
    }
}