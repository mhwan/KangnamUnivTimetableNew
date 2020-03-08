package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Items.LibrarySeatItem;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppContext;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;

import static java.lang.Integer.parseInt;

public class LibrarySeatFragment extends Fragment {
    private View view;
    private TextView[] usage_total, usage_remain, usage_use;
    private PieView[] pie;
    public LibrarySeatFragment(){

    }
    public static LibrarySeatFragment newInstance() {
        LibrarySeatFragment fragment = new LibrarySeatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library_usage, container, false);
        initView();
        return view;
    }

    private void initView(){
        usage_total = new TextView[3];
        usage_remain = new TextView[3];
        usage_use = new TextView[3];
        pie = new PieView[3];
        for (int i = 0; i<3; i++){
            String total = "library_usage_total_textview_"+(i+1);
            String remain = "library_usage_empty_textview_"+(i+1);
            String used = "library_usage_used_textview_"+(i+1);
            String pied = "usage_pie_"+(i+1);

            usage_total[i] = view.findViewById(getResources().getIdentifier(total, "id", getContext().getPackageName()));
            usage_remain[i] = view.findViewById(getResources().getIdentifier(remain, "id", getContext().getPackageName()));
            usage_use[i] = view.findViewById(getResources().getIdentifier(used, "id", getContext().getPackageName()));
            pie[i] = view.findViewById(getResources().getIdentifier(pied, "id", getContext().getPackageName()));
        }

        for (int i = 0; i<3; i++) {
            pie[i].setTextColor(ContextCompat.getColor(AppContext.getContext(), R.color.colorPrimaryBlack));
            pie[i].setInnerBackgroundColor(ContextCompat.getColor(AppContext.getContext(), R.color.colorBackgroundLightBlue));
            pie[i].setMainBackgroundColor(ContextCompat.getColor(AppContext.getContext(), R.color.colorBackgroundPie));
        }
        pie[0].setPercentageBackgroundColor(ContextCompat.getColor(AppContext.getContext(), R.color.colorPieLightGreen));
        pie[1].setPercentageBackgroundColor(ContextCompat.getColor(AppContext.getContext(), R.color.colorPieSkyBlue));
        pie[2].setPercentageBackgroundColor(ContextCompat.getColor(AppContext.getContext(), R.color.colorPrimary));

        doSeatWork();
    }

    private void doSeatWork(){
        new KnuLibrarySeatTask().execute();
    }
    private void updateView(ArrayList<LibrarySeatItem> libraryItems){
        if (libraryItems != null && libraryItems.size() > 0) {
            for (int i=0; i<libraryItems.size(); i++){
                int total = libraryItems.get(i).getTotalSeat();
                int use = libraryItems.get(i).getUseSeat();
                int remain = libraryItems.get(i).getEmptySeat();

                float usage = (float) Math.round(((use*100d)/total)*10)/10.0f;
                //Log.d("library_"+i, remain+"");
                //Log.d("usage_"+i, usage+"");
                usage_total[i].setText(String.valueOf(total));
                usage_use[i].setText(String.valueOf(use));
                usage_remain[i].setText(String.valueOf(13));
                if (usage == 0)
                    pie[i].setPercentage(0.2f);
                pie[i].setInnerText(usage+"%");

                startPieAnimation();
            }
        } else {
            for (int i= 0; i<3; i++) {
                usage_total[i].setText("0");
                usage_use[i].setText("0");
                usage_remain[i].setText("0");
                pie[i].setPercentage(0.0f);
                pie[i].setInnerText("0%");
            }
        }
    }

    private void startPieAnimation(){
        for (int i = 0; i<3; i++) {
            PieAngleAnimation p = new PieAngleAnimation(pie[i]);
            p.setDuration(1100+(i*100));
            pie[i].startAnimation(p);
        }
    }
    class KnuLibrarySeatTask extends AsyncTask<Void, Void, ArrayList> {
        public static final String LIBRARY_SEAT_URL = "http://223.194.112.88/SEAT/domian5.asp";
        public KnuLibrarySeatTask(){}

        @Override
        protected void onPreExecute() {
            if (!AppUtility.getAppinstance().isNetworkConnection())
                cancel(true);
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            if (arrayList == null)
                Toast.makeText(AppContext.getContext(), AppContext.getContext().getString(R.string.message_error_library_seat), Toast.LENGTH_SHORT).show();
            updateView(arrayList);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(AppContext.getContext(), AppContext.getContext().getString(R.string.message_no_network), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayList doInBackground(Void... voids) {
            ArrayList<LibrarySeatItem> itemses = new ArrayList<>();
            URL url = null;
            try {
                url = new URL(LIBRARY_SEAT_URL);
                Document document = Jsoup.parse(url, 10000);
                Elements elements = document.getElementsByTag("tr");

                for (int i = 3; i < elements.size(); i++) {
                    Elements innerElements = elements.get(i).getElementsByTag("td");
                    int total = parseInt(innerElements.get(2).text());
                    int useSeat = parseInt(innerElements.get(3).text());
                    int emptySeat = total - useSeat;
                    itemses.add(new LibrarySeatItem(total, useSeat, emptySeat));
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return itemses;
        }
    }
}
