package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.CustomUI.DividerItemDecoration;
import com.mhwan.kangnamunivtimetable.CustomUI.EmployeeSearchAdapter;
import com.mhwan.kangnamunivtimetable.Items.EmployeeItem;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class EmployeeSearchActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private EmployeeSearchAdapter searchAdapter;
    public static final String GET_SEARCH_URL = "https://web.kangnam.ac.kr/menu/7a5545db676e4a6f2d3995ceed5e7c4d.do?searchValue3=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFromBaseView(R.layout.activity_employee_search);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_main);
        initView();
    }

    private void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.emp_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.line_divider));
        searchAdapter = new EmployeeSearchAdapter(this);
        recyclerView.setAdapter(searchAdapter);

        searchView = (SearchView) findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() >= 2){
                    new KnuSearchTask(s).execute();
                } else
                    Toast.makeText(getActivity(), getString(R.string.message_more_than_two), Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.requestFocus();
        setAllViews(0);
    }

    private void setAllViews(int i){
        if (i > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void refreshSearchResult(ArrayList data) {
        setAllViews(data.size());
        searchAdapter.resetEmployeeList(data);
        if (data.size() == 0)
            Toast.makeText(getActivity(), getString(R.string.message_search_no_result),Toast.LENGTH_SHORT).show();
    }
    class KnuSearchTask extends AsyncTask<Void, Void, ArrayList> {
        private String query;

        public KnuSearchTask(String query){
            this.query = query;
        }
        @Override
        protected void onPreExecute() {
            if (!AppUtility.getAppinstance().isNetworkConnection())
                cancel(true);
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            refreshSearchResult(arrayList);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(EmployeeSearchActivity.this, getString(R.string.message_no_network), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayList doInBackground(Void... voids) {
            Document document = null;
            ArrayList<EmployeeItem> list = new ArrayList<>();
            try {
                document = Jsoup.connect(GET_SEARCH_URL+query).header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9").get();
                Elements items = document.select("div.tbody ul");
                for (Element item : items){
                    EmployeeItem emp = new EmployeeItem();

                    Elements liItem = item.select("li");
                    liItem.select("span.mb_th").remove();

                    emp.setAffiliation(liItem.get(0).text());
                    emp.setPhonenumber(liItem.get(2).text());
                    emp.setName(getRealName(liItem.get(1).text()));
                    emp.setEmail(liItem.get(4).text());
                    String[] sep = separateLocations(liItem.get(3).text());
                    if (sep != null) {
                        emp.setLocation(sep[0]);
                        emp.setLocation2(sep[1]);
                    }

                    list.add(emp);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
    }

    private String[] separateLocations(String str) {
        if (str == null || str == "")
            return null;
        if (!str.contains("-")) {
            return new String[] {str, ""};
        } else {
            String[] result = str.split("-");
            result[1] = result[1].replaceAll("[()]", "");

            return result;
        }
    }

    private String getRealName(String str){
        String result = "";
        if (str == null || str == "")
            return "";

        int i = str.lastIndexOf("(");
        if (i < 0)
            return str;
        else {
            result = str.substring(0, i);
            return result;
        }
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.name_navi_search);
    }

    @Override
    protected Activity getActivity() {
        return EmployeeSearchActivity.this;
    }

    @Override
    protected void hideKeyboard() {

    }
    @Override
    protected void finishActivity() {
        this.finish();
    }
}
