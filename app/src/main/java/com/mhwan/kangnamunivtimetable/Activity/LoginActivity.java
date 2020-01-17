package com.mhwan.kangnamunivtimetable.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AccountPreference;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuLogin;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private LonginTask longinTask;
    private TextView login_wrong_message;
    private EditText input_id, input_pw;

    /**
     * 처음에 로그인이 일어날때 애니메이션을 줄것인가
     * 아이콘이 안보이다가 타~악하고 뜨는 느낌
     * 밑에는 파랑색 위에는 아이콘+그림자
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_wrong_message = findViewById(R.id.login_wrong_message);
        input_id = findViewById(R.id.input_id);
        input_pw = findViewById(R.id.input_pw);

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = input_id.getText().toString();
                String pw = input_pw.getText().toString();
                if (checkIsValidData(id, pw))
                    loginWork(id, pw);
            }
        });
    }

    public class LonginTask extends AsyncTask<Void, Void, Object> {
        private String id, pw;
        private KnuLogin login;

        public void setIdPW(String id, String pw) {
            this.id = id;
            this.pw = pw;
        }

        @Override
        protected void onPreExecute() {
            if (login == null)
                login = new KnuLogin(id, pw);
            login.setIdPW(id, pw);

            if (!AppUtility.getAppinstance().isNetworkConnection())
                cancel(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(LoginActivity.this, getString(R.string.message_cancel_login), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o instanceof ArrayList && o != null) {
                //성공
                ArrayList<String> result = (ArrayList<String>) o;
                String str = TextUtils.join(";", result);
                AccountPreference accountPreference = new AccountPreference(LoginActivity.this);
                accountPreference.setCookies(str);
                accountPreference.setId(id);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("cookies", str);
                startActivity(intent);
                finish();

            } else if (o instanceof String) {
                //아이디 또는 비밀번호 오류
                showWrongMessage((String) o);
            } else {
                //오류발생
            }
        }

        @Override
        protected Object doInBackground(Void... voids) {
            if (!isCancelled()) {
                Object result = login.doLogin();
                return result;
            }
            return null;
        }
    }

    private void loginWork(final String id, String pw) {
        longinTask = new LonginTask();
        longinTask.setIdPW(id, pw);
        longinTask.execute();
    }

    private boolean checkIsValidData(String id, String pw) {
        boolean result = true;
        if (id == null || id.isEmpty()) {
            result = false;
            showWrongMessage(getString(R.string.message_input_id));
        } else if (pw == null || pw.isEmpty()) {
            result = false;
            showWrongMessage(getString(R.string.message_input_pw));
        }

        return result;
    }

    private void showWrongMessage(String message) {
        if (login_wrong_message.getVisibility() == View.GONE)
            login_wrong_message.setVisibility(View.VISIBLE);
        login_wrong_message.setText(message);

        Animation shakeanim = AnimationUtils.loadAnimation(this, R.anim.animation_shake);
        login_wrong_message.startAnimation(shakeanim);
    }
}
