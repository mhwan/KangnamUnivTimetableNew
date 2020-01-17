package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Callback.TimeTableDataListener;
import com.mhwan.kangnamunivtimetable.CustomUI.ColorChoosePreference;
import com.mhwan.kangnamunivtimetable.CustomUI.CustomPreference;
import com.mhwan.kangnamunivtimetable.CustomUI.CustomPreferenceCategory;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AccountPreference;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.GetTimeTableTask;

import java.util.ArrayList;


public class SettingActivity extends BaseActivity {
    private static ArrayList<TimetableSubject> subjects;
    private static AppDatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFromBaseView(R.layout.activity_setting);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_main);
        getTableToDatabase();
        initView();

    }

    private void getTableToDatabase() {
        helper = new AppDatabaseHelper(getContext());
        subjects = helper.getAllTimeTable();
    }

    private void initView() {
        TextView nameText = findViewById(R.id.setting_name);
        TextView idText = findViewById(R.id.setting_id);
        nameText.setText(AppUtility.getAppinstance().getName(preference.getNameFromCookie()));
        idText.setText(preference.getId());
        findViewById(R.id.setting_button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        getFragmentManager().beginTransaction()
                .replace(R.id.setting_frame, new SettingsFragment(), null).commit();
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.name_setting);
    }

    @Override
    protected Activity getActivity() {
        return SettingActivity.this;
    }

    @Override
    protected void hideKeyboard() {
        return;
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Preference pref_remove_schedule, pref_app_ver, pref_app_guide, pref_reset, pre_update_table, pref_mail, pref_share;
        private SwitchPreference pref_show_alarm, pref_vibrate;

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            //super.onViewCreated(view, savedInstanceState);
            ListView listView = view.findViewById(android.R.id.list);
            listView.setDivider(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.colorDividerGray))); // or some other color int
            listView.setDividerHeight(1); //wrorng code
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_settings);
            PreferenceScreen screen = this.getPreferenceScreen();
            CustomPreferenceCategory category = new CustomPreferenceCategory(screen.getContext());
            category.setTitle(getString(R.string.name_timetable));
            screen.addPreference(category);

            CustomPreference preference = new CustomPreference(screen.getContext());
            preference.setTitle(getString(R.string.name_update_timetable));
            preference.setKey("key_update_timetable");
            preference.setSummary(getString(R.string.summary_upadate_timetable));
            category.addPreference(preference);

            if (subjects != null) {
                for (int i = 0; i < subjects.size(); i++) {
                    ColorChoosePreference choosePreference = new ColorChoosePreference(screen.getContext());
                    choosePreference.setTitle(subjects.get(i).getName() + " " + getString(R.string.subject_color));
                    choosePreference.setColor(subjects.get(i).getColor());
                    choosePreference.setSubjectId(subjects.get(i).getId());

                    category.addPreference(choosePreference);
                }
            }

            pref_remove_schedule = findPreference("key_remove_schedule");
            pref_app_ver = findPreference("key_app_version");
            pref_app_guide = findPreference("key_guide");
            pref_reset = findPreference("key_reset_app");
            pre_update_table = findPreference("key_update_timetable");
            pref_vibrate = (SwitchPreference) findPreference("key_alarm_vibrate");
            pref_show_alarm = (SwitchPreference) findPreference("key_show_alarm");
            pref_mail = findPreference("key_mail_to_developer");
            pref_share = findPreference("key_share_friends");


            pref_remove_schedule.setOnPreferenceClickListener(this);
            pref_app_ver.setOnPreferenceClickListener(this);
            pref_app_guide.setOnPreferenceClickListener(this);
            pref_reset.setOnPreferenceClickListener(this);
            pre_update_table.setOnPreferenceClickListener(this);
            pref_vibrate.setOnPreferenceChangeListener(this);
            pref_show_alarm.setOnPreferenceChangeListener(this);
            pref_mail.setOnPreferenceClickListener(this);
            pref_share.setOnPreferenceClickListener(this);

            pref_app_ver.setSummary(AppUtility.getAppinstance().getAppVersion());
        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals(pref_remove_schedule.getKey())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.message_remove_all_schedule));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        helper.removeAllScheduleItems();
                        Toast.makeText(getActivity(), getString(R.string.message_success_remove_all_schedule), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
                return true;
            } else if (key.equals(pref_app_ver.getKey())) {
                return true;
            } else if (key.equals(pref_app_guide.getKey())) {
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
                return true;
            } else if (key.equals(pref_mail.getKey())) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "mhwanbae21@gmail.com", null));
                startActivity(Intent.createChooser(intent, null));
            } else if (key.equals(pref_share.getKey())) {

                /*
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri.Builder uriBuilder = Uri.parse("https://play.google.com/store/apps/details")
                        .buildUpon()
                        .appendQueryParameter("id", "com.kakao.talk")
                        .appendQueryParameter("launch", "true");

                intent.setData(uriBuilder.build());
                intent.setPackage("com.android.vending");
                startActivity(intent);*/

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "강남대학교 시간표, 이 앱 한번 설치해봐! https://play.google.com/store/apps/details?id=com.mhwan.kangnamunivtimetable");
                startActivity(Intent.createChooser(emailIntent, "친구에게"));
            } else if (key.equals(pref_reset.getKey())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.message_reset));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        helper.removeAllScheduleItems();
                        helper.removeAllTimetable();
                        removeAllPreference();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        getActivity().finish();
                        Toast.makeText(getActivity(), getString(R.string.message_all_reset), Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
                return true;
            } else if (key.equals(pre_update_table.getKey())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.message_really_update_timetable));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AccountPreference accountPreference = new AccountPreference(getActivity());
                        GetTimeTableTask tableTask = new GetTimeTableTask(getActivity(), new TimeTableDataListener() {
                            @Override
                            public void onDataSet(ArrayList<TimetableSubject> list) {
                                if (list != null) {
                                    helper.removeAllTimetable();
                                    helper.addAllTimeTable(list);
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                    Toast.makeText(getActivity(), getString(R.string.message_update_timetable), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        tableTask.setIdCookie(accountPreference.getId(), accountPreference.getCookies());
                        tableTask.execute();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();

                return true;
            }
            return false;
        }

        private void removeAllPreference() {
            AccountPreference preference = new AccountPreference(getActivity());
            preference.removeAll();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
        }

        public static boolean getFinishedToOrdering(Context context) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean("key_show_finish", true);
        }

        public static boolean getIsVibrate(Context context) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean("key_alarm_vibrate", true);
        }

        public static boolean getIsShowAlarm(Context context) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean("key_show_alarm", true);
        }

        public static int getDefaultAlarmTime(Context context) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return Integer.parseInt(preferences.getString("key_alarm_show_time", "0"));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (key.equals(pref_show_alarm.getKey())) {
                //pref_show_alarm.setChecked((boolean) newValue);
                if (!pref_show_alarm.isChecked()) {
                    Toast.makeText(getActivity(), getString(R.string.message_show_alarm_popup), Toast.LENGTH_SHORT).show();
                    pref_show_alarm.setChecked(true);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.message_no_alarm_popup), Toast.LENGTH_SHORT).show();
                    pref_show_alarm.setChecked(false);
                }
            } else if (key.equals(pref_vibrate.getKey())) {
                pref_vibrate.setChecked((boolean) newValue);
                if (pref_vibrate.isChecked())
                    Toast.makeText(getActivity(), getString(R.string.message_alarm_vibrate), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), getString(R.string.message_alarm_no_vibrate), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }
}
