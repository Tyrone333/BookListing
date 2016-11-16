package com.example.android.booklisting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String https = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String httpsEnd = "&maxResults=40";
    private static String searchText = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
                searchText = searchEditText.getText().toString().trim().replace(" ", "+");
                new bookAsyncTask().execute();
//                Toast.makeText(MainActivity.this, https + searchText + httpsEnd, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class bookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<Book> doInBackground(String... urls) {

            urls = new String[]{https + searchText + httpsEnd};
            ArrayList<Book> books = Utils.fetchBookList(urls[0]);
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (books == null) {
                return;
            }
            updateUi(books);

        }
    }
    private void updateUi(ArrayList<Book> books) {
        if (books.size() == 0) {
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(null);
            TextView emptyText = (TextView) findViewById(R.id.empty_view);
            emptyText.setText(R.string.empty_phrase);
        } else {
            TextView emptyText = (TextView) findViewById(R.id.empty_view);
            emptyText.setText("");
            BookAdapter adapter = new BookAdapter(MainActivity.this, books);
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
        }

        }

    }
