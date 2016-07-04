package com.huan.percy.communitysocialsystem.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.huan.percy.communitysocialsystem.R;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Percy on 2016/7/2.
 */
public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder> {
    private LinkedList<Map<String, Object>> list;

    /**
     * 这里和使用listview时使用的adapter基本一样，都是要传入数据集合的
     *
     * @param list
     */
    public LocalAdapter(LinkedList<Map<String, Object>> list) {
        this.list = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = View.inflate(viewGroup.getContext(), R.layout.list_item_layout, null);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.author_tv.setText(list.get(position).get("author").toString());
        viewHolder.article_tv.setText(list.get(position).get("article").toString());
        viewHolder.date_tv.setText(analyzeTime(list.get(position).get("date").toString()));
        viewHolder.face.setImageResource((int)list.get(position).get("face"));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author_tv;
        public TextView article_tv;
        public TextView date_tv;
        public ImageView face;

        public ViewHolder(View itemView) {
            super(itemView);
            author_tv = (TextView) itemView.findViewById(R.id.author);
            article_tv = (TextView) itemView.findViewById(R.id.article);
            date_tv = (TextView) itemView.findViewById(R.id.date);
            face = (ImageView) itemView.findViewById(R.id.face);
        }
    }

    public void add(Map<String, Object> item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Map<String, Object> item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public String analyzeTime(String date){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = sDateFormat.format(new java.util.Date());

        int nowDay = Integer.parseInt(nowDate.substring(8, 10));
        int day = Integer.parseInt(date.substring(8, 10));
        if(nowDay == day){return date.substring(11, 16);}
        else if((nowDay - 1) == day){return "昨天 " + date.substring(11, 16);}
        else{return date.substring(5, 16);}
    }
}



