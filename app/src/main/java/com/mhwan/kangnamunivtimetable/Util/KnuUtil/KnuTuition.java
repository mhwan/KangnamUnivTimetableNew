package com.mhwan.kangnamunivtimetable.Util.KnuUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;

public class KnuTuition {
    private String id;
    private String cookies;
    public static final String GET_SEMESTER_URL = "https://m.kangnam.ac.kr/knusmart/s/s260l.do";
    public static final String GET_TUITION_DETAIL_BASE_URL = "https://m.kangnam.ac.kr/knusmart/s/s260t.do?";
    public static final String GET_TUITION_URL = "https://m.kangnam.ac.kr/knusmart/s/s260.do?";


    public class TuitionSemester {
        public String schl_year;
        public String schl_smst;
    }


    /**
     * actual_sum act_sum
     * lecture_scholar lctr_amnt
     * pay_amount amnt_0002
     * pay_dongmun_amount amnt_0008
     * pay_iphak amnt_0001
     * pay_ot amnt_0007
     * //미납일때
     * pay_term used_term
     * bank_numb;
     */
    public class TuitionItem {
        public String actual_sum;
        public String pay_gubn;
        public String schl_date;
        public String bank_name;
        public String lecture_scholar;
        public String pay_amount;
        public String pay_dongmun_amount;
        public String pay_iphak;
        public String pay_ot;
        public String pay_term;
        public String bank_numb;
        public boolean isPay;
        public String semester;
        public TuitionItem(){}
    }

    public TuitionSemester[] doGetAvailableSemesters() {
        try {
            URL url = new URL(GET_SEMESTER_URL);
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
                TuitionSemester[] semesters = (TuitionSemester[]) AppUtility.getAppinstance().reverseArray(gson.fromJson(array, TuitionSemester[].class));
                return semesters;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<TuitionItem> doGetAllTuition() {
        TuitionSemester[] semesters = doGetAvailableSemesters();
        ArrayList<TuitionItem> list = null;
        if (semesters != null && semesters.length > 0) {
            list = new ArrayList<>();
            for (TuitionSemester semester : semesters) {
                TuitionItem item = doGetSemesterTuition(semester);
                item.semester = semester.schl_year+"-"+semester.schl_smst;
                int s = item.pay_gubn.indexOf("'>")+2;
                int e = item.pay_gubn.lastIndexOf("<");

                item.pay_gubn = item.pay_gubn.substring(s,e);

                list.add(item);
            }

            Collections.reverse(list);
        }


        return list;
    }

    public TuitionItem doGetSemesterTuition(TuitionSemester semester){
        URL url = null;
        HttpsURLConnection httpsURLConnection = null;
        try {
            url = new URL(GET_TUITION_DETAIL_BASE_URL+getSemesterParams(semester.schl_year, semester.schl_smst));
            URLConnection connection = url.openConnection();
            httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.addRequestProperty("Cookie", cookies);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            JsonObject object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));

            if (object.get("result").getAsString().equals("success")){
                JsonObject object1 = object.get("data").getAsJsonArray().get(0).getAsJsonObject();
                String str = object1.get("pay_gubn").getAsString();
                TuitionItem item = null;
                Gson gson = new Gson();
                if (str.contains("완납")) {
                    JsonObject obj2 = changeString(gson.toJson(object1));
                    item = gson.fromJson(obj2, TuitionItem.class);
                    item.isPay = true;

                } else {
                    item = doGetSimpleTuition(getSemesterParams(semester.schl_year, semester.schl_smst), gson);
                    item.isPay = false;
                    if (item.bank_numb!= null && item.bank_numb.contains("<br>"))
                        item.bank_numb = item.bank_numb.replaceAll("<br>", "\\\n\\\t");
                }

                return item;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }


    private TuitionItem doGetSimpleTuition(String params, Gson gson){
        HttpsURLConnection httpsURLConnection;
        TuitionItem tuitionItem;
        try {
            URL url = new URL(GET_TUITION_URL+params);
            URLConnection connection = url.openConnection();
            httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.addRequestProperty("Cookie", cookies);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            JsonObject object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));

            if (object.get("result").getAsString().equals("success")) {
                JsonObject object2 = changeString(gson.toJson(object.get("data")));
                tuitionItem = gson.fromJson(object2, TuitionItem.class);

                return tuitionItem;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * actual_sum act_sum
     * lecture_scholar lctr_amnt
     * pay_amount amnt_0002
     * pay_dongmun_amount amnt_0008
     * pay_iphak amnt_0001
     * pay_ot amnt_0007
     * //미납일때
     * pay_term used_term
     * bank_numb;
     */
    private JsonObject changeString(String str){
        String result = str;
        result = result.replaceAll("act_sum", "actual_sum");
        result = result.replaceAll("lctr_amnt", "lecture_scholar");
        result = result.replaceAll("amnt_0002", "pay_amount");
        result = result.replaceAll("amnt_0008", "pay_dongmun_amount");
        result = result.replaceAll("amnt_0001", "pay_iphak");
        result = result.replaceAll("amnt_0007", "pay_ot");
        result = result.replaceAll("used_term", "pay_term");

        //System.out.print(result);
        JsonParser jsonParser = new JsonParser();
        return (JsonObject) jsonParser.parse(result);
    }

    public KnuTuition(String id, String cookies){
        this.id = id;
        this.cookies = cookies;
    }

    private String getSemesterParams(String y, String s){
        return "schl_year="+y+"&schl_smst="+s;
    }
}
