package com.mhwan.kangnamunivtimetable.Util.KnuUtil;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.SSLCertificateUtil;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

public class KnuScholarship extends BaseKnuService{
    public static final String GET_SCHOLARSHIP_URL = "https://m.kangnam.ac.kr/knusmart/s/s253.do";

    public class ScholarshipItem {
        public String name;
        public String amount;
        public String grade;
        public String year;
        public String semester;
        public String dept;
    }
    public KnuScholarship(String id, String cookies){
        super(id, cookies);
    }

    @Override
    protected String getSSLURL() {
        return GET_SCHOLARSHIP_URL;
    }
    public Object doGetAllScholarship2() {
        int i =0;
        do {
            try {
                ArrayList<ScholarshipItem> result = null;
                URL url = new URL(GET_SCHOLARSHIP_URL);
                URLConnection connection = url.openConnection();

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.addRequestProperty("Cookie", cookies);
                httpsURLConnection.setUseCaches(false);
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);

                JsonObject object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));
                if (object.get("result").getAsString().equals("success")){
                    Gson gson = new Gson();
                    JsonArray array = changeString(gson.toJson(object.get("data")));

                    ScholarshipItem[] arr = (ScholarshipItem []) (gson.fromJson(array, ScholarshipItem[].class));

                    if (arr != null && arr.length > 0) {
                        result = new ArrayList<>();
                        for (ScholarshipItem item : arr) {
                            if (item != null) {
                                item.semester = item.year + "-" + item.semester;
                                result.add(item);
                            }
                        }

                    }
                    return result;
                }

            }catch (SSLHandshakeException ex1) {
                Log.e("handshakeException", "certificateInvalid");
                ignoreWorkSSLCertificate();
            } catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }while (isRequestPossible(i++));

        return new Integer(-1);
    }

    /*
    public ArrayList<ScholarshipItem> doGetAllScholarship(){
        ArrayList<ScholarshipItem> result = null;
        try {

            URL url = new URL(GET_SCHOLARSHIP_URL);
            URLConnection connection = url.openConnection();

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.addRequestProperty("Cookie", cookies);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            JsonObject object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));
            if (object.get("result").getAsString().equals("success")){
                Gson gson = new Gson();
                JsonArray array = changeString(gson.toJson(object.get("data")));

                ScholarshipItem[] arr = (ScholarshipItem []) (gson.fromJson(array, ScholarshipItem[].class));

                if (arr != null && arr.length > 0) {
                    result = new ArrayList<>();
                    for (ScholarshipItem item : arr) {
                        if (item != null) {
                            item.semester = item.year + "-" + item.semester;
                            result.add(item);
                        }
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }
*/
    private JsonArray changeString(String str){
        String result = str;
        result = result.replaceAll("schp_kfnm", "name");
        result = result.replaceAll("schp_amnt", "amount");
        result = result.replaceAll("schl_year", "year");
        result = result.replaceAll("schl_smst", "semester");
        result = result.replaceAll("dept_code", "dept");
        result = result.replaceAll("stnt_grad", "grade");

        //System.out.print(result);
        JsonParser jsonParser = new JsonParser();
        return (JsonArray) jsonParser.parse(result);
    }
}
