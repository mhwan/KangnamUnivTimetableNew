package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Activity.Fragment.MainScheduleFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.ShuttleBusFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.TimeTableDetailFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.TimeTableFragment;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AccountPreference;
import com.mhwan.kangnamunivtimetable.Util.AppContext;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class MainActivity extends BaseActivity implements TimeTableFragment.onTableClickListener, TimeTableDetailFragment.TableChangeColorListener {
    private TimeTableFragment timeTableFragment;
    private ShuttleBusFragment shuttleBusFragment;
    private MainScheduleFragment mainScheduleFragment;
    private AppDatabaseHelper dbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFromBaseView(R.layout.activity_main);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_main);
        dbHelper = new AppDatabaseHelper(getContext());
        //Log.d("address", DebugDB.getAddressLog());
        /*
        키해쉬를 받아옴
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.mhwan.kangnamunivtimetable", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/


        /*
        TextView temp_hash = findViewById(R.id.text_keyhash);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    AppContext.getContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                temp_hash.setText(Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/


        initLayout();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("cookies", preference.getCookies());
        isChangeSemester();
    }

    private void initLayout() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        timeTableFragment = new TimeTableFragment();
        timeTableFragment.setListener(this);
        shuttleBusFragment = new ShuttleBusFragment();
        mainScheduleFragment = new MainScheduleFragment();
        fragmentTransaction.add(R.id.main_table_layout, timeTableFragment);
        fragmentTransaction.add(R.id.main_shuttlbus_layout, shuttleBusFragment);
        fragmentTransaction.add(R.id.main_schedule_layout, mainScheduleFragment);
        fragmentTransaction.commit();
    }


    private void isChangeSemester() {
        AccountPreference preference = new AccountPreference(this);
        int sem = preference.getSemester();
        if (sem < 0) {
            preference.setSemester(AppUtility.getAppinstance().getOnlySemester());
            preference.setShowSemester(true);
        } else {
            if (sem != AppUtility.getAppinstance().getOnlySemester()) {
                if (preference.getShowSemester()) {
                    showDialog();
                    preference.setShowSemester(true);
                }
                preference.setSemester(AppUtility.getAppinstance().getOnlySemester());
            }
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.title_change_semester));
        builder.setMessage(getString(R.string.message_change_semester));
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected Activity getActivity() {
        return MainActivity.this;
    }

    @Override
    protected void hideKeyboard() {
        return;
    }

    @Override
    public void onTimeTableClicked(Object o) {
        if (o instanceof TimetableSubject) {
            TimetableSubject subject = (TimetableSubject) o;
            TimeTableDetailFragment fragment = TimeTableDetailFragment.getInstance();
            fragment.setObject(subject, dbHelper.getApproachScheduleItems(subject.getName(), 3));
            fragment.setListener(this);
            fragment.show(getSupportFragmentManager(), "bottomSheet");
        }
    }

    @Override
    public void onChangeColor(int id, int color) {
        timeTableFragment.changeBackgroundColor(color, id);
    }
    @Override
    protected void finishActivity() {
        this.finish();
    }

}
