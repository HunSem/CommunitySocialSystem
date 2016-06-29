package com.huan.percy.communitysocialsystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MaterialRefreshLayout materialRefreshLayout;
    private ListView mListView;
    private static boolean LOCAL_SELECTED = true;

    private int[] to={R.id.author, R.id.article, R.id.date};   //这里是ListView显示每一列对应的list_item中控件的id
    List<Map<String, Object>> localListItems = new ArrayList<Map<String, Object>>();
    List<Map<String, Object>> lifeListItems = new ArrayList<Map<String, Object>>();

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

        final SimpleAdapter localAdapter = new SimpleAdapter(this, localListItems, R.layout.list_item_layout,
                new String[] {"author", "article", "date"},
                to);
        final SimpleAdapter lifeAdapter = new SimpleAdapter(this, lifeListItems, R.layout.list_item_layout,
                new String[] {"title", "article", "date"},
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
                                loadLocalData(localAdapter);
                            } else {
                                loadLifeData(lifeAdapter);
                            }

                            // 结束下拉刷新...
                            materialRefreshLayout.finishRefresh();
                        }
                    }, 2000);

                }

                @Override
                public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
                    super.onRefreshLoadMore(materialRefreshLayout);

                    materialRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (LOCAL_SELECTED){
                                //下拉刷新
                                loadLocalMore(localAdapter);
                            } else {
                                loadLifeMore(lifeAdapter);
                            }

                            // 结束上拉刷新...
                            materialRefreshLayout.finishRefreshLoadMore();
                        }
                    }, 2000);
                }

                @Override
                public void onfinish() {

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
        if (id == R.id.action_settings) {
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



    private void loadLocalData(SimpleAdapter localAdapter) {
        localListItems.clear();
        for(int i = 0; i < 10; i++){
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("author", "HunSem");
            listItem.put("article", "啊是你倒是");
            listItem.put("date", "08:40");
            localListItems.add(listItem);
        }
        mListView.setAdapter(localAdapter);

        localAdapter.notifyDataSetChanged();

    }

    private void loadLocalMore(SimpleAdapter localAdapter){

        Map<String, Object> listItem = new HashMap<String, Object>();
        listItem.put("author", "HunSem");
        listItem.put("article", "打了卡升级了");
        listItem.put("date", "08:40");
        localListItems.add(listItem);
        localAdapter.notifyDataSetChanged();
    }

    private void loadLifeData(SimpleAdapter lifeAdapter) {
        lifeListItems.clear();
        for(int i = 0; i < 10; i++){
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("title", "老板娘跑了！！");
            listItem.put("article", "清仓大甩卖！");
            listItem.put("date", "08:40");
            lifeListItems.add(listItem);
        }
        mListView.setAdapter(lifeAdapter);

        lifeAdapter.notifyDataSetChanged();

    }

    private void loadLifeMore(SimpleAdapter lifeAdapter){

        Map<String, Object> listItem = new HashMap<String, Object>();
        listItem.put("title", "老板娘回来了！！");
        listItem.put("article", "优惠促销！");
        listItem.put("date", "08:40");
        lifeListItems.add(listItem);
        lifeAdapter.notifyDataSetChanged();
    }

}



