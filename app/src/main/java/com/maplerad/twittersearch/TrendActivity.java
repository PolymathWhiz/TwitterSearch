package com.maplerad.twittersearch;

/**
 * Created by Polygod on 4/5/18.
 */


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;

public class TrendActivity extends AppCompatActivity {
    private ArrayList<String> trends; // List of trend names
    private HashMap<String, String> trendsUrls; // Hash map of trend names to their urls
    private SearchesAdapter adapter; // for binding data to RecyclerView
    private JSONArray trendsJson = null;
    private Integer DISPLAY_AMNT = 5; // Can make this go up to 10, but spec says 5

    // configures the GUI and registers event listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        trends = new ArrayList<String>();
        trendsUrls = new HashMap<String, String>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create RecyclerView.Adapter to bind tags to the RecyclerView
        adapter = new SearchesAdapter(trends, itemClickListener, itemLongClickListener);
        recyclerView.setAdapter(adapter);

        // specify a custom ItemDecorator to draw lines between list items
        recyclerView.addItemDecoration(new ItemDivider(this));

        ApiRequestTask apiRequest = new ApiRequestTask(new ApiRequestTask.ApiResponse() {
            public void onResponse(String response) {
                try {
                    trendsJson = new JSONArray(response);
                    JSONArray tmp = trendsJson.getJSONObject(0).getJSONArray("trends");
                    trends.clear();

                    for (int i = 0; i < DISPLAY_AMNT; i++) {
                        String name = tmp.getJSONObject(i).getString("name");
                        String url = tmp.getJSONObject(i).getString("url");
                        trends.add(name);
                        trendsUrls.put(name, url);
                    }
                    adapter.notifyDataSetChanged(); // update trends in RecyclerView
                } catch (Exception e) {
                    Util.print(Log.getStackTraceString(e));
                }
            }
        });

        if (MainActivity.APP_TOKEN != null) {
            apiRequest.execute("https://api.twitter.com/1.1/trends/place.json?id=1", "GET", MainActivity.APP_TOKEN);
        }
    }


    // itemClickListener launches web browser to display search results
    private final OnClickListener itemClickListener =
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get query string and create a URL representing the search
                    String trend = ((TextView) view).getText().toString();
                    String url = trendsUrls.get(trend);

                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(webIntent);
                }
            };

    // Does nothing ATM.
    private final OnLongClickListener itemLongClickListener =
            new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return true;
                }
            };
}

