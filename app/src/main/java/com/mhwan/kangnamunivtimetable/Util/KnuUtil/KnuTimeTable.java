package com.mhwan.kangnamunivtimetable.Util.KnuUtil;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mhwan.kangnamunivtimetable.Items.Days;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppContext;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

public class KnuTimeTable extends BaseKnuService {
    public static final String GET_TIMETABLE_URL = "https://m.kangnam.ac.kr/knusmart/s/s251.do";
    private String[] dayList = {"MON", "TUE", "WED", "THU", "FRI"};
    //private String timtablestr = "{\"result\":\"success\",\"result_msg\":\"성공\",\"data\":[{\"time_day1\":\"공학수학 후B102\",\"time_code\":\"1a\",\"time_day3\":\"네트워크 경408-1\",\"time_day2\":\"가상현실콘텐츠기초 이109\",\"real_time\":\"09:00-09:25\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":\"공학수학 후B102\",\"time_code\":\"1b\",\"time_day3\":\"네트워크 경408-1\",\"time_day2\":\"가상현실콘텐츠기초 이109\",\"real_time\":\"09:25-09:50\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":\"공학수학 후B102\",\"time_code\":\"2a\",\"time_day3\":\"네트워크 경408-1\",\"time_day2\":\"가상현실콘텐츠기초 이109\",\"real_time\":\"09:50-10:15\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":\"공학수학 후B102\",\"time_code\":\"2b\",\"time_day3\":\"네트워크 경408-1\",\"time_day2\":\"가상현실콘텐츠기초 이109\",\"real_time\":\"10:25-10:50\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":\"공학수학 후B102\",\"time_code\":\"3a\",\"time_day3\":\"네트워크 경408-1\",\"time_day2\":\"가상현실콘텐츠기초 이109\",\"real_time\":\"10:50-11:15\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":\"공학수학 후B102\",\"time_code\":\"3b\",\"time_day3\":\"네트워크 경408-1\",\"time_day2\":\"가상현실콘텐츠기초 이109\",\"real_time\":\"11:15-11:40\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":\"공학수학 후B102\",\"time_code\":\"4a\",\"time_day3\":\"네트워크 경408-1\",\"time_day2\":\"Academic English 후B103\",\"real_time\":\"11:50-12:15\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":\"공학수학 후B102\",\"time_code\":\"4b\",\"time_day3\":\"네트워크 경408-1\",\"time_day2\":\"Academic English 후B103\",\"real_time\":\"12:15-12:40\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":null,\"time_code\":\"5a\",\"time_day3\":null,\"time_day2\":\"Academic English 후B103\",\"real_time\":\"12:40-13:05\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":null,\"time_code\":\"5b\",\"time_day3\":null,\"time_day2\":\"Academic English 후B103\",\"real_time\":\"13:15-13:40\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":null,\"time_code\":\"6a\",\"time_day3\":\"운영체제 후B103\",\"time_day2\":\"Academic English 후B103\",\"real_time\":\"13:40-14:05\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":null,\"time_code\":\"6b\",\"time_day3\":\"운영체제 후B103\",\"time_day2\":\"Academic English 후B103\",\"real_time\":\"14:05-14:30\",\"time_day5\":null,\"time_day4\":null},{\"time_day1\":null,\"time_code\":\"7a\",\"time_day3\":\"운영체제 후B103\",\"time_day2\":null,\"real_time\":\"14:40-15:05\",\"time_day5\":null,\"time_day4\":\"대학생의인생설계 샬303\"},{\"time_day1\":null,\"time_code\":\"7b\",\"time_day3\":\"운영체제 후B103\",\"time_day2\":null,\"real_time\":\"15:05-15:30\",\"time_day5\":null,\"time_day4\":\"대학생의인생설계 샬303\"},{\"time_day1\":null,\"time_code\":\"8a\",\"time_day3\":\"운영체제 후B103\",\"time_day2\":null,\"real_time\":\"15:30-15:55\",\"time_day5\":null,\"time_day4\":\"대학생의인생설계 샬303\"},{\"time_day1\":null,\"time_code\":\"8b\",\"time_day3\":\"운영체제 후B103\",\"time_day2\":null,\"real_time\":\"16:05-16:30\",\"time_day5\":null,\"time_day4\":\"대학생의인생설계 샬303\"},{\"time_day1\":null,\"time_code\":\"9a\",\"time_day3\":\"운영체제 후B103\",\"time_day2\":null,\"real_time\":\"16:30-16:55\",\"time_day5\":null,\"time_day4\":\"대학생의인생설계 샬303\"},{\"time_day1\":null,\"time_code\":\"9b\",\"time_day3\":\"운영체제 후B103\",\"time_day2\":null,\"real_time\":\"16:55-17:20\",\"time_day5\":null,\"time_day4\":\"대학생의인생설계 샬303\"}]}";

    public KnuTimeTable(String id, String cookies) {
        super(id, cookies);
    }

    @Override
    protected String getSSLURL() {
        return GET_TIMETABLE_URL;
    }


    /*
    public ArrayList<TimetableSubject> doGetTimeTable() {
        ArrayList<TimetableSubject> result = null;
        try {
            URL url = new URL(GET_TIMETABLE_URL);
            URLConnection connection = url.openConnection();

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.addRequestProperty("Cookie", cookies);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            JsonObject object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));
            if (object.get("result").getAsString().equals("success")) {
                JsonElement json = object.get("data");
                Gson gson = new Gson();
                result = setTimeTable(changeDayString(gson.toJson(json)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public Object doGetTimeTable2(){
        int i =0;
        do {
            ArrayList<TimetableSubject> result = null;
            try {
                URL url = new URL(GET_TIMETABLE_URL);
                URLConnection connection = url.openConnection();

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.addRequestProperty("Cookie", cookies);
                httpsURLConnection.setUseCaches(false);
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);

                JsonObject object = AppUtility.getAppinstance().getStringToJson(AppUtility.getAppinstance().readInputStreamToString(httpsURLConnection));
                if (object.get("result").getAsString().equals("success")) {
                    JsonElement json = object.get("data");
                    Gson gson = new Gson();
                    result = setTimeTable(changeDayString(gson.toJson(json)));
                    return result;
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
    public ArrayList doGetTableTemp() {
        ArrayList<TimetableSubject> result = null;
        JsonObject object = AppUtility.getAppinstance().getStringToJson(timtablestr);
        if (object.get("result").getAsString().equals("success")) {
            JsonElement json = object.get("data");
            Gson gson = new Gson();
            result = setTimeTable(changeDayString(gson.toJson(json)));
        }
        return result;
    }*/

    public HashMap<Days, ArrayList> parseTimeTable(ArrayList<TimetableSubject> list) {
        HashMap<Days, ArrayList> resultData = new HashMap<>();

        for (Days d : Days.values()) {
            ArrayList<TimetableSubject> templist = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (d.equals(list.get(i).getDay())) {
                    templist.add(list.get(i));
                }
            }
            //정렬을 해야하나?
            resultData.put(d, templist);
        }

        return resultData;
    }

    private ArrayList<TimetableSubject> setTimeTable(JsonArray array) {
        ArrayList<TimetableSubject> data = new ArrayList<>();

        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            String startTime = object.get("real_time").getAsString().split("-")[0];
            String endTime = object.get("real_time").getAsString().split("-")[1];
            String code = object.get("time_code").getAsString();
            for (String d : dayList) {
                if (object.has(d) && !object.get(d).getAsString().equals("null")) {
                    String temp_lecture = object.get(d).getAsString();
                    String lectureRoom = temp_lecture.substring(temp_lecture.lastIndexOf(' ') + 1);
                    String lectureName = temp_lecture.substring(0, temp_lecture.lastIndexOf(' '));

                    Days day = Days.fromString(d);

                    TimetableSubject temp = new TimetableSubject(lectureName, lectureRoom, day);
                    if (data.size() > 0 && data.contains(temp)) {
                        int i = data.indexOf(temp);
                        if (i >= 0) {
                            data.get(i).setEndTime(endTime);
                            data.get(i).setEndTimeCode(code);
                        }
                    } else if (data.size() == 0 || (data.size() > 0 && !data.contains(temp))) {
                        temp.setStartTime(startTime);
                        temp.setEndTime(endTime);
                        temp.setStartTimeCode(code);
                        temp.setEndTimeCode(code);
                        temp.setClassRoom(lectureRoom);
                        temp.setUnit(String.valueOf(0));
                        temp.setScore("A+");
                        data.add(temp);
                    }
                }
            }
        }

        return setRandomColor(data);
    }

    private JsonArray changeDayString(String str) {
        String result = str;
        result = result.replaceAll("time_day1", "MON");
        result = result.replaceAll("time_day2", "TUE");
        result = result.replaceAll("time_day3", "WED");
        result = result.replaceAll("time_day4", "THU");
        result = result.replaceAll("time_day5", "FRI");

        //System.out.print(result);
        JsonParser jsonParser = new JsonParser();
        return (JsonArray) jsonParser.parse(result);
    }

    private ArrayList<TimetableSubject> setRandomColor(ArrayList<TimetableSubject> subjects) {
        if (subjects.size() > 0) {
            int[] numbers = new int[subjects.size()];
            Random gen = new Random();

            for (int i = 0; i < subjects.size(); i++) {
                numbers[i] = gen.nextInt(AppContext.getContext().getResources().getIntArray(R.array.Color_array).length);
                for (int j = 0; j < i; j++) {
                    if (numbers[i] == numbers[j]) {
                        i = i - 1;
                        break;
                    }
                }
            }

            for (int i = 0; i < numbers.length; i++)
                subjects.get(i).setColor(numbers[i]);

        }
        return subjects;
    }
}
