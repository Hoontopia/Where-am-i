package com.example.sec.whereami;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Created by CodeFactory on 2016-08-11.
 */
public class Parsing {
    WebAccess webAccess = new WebAccess();
    HashMap<Integer, String> hashMap = new HashMap();
    public String[] getLocData(String types, String typeName, String name, String radius, String lat, String lng){
        String result[];
        String jasonData;
        if(types.equals("buildings")){
            jasonData=webAccess.downloadFromUrl(webAccess.getGeoAPI_URL(lat, lng));
            result = nearPlacePs(jasonData, lat, lng, radius, typeName);
        }
        else{
            jasonData=webAccess.downloadFromUrl(webAccess.getPlaceAPI_URL(lat, lng, radius, types, name));
            result = searchPlacePs(jasonData, lat, lng, radius, typeName);
        }
        return result;
    }

    /* Google Place API를 위한 함수 */
    public String[] searchPlacePs(String jsonData, String lat, String lng, String radius, String typeName) {
        String address[];
        try {
            JSONParser jsonParser = new JSONParser();
            org.json.simple.JSONObject jsonObject = null;
            try {
                jsonObject = (org.json.simple.JSONObject) jsonParser.parse(jsonData);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            org.json.simple.JSONArray result = (org.json.simple.JSONArray) jsonObject.get("results");
            address= new String[result.size()+1];
            address[0] = "주변 "+ radius + "미터 안의 " + typeName + " 검색결과";
            int addridx = 1;
            for(int i=0; i<result.size(); i++) {
                org.json.simple.JSONObject testObject = (org.json.simple.JSONObject) result.get(i);
                String res = (String)testObject.get("name");
                org.json.simple.JSONObject geo = (org.json.simple.JSONObject) testObject.get("geometry");
                org.json.simple.JSONObject loc = (org.json.simple.JSONObject) geo.get("location");

                if(isStringDouble(res))
                    continue;

                int dist = Calculate.calDistance(Double.parseDouble(lat), Double.parseDouble(lng), (double)loc.get("lat"), (double)loc.get("lng"));
                int dirs = Calculate.bearingP1toP2(Double.parseDouble(lat), Double.parseDouble(lng), (double)loc.get("lat"), (double)loc.get("lng"), CpsManager.getAzimuth());
                res += "  " + String.valueOf(dist) + "미터";
                res += "  " + String.valueOf(dirs) + "시방향";
                // Toast.makeText(getApplicationContext(),"result: " + res, Toast.LENGTH_SHORT).show();
                address[addridx++] = res;

            }
        }catch (NoSuchElementException e) {
            address = new String[2];
            address[0] = "주변 " + radius + "미터 안의 " + typeName + " 검색결과";
            address[1] = "검색된 결과가 없습니다.";
        }
        return address;
    }

    /* Google Geo API를 위한 함수 */
    public String[] nearPlacePs(String jsonData, String lat, String lng, String radius, String typeName) {
        String address[];
        try{
            JSONParser jsonParser = new JSONParser();
            org.json.simple.JSONObject jsonObject = null;
            try {
                jsonObject = (org.json.simple.JSONObject) jsonParser.parse(jsonData);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            org.json.simple.JSONArray result = (org.json.simple.JSONArray) jsonObject.get("results");
            address = new String[result.size()+1];
            int addridx = 1;
            address[0] = "주변 " + radius + "미터 안의 " + typeName + " 검색결과";
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

                int dist = Calculate.calDistance(Double.parseDouble(lat), Double.parseDouble(lng), (double)loc.get("lat"), (double)loc.get("lng"));

                if(dist > 300)
                    continue;

                int dirs = Calculate.bearingP1toP2(Double.parseDouble(lat), Double.parseDouble(lng), (double)loc.get("lat"), (double)loc.get("lng"), CpsManager.getAzimuth());
                res += "  " + String.valueOf(dist) + "미터";
                res += "  " + String.valueOf(dirs) + "시방향";
                // Toast.makeText(getApplicationContext(),"result: " + res, Toast.LENGTH_SHORT).show();
                address[addridx++] = res;
                hashMap.put(dist,formatted_address);
            }
        } catch (NoSuchElementException e) {
            address = new String[2];
            address[0] = "주변 " + radius + "미터 안의 " + typeName + " 검색결과";
            address[1] = "검색된 결과가 없습니다.";
        }
        return address;
    }
    public static boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getCurrentLoc(){
        TreeMap<Integer, String> treeMap = new TreeMap<>(hashMap);
        Iterator<Integer> treeMapIter = treeMap.keySet().iterator();
        treeMapIter.hasNext();
        int key = treeMapIter.next();
        String value = treeMap.get( key );
        return value;
    }
}
