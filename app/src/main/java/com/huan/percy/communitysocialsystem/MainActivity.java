package com.huan.percy.communitysocialsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String IP = "http://192.168.23.178:8003";
    private final String GET_BROADCAST_REQUEST = "/LingliServer/GetBroadcast";
    private final String GET_LIFE_REQUEST = "/LingliServer/GetService";

    private final int NOTIFY_BROADCAST_CHANGED = 0;
    private final int NOTIFY_LIFE_INFO_CHANGED = 1;
    private final String GET_LASTEST = "0";
    private final String GET_BEFORE = "1";

    private boolean networkState = false;
    private boolean isMore = false;

    private String location;
    private MaterialRefreshLayout materialRefreshLayout;
    private ListView mListView;
    private static boolean LOCAL_SELECTED = true;
    private int[] to={R.id.author, R.id.article, R.id.date, R.id.face};   //这里是ListView显示每一列对应的list_item中控件的id
    LinkedList<Map<String, Object>> localListItems = new LinkedList<Map<String, Object>>();
    LinkedList<Map<String, Object>> lifeListItems = new LinkedList<Map<String, Object>>();


    SimpleAdapter localAdapter;
    SimpleAdapter lifeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddBroadcastActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        localAdapter = new SimpleAdapter(getApplicationContext(), localListItems, R.layout.list_item_layout,
                new String[] {"author", "article", "date", "face"},
                to);
        lifeAdapter = new SimpleAdapter(getApplicationContext(), lifeListItems, R.layout.list_item_layout,
                new String[] {"title", "article", "date", "face"},
                to);

        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        mListView = (ListView) findViewById(R.id.list_view);

        if (materialRefreshLayout != null) {
            materialRefreshLayout.setSunStyle(true);
            materialRefreshLayout.setLoadMore(true);
            materialRefreshLayout.autoRefresh();
            materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                    materialRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (LOCAL_SELECTED){
                                //下拉刷新
                                getBroadcast(GET_LASTEST);
                                //loadLocalData();
                            } else {
                                getLifeIfo(GET_LASTEST);
                                //loadLifeData();
                            }

                            // 结束下拉刷新...
                            materialRefreshLayout.finishRefresh();
                        }
                    }, 2000);

                }

                @Override
                public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
                    materialRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (LOCAL_SELECTED){
                                //上拉刷新
                                getBroadcast(GET_BEFORE);
                                //loadLocalData();
                            } else {
                                getLifeIfo(GET_BEFORE);
                                //loadLifeData();
                            }
                            // 结束上拉刷新...
                            materialRefreshLayout.finishRefreshLoadMore();
                        }
                    }, 2000);
                }

                @Override
                public void onfinish() {
                    if(!networkState){
                        Toast.makeText(getApplicationContext(),
                                "网络开小差了 ╮(╯▽╰)╭", Toast.LENGTH_LONG).show();
                    } else if(networkState && isMore){
                        networkState = false;
                        Toast.makeText(getApplicationContext(),
                                "来了来了 ╰(￣▽￣)╭", Toast.LENGTH_LONG).show();
                    } else if (networkState && !isMore){
                        Toast.makeText(getApplicationContext(),
                                "已经没有了 ╯﹏╰", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "刷新好了", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "╰(￣▽￣)╭", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_offline) {
            offline();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_user_broadcast) {
            LOCAL_SELECTED = true;
            materialRefreshLayout.autoRefresh();
        } else if (id == R.id.nav_life_info) {
            LOCAL_SELECTED = false;
            materialRefreshLayout.autoRefresh();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadLocalData(int savePosition) {

        mListView.setAdapter(localAdapter);
        mListView.setSelection(savePosition);
        localAdapter.notifyDataSetChanged();

    }


    private void loadLifeData(int savePosition) {
        mListView.setAdapter(lifeAdapter);
        mListView.setSelection(savePosition);
        lifeAdapter.notifyDataSetChanged();
    }

    public void getBroadcast(final String timeFlag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(IP+GET_BROADCAST_REQUEST);//服务器地址，指向Servlet
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    //获取本地街道Cookie
                    SharedPreferences pref = getSharedPreferences("Cookie", MODE_PRIVATE);
                    location  = pref.getString("location", "null");

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = sDateFormat.format(new java.util.Date());

                    //首次刷新
                    if (localListItems.size() == 0){
                        params.add(new BasicNameValuePair("flag", GET_BEFORE));
                    } else {
                        params.add(new BasicNameValuePair("flag", timeFlag));
                    }

                    params.add(new BasicNameValuePair("location", location));
                    //判断是下拉刷新还是上拉刷新
                    if(timeFlag.equals("0") && localListItems.size() > 0){
                        date = localListItems.get(0).get("date").toString();
                        Log.d("time", "下拉"+date);
                    } else if (timeFlag.equals("1") && localListItems.size() > 0){
                        date = localListItems.get(localListItems.size() - 1).get("date").toString();
                        Log.d("time", "上拉"+date);
                    }

                    params.add(new BasicNameValuePair("date", date));
                    Log.d("time", "first"+date);

                    final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");//以UTF-8格式发送
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200)//在200毫秒之内接收到返回值
                    {
                        int savePosition = 0;
                        if(localListItems.size()!= 0 && (!timeFlag.equals(GET_LASTEST))){
                            savePosition = localListItems.size() - 1;
                        }

                        HttpEntity entity1 = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity1, "utf-8");//以UTF-8格式解析

                        JSONArray result = new JSONArray(response); //Convert String to JSON Object

                        FaceMatch faceMatch = new FaceMatch();
                        if(timeFlag.equals("0") && localListItems.size() > 0){
                            isMore = true;
                            for(int i = result.length() - 1; i >= 0; i--){
                                Map<String, Object> listItem = new HashMap<String, Object>();
                                JSONObject item = result.getJSONObject(i);
                                listItem.put("author", item.getString("author"));
                                listItem.put("article", item.getString("article"));
                                listItem.put("date", item.getString("date"));
                                listItem.put("face", faceMatch.getLocalFace());
                                localListItems.addFirst(listItem);
                            }
                        } else {
                            isMore = true;
                            for(int i = 0; i < result.length(); i++){
                                Map<String, Object> listItem = new HashMap<String, Object>();
                                JSONObject item = result.getJSONObject(i);
                                listItem.put("author", item.getString("author"));
                                listItem.put("article", item.getString("article"));
                                listItem.put("date", item.getString("date"));
                                listItem.put("face", faceMatch.getLocalFace());
                                localListItems.add(listItem);
                            }
                        }
                        Message message = new Message();
                        message.what = NOTIFY_BROADCAST_CHANGED;
                        message.obj = savePosition;
                        handler.sendMessage(message);//使用Message传递消息给线程
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getLifeIfo(final String timeFlag){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(IP+GET_LIFE_REQUEST);//服务器地址，指向Servlet
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    //获取本地街道Cookie
                    SharedPreferences pref = getSharedPreferences("Cookie", MODE_PRIVATE);
                    location  = pref.getString("location", "null");

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = sDateFormat.format(new java.util.Date());

                    //首次刷新
                    if (lifeListItems.size() == 0){
                        params.add(new BasicNameValuePair("flag", GET_BEFORE));
                    } else {
                        params.add(new BasicNameValuePair("flag", timeFlag));
                    }

                    params.add(new BasicNameValuePair("location", location));
                    //判断是下拉刷新还是上拉刷新
                    if(timeFlag.equals("0") && lifeListItems.size() > 0){
                        date = lifeListItems.get(0).get("date").toString();
                        Log.d("time", "下拉"+date);
                    } else if (timeFlag.equals("1") && lifeListItems.size() > 0){
                        date = lifeListItems.get(lifeListItems.size() - 1).get("date").toString();
                        Log.d("time", "上拉"+date);
                    }

                    params.add(new BasicNameValuePair("location", location));
                    params.add(new BasicNameValuePair("date", date));

                    final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");//以UTF-8格式发送
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200)//在200毫秒之内接收到返回值
                    {
                        int savePosition = 0;
                        if(lifeListItems.size()!= 0 && (!timeFlag.equals(GET_LASTEST))){
                            savePosition = lifeListItems.size() - 1;
                        }
                        HttpEntity entity1 = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity1, "utf-8");//以UTF-8格式解析

                        JSONArray result = new JSONArray(response); //Convert String to JSON Object
                        FaceMatch  faceMatch = new FaceMatch();
                        if(timeFlag.equals("0") && lifeListItems.size() > 0){
                            isMore = true;
                            for(int i = result.length() - 1; i >= 0; i--){
                                Map<String, Object> listItem = new HashMap<String, Object>();
                                JSONObject item = result.getJSONObject(i);
                                listItem.put("title", item.getString("title"));
                                listItem.put("article", item.getString("article"));
                                listItem.put("date", item.getString("date"));
                                listItem.put("face", faceMatch.getLocalFace());
                                lifeListItems.addFirst(listItem);
                            }
                        } else {
                            isMore = true;
                            for(int i = 0; i < result.length(); i++){
                                Map<String, Object> listItem = new HashMap<String, Object>();
                                JSONObject item = result.getJSONObject(i);
                                listItem.put("title", item.getString("title"));
                                listItem.put("article", item.getString("article"));
                                listItem.put("date", item.getString("date"));
                                listItem.put("face", faceMatch.getLocalFace());
                                lifeListItems.add(listItem);
                            }
                        }
                        Message message = new Message();
                        message.what = NOTIFY_LIFE_INFO_CHANGED;
                        message.obj = savePosition;
                        handler.sendMessage(message);//使用Message传递消息给线程
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int savePosition = (int) msg.obj;
            switch (msg.what) {
                case NOTIFY_BROADCAST_CHANGED:
                    networkState = true;
                    loadLocalData(savePosition);
                    break;
                case NOTIFY_LIFE_INFO_CHANGED:
                    networkState = true;
                    loadLifeData(savePosition);
                default:
                    break;
            }
        }
    };

    private void offline(){
        SharedPreferences.Editor editor = getSharedPreferences("Cookie",
                MODE_PRIVATE).edit();

        editor.putBoolean("logined", false);
        editor.putString("email", "");
        editor.putString("pwd", "");
        editor.putString("name", "");
        editor.putString("location","");
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}



