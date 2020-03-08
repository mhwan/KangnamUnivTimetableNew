package com.mhwan.kangnamunivtimetable.Util.KnuUtil;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mhwan.kangnamunivtimetable.Items.Subject;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.SSLCertificateUtil;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

public class KnuGrade extends BaseKnuService{
    public static final String GET_SEMESETER_URL = "https://m.kangnam.ac.kr/knusmart/s/s252l.do";
    public static final String SEARCH_SCORE_URL = "https://m.kangnam.ac.kr/knusmart/s/s252.do?";
    private HashMap<Semester, Grade> gradeList;
    private boolean mode = true;


    public class Semester {
        public String schl_year;
        public String schl_smst;
    }

    public class Grade {
        public Subject[] subjects;
        public Semester semester;
        public String stnt_dept;
        public String smst_unit;
        public String totl_unit;
        public String smst_avrg;
        public String totl_avrg;
    }

    public KnuGrade(String id, String cookie) {
        super(id, cookie);
    }

    @Override
    protected String getSSLURL() {
        if (mode)
            return GET_SEMESETER_URL;
        return GET_SEMESETER_URL;
    }


    public Object doGetAvilableSemester2() {
        int i =0;
        do {
            try {
                URL url = new URL(GET_SEMESETER_URL);
                URLConnection connection = url.openConnection();

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.addRequestProperty("Cookie", cookies);
                httpsURLConnection.setUseCaches(false);
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);

                JsonObject object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));
                if (object.get("result").getAsString().equals("success")) {
                    JsonArray array = object.get("data").getAsJsonArray();
                    Gson gson = new Gson();
                    Semester[] semesters = (Semester[]) AppUtility.getAppinstance().reverseArray(gson.fromJson(array, Semester[].class));
                    return semesters;
                } else
                    return null;

            } catch (SSLHandshakeException ex1) {
                Log.e("handshakeException", "certificateInvalid");
                ignoreWorkSSLCertificate();
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } while (isRequestPossible(i++));

        return new Integer(-1);
    }
    /*
    public Semester[] doGetAvilableSemester() {
        try {
            URL url = new URL(GET_SEMESETER_URL);
            URLConnection connection = url.openConnection();

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.addRequestProperty("Cookie", cookies);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            JsonObject object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));
            if (object.get("result").getAsString().equals("success")) {
                JsonArray array = object.get("data").getAsJsonArray();
                Gson gson = new Gson();
                Semester[] semesters = (Semester[]) AppUtility.getAppinstance().reverseArray(gson.fromJson(array, Semester[].class));
                return semesters;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/

    public HashMap<Semester, Grade> doGetAllGrade(Semester[] semesters) {
        if (semesters != null && semesters.length > 0) {
            gradeList = new HashMap<>();
            Gson gson = new Gson();
            for (Semester semester : semesters) {
                String gradeJSon = getSpecificgrade(semester);
                if (gradeJSon != null) {
                    Grade grade = new Grade();
                    JsonObject object = AppUtility.getAppinstance().getStringToJson(gradeJSon);
                    if (object.get("result").getAsString().equals("success")) {
                        grade.semester = semester;
                        JsonObject o1 = object.get("data").getAsJsonObject();
                        grade = gson.fromJson(o1, Grade.class);
                        JsonElement result = object.get("data2");
                        JsonArray o2 = changeString(gson.toJson(result));
                        grade.subjects = (Subject[]) AppUtility.getAppinstance().reverseArray(gson.fromJson(o2, Subject[].class));
                    }

                    gradeList.put(semester, grade);
                }
            }
            return gradeList;
        }
        return null;
    }

    private JsonArray changeString(String str) {
        String result = str;
        result = result.replaceAll("fnsh_gubn", "classify");
        result = result.replaceAll("cnvt_scor", "score");
        result = result.replaceAll("subj_knam", "name");
        result = result.replaceAll("subj_unit", "unit");

        //System.out.print(result);
        JsonParser jsonParser = new JsonParser();
        return (JsonArray) jsonParser.parse(result);
    }

    //이것도 무시처리 할것인가?
    private String getSpecificgrade(Semester semester){
        URL url = null;
        HttpsURLConnection httpsURLConnection = null;
        try {
            url = new URL(SEARCH_SCORE_URL + getSemseterParams(semester.schl_year, semester.schl_smst));
            URLConnection connection = url.openConnection();
            httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.addRequestProperty("Cookie", cookies);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            return AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getSemseterParams(String y, String s) {
        return "cors_gubn=1&stnt_numb=" + id + "&schl_year=" + y + "&schl_smst=" + s;
    }



    public static ArrayList<Semester> sortSemester(ArrayList<Semester> key) {
        Collections.sort(key, new Comparator<Semester>() {
            @Override
            public int compare(Semester o1, Semester o2) {
                if (o1.schl_year.compareTo(o2.schl_year) > 0)
                    return 1;
                else if (o1.schl_year.compareTo(o2.schl_year) == 0)
                    return o1.schl_smst.compareTo(o2.schl_smst);
                else
                    return -1;
            }
        });

        return key;
    }
}
