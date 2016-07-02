package com.huan.percy.communitysocialsystem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.huan.percy.communitysocialsystem.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Percy on 2016/7/2.
 */
public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder> {
    private LinkedList<Map<String, Object>> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        protected TextView author;
        protected TextView article;
        protected TextView date;
        protected ImageView face;

        public ViewHolder(View v) {
            super(v);
            author =  (TextView) v.findViewById(R.id.author);
            article = (TextView)  v.findViewById(R.id.article);
            date = (TextView)  v.findViewById(R.id.date);
            face = (ImageView) v.findViewById(R.id.face);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public LocalAdapter(LinkedList<Map<String, Object>> myDataset) {
        this.mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LocalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.author.setText(mDataset.get(position).get("author").toString());
        holder.article.setText(mDataset.get(position).get("article").toString());
        holder.date.setText(mDataset.get(position).get("date").toString());
        holder.face.setImageResource(Integer.parseInt(mDataset.get(position).get("face").toString()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}



