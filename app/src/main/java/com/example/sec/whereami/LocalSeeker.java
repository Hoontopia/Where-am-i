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
    public static double M_PI = Math.PI;
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
        double seta = getSeta(P1_latitude, P1_longitude, P2_latitude, P2_longitude);

        if(seta == cps) {
            return whatIsClockDir(seta);
        }
        else if(seta < cps) {
            return whatIsClockDir(seta - cps + 360);
        }
        else {
            return whatIsClockDir(seta-cps);
        }
    }

    public int whatIsClockDir(double seta) {
        int result = 12;
        if(345 < seta || seta <= 15){
            result = 12;
        }else if(15 < seta && seta <= 45){
            result = 1;
        }else if(45 < seta && seta <= 75){
            result = 2;
        }else if(75 < seta && seta <= 105){
            result = 3;
        }else if(105 < seta && seta <= 135){
            result = 4;
        }else if(135 < seta && seta <= 165){
            result = 5;
        }else if(165 < seta && seta <= 195){
            result = 6;
        }else if(195 < seta && seta <= 225){
            result = 7;
        }else if(225 < seta && seta <= 255){
            result = 8;
        }else if(255 < seta && seta <= 285){
            result = 9;
        }else if(285 < seta && seta <= 315){
            result = 10;
        }else if(315 < seta && seta <= 345){
            result = 11;
        }
        return result;
    }

    public double getSeta(double P1_latitude, double P1_longitude, double P2_latitude, double P2_longitude) {
        double Lat1, Long1, Lat2, Long2;
        double y,x;
        double ret;
        Lat1 = Math.toRadians(P1_latitude);
        Long1 = Math.toRadians(P1_longitude);
        Lat2 = Math.toRadians(P2_latitude);
        Long2 = Math.toRadians(P2_longitude);

        y = Math.sin(Long2 - Long1) * Math.cos(Lat2);
        x = Math.cos(Lat1) * Math.sin(Lat2) - Math.sin(Lat1) * Math.cos(Lat2) * Math.cos(Long2 - Long1);

        ret = Math.atan2(y,x);
        return Math.toDegrees(ret);

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
    private double deg2rad(double deg){ return (double)(deg * Math.PI / (double)180d);}
    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){ return (double)(rad * (double)180d / Math.PI);}
}