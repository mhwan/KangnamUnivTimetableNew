package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mhwan.kangnamunivtimetable.R;

public class CheckedCircleView extends RelativeLayout implements Checkable {
    private boolean isChecked = false;
    private int bgColor;
    private ImageView ic_check;
    private CircleView bg_circle;
    private static final int[] STATE_CHECKABLE = {android.R.attr.state_checked};

    public CheckedCircleView(Context context) {
        super(context);
        initView();
    }

    public CheckedCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        setTypedArray(context, attrs);
    }

    public CheckedCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setTypedArray(context, attrs);
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ui_check_circle, this, false);
        addView(view);

        ic_check = view.findViewById(R.id.circle_check);
        bg_circle = view.findViewById(R.id.circle_bg);
    }

    private void setTypedArray(Context context, AttributeSet attrs) {
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedCircleView, 0, 0);
        int bgcolor = attrArray.getColor(R.styleable.CheckedCircleView_bg_color, ContextCompat.getColor(context, R.color.colorPrimary));
        boolean ischeck = attrArray.getBoolean(R.styleable.CheckedCircleView_check, false);

        setBackgroundColor(bgcolor);
        setChecked(ischeck);
        attrArray.recycle();
    }

    public void setCheckIcon(){

    }
    public void setBackgroundColor(int color) {
        bg_circle.setCircleBackgroundColor(color);
        invalidate();
    }

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, STATE_CHECKABLE);
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        this.isChecked = checked;
        if (isChecked) {
            ic_check.setVisibility(VISIBLE);
            ((Animatable) ic_check.getDrawable()).start();
        } else {
            ic_check.setVisibility(GONE);
        }
        invalidate();
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
        refreshDrawableState();
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
}
