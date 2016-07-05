package com.huan.percy.communitysocialsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.huan.percy.communitysocialsystem.adapter.LifeAdapter;
import com.huan.percy.communitysocialsystem.adapter.LocalAdapter;
import com.youth.banner.Banner;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MaterialIntroListener {

    private final String IP = "http://123.206.73.194:8003";
    private final String GET_BROADCAST_REQUEST = "/LingliServer/GetBroadcast";
    private final String GET_LIFE_REQUEST = "/LingliServer/GetService";

    private final int NOTIFY_BROADCAST_CHANGED = 0;
    private final int NOTIFY_LIFE_INFO_CHANGED = 1;
    private final String GET_LASTEST = "0";
    private final String GET_BEFORE = "1";
    private String location;
    private MaterialRefreshLayout materialRefreshLayout;
    private RecyclerView mRecyclerView;
    private static boolean LOCAL_SELECTED = true;

    private Banner banner;
    String[] images;
    String[] titles;

    LinkedList<Map<String, Object>> localListItems = new LinkedList<Map<String, Object>>();
    LinkedList<Map<String, Object>> lifeListItems = new LinkedList<Map<String, Object>>();


    LocalAdapter localAdapter;
    LifeAdapter lifeAdapter;

    FloatingActionButton fab;
    DrawerLayout drawer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddBroadcastActivity.class);
                startActivity(intent);
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();

        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if (materialRefreshLayout != null) {
            materialRefreshLayout.setSunStyle(true);
            materialRefreshLayout.setLoadMore(true);
            materialRefreshLayout.autoRefresh();
            showIntro(materialRefreshLayout, "Broadcast",
                    getString(R.string.tip_broadcast), FocusGravity.CENTER);
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
//                    if(!networkState){
//                        Toast.makeText(getApplicationContext(),
//                                "网络开小差了 ╮(╯▽╰)╭", Toast.LENGTH_LONG).show();
//                    } else if(networkState && isMore){
//                        networkState = false;
//                        Toast.makeText(getApplicationContext(),
//                                "来了来了 ╰(￣▽￣)╭", Toast.LENGTH_LONG).show();
//                    } else if (networkState && !isMore){
//                        Toast.makeText(getApplicationContext(),
//                                "已经没有了 ╯﹏╰", Toast.LENGTH_LONG).show();
//                    }
                }
            });
        }

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
            //切换适配器
            initView();
            loadLocalData();
            //显示FAB
            fab.show();
            //自动刷新数据
            materialRefreshLayout.autoRefresh();
            //隐藏banner
            banner.setVisibility(View.VISIBLE);
            banner.isAutoPlay(true);
        } else if (id == R.id.nav_life_info) {
            LOCAL_SELECTED = false;
            //切换适配器
            initView();
            loadLifeData();
            //自动刷新
            materialRefreshLayout.autoRefresh();
            //隐藏FAB
            fab.hide();
            //显示Banner
            banner.setVisibility(View.GONE);
            banner.isAutoPlay(false);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadLocalData() {
        localAdapter.notifyDataSetChanged();
    }

    private void loadLifeData() {
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

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                        //Log.d("time", "下拉"+date);
                    } else if (timeFlag.equals("1") && localListItems.size() > 0){
                        date = localListItems.get(localListItems.size() - 1).get("date").toString();
                        //Log.d("time", "上拉"+date);
                    }



                    params.add(new BasicNameValuePair("date", date));

                    final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");//以UTF-8格式发送
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200)//在200毫秒之内接收到返回值
                    {

                        HttpEntity entity1 = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity1, "utf-8");//以UTF-8格式解析

                        JSONArray result = new JSONArray(response); //Convert String to JSON Object
                        //Log.d("length", "JSON: " + response);
                        FaceMatch faceMatch = new FaceMatch();
                        if(timeFlag.equals("0") && localListItems.size() > 0){
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

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                        //Log.d("time", "下拉"+date);
                    } else if (timeFlag.equals("1") && lifeListItems.size() > 0){
                        date = lifeListItems.get(lifeListItems.size() - 1).get("date").toString();
                        //Log.d("time", "上拉"+date);
                    }

                    params.add(new BasicNameValuePair("location", location));
                    params.add(new BasicNameValuePair("date", date));

                    final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");//以UTF-8格式发送
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200)//在200毫秒之内接收到返回值
                    {
                        HttpEntity entity1 = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity1, "utf-8");//以UTF-8格式解析

                        JSONArray result = new JSONArray(response); //Convert String to JSON Object
                        FaceMatch  faceMatch = new FaceMatch();
                        if(timeFlag.equals("0") && lifeListItems.size() > 0){
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
            switch (msg.what) {
                case NOTIFY_BROADCAST_CHANGED:
                    if (localListItems.size() == 0){
                        //社区内无广播时自动添加系统提示信息
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String nowDate = sDateFormat.format(new java.util.Date());
                        Map<String, Object> listItem = new HashMap<String, Object>();
                        listItem.put("author", "邻里");
                        listItem.put("article", getString(R.string.init_data));
                        listItem.put("date", nowDate);
                        listItem.put("face", R.drawable.bee);
                        localListItems.addFirst(listItem);
                    }
                    loadLocalData();
                    break;
                case NOTIFY_LIFE_INFO_CHANGED:
                    if (lifeListItems.size() == 0){
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String nowDate = sDateFormat.format(new java.util.Date());
                        Map<String, Object> listItem = new HashMap<String, Object>();
                        listItem.put("title", "邻里");
                        listItem.put("article", getString(R.string.init_life));
                        listItem.put("date", nowDate);
                        listItem.put("face", R.drawable.bee);
                        lifeListItems.addFirst(listItem);
                    }
                    loadLifeData();
                default:
                    break;
            }
        }
    };

    private void offline(){
        //修改、清空本地COOKIE
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

    public void showIntro(View view, String id, String text, FocusGravity focusGravity) {
        new MaterialIntroView.Builder(MainActivity.this)
                .enableDotAnimation(true)
                .setFocusGravity(focusGravity)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(100)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(text)
                .setTarget(view)
                .setListener(this)
                .setUsageId(id)
                .show();
    }

    @Override
    public void onUserClicked(String s) {
        switch (s){
            case "Broadcast":
                showIntro(fab, "Add", getString(R.string.tip_add), FocusGravity.CENTER);
                break;
            case "Add":
                showIntro(toolbar, "More", getString(R.string.tip_more), FocusGravity.CENTER);
                break;
            case "More":
                showIntro(drawer, "Change", getString(R.string.tip_drawer), FocusGravity.LEFT);
                break;
            case "Change":
                showIntro(toolbar, "Tool", getString(R.string.tip_offline), FocusGravity.RIGHT);
                break;
            default:
                break;
        }
    }

    private void initView() {
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        //设置并列2行的layoutManager
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setNestedScrollingEnabled(false);
        //设置线性布局的layoutManager
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(linearLayoutManager);
        if (LOCAL_SELECTED){

            localAdapter = new LocalAdapter(localListItems);
            mRecyclerView.setAdapter(localAdapter);
        } else{
            lifeAdapter = new LifeAdapter(lifeListItems);
            mRecyclerView.setAdapter(lifeAdapter);
        }

        banner = (Banner) findViewById(R.id.banner);

        /**
         * Banner样式设置需要在设置图片和标题前完成设置
         */
        //可以选择设置图片网址，或者资源文件，默认加载框架Glide
        String imagePath1 = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.drawable.pic1).toString();
        String imagePath2 = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.drawable.pic2).toString();

        String imagePath4 = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.drawable.pic4).toString();
        String imagePath5 = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.drawable.pic5).toString();
        images =  new String[] {imagePath1, imagePath2, imagePath4, imagePath5};
        titles = new String[] {getString(R.string.title_1),
                getString(R.string.title_2),
                getString(R.string.title_3),
                getString(R.string.title_4), };

        banner.setDelayTime(4000);
        banner.setIndicatorGravity(Banner.RIGHT);
        banner.setBannerStyle(Banner.NUM_INDICATOR_TITLE);
        banner.setImages(images);
        banner.setBannerTitle(titles);
        banner.setVisibility(View.VISIBLE);
        banner.isAutoPlay(true);
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void OnBannerClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

}
