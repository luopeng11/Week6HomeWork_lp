package com.luopeng.week6homework_lp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.luopeng.week6homework_lp.adapters.MyBaseAdapter;
import com.luopeng.week6homework_lp.beans.News;
import com.luopeng.week6homework_lp.urls.Urls;
import com.luopeng.week6homework_lp.utils.HttpsUtils;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private String search;
    private TextView title;
    private ListView mListView;
    private ArrayList<News.DataBean> mData = new ArrayList<>();
    private MyBaseAdapter mAdapter;
    private RelativeLayout mSearchView;
    private View mFrameLayout;
    private ContentFragment mContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search = getIntent().getStringExtra("trim");
        initView();
        initListView();
        initData();
        initFragment();
    }

    private void initFragment() {
        mContentFragment = new ContentFragment();
        mContentFragment.setCallback(new ContentFragment.MessageCallback() {
            @Override
            public void backToActivity() {//fragment中点击了返回-->隐藏fragment，显示activity
                mSearchView.setVisibility(RelativeLayout.VISIBLE);
                mFrameLayout.setVisibility(FrameLayout.GONE);
            }

            @Override
            public void collect() {
                String id1 = mData.get(currentPosition).getId();
                String create_time = mData.get(currentPosition).getCreate_time();
                String description = mData.get(currentPosition).getDescription();
                String nickname = mData.get(currentPosition).getNickname();
                String source = mData.get(currentPosition).getSource();
                String title = mData.get(currentPosition).getTitle();
                String wap_thumb = mData.get(currentPosition).getWap_thumb();
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frameLayout, mContentFragment)
                .commit();
    }

    private void initData() {
        HttpsUtils.loadBytes(this,Urls.SEARCH_URL+search,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 110:
                        byte[] bytes= (byte[]) msg.obj;
                        if (bytes != null) {
                            News news = JSON.parseObject(new String(bytes), News.class);
                            mData.addAll(news.getData());
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });
    }

    private void initListView() {
        mAdapter = new MyBaseAdapter(mData, this);
        mListView.setAdapter(mAdapter);
        //西湖龙井

    }

    private void initView() {
        mFrameLayout = findViewById(R.id.content_frameLayout);
        title = (TextView) findViewById(R.id.searchAct_textview_title);
        title.setText(search);
        mSearchView = (RelativeLayout) findViewById(R.id.searchView_activity);
        mListView = (ListView) findViewById(R.id.search_listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int position, long id) {
                currentPosition=position;
                String id1 = mData.get(position).getId();

                //点击隐藏主界面 ，显示frameLayout
                mSearchView.setVisibility(RelativeLayout.GONE);
                mFrameLayout.setVisibility(FrameLayout.VISIBLE);

                //设置webView内容
                mContentFragment.setId(id1);
            }
        });
    }
    private int currentPosition;
    //返回按钮点击事件  点击返回主页面
    public void search_Act(View view) {
        this.finish();
        //this.startActivity(new Intent(this,MainActivity.class));
    }
}
