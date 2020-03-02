package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SchoolScheduleFragment extends Fragment {
    private View view;
    private WebView webView;
    private String html = "Loading...";
    final String mime = "text/html";
    final String encoding = "utf-8";

    public static SchoolScheduleFragment newInstance() {
        SchoolScheduleFragment fragment = new SchoolScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SchoolScheduleFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_school_schedule, container, false);
        initView();
        return view;
    }

    private void initView() {
        webView = view.findViewById(R.id.web_view);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //Display "Loading..." message while waiting
        webView.loadData(html, mime, encoding);
        //Invoke the AsyncTask
        new GetSchedule().execute();

    }

    private class GetSchedule extends AsyncTask<Void, Void, String> {
        // This is run in a background thread
        private static final String URL_SCHEDULE = "http://web.kangnam.ac.kr/menu/02be162adc07170ec7ee034097d627e9.do";

        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(URL_SCHEDULE)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .get();

                Elements ele = doc.select("div.cont");
                doc.body().empty().append(ele.toString());
                html = doc.toString();
                return html;
            } catch (Exception e) {
                //Log.d("APP", e.toString());
            }
            return "error";
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.d("APP", "1");
            if (result == "error")
                Toast.makeText(AppContext.getContext(), AppContext.getContext().getString(R.string.message_cancel_school_schedule), Toast.LENGTH_SHORT).show();

            webView.loadDataWithBaseURL(URL_SCHEDULE, result, mime, encoding, "");
        }
    }
}
