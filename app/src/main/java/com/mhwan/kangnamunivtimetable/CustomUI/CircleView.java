package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mhwan.kangnamunivtimetable.R;

public class CircleView extends View {
    private Paint circlePaint, borderPaint;
    private int w, h;
    private int background_color;
    private int boarder_width;
    private int boarder_color;
    private boolean isDrawStroke = false;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void setTypedArray(Context context, AttributeSet attrs) {
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0);
        this.background_color = attrArray.getColor(R.styleable.CircleView_circle_background_color, 0);
        this.boarder_width = (int) attrArray.getDimension(R.styleable.CircleView_circle_border, 0f);
        this.boarder_color = attrArray.getColor(R.styleable.CircleView_circle_border_color, 0);

        attrArray.recycle();
    }

    private void init(Context context, AttributeSet attrs) {
        setTypedArray(context, attrs);
        if (boarder_width > 0)
            isDrawStroke = true;

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(background_color);
        circlePaint.setStyle(Paint.Style.FILL);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        if (isDrawStroke) {
            borderPaint.setStrokeWidth(boarder_width);
            borderPaint.setColor(boarder_color);
        }
    }

    public void setBoarder(int dp) {
        boarder_width = dp;
        if (boarder_width > 0)
            isDrawStroke = true;
        this.invalidate();
    }

    public void setBoarderColor(int color) {
        boarder_color = color;
        this.invalidate();
    }

    public void setCircleBackgroundColor(int color) {
        background_color = color;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        circlePaint.setColor(background_color);
        int radius = Math.min(w, h) / 2;
        canvas.drawCircle(w / 2, h / 2, radius, circlePaint);
        if (isDrawStroke) {
            borderPaint.setColor(boarder_color);
            borderPaint.setStrokeWidth(boarder_width);
            canvas.drawCircle(w / 2, h / 2, radius - (boarder_width / 2), borderPaint);
        }
    }
}
