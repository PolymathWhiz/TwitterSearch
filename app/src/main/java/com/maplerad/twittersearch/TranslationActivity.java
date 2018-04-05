package com.maplerad.twittersearch;

/**
 * Created by Polygod on 4/5/18.
 */

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TranslationActivity extends AppCompatActivity {
    private ArrayList<String> translatedStatuses;
    private SearchesAdapter adapter; // for binding data to RecyclerView
    private JSONObject statusesJson = null;
    private Integer DISPLAY_AMNT = 5; // Can make this go up to 10, but spec says 5
    private String text;

    // configures the GUI and registers event listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        translatedStatuses = new ArrayList<String>();

        final Bundle extras = getIntent().getExtras();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create RecyclerView.Adapter to bind tags to the RecyclerView
        adapter = new SearchesAdapter(translatedStatuses, itemClickListener, itemLongClickListener);
        recyclerView.setAdapter(adapter);

        // specify a custom ItemDecorator to draw lines between list items
        recyclerView.addItemDecoration(new ItemDivider(this));

        ApiRequestTask apiRequest = new ApiRequestTask(new ApiRequestTask.ApiResponse() {
            public void onResponse(String response) {
                try {
                    statusesJson = new JSONObject(response);
                    JSONArray tmp = statusesJson.getJSONArray("statuses");
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    for (int i = 0; i < DISPLAY_AMNT; i++) {
                        text = tmp.getJSONObject(i).getString("text");
                        String french;
                        french = GoogleTranslator.translateTo(extras.getString("lang"), text);
                        translatedStatuses.add(french);
                    }
                    adapter.notifyDataSetChanged(); // update trends in RecyclerView
                } catch (Exception e){
                    Util.print(Log.getStackTraceString(e));
                }
            }
        });


        if (MainActivity.APP_TOKEN != null) {
            try {
                apiRequest.execute(" https://api.twitter.com/1.1/search/tweets.json?q=" + extras.getString("query", "UTF-8"),
                        "GET", MainActivity.APP_TOKEN);
            } catch (Exception e) {
                Util.print(Log.getStackTraceString(e));

            }
        }
    }

    private final OnClickListener itemClickListener =
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Do nothing for now
                }
            };

    private final OnLongClickListener itemLongClickListener =
            new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return true;
                }
            };
}
