package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.mhwan.kangnamunivtimetable.R;


public class CircleIndicator extends LinearLayout {
    private int size;
    private Context context;
    private int counts;
    //원 사이의 간격
    private int DefaultMargin = 10;

    //애니메이션 시간
    private int defaultAnimDuration = 250;

    private int mDefaultCircle;
    private int mSelectCircle;

    private View[] circles;

    public CircleIndicator(Context context) {
        super(context);
        this.context = context;
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context, attrs);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicatorss, 0, 0);
        this.counts = attrArray.getInt(R.styleable.CircleIndicatorss_circle_count, 2);
        this.DefaultMargin = (int) attrArray.getDimension(R.styleable.CircleIndicatorss_circle_margin, 10f);
        this.size = (int) attrArray.getDimension(R.styleable.CircleIndicatorss_circle_size, 18f);
        this.mSelectCircle = attrArray.getResourceId(R.styleable.CircleIndicatorss_select_circle_resource, 0);
        this.mDefaultCircle = attrArray.getResourceId(R.styleable.CircleIndicatorss_non_select_circle_resource, 0);

        attrArray.recycle();
        updateView();
    }

    private void updateView() {
        circles = new View[counts];

        for (int i = 0; i < counts; i++) {
            circles[i] = new View(context);
            LayoutParams params = new LayoutParams
                    (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.topMargin = DefaultMargin;
            params.bottomMargin = DefaultMargin;
            params.leftMargin = DefaultMargin;
            params.rightMargin = DefaultMargin;
            params.gravity = Gravity.CENTER;
            circles[i].setLayoutParams(params);
            circles[i].getLayoutParams().width = size;
            circles[i].getLayoutParams().height = size;
            circles[i].setBackgroundResource(mDefaultCircle);
            circles[i].setTag(circles[i].getId(), false);
            this.addView(circles[i]);
        }

        selectDot(0);
    }

    public void selectDot(int position) {
        for (int i = 0; i < circles.length; i++) {
            if (i == position) {
                circles[i].setBackgroundResource(mSelectCircle);
                scaleAnimation(circles[i], true, 1f, 1.2f);
            } else {

                if ((boolean) circles[i].getTag(circles[i].getId()) == true) {
                    circles[i].setBackgroundResource(mDefaultCircle);
                    scaleAnimation(circles[i], false, 1.2f, 1f);
                }
            }
        }
    }


    /**
     * 선택된 점의 애니메이션
     *
     * @param view
     * @param startScale
     * @param endScale
     */
    public void scaleAnimation(View view, boolean b, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(defaultAnimDuration);
        view.startAnimation(anim);
        view.setTag(view.getId(), b);
    }

}
