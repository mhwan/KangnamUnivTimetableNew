package com.mhwan.kangnamunivtimetable.CustomUI;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;


import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.ScreenUtility;

public class ChangeColorDialog extends Dialog {
    private Context context;
    private View.OnClickListener cancelListener, okListener;
    private RecyclerView recyclerView;
    private ChangeColorAdapter adapter;
    private int checkid;

    public ChangeColorDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.context = context;
    }

    public ChangeColorDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_dialog_color_change);

        findViewById(R.id.dialog_cancel).setOnClickListener(cancelListener);
        findViewById(R.id.dialog_ok).setOnClickListener(okListener);
        recyclerView = findViewById(R.id.dialog_recyclerview);
        ViewTreeObserver treeObserver = recyclerView.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width = recyclerView.getMeasuredWidth();
                drawGridView(width);
            }
        });
    }

    private void drawGridView(int width) {
        int itemwidth = ScreenUtility.getInstance().dpToPx(32);
        int coloum = (width / itemwidth) - 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), coloum));
        adapter = new ChangeColorAdapter(getContext(), context.getResources().getIntArray(R.array.Color_array), checkid);
        recyclerView.setAdapter(adapter);

    }

    public void setListener(View.OnClickListener cancelListener, View.OnClickListener okListener) {
        this.cancelListener = cancelListener;
        this.okListener = okListener;
    }

    public void setCheckid(int id) {
        this.checkid = id;
    }

    public int getCheckid() {
        return adapter.getCheckid();
    }
}
