package com.example.sec.whereami;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, TextToSpeech.OnInitListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {
    MapView mapView;
    MapReverseGeoCoder mReverseGeoCoder;
    TextToSpeech _tts;
    boolean _ttsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(API_Key.key);
        mapView.setCurrentLocationEventListener(this);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);
        container.addView(mapView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading); // 좌표 추적모드 On
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mReverseGeoCoder = new MapReverseGeoCoder(API_Key.key, mapView.getMapCenterPoint(), this, this);
        mReverseGeoCoder.startFindingAddress();
        // 현재위치 바뀔때마다 Toast
    }
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        _tts = new TextToSpeech(getApplicationContext(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
    @Override
    public void onInit(int status) {
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
        Toast.makeText(this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
        _tts.setLanguage(Locale.KOREA);
        _ttsActive=true;
        _tts.speak("이름 : "+ result, TextToSpeech.QUEUE_FLUSH, null);
    }
}
