package com.example.android.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String https = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String httpsEnd = "&maxResults=40";
    private static String searchText = "";

    private TextView mEmptyStateTextView;
    private ListView listView;

    BookAdapter adapter;
    ArrayList<Book> books;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        Button searchButton = (Button) findViewById(R.id.button_search);

        //Get the saved data and show it
        if (savedInstanceState != null) {
            books = savedInstanceState.getParcelableArrayList("key");
            adapter = new BookAdapter(MainActivity.this, books);
            listView.setAdapter(adapter);
        }

        //Set up the empty view
        listView.setEmptyView(mEmptyStateTextView);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    //Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    //Fire off the AsyncTask
                    EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
                    searchText = searchEditText.getText().toString().trim().replace(" ", "+");
                    new FetchBookAsyncTask().execute();

                } else {
                    // First, hide loading indicator so error message will be visible
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);

                    // Clear the adapter of previous book data
                    if (adapter != null) {
                        adapter.clear();
                    }

                    // Update empty state with no connection error message
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });
    }

    //Save the current books list so we dont lose our view when we rotate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", books);
        super.onSaveInstanceState(outState);
    }

    private class FetchBookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

        @Override
        protected void onPreExecute() {
            // Show loading indicator because we start to fetch data
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<Book> doInBackground(String... urls) {
            //first create the URL and then fetch the data
            urls = new String[]{https + searchText + httpsEnd};
            books = Utils.fetchBookList(urls[0]);
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {

            // Hide loading indicator because the data has been loaded
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Clear the adapter of previous book data
            if (adapter != null) {
                adapter.clear();
            }

            // Set empty state text to display "No books found."
            mEmptyStateTextView.setText(R.string.no_books);

            if (books != null && !books.isEmpty()) {
                updateUi(books);
            }
        }
    }


    private void updateUi(ArrayList<Book> books) {

        adapter = new BookAdapter(MainActivity.this, books);
        listView.setAdapter(adapter);


    }

}
