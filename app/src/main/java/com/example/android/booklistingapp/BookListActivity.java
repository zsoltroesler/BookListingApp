package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BookListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Books>> {

    public String keywords;

    /** search URL according to user's search criterion */
    private String searchUrl;

    /** base URL for book list data from the Google Books API dataset */
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=40&q=";

    /** List<> for the list of books */
    private List<Books> books = new ArrayList<>();

    /** RecyclerView variable */
    private RecyclerView booksRecyclerView;

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 0;

    /** Tag for log messages */
    private static final String LOG_TAG = BookListActivity.class.getName();

    /** Adapter for the list of books */
    private BooksAdapter booksAdapter;

    /** TextView that is displayed when the list is empty  */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Log.i(LOG_TAG, "TEST: BookListActivity onCreate() called...");

        // Get the search criterion from MainActivity
        Intent intent = getIntent();
        keywords = intent.getStringExtra("KEYWORDS");

        // Updated search URL with user's keyword(s)
        searchUrl = BASE_URL + keywords;

        // Find a reference to the {@link RecyclerView} in the layout
        booksRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_list);

        // Attach a LayoutManager to this RecyclerView
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set vertical divider among list items
        booksRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Set a new BooksAdapter on booksAdapter variable
        booksAdapter = new BooksAdapter(this, books);

        // Set the adapter on RecyclingView
        booksRecyclerView.setAdapter(booksAdapter);

        // Find a reference to the {@link TextView} in the layout
        mEmptyStateTextView = (TextView) findViewById(R.id.textview_empty);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, establish connection
        if (networkInfo != null && networkInfo.isConnected()) {

            // Hide loading indicator
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(getString(R.string.no_internet_connection));
        }
    }

    @Override
    public Loader<List<Books>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called...");

        // Loading indicator is visible because the data is being loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        // Create a new loader for the given URL
        return new BooksLoader(this, searchUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> result) {
        Log.i(LOG_TAG, "TEST: onLoadFinished() called...");


        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the booksAdapter from the previous data
        booksAdapter.setBooksList(null);

        // If there is a valid list of {@link Books}, then add them to the adapter's
        // data set. This will trigger the RecyclerView to update.
        if (result != null && !result.isEmpty()) {
            books = result;
            booksAdapter.setBooksList(result);
            booksAdapter.notifyDataSetChanged();
        } else {
            mEmptyStateTextView.setText(getString(R.string.no_books));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        Log.i(LOG_TAG, "TEST: onLoadReset() called...");
        booksAdapter.setBooksList(null);
    }
}
