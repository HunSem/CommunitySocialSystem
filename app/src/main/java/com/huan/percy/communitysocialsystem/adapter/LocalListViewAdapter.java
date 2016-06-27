package com.huan.percy.communitysocialsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huan.percy.communitysocialsystem.R;

import java.util.List;

/**
 * Created by Percy on 2016/6/27.
 */
public class LocalListViewAdapter extends BaseAdapter {
    private List<String> datas;
    private LayoutInflater inflater;

    public LocalListViewAdapter(Context context, List<String> data) {
        super();
        inflater = LayoutInflater.from(context);
        datas = data;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(datas.get(position));
        return convertView;
    }

    public List<String> getData() {
        return datas;
    }

}
