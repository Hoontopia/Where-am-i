package com.example.sec.whereami;

/**
 * Created by CodeFactory on 2016-08-11.
 */
public class Calculate {
    /* 위도 경도를 이용해 각도를 계산하는 함수 */
    static public int bearingP1toP2(double P1_latitude, double P1_longitude, double P2_latitude, double P2_longitude, float cps) {
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

    static private int whatIsClockDir(double seta) {
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

    static private double getSeta(double P1_latitude, double P1_longitude, double P2_latitude, double P2_longitude) {
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
    static public int calDistance(double lat1, double lon1, double lat2, double lon2){
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
    static private double deg2rad(double deg){ return (double)(deg * Math.PI / (double)180d);}
    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    static private double rad2deg(double rad){ return (double)(rad * (double)180d / Math.PI);}
}
