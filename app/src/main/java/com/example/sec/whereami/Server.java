package com.example.sec.whereami;

/**
 * Created by CodeFactory on 2016-08-12.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.parser.JSONParser;
        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
        import java.net.URLConnection;
        import java.net.URLEncoder;

/**
 * Created by songjonghun on 2016. 8. 10..
 */
public class Server extends AsyncTask<String, Void, String> {
    String local_name;
    String local_lat;
    String local_lng;
    String command;
    WebAccess webAccess;
    @Override
    protected String doInBackground(String... params) {
        command = (String)params[0];
        if(command.equals("WRITE")) {
            local_name = (String)params[1];
            local_lat = (String)params[2];
            local_lng = (String)params[3];
            WriteData(local_name, local_lat, local_lng);
        }
        else
            ReadData();

        return null;
    }

    public void WriteData(String local_name, String local_lat, String local_lng) {
        try {
            String link = "http://ec2-54-199-165-247.ap-northeast-1.compute.amazonaws.com/insert.php";
            String data = URLEncoder.encode("local_name", "UTF-8") + "=" + URLEncoder.encode(local_name, "UTF-8");
            data += "&" + URLEncoder.encode("local_lat", "UTF-8") + "=" + URLEncoder.encode(local_lat, "UTF-8");
            data += "&" + URLEncoder.encode("local_lng", "UTF-8") + "=" + URLEncoder.encode(local_lng, "UTF-8");
            webAccess.uploadToUrl(link,data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void ReadData() {
        String json;
        json = webAccess.downloadFromUrl("http://ec2-54-199-165-247.ap-northeast-1.compute.amazonaws.com/index.php");
        Log.d("JSON: ", json);
        JSONParser jsonParser = new JSONParser();
        try {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(json);
            org.json.simple.JSONArray result = (org.json.simple.JSONArray) jsonObject.get("results");
            Log.d("SIZE: ", String.valueOf(result.size()));

            for(int i=0; i<result.size(); i++) {
                org.json.simple.JSONObject obj = (org.json.simple.JSONObject)result.get(i);
                Log.d("READ DATA: ", (String)obj.get("local_name") + " " + (String)obj.get("local_lat") + " " + (String)obj.get("local_lng"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error!!!", " asdasdasdasdasdasdasdasdasdassdasdasdasdasda");
        }
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Log.d("RESULT: ", s);
    }
}
