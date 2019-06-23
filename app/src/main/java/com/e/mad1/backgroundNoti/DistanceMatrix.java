package com.e.mad1.backgroundNoti;

import android.content.Context;
import android.util.Log;

import com.e.mad1.App;
import com.e.mad1.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class DistanceMatrix {

    private String APIkey;
    private String currentLocation;
    DistanceMatrix(String currentLocation,String key){
        this.currentLocation = currentLocation;
        this.APIkey = key;
    }

    int getDuration(String destination){
        if (currentLocation != null) {
            try {
                JSONObject read = new JSONObject(getDistanceJSON(destination));
                JSONObject rows = read.getJSONArray("rows").getJSONObject(0);
                Log.i("distance", rows.toString());

                JSONObject elements = rows.getJSONArray("elements").getJSONObject(0);
                Log.i("distance", elements.toString());

                int duration = elements.getJSONObject("duration").getInt("value");
                Log.i("distance", Integer.toString(duration));

                return duration;

            } catch (JSONException e) {
                e.printStackTrace();
                return -1;
            }
        } else return -1;
    }

   private String getDistanceJSON(String destination) {
        HttpURLConnection connection;
        BufferedReader reader;
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?"
                    + "origins=" + currentLocation
                    + "&destinations=" + destination
                    + "&key=" + APIkey);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder buffer = new StringBuilder();
            String line;    // get line by line of json

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");

            } Log.i("distance",buffer.toString());
            connection.disconnect();
            reader.close();
            return  buffer.toString();

        } catch (MalformedURLException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
}
