package com.example.sec.whereami;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by songjonghun on 2016. 7. 16..
 */
public class LocalSeeker {

    public LocalSeeker() {}

    /* Google Place API를 이용할경우 */
    public String searchPlace(String lat, String lng, String radius, String types, String name) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + lat + "," + lng + "&" +
                "language=ko" + "&" +
                "types=" + types + "&" +
                "radius=" + radius + "&" +
                "name=" +name  + "&" +
                "key=" + API_Key.GOOGLE_KEY;
    }
    /* Google Geo API를 이용할경우 */
    public String getNearPlace(String lat, String lng) {
        return "https://maps.googleapis.com/maps/api/geocode/json?" +
                "latlng=" + lat + "," + lng + "&" +
                "language=ko" + "&" +
                "location_type=ROOFTOP" + "&" +
                "result_type=premise" + "&" +
                "key=" + API_Key.GOOGLE_KEY;
    }
    /* Url로부터 JSON DATA받아오기 */
    public String downloadFromUrl(String strUrl){
        StringBuilder sb = new StringBuilder();
        try{
            BufferedInputStream bis = null;
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            int responseCode;

            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            responseCode = con.getResponseCode();

            if(responseCode == 200){
                bis = new BufferedInputStream(con.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(bis,"UTF-8"));
                String line =null;
                while((line=reader.readLine())!=null)
                    sb.append(line);
                bis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    /* 위도 경도를 이용해 각도를 계산하는 함수 */
    public int bearingP1toP2(double P1_latitude, double P1_longitude, double P2_latitude, double P2_longitude, float cps) {
        double y = Math.sin(P2_longitude-P1_longitude) * Math.cos(P2_latitude);
        double x = Math.cos(P1_latitude)*Math.sin(P2_latitude) -
                   Math.sin(P1_latitude)*Math.cos(P2_latitude)*Math.cos(P2_longitude-P1_longitude);
        double result = Math.atan2(y, x) - cps*Math.PI/180;
        result = (result*180/Math.PI+360)%360;
        return (int)(result/30.0);
    }
    /* 위도 경도를 이용해 거리를 계산하는 함수 */
    public int calDistance(double lat1, double lon1, double lat2, double lon2){
        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return (int)dist;
    }
    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){return (double)(deg * Math.PI / (double)180d);}
    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){ return (double)(rad * (double)180d / Math.PI);}
}
