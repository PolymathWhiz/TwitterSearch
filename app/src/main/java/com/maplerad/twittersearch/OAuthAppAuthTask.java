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
public class OAuthAppAuthTask extends AsyncTask<String, Void, String> {

    public interface ApiResponse {
        void onResponse(String output);
    }

    public ApiResponse delegate = null;

    public OAuthAppAuthTask(ApiResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpConnection = null;
        OutputStream outputStream = null;
        String token = null;
        try {
            // Step 1: URL encode the key and secret, concat them together, base64 encode the concat keysecret
            // TODO: Skipping 1a, as they're already URL encoded, should do as it might break in future though(?)
            String keysecret = params[1] + ":" + params[2];
            String keysecretb64 = Base64.encodeToString(keysecret.getBytes(), Base64.NO_WRAP);

            // Step 2: Send a POST w/ basic auth, content-type, and 'grant_type=client_credentials' as body
            // Form the HTTP request
            URL _url = new URL(params[0]);
            httpConnection = (HttpURLConnection) _url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.addRequestProperty("Authorization", "Basic " + keysecretb64);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            outputStream = httpConnection.getOutputStream();
            outputStream.write("grant_type=client_credentials".getBytes());
            outputStream.flush();
            outputStream.close();

            // Step 3: Pull out the access_token
            // Get the HTTP response back
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String line;
            StringBuilder respBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                respBuilder.append(line);
            }
            // Pull out the token from the response
            String resp = respBuilder.toString();
            JSONObject json = new JSONObject(resp);
            token = json.getString("access_token");
        } catch (Exception e) {
            Util.print(Log.getStackTraceString(e));
        } finally {
            // Always disconnect
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

        return token;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.onResponse(result);
    }

}