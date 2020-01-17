package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.CustomUI.MapInfoAdapter;
import com.mhwan.kangnamunivtimetable.Items.MapRoom;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AccountPreference;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.ScreenUtility;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MapActivity extends BaseActivity implements MapView.OpenAPIKeyAuthenticationResultListener, MapView.POIItemEventListener {
    private BottomSheetBehavior bottomSheetBehavior;
    private FloatingActionButton fab;
    private ListView mapListview;
    private MapInfoAdapter adapter;
    private ImageButton button_dropdown;
    private int index;
    private String[] locations;
    private MapView mapView;
    private Spinner spinner;
    private Bundle bundle = null;
    public static String GET_INFO_URL = "http://web.kangnam.ac.kr/menu/183cf81ad3b35044012126decdcb581a.do?";
    private HashMap<Integer, String> list = new HashMap<Integer, String>() {{
        put(0, "bldgCode=101&lat=37.2765655&lng=127.133972");
        put(1, "bldgCode=102&lat=37.2769444&lng=127.133579");
        put(2, "bldgCode=103&lat=37.2741664&lng=127.13204");
        put(3, "bldgCode=105&lat=37.2754045&lng=127.13078");
        put(4, "bldgCode=106&lat=37.2754196&lng=127.133328");
        put(5, "bldgCode=107&lat=37.2771126&lng=127.134244");
        put(6, "bldgCode=108&lat=37.2765470&lng=127.132428");
        put(7, "bldgCode=109&lat=37.2761135&lng=127.133273");
        put(8, "bldgCode=110&lat=37.2761521&lng=127.130995");
        put(9, "bldgCode=111&lat=37.2781302&lng=127.134753");
        put(10, "bldgCode=112&lat=37.2778954&lng=127.135193");
        put(11, "bldgCode=143&lat=37.2784646&lng=127.133818");
        put(12, "bldgCode=113&lat=37.2757700&lng=127.134242");
        put(13, "bldgCode=114&lat=37.2758360&lng=127.131648");
        put(14, "bldgCode=127&lat=37.2750178&lng=127.130102");
        put(15, "bldgCode=141&lat=37.2745286&lng=127.132469");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFromBaseView(R.layout.activity_map);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_main);

        index = getIntent().getIntExtra("loc_index", 14);
        getInfoWork();
        initView();

        showToastAtFirstTime();
    }

    private void showToastAtFirstTime(){
        AccountPreference accountPreference = new AccountPreference(this);
        if (accountPreference.getOpenMaps() == false){
            Toast.makeText(getContext(), getString(R.string.message_first_information_map_open), Toast.LENGTH_SHORT).show();
            accountPreference.setOpenMaps(true);
        }
    }
    private void initView() {
        locations = getResources().getStringArray(R.array.Building_name_array);
        mapView = new MapView(this);
        mapView.setDaumMapApiKey("861614ba95a9ab283a78cbd33893ee00");
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        initMapPoint();
        selectMap(index);
        fab = findViewById(R.id.fab_map);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double[] geo = parsingGeoCode(list.get(index));
                Uri uri = Uri.parse("geo:" + geo[0] + "," + geo[1]);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        button_dropdown = findViewById(R.id.button_drop);
        button_dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateButton(true);
            }
        });
        FrameLayout bottomsheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        rotateButton(false);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Slide ...
                // 1이면 완전 펼쳐진 상태
                // 0이면 peekHeight인 상태
                // -1이면 숨김 상태
                //Log.i("TAG", "slideOffset " + slideOffset);
            }
        });


        mapListview = findViewById(R.id.map_listview);
        mapListview.setTextFilterEnabled(true);
        SearchView searchView = findViewById(R.id.map_search_view);
        setSearchViewColor(searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText))
                    mapListview.clearTextFilter();
                else
                    mapListview.setFilterText(newText);
                return true;
            }
        });
        setSpinner();

    }

    private void selectMap(int id) {
        MapPOIItem[] poiItem = mapView.getPOIItems();
        mapView.selectPOIItem(poiItem[id], true);
        mapView.setMapCenterPoint(poiItem[id].getMapPoint(), true);
    }

    private void initMapPoint() {
        MapPOIItem[] poiItems = new MapPOIItem[list.size()];
        for (int i = 0; i < list.size(); i++) {
            poiItems[i] = new MapPOIItem();
            Double[] geo = parsingGeoCode(list.get(i));
            poiItems[i].setMapPoint(MapPoint.mapPointWithGeoCoord(geo[0], geo[1]));
            poiItems[i].setTag(i);
            poiItems[i].setItemName(locations[i]);
            poiItems[i].setMarkerType(MapPOIItem.MarkerType.YellowPin);
            poiItems[i].setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);
        }
        mapView.setPOIItemEventListener(this);
        mapView.addPOIItems(poiItems);
    }

    private void rotateButton(boolean click) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            button_dropdown.animate().rotation(180).start();
            if (click)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            button_dropdown.animate().rotation(360).start();
            if (click)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void refreshInfoView(ArrayList<MapRoom> info) {
        if (!info.isEmpty()) {
            adapter = new MapInfoAdapter(MapActivity.this);
            adapter.setArraylist(info);
            mapListview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            mapListview.invalidateViews();
            mapListview.refreshDrawableState();
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        int i = mapPOIItem.getTag();
        spinner.setSelection(i);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    class KnuINFOTask extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected void onPreExecute() {
            if (!AppUtility.getAppinstance().isNetworkConnection()) {
                cancel(true);
            }
        }

        @Override
        protected ArrayList<MapRoom> doInBackground(Void... voids) {
            Document document = null;
            ArrayList<MapRoom> roons = new ArrayList<>();
            try {
                document = Jsoup.connect(GET_INFO_URL + list.get(index)).header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8").get();
                Elements items = document.select("div.tbody ul");
                for (Element item : items) {
                    Elements article = item.select("li");
                    String floor = article.get(0).text();
                    String name = article.get(1).text();
                    roons.add(new MapRoom(floor, name));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return roons;
        }

        @Override
        protected void onPostExecute(ArrayList data) {
            refreshInfoView(data);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(MapActivity.this, getString(R.string.message_cancel_building_info), Toast.LENGTH_SHORT).show();
        }
    }

    private void getInfoWork() {
        new KnuINFOTask().execute();
    }

    private void setSpinner() {
        spinner = findViewById(R.id.map_location_spinner);
        ArrayAdapter<String> loc_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, locations);
        loc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(loc_adapter);
        spinner.setSelection(index);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (index != position) {
                    index = position;
                    selectMap(index);
                    getInfoWork();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSearchViewColor(SearchView searchView) {
        LinearLayout ll = (LinearLayout) searchView.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(2);
        LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);

        SearchView.SearchAutoComplete autoComplete = searchView.findViewById(R.id.search_src_text);
        ImageView searchCloseButton = (ImageView) ll3.getChildAt(1);
        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ImageView labelView = (ImageView) ll.getChildAt(1);
        autoComplete.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenUtility.getInstance().dpToPx(16));
        autoComplete.setHintTextColor(getResources().getColor(R.color.colorLightBlack));
        autoComplete.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));

        searchIcon.getDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        searchCloseButton.getDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        labelView.getDrawable().setColorFilter(getResources().getColor(R.color.colorLightBlack), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {
        Log.i("DaumMap", String.format("Open API Key Authentication Result : code=%d, message=%s", i, s));
    }

    private Double[] parsingGeoCode(String value) {
        String[] temps = value.split("&");
        Double[] result = new Double[2];
        result[0] = Double.parseDouble(temps[1].substring(4));
        result[1] = Double.parseDouble(temps[2].substring(4));

        return result;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.name_campusMap);
    }

    @Override
    protected Activity getActivity() {
        return MapActivity.this;
    }

    @Override
    protected void hideKeyboard() {
        AppUtility.getAppinstance().hideKeyboard(getActivity());
    }
}
