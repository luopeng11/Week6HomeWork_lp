package com.luopeng.week6homework_lp;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luopeng.week6homework_lp.adapters.MyBaseAdapter;
import com.luopeng.week6homework_lp.beans.News;
import com.luopeng.week6homework_lp.sqlhelper.MySQLiteOpenHelper;

import java.util.ArrayList;

public class CollectionActivity extends AppCompatActivity {
    private TextView title;
    private ListView mListView;
    private ArrayList<News.DataBean> mData = new ArrayList<>();
    private MyBaseAdapter mAdapter;
    private RelativeLayout mSearchView;
    private View mFrameLayout;
    private ContentFragment mContentFragment;
    private MySQLiteOpenHelper mMySQLiteOpenHelper=new MySQLiteOpenHelper(this);
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = mMySQLiteOpenHelper.getReadableDatabase();
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
                mContentFragment.setCleanContent();
            }

            @Override
            public void collect() {
                Toast.makeText(CollectionActivity.this, "已存在", Toast.LENGTH_SHORT).show();
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frameLayout, mContentFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private void initData() {
        Cursor cursor = db.query("tb_collection", null, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String source = cursor.getString(cursor.getColumnIndex("source"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            String wap_thumb = cursor.getString(cursor.getColumnIndex("wap_thumb"));
            String create_time = cursor.getString(cursor.getColumnIndex("create_time"));
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            News.DataBean dataBean=new News.DataBean();
            dataBean.setId(id);
            dataBean.setTitle(title);
            dataBean.setSource(source);
            dataBean.setDescription(description);
            dataBean.setWap_thumb(wap_thumb);
            dataBean.setCreate_time(create_time);
            dataBean.setNickname(nickname);
            mData.add(dataBean);
        }
        mAdapter.notifyDataSetChanged();
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

    private void initListView() {
        mAdapter = new MyBaseAdapter(mData, this);
        mListView.setAdapter(mAdapter);
        //西湖龙井

    }

    private void initView() {
        mFrameLayout = findViewById(R.id.content_frameLayout);
        title = (TextView) findViewById(R.id.searchAct_textview_title);
        title.setText("我的收藏");
        mSearchView = (RelativeLayout) findViewById(R.id.searchView_activity);
        mListView = (ListView) findViewById(R.id.search_listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int position, long id) {
                String id1 = mData.get(position).getId();

                //点击隐藏主界面 ，显示frameLayout
                mSearchView.setVisibility(RelativeLayout.GONE);
                mFrameLayout.setVisibility(FrameLayout.VISIBLE);


                //设置webView内容
                mContentFragment.setId(id1);

            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, final long id) {
                new AlertDialog.Builder(CollectionActivity.this)
                        .setTitle("温馨提示").setMessage("是否要删除该条目？")
                        .setIcon(R.mipmap.icon_dialog)
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String id1 = mData.get(position).getId();
                                mData.remove(position);

                                //删除数据库
                                db.delete("tb_collection","id='"+id1+"'",null);

                                //要删除条目右移
                                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0, view.getWidth());
                                objectAnimator.setDuration(2000);

                                ArrayList<Animator> animators = new ArrayList<>();
                                animators.add(objectAnimator);

                                long j=2000;
                                //下方所有子条目上移
                                for (int i = position-mListView.getFirstVisiblePosition()+1; i <mListView.getChildCount() ; i++) {
                                    ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mListView.getChildAt(i), "translationY", 0, -view.getHeight()-mListView.getDividerHeight());
                                    objectAnimator1.setStartDelay(j);
                                    j+=500;
                                    objectAnimator1.setDuration(500);
                                    animators.add(objectAnimator1);
                                }
                                AnimatorSet set = new AnimatorSet();
                                set.playTogether(animators);
                                set.start();
                                set.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        //刷新数据
                                        mAdapter.notifyDataSetChanged();

                                        //重置控件
                                        for (int i = 0; i < mListView.getChildCount(); i++) {
                                            mListView.getChildAt(i).setTranslationX(0);
                                            mListView.getChildAt(i).setTranslationY(0);
                                        }
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                                Toast.makeText(CollectionActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();


                return true;
            }
        });
    }

    /**
     * //删除数据库
     db.delete("tb_collection","id='"+id1+"'",null);
     Toast.makeText(CollectionActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
     */
    //返回按钮点击事件  点击返回主页面
    public void search_Act(View view) {
        this.finish();

        //this.startActivity(new Intent(this,MainActivity.class));
    }
}
