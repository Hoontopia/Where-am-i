package com.example.sec.whereami;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements
        MapView.CurrentLocationEventListener, // 현재위치를 파악한다.
        TextToSpeech.OnInitListener, // 현재 액티비티에서 말할수 있다.
        MapReverseGeoCoder.ReverseGeoCodingResultListener, //트래킹 모드를 현재 액티비티에서 관리한다
        MapView.MapViewEventListener, /* 맵에 대한 행동을 현재 액티비티에서 관리한다 */
        SensorEventListener, DialogInterface.OnDismissListener
{
    private String lat = null;
    private String lng = null;
    private String name = null;
    private String typeName = "건물";
    private String types = "buildings";
    private String radius = "50";
    private RelativeLayout container;
    private String[] keys;
    private String[] values;
    private MapView mapView;
    public TextToSpeech _tts;
    private EditText nameText;
    private TextView current_Text;
    public SensorManager mSensorManager;
    public Sensor mOrientation;
    public CpsManager cpsManager;
    private ViewFlipper flipper, menu_flipeper;
    private ListView list;
    private ImageButton[] imageButtons;
    private GeoList geoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButtons = new ImageButton[6];
        if(isNetworkAvailable()==false){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("인터넷 연결상태 불량")
                    .setMessage("인터넷을 연결상태를 확인 해주세요")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int whichButton){
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            mapView = new MapView(this);
            mapView.setDaumMapApiKey(API_Key.DAUM_KEY);
            mapView.setCurrentLocationEventListener(this); //리스너 등록 (MainActivity가 지도맵의 이벤트를 관리한다)
            mapView.setMapViewEventListener(this);  // 리스너 등록 (MainActivity가 지도맵을 관리한다)
            container = (RelativeLayout) findViewById(R.id.map_view);
            container.addView(mapView);
            AccessibilitySetting.setAccessibilityIgnore(mapView);
            mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        list = (ListView)findViewById(R.id.list);
        flipper=(ViewFlipper)findViewById(R.id.viewFlipper);
        menu_flipeper = (ViewFlipper)findViewById(R.id.viewFlipper2);
        current_Text = (TextView)findViewById(R.id.currentLOC);
        AccessibilitySetting.setAccessibilityIgnore(current_Text);
        nameText = (EditText)findViewById(R.id.nameText);
        nameText.setInputType(InputType.TYPE_NULL); // 가상키보드 숨기기
        cpsManager = new CpsManager();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        keys = this.getResources().getStringArray(R.array.myvariablename_keys);
        values = this.getResources().getStringArray(R.array.myvariablename_values);
        _tts = new TextToSpeech(getApplicationContext(), this);
        _tts.setLanguage(Locale.KOREA);

        imageButtons[0] = (ImageButton)findViewById(R.id.addressInfo);
        imageButtons[1] = (ImageButton)findViewById(R.id.searchOPT);
        imageButtons[2] = (ImageButton)findViewById(R.id.search);
        imageButtons[3] = (ImageButton)findViewById(R.id.settings);
    }

    @Override /* 트래킹 모드가 ON일 때 사용자의 현재 위치가 업데이트 될 때 불려지는 함수 입니다. */
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        /* 현재위치의 위도 경도 값 출력 */
        lat = String.valueOf(mapPoint.getMapPointGeoCoord().latitude);
        lng = String.valueOf(mapPoint.getMapPointGeoCoord().longitude);
        Log.d("", lat+" "+lng);
        GetCurrentLoc getCurrentLoc = new GetCurrentLoc();
        getCurrentLoc.execute(types, typeName, name, radius, lat, lng);
    }

    public void OnOptionClicked(View view) {
        switch (view.getId()){
            case R.id.addressInfo:
                String currentLOC = current_Text.getText().toString();
                _tts.speak(currentLOC, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.add_loc:
                Register_LOC_Dlg reg_loc = new Register_LOC_Dlg(this, keys, values, lat, lng);
                reg_loc.show();
                reg_loc.setCanceledOnTouchOutside(false);
                break;
            case R.id.searchOPT:
                PickerDlg pickerDlg=new PickerDlg(this, keys, values, _tts);
                pickerDlg.setOnDismissListener(this);
                pickerDlg.setCancelable(false);
                pickerDlg.show();
                _tts.speak("검색하려는 타입과 반경을 스크롤하여 선택해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.search:
                name = nameText.getText().toString();
                GetCurrentLoc getCurrentLoc=new GetCurrentLoc();
                getCurrentLoc.execute(types, typeName, name, radius, lat, lng);
                _tts.speak("검색결과가 갱신되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
                flipper.showNext();
                menu_flipeper.showNext();
                break;
            case R.id.settings: break;
            case R.id.nameText:
                SpeechRecognition speechRecognition;
                speechRecognition = new SpeechRecognition(this, nameText);
                break;
        }
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        PickerDlg _dialog = (PickerDlg) dialog;
        types = _dialog.getType();
        typeName = _dialog.getTypeName();
        radius = _dialog.getRadius();
    }

    private class GetCurrentLoc extends AsyncTask<String, Integer, String[]> {
        Parsing parsing = new Parsing();
        @Override
        protected String[] doInBackground(String... params) {
            String[] result;
            result=parsing.getLocData(params[0],params[1],params[2],params[3],params[4],params[5]);
            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            geoList = new GeoList(MainActivity.this, strings);
            list.setAdapter(geoList);
            if(types.equals("buildings")) current_Text.setText(parsing.getCurrentLoc());
            Log.d("debug", types);
        }
    }

    public void onInfoClicked(View view) { /* 리스트뷰 띄우기 */
        flipper.showNext();
        menu_flipeper.showNext();
        _tts.speak("검색결과 리스트로 진입하였습니다.", TextToSpeech.QUEUE_FLUSH, null);
        for(int i=0; i<4; i++)
            AccessibilitySetting.setAccessibilityIgnore(imageButtons[i]);
        AccessibilitySetting.setAccessibilityIgnore(nameText);
    }
    public void onexitClicked(View view) { /* 지도복귀 */
        flipper.showPrevious();
        menu_flipeper.showPrevious();
        _tts.speak("검색결과 리스트를 빠져나왔습니다.", TextToSpeech.QUEUE_FLUSH, null);
        for(int i=0; i<4; i++)
            AccessibilitySetting.setAccessibilitySetting(imageButtons[i]);
        AccessibilitySetting.setAccessibilitySetting(nameText);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation,
                SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        /* 음성 */
        try{
            if(_tts != null){
                _tts.stop();
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
    @Override /* 맵을 다 보여줬을때 호출되는 함수*/
    public void onMapViewInitialized(MapView mapView) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading); // 좌표 추적모드 On
    }
    @Override /* 현재위치 주소를 찾은 경우 호출된다. */
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {}
    @Override /* 현재위치 주소를 못찾은 경우 호출된다. */
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {}
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
