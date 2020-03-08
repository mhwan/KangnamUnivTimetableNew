package com.mhwan.kangnamunivtimetable.Util.KnuUtil;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppContext;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.SSLCertificateUtil;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;


public class KnuLogin extends BaseKnuService{
    private String pw;
    public static final String LOGIN_URL = "https://m.kangnam.ac.kr/knusmart/c/c001.do?";
    public static final String LOGOUT_URL = "https://m.kangnam.ac.kr/knusmart/c/c003.do";

    public KnuLogin(String id, String pw) {
        super(id);
        this.pw = pw;
    }

    @Override
    protected String getSSLURL() {
        return LOGIN_URL+getParams();
    }

    public void setIdPW(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public boolean doLogout() {
        return true;
    }

    /*
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
            } catch (SSLHandshakeException ex1) {
                Log.e("handshakeException", "certificateInvalid");
                //doLogin();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
    }*/

    public Object doLogin2(){
        int i = 0;
        do {
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
            } catch (SSLHandshakeException ex1) {
                Log.e("handshakeException", "certificateInvalid");
                ignoreWorkSSLCertificate();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } while (isRequestPossible(i++));
        return new Integer(-1);
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