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
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.PtrUIHandler;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.loadmore.SwipeRefreshHelper;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.huan.percy.communitysocialsystem.adapter.LifeListViewAdapter;
import com.huan.percy.communitysocialsystem.adapter.LocalListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SwipeRefreshLayout mSryt;
    private ListView mListView;

    private List<String> mDatas = new ArrayList<>();
    private LocalListViewAdapter mAdapter;
    private LifeListViewAdapter mLifeAdapter;
    private SwipeRefreshHelper mSwipeRefreshHelper;


    private int page = 0;
    private Handler mHandler = new Handler();

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



        initView();
        initData();


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
            initData();
        } else if (id == R.id.nav_life_info) {
            updateData();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initView() {
        mSryt = (SwipeRefreshLayout) this.findViewById(R.id.list_view_frame);
        mListView = (ListView) findViewById(R.id.list_view);
        mSryt.setColorSchemeColors(Color.RED);
    }

    private void initData() {
        mAdapter = new LocalListViewAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        mSwipeRefreshHelper = new SwipeRefreshHelper(mSryt);

        mSryt.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshHelper.autoRefresh();
            }
        });

        mSwipeRefreshHelper.setOnSwipeRefreshListener(new SwipeRefreshHelper.OnSwipeRefreshListener() {
            @Override
            public void onfresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        page = 0;
                        for (int i = 0; i < 17; i++) {
                            mDatas.add(new String("  SwipeListView item  -" + i));
                        }
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshHelper.refreshComplete();
                        mSwipeRefreshHelper.setLoadMoreEnable(true);
                    }
                }, 1000);
            }
        });

        mSwipeRefreshHelper.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.add(new String("  SwipeListView item  - add " + page));
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshHelper.loadMoreComplete(true);
                        page++;
                        Toast.makeText(MainActivity.this, "load more complete", Toast.LENGTH_SHORT)
                                .show();
                    }
                }, 1000);
            }
        });

    }

    private void updateData(){
        mLifeAdapter = new LifeListViewAdapter(this, mDatas);
        mListView.setAdapter(mLifeAdapter);
        mSwipeRefreshHelper = new SwipeRefreshHelper(mSryt);

        mSryt.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshHelper.autoRefresh();
            }
        });

        mSwipeRefreshHelper.setOnSwipeRefreshListener(new SwipeRefreshHelper.OnSwipeRefreshListener() {
            @Override
            public void onfresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        page = 0;
                        for (int i = 0; i < 17; i++) {
                            mDatas.add(new String("  update  -" + i));
                        }
                        mLifeAdapter.notifyDataSetChanged();
                        mSwipeRefreshHelper.refreshComplete();
                        mSwipeRefreshHelper.setLoadMoreEnable(true);
                    }
                }, 1000);
            }
        });

        mSwipeRefreshHelper.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.add(new String("  SwipeListView item  - add " + page));
                        mLifeAdapter.notifyDataSetChanged();
                        mSwipeRefreshHelper.loadMoreComplete(true);
                        page++;
                        Toast.makeText(MainActivity.this, "load more complete", Toast.LENGTH_SHORT)
                                .show();
                    }
                }, 1000);
            }
        });

    }

    SwipeRefreshHelper.OnSwipeRefreshListener mOnSwipeRefreshListener = new SwipeRefreshHelper.OnSwipeRefreshListener() {
        @Override
        public void onfresh() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDatas.clear();
                    page = 0;
                    for (int i = 0; i < 17; i++) {
                        mDatas.add(new String("  SwipeListView item  -" + i));
                    }
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshHelper.refreshComplete();
                    mSwipeRefreshHelper.setLoadMoreEnable(true);
                }
            }, 1000);
        }
    };



}



