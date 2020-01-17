package com.mhwan.kangnamunivtimetable.Util.KnuUtil;

import com.google.gson.JsonObject;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class KnuLogin {
    private String id;
    private String pw;
    public static final String LOGIN_URL = "https://m.kangnam.ac.kr/knusmart/c/c001.do?";
    public static final String LOGOUT_URL = "https://m.kangnam.ac.kr/knusmart/c/c003.do";

    public KnuLogin(String id, String pw) {
        this.pw = pw;
        this.id = id;
    }

    public void setIdPW(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public boolean doLogout() {
        return true;
    }

    public Object doLogin() {
        URL url = null;
        JsonObject object = null;
        ArrayList<String> cookieList = null;
        try {
            url = new URL(LOGIN_URL + getParams());
            URLConnection connection = url.openConnection();

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            OutputStream stream = httpsURLConnection.getOutputStream();
            stream.write(getParams().getBytes());
            stream.flush();
            stream.close();

            object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));

            if (object.get("result").getAsString().equals("success")) {
                List<String> cookies = httpsURLConnection.getHeaderFields().get("Set-Cookie");
                if (!cookies.equals(null)) {
                    cookieList = new ArrayList<>();
                    for (String c : cookies) {
                        cookieList.add(snipCookie(c));
                    }
                }
                return cookieList;
            } else if (object.get("result").getAsString().equals("error")) {
                return object.get("result_msg").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String snipCookie(String s) {
        String temp = s.split(";")[0];
        temp.replaceAll("&quot;", "\"");
        return temp;
    }

    private String getParams() {
        return "user_id=" + this.id + "&user_pwd=" + this.pw;
    }

}