package com.mhwan.kangnamunivtimetable.Util.KnuUtil;

import android.util.Log;

import com.mhwan.kangnamunivtimetable.Util.SSLCertificateUtil;

public abstract class BaseKnuService {
    protected String id;
    protected String cookies;
    private static final int MAX_REQUEST = 3;

    public BaseKnuService(String id, String cookies){
        this.id = id;
        this.cookies = cookies;
    }

    public BaseKnuService(String id){
        this.id = id;
    }

    protected void ignoreWorkSSLCertificate(){
        Log.d("ignoreSSL", "work");
        SSLCertificateUtil SSLCertificateUtil = new SSLCertificateUtil();
        SSLCertificateUtil.postHttps(getSSLURL(), 1000, 1000);
    }

    protected boolean isRequestPossible(int i){
        Log.d("general_request", "request"+i);

        return i++ < MAX_REQUEST;
    }
    protected abstract String getSSLURL();
}
