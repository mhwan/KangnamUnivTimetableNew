package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.R;

public class EmailActivity extends BaseActivity {
    private WebView webView;
    private static final String URL = "http://mail.kangnam.ac.kr/mobile/mail/list.do;";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFromBaseView(R.layout.activity_email);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_main);
        initView();
    }

    private void initView(){

        /*
        try {
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();

            cookieManager.removeSessionCookie();
            cookieManager.setCookie(URL, preference.getCookies());
            CookieSyncManager.getInstance().sync();

        }catch(Exception e){
            e.printStackTrace();
        }
*/
        webView = (WebView) findViewById(R.id.web_view);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(URL+getJsessionId());
                return true;
            }
        });
        webView.loadUrl(URL+getJsessionId());

    }
    @Override
    protected String getToolbarTitle() {
        return getString(R.string.name_navi_email);
    }

    @Override
    protected Activity getActivity() {
        return EmailActivity.this;
    }

    @Override
    protected void hideKeyboard() {

    }

    public void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    public void onPause() {
        super.onPause();

        if (CookieSyncManager.getInstance() != null) {
            CookieSyncManager.getInstance().stopSync();
        }
    }

    private String getJsessionId(){
        String coockie = preference.getCookies();
        String[] s_coockie = coockie.split(";");

        String jsession = s_coockie[s_coockie.length-1];
        //Toast.makeText(getContext(), jsession, Toast.LENGTH_LONG).show();

        return jsession;
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else
            super.onBackPressed();
    }
    @Override
    protected void finishActivity() {
        this.finish();
    }
}
