package com.example.sec.whereami;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements
        MapView.CurrentLocationEventListener, // 현재위치를 파악한다.
        TextToSpeech.OnInitListener, // 현재 액티비티에서 말할수 있다.
        MapReverseGeoCoder.ReverseGeoCodingResultListener, //트래킹 모드를 현재 액티비티에서 관리한다
        MapView.MapViewEventListener, /* 맵에 대한 행동을 현재 액티비티에서 관리한다 */
        SensorEventListener
{
    String lat = null;
    String lng = null;
    static String name = null; //
    static String typename = null;
    static String types = null;
    static String radius = "50";
    String[] keys;
    String[] values;
    LocalSeeker localSeeker;
    MapView mapView;
    MapReverseGeoCoder mReverseGeoCoder;
    TextToSpeech _tts;
    PickerDlg pickerDlg;
    boolean _ttsActive = false;
    EditText nameText;
    TextView current_Text;
    boolean search_Mode = false;
    SensorManager mSensorManager;
    Sensor mOrientation;
    CpsManager cpsManager;
    ViewFlipper flipper;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localSeeker = new LocalSeeker();
        keys = this.getResources().getStringArray(R.array.myvariablename_keys);
        values = this.getResources().getStringArray(R.array.myvariablename_values);
        pickerDlg = new PickerDlg(this, keys, values);
        mapView = new MapView(this);
        mapView.setDaumMapApiKey(API_Key.DAUM_KEY);
        mapView.setCurrentLocationEventListener(this); //리스너 등록 (MainActivity가 지도맵의 이벤트를 관리한다)
        mapView.setMapViewEventListener(this);  // 리스너 등록 (MainActivity가 지도맵을 관리한다)
        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);
        container.addView(mapView);
        cpsManager = new CpsManager();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        list = (ListView)findViewById(R.id.list);
        flipper=(ViewFlipper)findViewById(R.id.viewFlipper);
        nameText = (EditText)findViewById(R.id.nameText);
        current_Text = (TextView)findViewById(R.id.currentLOC);
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }
    @Override /* 트래킹 모드가 ON일 때 사용자의 현재 위치가 업데이트 될 때 불려지는 함수 입니다. */
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        /* 현재위치의 위도 경도 값 출력 */
        lat = String.valueOf(mapPoint.getMapPointGeoCoord().latitude);
        lng = String.valueOf(mapPoint.getMapPointGeoCoord().longitude);
        JsonFormatPs ps = new JsonFormatPs();
        /* 위도 경도 더한 통해 url을 가져오는 함수 */
        if(search_Mode == false) { //
            String url = localSeeker.getNearPlace(lat, lng);
            Log.d("URL: ", url);
            ps.execute("NEAR", url);
            _tts.speak("검색결과가 갱신되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
        }
        else if(search_Mode == true){
            String url = localSeeker.searchPlace(lat, lng, radius, types, name);
            Log.d("URL: ", url);
            ps.execute("SEARCH", url);
            _tts.speak("검색결과가 갱신되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
        }
        /*  현재위치 Reverse Geo-Coding 결과를 비동기적으로 통보받고 싶을때 */
        //mReverseGeoCoder = new MapReverseGeoCoder(API_Key.DAUM_KEY, mapView.getMapCenterPoint(), this, this);
        //mReverseGeoCoder.startFindingAddress();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation,
                SensorManager.SENSOR_DELAY_UI);
        _tts = new TextToSpeech(getApplicationContext(), this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        /* 음성 */
        try{
            if(_tts != null){
                _tts.stop();
                _ttsActive = false;
            }
        }catch (Exception e){
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (_tts != null) {
                _tts.shutdown();
                _tts = null;
            }
        }catch(Exception e){
        }
    }
    public void OnOptionClicked(View view) {//
        switch (view.getId()){
            case R.id.tracking:
                search_Mode = false;
                String url = localSeeker.getNearPlace(lat, lng);
                JsonFormatPs ps = new JsonFormatPs();
                ps.execute("NEAR", url);
                _tts.speak("주변 건물 검색 모드가 설정되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.searchOPT:
                search_Mode = true;
                pickerDlg.show();
                _tts.speak("검색하려는 타입과 반경을 스크롤하여 선택해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.search:
                if(search_Mode == true) {
                    name = nameText.getText().toString();
                    url = localSeeker.searchPlace(lat, lng, radius, types, name);
                    ps = new JsonFormatPs();
                    Log.d("URL: ", url);
                    ps.execute("SEARCH", url);
                    _tts.speak("검색결과가 갱신되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
                    flipper.showNext();
                }
                break;
            case R.id.tts_Setting:
        }
    }
    @Override /* 현재위치 주소를 찾은 경우 호출된다. */
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }
    @Override /* 현재위치 주소를 못찾은 경우 호출된다. */
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }
    private void onFinishReverseGeoCoding(String result) {
        _tts.setLanguage(Locale.KOREA);
        _ttsActive=true;
        _tts.speak("이름 : "+ result, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override /* 맵을 다 보여줬을때 호출되는 함수*/
    public void onMapViewInitialized(MapView mapView) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading); // 좌표 추적모드 On
    }
    /* 인터넷이 켜저있는지 확인하는 함수 */
    private boolean isNetworkAvailable() {
        boolean available = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable())
            available = true;
        return available;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            cpsManager.setAzimuth(event.values[0]);
            //Log.d("방위각: ", String.valueOf(event.values[0]));
        }
    }
    private class JsonFormatPs extends AsyncTask<String, Integer, String> {
        String jsonInfo = null;
        @Override
        protected String doInBackground(String... url) {
            /* Url에 있는 JSON 형식의 데이터 받아오기 */
            jsonInfo = localSeeker.downloadFromUrl(url[1]);
            if(url[0].equals("NEAR")) // 만약 Geo API를 원할경우
                return "NEAR";
            else                 //  Google Place API를 원할경우
                return "SEARCH";

        }
        @Override
        protected void onPostExecute(String s) {
            if(s.equals("NEAR"))
                nearPlacePs(jsonInfo); //Geo API를 이용해 처리한다.
            else
                searchPlacePs(jsonInfo);  //Google Place API를 이용해 처리한다.
            this.cancel(true);
        }

        /* Google Place API를 위한 함수 */
        public void searchPlacePs(String jsonData) {
            try {
                JSONParser jsonParser = new JSONParser();
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(jsonData);
                org.json.simple.JSONArray result = (org.json.simple.JSONArray) jsonObject.get("results");
                String address[] = new String[result.size()+1];
                address[0] = "주변 "+ radius + "미터 안의 " + types + "검색결과";
                int addridx = 1;
                for(int i=0; i<result.size(); i++) {
                    org.json.simple.JSONObject testObject = (org.json.simple.JSONObject) result.get(i);
                    String res = (String)testObject.get("name");
                    org.json.simple.JSONObject geo = (org.json.simple.JSONObject) testObject.get("geometry");
                    org.json.simple.JSONObject loc = (org.json.simple.JSONObject) geo.get("location");

                    if(isStringDouble(res))
                        continue;

                    int dist = localSeeker.calDistance(Double.parseDouble(lat), Double.parseDouble(lng), (double)loc.get("lat"), (double)loc.get("lng"));
                    int dirs = localSeeker.bearingP1toP2(Double.parseDouble(lat), Double.parseDouble(lng), (double)loc.get("lat"), (double)loc.get("lng"), cpsManager.getAzimuth());
                    res += "  " + String.valueOf(dist) + "미터";
                    res += "  " + String.valueOf(dirs) + "시방향";
                    Toast.makeText(getApplicationContext(),"result: " + res, Toast.LENGTH_SHORT).show();
                    address[addridx++] = res;
                }
                GeoList geoList = new GeoList(MainActivity.this, address);// 추가
                list.setAdapter(geoList);// 추가
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        /* Google Geo API를 위한 함수 */
        public void nearPlacePs(String jsonData) {
            HashMap<Integer, String> hashMap = new HashMap();

            try{
                JSONParser jsonParser = new JSONParser();
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(jsonData);
                org.json.simple.JSONArray result = (org.json.simple.JSONArray) jsonObject.get("results");
                String address[] = new String[result.size()+1];
                int addridx = 1;
                address[0] = "주변 건물 검색결과";
                for(int i=0; i<result.size(); i++) {
                    org.json.simple.JSONObject object = (org.json.simple.JSONObject)result.get(i);

                    String formatted_address = (String)object.get("formatted_address");

                    org.json.simple.JSONArray address_components  = ( org.json.simple.JSONArray) object.get("address_components");
                    org.json.simple.JSONObject name = (org.json.simple.JSONObject) address_components.get(0);
                    String res = (String)name.get("long_name");
                    org.json.simple.JSONObject geo = (org.json.simple.JSONObject) object.get("geometry");
                    org.json.simple.JSONObject loc = (org.json.simple.JSONObject) geo.get("location");

                    if(isStringDouble(res))
                        continue;

                    int dist = localSeeker.calDistance(Double.parseDouble(lat), Double.parseDouble(lng), (double)loc.get("lat"), (double)loc.get("lng"));
                    int dirs = localSeeker.bearingP1toP2(Double.parseDouble(lat), Double.parseDouble(lng), (double)loc.get("lat"), (double)loc.get("lng"), cpsManager.getAzimuth());
                    res += "  " + String.valueOf(dist) + "미터";
                    res += "  " + String.valueOf(dirs) + "시방향";
                    Toast.makeText(getApplicationContext(),"result: " + res, Toast.LENGTH_SHORT).show();
                    address[addridx++] = res;
                    hashMap.put(dist,formatted_address);
                }

                TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>( hashMap );
                Iterator<Integer> treeMapIter = treeMap.keySet().iterator();
                treeMapIter.hasNext();
                int key = treeMapIter.next();
                String value = treeMap.get( key );
                current_Text.setText(value);

                GeoList geoList = new GeoList(MainActivity.this, address);
                list.setAdapter(geoList);// 추가
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @Override
    protected void onStart() {super.onStart();}
    @Override
    protected void onStop() {super.onStop();}
    public void onInfoClicked(View view) { /* 리스트뷰 띄우기 */ flipper.showNext(); }
    public void onexitClicked(View view) { /* 지도복귀 */ flipper.showPrevious();}
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {}
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {}
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {}
    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {}
    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {}
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {}
    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {}
    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {}
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    @Override
    public void onInit(int status) {}
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {}
    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {}
    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {}
}
