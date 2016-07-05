package com.huan.percy.communitysocialsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private int position = 1;
    private CollapsingToolbarLayoutState state;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView article = (TextView) findViewById(R.id.detail_article);
        ImageView title_pic = (ImageView) findViewById(R.id.title_pic);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 1);
        switch (position){
            case 1:
                article.setText(getString(R.string.large_text_1));
                title_pic.setImageResource(R.drawable.pic1);
                break;
            case 2:
                article.setText(getString(R.string.large_text_2));
                title_pic.setImageResource(R.drawable.pic2);
                break;
            case 3:
                article.setText(getString(R.string.large_text_3));
                title_pic.setImageResource(R.drawable.pic4);
                break;
            case 4:
                article.setText(getString(R.string.large_text_4));
                title_pic.setImageResource(R.drawable.pic5);
                break;
            default:
                article.setText("暂时无法加载数据");
                title_pic.setImageResource(R.drawable.pic1);
                break;
        }

        AppBarLayout app_bar=(AppBarLayout)findViewById(R.id.app_bar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (app_bar != null) {
            app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                    if (verticalOffset == 0) {
                        if (state != CollapsingToolbarLayoutState.EXPANDED) {
                            state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                            collapsingToolbarLayout.setTitle("新闻详情");//设置title为EXPANDED
                        }
                    } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                        if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                            collapsingToolbarLayout.setTitle("");//设置title不显示
                            state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                        }
                    } else {
                        if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                            collapsingToolbarLayout.setTitle("新闻详情");//设置title为INTERNEDIATE
                            state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                        }
                    }
                }
            });
        }
    }

}
