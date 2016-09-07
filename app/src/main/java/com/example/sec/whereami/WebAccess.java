package com.example.sec.whereami;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by CodeFactory on 2016-08-11.
 */
public class WebAccess {
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

    public void uploadToUrl(String link, String data){
        try {
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* Google Place API를 이용할경우 */
    public String getPlaceAPI_URL(String lat, String lng, String radius, String types, String name) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + lat + "," + lng + "&" +
                "language=ko" + "&" +
                "types=" + types + "&" +
                "radius=" + radius + "&" +
                "name=" +name  + "&" +
                "key=" + API_Key.GOOGLE_KEY;
    }

    /* Google Geo API를 이용할경우 */
    public String getGeoAPI_URL(String lat, String lng) {
        return "https://maps.googleapis.com/maps/api/geocode/json?" +
                "latlng=" + lat + "," + lng + "&" +
                "language=ko" + "&" +
                "key=" + API_Key.GOOGLE_KEY;
    }
}
