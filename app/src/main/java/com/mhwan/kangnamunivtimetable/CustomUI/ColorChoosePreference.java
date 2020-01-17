package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.R;

public class ColorChoosePreference extends Preference {
    private ChangeColorDialog mDialog;
    private Context mContext;
    private CircleView circleView;
    private int[] colorArr;
    private int colorid, subjectId;

    public ColorChoosePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setLayoutResource(R.layout.layout_preference_color_choose);
        mContext = context;
        colorArr = context.getResources().getIntArray(R.array.Color_array);
    }

    public ColorChoosePreference(Context context) {
        super(context);
        setLayoutResource(R.layout.layout_preference_color_choose);
        mContext = context;
        colorArr = context.getResources().getIntArray(R.array.Color_array);
    }

    public ColorChoosePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.layout_preference_color_choose);
        mContext = context;
        colorArr = context.getResources().getIntArray(R.array.Color_array);
    }


    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        circleView = view.findViewById(R.id.preference_circle);
        setColor(colorid);
    }

    public void setColor(int color) {
        this.colorid = color;
        if (circleView != null)
            circleView.setCircleBackgroundColor(colorArr[colorid]);
    }

    public void setSubjectId(int id) {
        this.subjectId = id;
    }

    @Override
    protected void onClick() {
        super.onClick();

        mDialog = new ChangeColorDialog(getContext());
        mDialog.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(mDialog.getCheckid());
                mDialog.dismiss();
            }
        });
        mDialog.setCheckid(colorid);
        mDialog.show();
    }

    private void changeColor(int colorid) {
        this.colorid = colorid;
        setColor(colorid);
        if (mDialog != null)
            mDialog.dismiss();
        AppDatabaseHelper dbHelper = new AppDatabaseHelper(getContext());
        dbHelper.updateColor(subjectId, colorid);
    }
}
