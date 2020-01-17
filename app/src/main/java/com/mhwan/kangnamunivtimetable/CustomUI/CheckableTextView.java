package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Checkable;

import com.mhwan.kangnamunivtimetable.R;

public class CheckableTextView extends AppCompatTextView implements Checkable {
    private boolean isChecked = false;
    private static final int[] STATE_CHECKABLE = {android.R.attr.state_checked};
    private int changed_color, default_color;
    private boolean checkable = false;

    public CheckableTextView(Context context) {
        super(context);
    }

    public CheckableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CheckableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CheckableTextView, 0, 0);
        changed_color = attrArray.getColor(R.styleable.CheckableTextView_selected_text_color, 0);
        default_color = attrArray.getColor(R.styleable.CheckableTextView_default_text_color, 0);
        checkable = attrArray.getBoolean(R.styleable.CheckableTextView_checkable, false);
        isChecked = attrArray.getBoolean(R.styleable.CheckableTextView_setCheck, false);
        attrArray.recycle();
        changeTextColor((isChecked == true) ? changed_color : default_color);
    }

    private void changeTextColor(int color) {
        this.setTextColor(color);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changeTextColor(changed_color);
                refreshDrawableState();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                changeTextColor(default_color);
                refreshDrawableState();
                break;
        }
        return super.onTouchEvent(event);
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
        isChecked = checked;
        if (isChecked)
            changeTextColor(changed_color);
        else
            changeTextColor(default_color);

        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        Log.d("toggle!!!", "" + !isChecked);
        setChecked(!isChecked);
        refreshDrawableState();
    }

    @Override
    public boolean performClick() {
        if (checkable == true) {
            toggle();
        }
        return super.performClick();
    }

    /*
    @Override
    public void onClick(View v) {
        Log.d("click", "on");
        if (checkable)
            toggle();
    }*/
}
