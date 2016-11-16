package com.luopeng.week6homework_lp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.luopeng.week6homework_lp.adapters.MyFragmentPagerAdapter;
import com.luopeng.week6homework_lp.beans.News;
import com.luopeng.week6homework_lp.sqlhelper.MySQLiteOpenHelper;
import com.softpo.viewpagertransformer.RotateDownTransformer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ImageButton mSearch;
    private ViewPager mViewPager;
    private List<Fragment> mFragments=new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private EditText mEditText;
    private FrameLayout mFrameLayout;
    private ContentFragment mFragment;
    private MySQLiteOpenHelper mMySQLiteOpenHelper=new MySQLiteOpenHelper(this);
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建数据库对象
        db = mMySQLiteOpenHelper.getReadableDatabase();
        initView();
        initTabLayout();
        initData();
        initViewPager();
        addContentFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private void addContentFragment() {
        mFragment = new ContentFragment();
        mFragment.setCallback(new ContentFragment.MessageCallback() {
            @Override
            public void backToActivity() {//详情界面点击了返回键
                //显示主界面，隐藏详情界面
                mDrawerLayout.setVisibility(FrameLayout.VISIBLE);
                mFrameLayout.setVisibility(DrawerLayout.GONE);
            }

            @Override
            public void collect() {//详情界面点击了收藏
                String id = currentVebItem.getId();
                String title = currentVebItem.getTitle();
                String source = currentVebItem.getSource();
                String description = currentVebItem.getDescription();
                String wap_thumb = currentVebItem.getWap_thumb();
                String create_time = currentVebItem.getCreate_time();
                String nickname = currentVebItem.getNickname();

                //存进数据库
                db.execSQL("insert into tb_collection(id,title,source,description,wap_thumb,create_time,nickname) " +
                        "values('"+id+"','"+title+"'," +
                        "'"+source+"','"+description+"','"+wap_thumb+"'," +
                        "'"+create_time+"','"+nickname+"');");
                Toast.makeText(MainActivity.this, "保存条目进收藏", Toast.LENGTH_SHORT).show();
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_frameLayout, mFragment)
                .commit();
    }
    /**
     * id : 8218
     * title : 认识凤凰单丛茶
     * source : 原创
     * description :
     * wap_thumb : http://s1.sns.maimaicha.com/images/2016/01/06/20160106110314_22333_suolue3.jpg
     * create_time : 01月06日11:04
     * nickname : bubu123
     */

    private void initViewPager() {
        mAdapter = new MyFragmentPagerAdapter(mFragments,getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(true,new RotateDownTransformer());
    }

    //当前正在webView中显示的listView子条目
    private News.DataBean currentVebItem;
    //初始化主界面ViewPager的Fragment
    private void initData() {
        for (int i = 0; i < 5; i++) {
            ListFragment fragment = new ListFragment();
            fragment.setCallback(new ListFragment.ListFrm_mainActCallback() {
                @Override
                public void callItem(News.DataBean dataBean) {
                    currentVebItem = dataBean;
                    // TODO: 2016/11/12 设置webView

                    //设置详情界面webView内容
                    mFragment.setId(currentVebItem.getId());
                    //显示详情界面，隐藏主界面
                    mFrameLayout.setVisibility(FrameLayout.VISIBLE);
                    mDrawerLayout.setVisibility(DrawerLayout.GONE);

                    //将当前条目添加大历史记录
                    String id = currentVebItem.getId();
                    String title = currentVebItem.getTitle();
                    String source = currentVebItem.getSource();
                    String description = currentVebItem.getDescription();
                    String wap_thumb = currentVebItem.getWap_thumb();
                    String create_time = currentVebItem.getCreate_time();
                    String nickname = currentVebItem.getNickname();

                    //存进数据库
                    db.execSQL("insert into tb_history(id,title,source,description,wap_thumb,create_time,nickname) " +
                            "values('"+id+"','"+title+"'," +
                            "'"+source+"','"+description+"','"+wap_thumb+"'," +
                            "'"+create_time+"','"+nickname+"');");
                    Toast.makeText(MainActivity.this, "保存条目进历史记录", Toast.LENGTH_SHORT).show();
                }
            });
            Bundle bundle = new Bundle();
            bundle.putInt("type",i);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }

    }

    private void initTabLayout() {
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setText("头条");
        mTabLayout.addTab(tab);

        TabLayout.Tab tab1 = mTabLayout.newTab();
        tab1.setText("百科");
        mTabLayout.addTab(tab1);

        TabLayout.Tab tab2 = mTabLayout.newTab();
        tab2.setText("咨询");
        mTabLayout.addTab(tab2);

        TabLayout.Tab tab3 = mTabLayout.newTab();
        tab3.setText("经营");
        mTabLayout.addTab(tab3);

        TabLayout.Tab tab4 = mTabLayout.newTab();
        tab4.setText("数据");
        mTabLayout.addTab(tab4);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.main_tab);
        mSearch = (ImageButton) findViewById(R.id.main_btn_search);
        mViewPager = (ViewPager) findViewById(R.id.main_ViewPager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mEditText = (EditText) findViewById(R.id.edit_search);
        mFrameLayout = (FrameLayout) findViewById(R.id.main_frameLayout);
    }
    //抽屉显示按钮点击事件   点击显示抽屉
    public void showDrawer(View view) {
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)){
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
    }
    //搜索按钮点击事件  点击开始搜索
    public void goSearch(View view) {
        // 得到地址 跳转到新activiry-->搜索Activity

        String trim = mEditText.getText().toString().trim();
        if (trim.length()==0) {
            Toast.makeText(this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("trim", trim);
            this.startActivity(intent);
        }
    }

    //抽屉返回按钮，点击收起抽屉
    public void search(View view) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)){
            mDrawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    //我的收藏点击事件，点击显示我的收藏
    public void myCollection(View view) {
        Intent intent = new Intent(this, CollectionActivity.class);
        this.startActivity(intent);
    }
}
