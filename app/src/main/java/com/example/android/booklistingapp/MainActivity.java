package com.example.android.booklistingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    /**
     * EditText variable
     */
    private EditText editText;

    /**
     * Search text variable
     */
    private String searchText;

    /**
     * Button variable
     */
    private Button searchButton;

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "TEST: MainActivity onCreate() called...");

        // Find a reference to the {@link EditText} in the layout
        editText = (EditText) findViewById(R.id.edittext_query);

        // Perform the search without pressing the search button
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                    // Perform also the search by press the keyboard
                    searchButton.callOnClick();
                return true;
            }

        });

        // Find a reference to the {@link Button} in the layout
        searchButton = (Button) findViewById(R.id.button_search);

        // Set a click listener on search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "TEST: onClick() called...");

                // Get the user's search input
                searchText = editText.getText().toString();

                // If EditText is empty although search Button pressed, warning message pops up
                if (searchText.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.search_criterion, Toast.LENGTH_LONG).show();
                } else {
                    searchText = searchText.replaceAll(" ", "+");
                    Intent intent = new Intent(MainActivity.this, BookListActivity.class);
                    intent.putExtra("KEYWORDS", searchText);
                    startActivity(intent);
                }
            }
        });
    }
}