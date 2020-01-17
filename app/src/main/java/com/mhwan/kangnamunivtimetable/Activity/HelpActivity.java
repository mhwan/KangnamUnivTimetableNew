package com.mhwan.kangnamunivtimetable.Activity;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.CustomUI.CircleIndicator;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AccountPreference;

import java.util.ArrayList;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private int[] colorList = null;
    private int position;
    private ArgbEvaluator evaluator = new ArgbEvaluator();
    private boolean toShowLogin = false;
    private AccountPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setupColors();
        preference = new AccountPreference(getApplicationContext());

        if (getIntent().hasExtra("SHOW_LOGIN")) {
            toShowLogin = true;
            preference.setShowHelp(true);
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        final CircleIndicator indicator = findViewById(R.id.help_indicator);
        final ImageButton mNextBtn = findViewById(R.id.intro_btn_next);
        final Button mFinishBtn = findViewById(R.id.intro_btn_finish);
        Button mSkipBtn = findViewById(R.id.intro_btn_skip);
        mFinishBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mSkipBtn.setOnClickListener(this);

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(new IntroAdapter(this, setupData()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 3 ? position : position + 1]);
                mViewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                indicator.selectDot(position);
                mViewPager.setBackgroundColor(colorList[position]);

                mNextBtn.setVisibility(position == 3 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private class IntroAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Context context;
        private ArrayList<IntroData> introData;

        public IntroAdapter(Context context, ArrayList introData) {
            this.context = context;
            this.layoutInflater = (LayoutInflater) this.context.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.introData = introData;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = this.layoutInflater.inflate(R.layout.ui_intro, container, false);
            ImageView displayImage = view.findViewById(R.id.intro_image);
            TextView title = view.findViewById(R.id.intro_title);
            TextView content = view.findViewById(R.id.intro_content);
            content.setMovementMethod(new ScrollingMovementMethod());
            displayImage.setImageResource(introData.get(position).imageId);
            title.setText(introData.get(position).title);
            content.setText(introData.get(position).content);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private ArrayList setupData() {
        ArrayList<IntroData> data = new ArrayList<>();
        data.add(new IntroData(R.drawable.intro_table_1, getResources().getString(R.string.help_main), getResources().getString(R.string.help_main_content)));
        data.add(new IntroData(R.drawable.intro_grade_2, getResources().getString(R.string.help_grade), getResources().getString(R.string.help_grade_content)));
        data.add(new IntroData(R.drawable.intro_map_3, getResources().getString(R.string.help_map), getResources().getString(R.string.help_map_content)));
        data.add(new IntroData(R.drawable.intro_schedule_4, getResources().getString(R.string.help_report), getResources().getString(R.string.help_report_content)));

        return data;
    }

    private void setupColors() {
        int color1 = ContextCompat.getColor(this, R.color.colorPrimary);
        int color2 = ContextCompat.getColor(this, R.color.colorLightRed);
        int color4 = ContextCompat.getColor(this, R.color.colorLightPurple);
        int color3 = ContextCompat.getColor(this, R.color.colorLightIndigo);

        colorList = new int[]{color3, color2, color4, color1};
    }

    private class IntroData {
        private int imageId;
        private String title, content;

        public IntroData(int imageId, String title, String content) {
            this.imageId = imageId;
            this.title = title;
            this.content = content;
        }

        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.intro_btn_finish:
                changeNewActivity();
                break;
            case R.id.intro_btn_next:
                position++;
                mViewPager.setCurrentItem(position, true);
                break;
            case R.id.intro_btn_skip:
                Toast.makeText(HelpActivity.this, getString(R.string.message_skip_help_activity), Toast.LENGTH_SHORT).show();
                changeNewActivity();
                break;
        }
    }

    //로그인이 필요하면 로그인 액티비티로 넘기고 닫고, 그게 아니면 그냥 클로즈
    private void changeNewActivity() {
        preference.setShowHelp(true);
        if (toShowLogin) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else
            finish();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
