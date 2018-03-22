package com.maplerad.twittersearch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String SEARCHES = "searches";
    private EditText queryEditText;
    private EditText tagEditText;
    private FloatingActionButton saveFloatingActionButton;
    private SharedPreferences savedSearches;
    private List<String> tags;
    private SearchesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queryEditText = ((TextInputLayout) findViewById(R.id.queryTextInputLayout)).getEditText();
        tagEditText = ((TextInputLayout) findViewById(R.id.tagTextInputLayout)).getEditText();

        queryEditText.addTextChangedListener(textWatcher);
        tagEditText.addTextChangedListener(textWatcher);

        savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);

        tags = new ArrayList<>(savedSearches.getAll().keySet());

        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            updateSaveFab();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void updateSaveFab() {
        if (queryEditText.getText().toString().isEmpty() || tagEditText.getText().toString().isEmpty()) {
            saveFloatingActionButton.hide();
        } else {
            saveFloatingActionButton.show();
        }
    }

    private final View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String query = queryEditText.getText().toString();
            String tag = tagEditText.getText().toString();

            if (!query.isEmpty() && !tag.isEmpty()) {

                // This hides the virtual keyboard
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);

                queryEditText.setText("");
                tagEditText.setText("");

                queryEditText.requestFocus();
                addTagSearch(tag, query);
            }
        }
    };

    private void addTagSearch(String tag, String query) {
        SharedPreferences.Editor prefEditor = savedSearches.edit();
        prefEditor.putString(tag, query);
        prefEditor.apply();

        if (!tags.contains(tag)) {
            tags.add(tag);
            Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
            adapter.notifyDataSetChanged();
        }
    }

    private final View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String tag = ((TextView) view).getText().toString();

            String urlString = getString(R.string.search_URL) + Uri.encode(savedSearches.getString(tag, ""), "UTF-8");

            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));

            startActivity(webIntent);
        }
    };

    private final View.OnLongClickListener itemOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            final String tag = ((TextView) view).getText().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setItems(R.array.dialog_items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            shareSearch(tag);
                            break;
                        case 1:
                            tagEditText.setText(tag);
                            queryEditText.setText(savedSearches.getString(tag, ""));
                            break;
                        case 2:
                            deleteSearch(tag);
                            break;
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);

            builder.create().show(); // You need this to make the builder show ~_-

            return true;
        }
    };

    private void shareSearch(String tag) {
        String urlString = getString(R.string.search_URL) + Uri.encode(savedSearches.getString(tag, ""), "UTF-8");

        Intent shareIntent = new Intent();

        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.share_subject));

        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, urlString));

        shareIntent.setType("text/plain");

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_search)));
    }
}
