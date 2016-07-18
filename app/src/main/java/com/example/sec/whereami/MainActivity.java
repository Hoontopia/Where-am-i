package com.example.sec.whereami;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
<<<<<<< HEAD
=======
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
<<<<<<< HEAD

import net.daum.android.map.util.URLEncoder;
=======
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

<<<<<<< HEAD
<<<<<<< HEAD

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {
=======
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, TextToSpeech.OnInitListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {
>>>>>>> d6063f55c83bf4a42776e5d90a036d926806de62
=======
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, TextToSpeech.OnInitListener,
        MapReverseGeoCoder.ReverseGeoCodingResultListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
    MapView mapView;
    MapReverseGeoCoder mReverseGeoCoder;
    TextToSpeech _tts;
    boolean _ttsActive = false;
<<<<<<< HEAD

=======
    private GoogleApiClient mGoogleApiClient;
>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(API_Key.key);
        mapView.setCurrentLocationEventListener(this);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);
        container.addView(mapView);

<<<<<<< HEAD

    }

    @Override
=======
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading); // 좌표 추적모드 On
<<<<<<< HEAD
=======
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            public static final String TAG = "뷁" ;
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
            }
        });
>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
<<<<<<< HEAD
<<<<<<< HEAD
        MapReverseGeoCoder mapGeoCoder = new MapReverseGeoCoder("4b84abed6f29d6e833dc233ec40243ed", mapPoint, this, this );
        mapGeoCoder.startFindingAddress();

=======
=======

>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mReverseGeoCoder = new MapReverseGeoCoder(API_Key.key, mapView.getMapCenterPoint(), this, this);
        mReverseGeoCoder.startFindingAddress();
        // 현재위치 바뀔때마다 Toast
<<<<<<< HEAD
>>>>>>> d6063f55c83bf4a42776e5d90a036d926806de62
=======

>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
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
<<<<<<< HEAD
<<<<<<< HEAD
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

=======
=======
>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
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
<<<<<<< HEAD
>>>>>>> d6063f55c83bf4a42776e5d90a036d926806de62
=======
>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
<<<<<<< HEAD
<<<<<<< HEAD

=======
=======
>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
<<<<<<< HEAD
        Toast.makeText(this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
        _tts.setLanguage(Locale.KOREA);
        _ttsActive=true;
        _tts.speak("이름 : "+ result, TextToSpeech.QUEUE_FLUSH, null);
>>>>>>> d6063f55c83bf4a42776e5d90a036d926806de62
=======
        /*
        Toast.makeText(this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();*/
        _tts.setLanguage(Locale.KOREA);
        _ttsActive=true;
        _tts.speak("이름 : "+ result, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

>>>>>>> 304b92965cbea079e3cd2a09acef1fa5155bec41
    }
}
