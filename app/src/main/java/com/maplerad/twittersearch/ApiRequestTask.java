package com.maplerad.twittersearch;

/**
 * Created by Polygod on 4/5/18.
 */

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** Performs an API Request after obtaining an oauth token
 * Example: apiRequest("https://api.twitter.com/1.1/trends/place.json?id=1", "GET", "thisismytokenyo")
 * Docs: https://dev.twitter.com/oauth/application-only
 * Todo: Add param support
 */
public  class ApiRequestTask extends AsyncTask<String, Void, String> {

    public interface ApiResponse {
        void onResponse(String output);
    }

    public ApiResponse delegate = null;

    public ApiRequestTask(ApiResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpConnection = null;
        String resp = null;

        try {
            // Form the HTTP request
            URL _url = new URL(params[0]);
            httpConnection = (HttpURLConnection) _url.openConnection();
            httpConnection.setRequestMethod(params[1]);
            httpConnection.setRequestProperty("Authorization", "Bearer " + params[2]);
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.connect();

            // Get the HTTP response back
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));

            // Parse out the JSON
            String line;
            StringBuilder respBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                respBuilder.append(line);
            }

            resp = respBuilder.toString();
        } catch (Exception e) {
            Util.print(Log.getStackTraceString(e));
        } finally {
            // Always disconnect
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return resp;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.onResponse(result);
    }
}