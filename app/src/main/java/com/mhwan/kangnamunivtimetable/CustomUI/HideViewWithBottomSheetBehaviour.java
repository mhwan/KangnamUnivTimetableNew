package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class HideViewWithBottomSheetBehaviour extends AppBarLayout.ScrollingViewBehavior {

    private static final float UNDEFINED = Float.MAX_VALUE;

    private float childStartY = UNDEFINED;

    public HideViewWithBottomSheetBehaviour() {
    }

    public HideViewWithBottomSheetBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return getBottomSheetBehavior(dependency) != null;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        BottomSheetBehavior bottomSheetBehavior = getBottomSheetBehavior(dependency);
        if (bottomSheetBehavior != null) {
            float slideOffset = getSlideOffset(parent, dependency, bottomSheetBehavior);

            child.setAlpha(1 - slideOffset);

            if (childStartY == UNDEFINED) {
                childStartY = child.getY();
            }

            int childHeight = child.getMeasuredHeight();
            float childY = childStartY - (childHeight * slideOffset);
            child.setY(childY);
        }
        return true;
    }

    private float getSlideOffset(CoordinatorLayout parent, View dependency, BottomSheetBehavior bottomSheetBehavior) {
        int parentHeight = parent.getMeasuredHeight();
        float sheetY = dependency.getY();
        int peekHeight = bottomSheetBehavior.getPeekHeight();
        int sheetHeight = dependency.getHeight();
        float collapseY = parentHeight - peekHeight;
        float expandY = parentHeight - sheetHeight;
        float deltaY = collapseY - expandY;

        return (parentHeight - peekHeight - sheetY) / deltaY;
    }

    @Nullable
    private BottomSheetBehavior getBottomSheetBehavior(@NonNull View view) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            return (BottomSheetBehavior) behavior;
        }
        return null;
    }
}
