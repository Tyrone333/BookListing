package com.example.android.booklisting;

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

/**
 * Created by tyrone3 on 16.11.16.
 */

public class Utils {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = Utils.class.getSimpleName();

    public static ArrayList<Book> fetchBookList(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
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
        String jsonResponse = "No internet connection";


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
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
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
     * Return an {@link } object by parsing out information
     * about the Books we are looking for
     */
    private static ArrayList<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        ArrayList<Book> books = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            if (!baseJsonResponse.isNull("items")) {
                JSONArray featureArray = baseJsonResponse.getJSONArray("items");
                // If there are results in the features array
                for (int i = 0; i < featureArray.length(); i++) {
                    String author;
                    String title;
                    // Extract out the first item (which is a book)
                    JSONObject firstFeature = featureArray.getJSONObject(i);
                    JSONObject info = firstFeature.getJSONObject("volumeInfo");

                    // Extract out the title and author
                    if (info.has("authors")) {
                        JSONArray authors = info.getJSONArray("authors");
                        author = authors.optString(0);
                    } else {
                        author = "Not Available";
                    }
                    if (info.has("title")) {
                        title = info.getString("title");
                    } else {
                        title = "Not Available";
                    }

                    // Create a new {@link Book} object
                    Book book = new Book(title, author);
                    books.add(book);
                }

            } else {
                books = null;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Book JSON results", e);
        }
        return books;
    }
}
