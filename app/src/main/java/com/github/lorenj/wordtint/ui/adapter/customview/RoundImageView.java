package com.github.lorenj.wordtint.ui.adapter.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.github.lorenj.wordtint.R;


public class RoundImageView extends ImageView {

    float width, height;
    private int leftTopRadius;
    private int rightTopRadius;
    private int leftBottomRadius;
    private int rightBottomRadius;
    private int radius;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    private void init(Context context, AttributeSet attrs) {

        //读取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Round_Angle_Image_View);
        int defaultRadius = 0;
        radius = typedArray.getDimensionPixelOffset(R.styleable.Round_Angle_Image_View_radius, defaultRadius);
        leftTopRadius = typedArray.getDimensionPixelOffset(R.styleable.Round_Angle_Image_View_left_top_radius, defaultRadius);
        rightTopRadius = typedArray.getDimensionPixelOffset(R.styleable.Round_Angle_Image_View_right_top_radius, defaultRadius);
        leftBottomRadius = typedArray.getDimensionPixelOffset(R.styleable.Round_Angle_Image_View_left_bottom_radius, defaultRadius);
        rightBottomRadius = typedArray.getDimensionPixelOffset(R.styleable.Round_Angle_Image_View_right_bottom_radius, defaultRadius);

        //如果四个角的值没有设置，就用通用的radius
        if (leftTopRadius == defaultRadius) {
            leftTopRadius = radius;
        }
        if (rightTopRadius == defaultRadius) {
            rightTopRadius = radius;
        }
        if (leftBottomRadius == defaultRadius) {
            leftBottomRadius = radius;
        }
        if (rightBottomRadius == defaultRadius) {
            rightBottomRadius = radius;
        }
        typedArray.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (width > 12 && height > 12) {
            Path path = new Path();
            /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
            float rids[] = {leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, leftBottomRadius, leftBottomRadius, rightBottomRadius, rightBottomRadius};
            path.addRoundRect(new RectF(0, 0, width, height), rids, Path.Direction.CW);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
    }
}