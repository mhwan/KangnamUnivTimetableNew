package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AccountPreference;
import com.mhwan.kangnamunivtimetable.Util.AppContext;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.DoubleBackKeyPressed;
import com.rupins.drawercardbehaviour.CardDrawerLayout;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected Toolbar toolbar;
    private CardDrawerLayout drawer;
    private FrameLayout root_layout;
    protected View toolbarview;
    protected AccountPreference preference;
    private DoubleBackKeyPressed doubleBackKeyPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        preference = new AccountPreference(getContext());
        viewInit();
        doubleBackKeyPressed = new DoubleBackKeyPressed(getActivity(), root_layout);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (toolbar != null) {
            drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            drawer.setViewScale(Gravity.START, 0.8f);
            drawer.setViewScrimColor(GravityCompat.START, Color.BLACK);
            drawer.setRadius(Gravity.START, 35);
            drawer.setViewElevation(Gravity.START, 20);
            findViewById(R.id.button_drawer_ttable).setOnClickListener(this);
            findViewById(R.id.button_drawer_grade).setOnClickListener(this);
            findViewById(R.id.button_drawer_setting).setOnClickListener(this);
            findViewById(R.id.button_drawer_report).setOnClickListener(this);
            findViewById(R.id.button_drawer_map).setOnClickListener(this);
            findViewById(R.id.button_drawer_scholarship).setOnClickListener(this);
            findViewById(R.id.button_drawer_library).setOnClickListener(this);
            //findViewById(R.id.button_drawer_email).setOnClickListener(this);
            findViewById(R.id.button_drawer_search).setOnClickListener(this);
            toolbarview.findViewById(R.id.main_toolbar_drawer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard();
                    if (!drawer.isDrawerOpen(Gravity.START))
                        drawer.openDrawer(GravityCompat.START);

                }
            });
            ((TextView) findViewById(R.id.nav_header_name)).setText(AppUtility.getAppinstance().getName(preference.getNameFromCookie()));
            ((TextView) findViewById(R.id.nav_header_id)).setText(preference.getId());
            findViewById(R.id.nav_button_logout).setOnClickListener(this);

            Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getSupportActionBar().setCustomView(toolbarview, layoutParams);
        }
    }


    private void viewInit() {
        root_layout = findViewById(R.id.base_frame);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackKeyPressed != null) {
                doubleBackKeyPressed.onBackPressed();
            } else
                super.onBackPressed();
        }
    }

    protected void setToolbar(int id, int layout) {
        toolbar = findViewById(id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        toolbarview = LayoutInflater.from(this).inflate(layout, null);
        ((TextView) toolbarview.findViewById(R.id.toolbar_title)).setText(getToolbarTitle());
    }

    protected void setContentFromBaseView(int viewId) {
        View new_layout = LayoutInflater.from(this).inflate(viewId, root_layout, false);
        root_layout.addView(new_layout);
    }

    protected void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.message_really_logout))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    protected void logoutUser() {
        preference.removeUserPreference();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        Intent intent = null;
        switch (id) {
            case R.id.button_drawer_ttable:
                intent = new Intent(getActivity(), MainActivity.class);
                break;
            case R.id.button_drawer_grade:
                intent = new Intent(getActivity(), ScoreActivity.class);
                break;
            case R.id.button_drawer_report:
                intent = new Intent(getActivity(), ScheduleActivity.class);
                break;
            case R.id.button_drawer_setting:
                intent = new Intent(getActivity(), SettingActivity.class);
                break;
            case R.id.button_drawer_map:
                if (!getToolbarTitle().equals(getString(R.string.name_campusMap)))
                    intent = new Intent(getActivity(), MapActivity.class);
                break;
            case R.id.nav_button_logout:
                showLogoutDialog();
                break;
            case R.id.button_drawer_scholarship:
                intent = new Intent(getActivity(), ScholarshipActivity.class);
                break;
            case R.id.button_drawer_library :
                intent = new Intent(getActivity(), LibraryActivity.class);
                break;
                /*
            case R.id.button_drawer_email :
                intent = new Intent(getActivity(), EmailActivity.class);
                break;*/
            case R.id.button_drawer_search :
                intent = new Intent(getActivity(), EmployeeSearchActivity.class);
                break;
        }

        if (id != R.id.nav_button_logout && intent != null) {
            intent.putExtra("cookies", preference.getCookies());
            startNewActivity(intent);
        }
        drawer.closeDrawers();
    }

    protected void startNewActivity(Intent intent) {
        startActivity(intent);
        finishActivity();
    }

    protected abstract String getToolbarTitle();

    protected abstract Activity getActivity();

    protected abstract void hideKeyboard();

    protected Context getContext() {
        return getActivity();
    }

    protected abstract void finishActivity();
}
