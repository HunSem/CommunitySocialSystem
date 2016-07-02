package com.huan.percy.communitysocialsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.huan.percy.communitysocialsystem.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Percy on 2016/7/2.
 */
public class LocalAdapter extends SimpleAdapter {
    private List<? extends Map<String, ?>> mData;

    private int mResource;

    private String[] mFrom;

    private int[] mTo;

    private LayoutInflater mInflater;

    private ViewBinder mViewBinder;

    private static class ViewHolder {
        TextView author;
        TextView article;
        TextView time;
        ImageView face;
    }

    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public LocalAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mData = data;
        mResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final View view = super.getView(position, convertView, parent);

        ViewHolder holder = (ViewHolder) view.getTag();
        if(holder == null) {
            holder = new ViewHolder();
            holder.author = (TextView) view.findViewById(R.id.author);
            holder.time = (TextView) view.findViewById(R.id.date);
            holder.article = (TextView) view.findViewById(R.id.article);
            holder.face = (ImageView) view.findViewById(R.id.face);

            view.setTag(holder);
            holder.author.setText(mData.get(position).get("author").toString());
            holder.article.setText(mData.get(position).get("article").toString());
            holder.face.setImageResource(Integer.parseInt(mData.get(position).get("face").toString()));

            String date = mData.get(position).get("date").toString();
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String nowDay = sDateFormat.format(new java.util.Date()).substring(9, 11);
            if(date.substring(9, 11).equals(nowDay)){
                holder.time.setText(date.substring(12, 17));
            } else if((Integer.parseInt(date.substring(9, 11))+1) == Integer.parseInt(nowDay)){
                holder.time.setText("昨天 "+ date.substring(12, 17));
            } else {
                holder.time.setText(date.substring(6, 17));
            }

        }

        return view;
    }
}



