package com.example.android.booklisting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tyrone3 on 16.11.16.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private class ViewHolder {
        TextView title;
        TextView author;
    }

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.list_item_title);
            holder.author = (TextView) convertView.findViewById(R.id.list_item_author);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(getItem(position).getTitle());
        holder.author.setText(getItem(position).getAuthor());
        return convertView;
        }
    }