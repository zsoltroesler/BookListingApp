package com.example.android.booklistingapp;

/**
 * Created by Zsolt on 10/3/2017.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving book list data from Google Books API.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google Books API dataset and return an {@link Books} object to represent a list of books.
     */
    public static List<Books> fetchBooksData(String requestUrl) {
        Log.i(LOG_TAG, "TEST: fetchBooksData() called...");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a {@link Books} object
        List<Books> books = extractBooks(jsonResponse);

        // Return the {@link Books}
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Books} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Books> extractBooks(String booksJSON) {

        // Create an empty ArrayList that we can start adding books to
        List<Books> books = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Try to parse the JSOn response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the booksJSON
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            // If there are results in the items array
            if (baseJsonResponse.has("items")) {
                // Extract the JSONArray associated with the key called "items",
                // which represents a list of items (or books).
                JSONArray booksArray = baseJsonResponse.getJSONArray("items");
                // For each book in the booksArray, create a {@link Books} object
                for (int i = 0; i < booksArray.length(); i++) {

                    // Get a single book at position i within the list of books
                    JSONObject currentBook = booksArray.getJSONObject(i);

                    // For a given book, extract the JSONObject associated with the
                    // key called "volumeInfo", which represents all the data of a book
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    // Extract the value for the key called "title"
                    String title = volumeInfo.getString("title");

                    // Create an ArrayList for authors
                    List<String> authorList = new ArrayList<>();
                    // If the book has more than one authors than add to the ArrayList
                    if (volumeInfo.has("authors")) {
                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authors.length(); j++) {
                            authorList.add(authors.getString(j));
                        }
                    }

                    // Extract the value for the key called "infoLink"
                    String urlBook = volumeInfo.getString("infoLink");

                    // For a given book image, extract the JSONObject associated with the
                    // key called "imageLinks", which represents URLs as source links
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                    // Create a String for image book URL
                    String urlImage = "";

                    // Get value for smallThumbnail if the key exists
                    if (volumeInfo.has("imageLinks")) {
                        if (imageLinks.has("smallThumbnail")) {
                            urlImage = imageLinks.getString("smallThumbnail");
                        }
                    }  else {
                        urlImage = "";
                    }

                    // Create a new {@link Books} object with the title, authors, url for image
                    // and url for info from the JSON response.
                    Books book = new Books(title, authorList, urlImage, urlBook);

                    // Add the new {@link Books} to the list of books.
                    books.add(book);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the books JSON results", e);
        }
        return books;
    }
}
