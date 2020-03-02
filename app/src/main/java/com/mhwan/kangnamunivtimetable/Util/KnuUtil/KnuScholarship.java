package com.mhwan.kangnamunivtimetable.Util.KnuUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class KnuScholarship {
    private String id;
    private String cookies;
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
        this.id = id;
        this.cookies = cookies;
    }

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

    private JsonArray changeString(String str){
        String result = str;
        result = result.replaceAll("schp_kfnm", "name");
        result = result.replaceAll("schp_amnt", "amount");
        result = result.replaceAll("schl_year", "year");
        result = result.replaceAll("schl_smst", "semester");
        result = result.replaceAll("dept_code", "dept");
        result = result.replaceAll("stnt_grad", "grade");

        System.out.print(result);
        JsonParser jsonParser = new JsonParser();
        return (JsonArray) jsonParser.parse(result);
    }
}
