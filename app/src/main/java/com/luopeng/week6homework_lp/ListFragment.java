package com.luopeng.week6homework_lp;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luopeng.week6homework_lp.adapters.MyBaseAdapter;
import com.luopeng.week6homework_lp.adapters.MyPagerAdapter;
import com.luopeng.week6homework_lp.beans.News;
import com.luopeng.week6homework_lp.beans.Titles;
import com.luopeng.week6homework_lp.cache.MyLruCache;
import com.luopeng.week6homework_lp.urls.Urls;
import com.luopeng.week6homework_lp.utils.HttpsUtils;
import com.luopeng.week6homework_lp.utils.NetWorkUtils;
import com.luopeng.week6homework_lp.utils.SdCardUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {


    private final MyLruCache mLruCache;
    private ListView mListView;
    private List<News.DataBean> mData=new ArrayList<>();
    private MyBaseAdapter mAdapter;
    private int mType;
    private LinearLayout mLinearLayout;
    private ViewPager mViewPager;
    private MyPagerAdapter mPagerAdapter;
    private int mLastPage;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 120:
                    if(this.hasMessages(120)){
                        this.removeMessages(120);
                    }
                    Log.d("tag", "---------->handleMessage: 发消息" +mType);
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                    this.sendEmptyMessageDelayed(120,2000);
                    break;
                case 130:
                    if (this.hasMessages(120)){
                        this.removeMessages(120);
                    }
                    break;
            }
        }
    };
    private LinearLayout foot_LoadMore;
    private LinearLayout foot_Loading;
    private PullToRefreshListView mPullToRefreshListView;
    private boolean isPullDownToRefresh;//刷新时是否是下拉

    public ListFragment() {
        // Required empty public constructor
        int maxsize= (int) (Runtime.getRuntime().maxMemory()/16);
        mLruCache = new MyLruCache(maxsize);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initHeaderData();
        Log.d("tag", "---------->onCreate: " +mType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getPath();
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_list, container, false);
        initView(ret);
        initData();
        addHeader();
        addFoot();
        return ret;
    }

    @Override
    public void onStop() {
        super.onStop();



        Log.d("tag", "---------->onStop: " +mType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //重置数据
        mHandler.sendEmptyMessage(130);
        header_data.clear();
        mData.clear();
        page=1;
        Log.d("tag", "---------->onDestroyView: " +mType+", page"+page);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("tag", "---------->onDetach: "+mType );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("tag", "---------->onDestroy: "+mType );
    }


    /**
     * 添加尾布局
     */
    private void addFoot() {
        View foot = LayoutInflater.from(getContext()).inflate(R.layout.foot, mListView, false);
        foot_LoadMore = ((LinearLayout) foot.findViewById(R.id.foot_loadMore));
        foot_Loading = ((LinearLayout) foot.findViewById(R.id.foot_loading));

        mListView.addFooterView(foot);

        foot_LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foot_LoadMore.setVisibility(TextView.GONE);
                foot_Loading.setVisibility(LinearLayout.VISIBLE);
                //刷新数据
                page++;
                Log.d("flag", "---------->onClick: 点击尾布局加载数据" +page+" ,"+mType);
                isPullDownToRefresh=false;
                initData();

                Toast.makeText(getActivity(), "加载更多", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 添加头布局
     */
    private void addHeader() {
        if (mType!=0){
           return;
        }
        Log.d("tag", "---------->addHeader: " +mType);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.header, mListView, false);
        mViewPager = (ViewPager) header.findViewById(R.id.header_viewPager);
        mLinearLayout = (LinearLayout) header.findViewById(R.id.header_linear);
        initViewPager();
        initHeaderData();
        mListView.addHeaderView(header);
    }

    /**
     * 初始化头布局ViewPager
     */
    private void initViewPager() {
        mPagerAdapter = new MyPagerAdapter(header_data);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((ImageView) mLinearLayout.getChildAt(mLastPage)).setImageResource(R.mipmap.page);
                ((ImageView) mLinearLayout.getChildAt(position%3)).setImageResource(R.mipmap.page_now);
                mLastPage = position%3;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state==ViewPager.SCROLL_STATE_DRAGGING){
                    mHandler.sendEmptyMessage(130);
                }else if (state==ViewPager.SCROLL_STATE_IDLE){
                    mHandler.sendEmptyMessageDelayed(120,2000);
                }
            }
        });
        // TODO: 2016/11/12 设置图片轮播
        mHandler.sendEmptyMessageDelayed(120,2000);

    }

    private List<ImageView> header_data=new ArrayList<>();

    /**
     * 下载头布局数据和图片
     */
    private void initHeaderData() {
        // TODO: 2016/11/11 !!!!下载头布局数据
        if (NetWorkUtils.isConnected(getContext())){
            HttpsUtils.loadBytes(getActivity(),Urls.HEADERIMAGE_URL,new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 110:
                            byte[]bytes= (byte[]) msg.obj;
                            if (bytes != null) {
                                Titles titles = JSON.parseObject(new String(bytes), Titles.class);
                                for (int i = 0; i < titles.getData().size(); i++) {

                                    //一级二级缓存取数据
                                    String imagePath = titles.getData().get(i).getImage();
                                    Bitmap bitmap=getBitmapFromCacheAndLru(imagePath);


                                    if (bitmap!=null) {
                                        ImageView imageView = new ImageView(getContext());
                                        imageView.setImageBitmap(bitmap);
                                        header_data.add(imageView);
                                        if (header_data.size()==3){
                                            mPagerAdapter.notifyDataSetChanged();
                                        }
                                    }else {//一级二级缓存都没有数据--有网络--下载
                                        HttpsUtils.loadBytes(getActivity(),imagePath,new Handler(){
                                            @Override
                                            public void handleMessage(Message msg) {
                                                super.handleMessage(msg);
                                                switch (msg.what){
                                                    case 110:
                                                        byte[]bytes= (byte[]) msg.obj;
                                                        if (bytes != null) {
                                                            ImageView imageView = new ImageView(getContext());
                                                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                                                            header_data.add(imageView);
                                                            if (header_data.size()==3){
                                                                mPagerAdapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                            break;
                    }
                }
            });
        }else {//无网络--取
            String[] split = Urls.HEADERIMAGE_URL.split("/");
            byte[] data = SdCardUtils.getData(getContext().getExternalCacheDir().getAbsolutePath() +
                    File.separator + split[split.length - 1]);
            if (data != null) {
                Titles titles = JSON.parseObject(new String(data), Titles.class);
                for (int i = 0; i < titles.getData().size(); i++) {

                    //一级二级缓存取数据
                    Bitmap bitmap=getBitmapFromCacheAndLru(titles.getData().get(i).getImage());

                    ImageView imageView = new ImageView(getContext());
                    if (bitmap!=null) {
                        imageView.setImageBitmap(bitmap);
                    }else {//一级二级缓存都没有数据--也没有网络--使用小机器人
                        imageView.setImageResource(R.mipmap.ic_launcher);
                    }
                    header_data.add(imageView);
                    if (header_data.size()==3){
                            mPagerAdapter.notifyDataSetChanged();
                    }

                }
            }
        }

    }

    private Bitmap getBitmapFromCacheAndLru(String path) {
        String[] split1 = path.split("/");
        String fileName=split1[split1.length-1];
        Bitmap bitmap = mLruCache.get(fileName);

        if (bitmap==null){//一级缓存没有，二级缓存取
            byte[] data1 = SdCardUtils.getData(getContext().getExternalCacheDir().getAbsolutePath() +
                    File.separator + fileName);
            if (data1 != null) {//二级缓存取到数据--存进一级缓存
                bitmap = BitmapFactory.decodeByteArray(data1, 0, data1.length);

                mLruCache.put(fileName,bitmap);
            }
        }
        return bitmap;
    }

    /**
     * 获取fragment类型
     */
    private void getPath() {
        Bundle bundle = getArguments();
        mType = bundle.getInt("type", -1);
        switch (mType){
            case 0:
                path = Urls.HEADLINE_URL + Urls.HEADLINE_TYPE;
                break;
            case 1:
                path = Urls.BASE_URL + Urls.CYCLOPEDIA_TYPE;
                break;
            case 2:
                path = Urls.BASE_URL + Urls.CONSULT_TYPE;
                break;
            case 3:
                path = Urls.BASE_URL + Urls.OPERATE_TYPE;
                break;
            case 4:
                path = Urls.BASE_URL + Urls.DATA_TYPE;
                break;
        }
    }

    private int page=1;
    private String path;
    private void initData() {
        if (path != null) {
            if (!NetWorkUtils.isConnected(getContext())){//无网络--取
                String[] split = (path + page).split("/");
                byte[] data = SdCardUtils.getData(getContext().getExternalCacheDir().getAbsolutePath() +
                        File.separator + split[split.length - 1]);
                if (data != null) {
                    News news = JSON.parseObject(new String(data), News.class);
                    if (isPullDownToRefresh){
                        mData.addAll(0,news.getData());
                    }else {
                        mData.addAll(news.getData());
                    }

                    mAdapter.notifyDataSetChanged();
                    //隐藏刷新窗口
                    if (mPullToRefreshListView.isRefreshing()){
                        mPullToRefreshListView.onRefreshComplete();//暂停刷新
                    }
                    //重置尾布局
                    if (foot_LoadMore!=null&&foot_Loading!=null){//无网络时数据更新快，还未加载尾布局
                        foot_LoadMore.setVisibility(TextView.VISIBLE);
                        foot_Loading.setVisibility(LinearLayout.GONE);
                    }
                }

            }else {//有网络--下载
                HttpsUtils.loadBytes(getActivity(),path + page,new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what){
                            case 110:
                                Log.d("tag", "---------->handleMessage:type " +mType+", page: "+page+"   "+isPullDownToRefresh);
                                byte[]bytes= (byte[]) msg.obj;
                                if (bytes != null) {
                                    News news = JSON.parseObject(new String(bytes), News.class);
                                    if (isPullDownToRefresh){
                                        mData.addAll(0,news.getData());
                                    }else {
                                        mData.addAll(news.getData());
                                    }

                                    mAdapter.notifyDataSetChanged();
                                    //隐藏刷新窗口
                                    if (mPullToRefreshListView.isRefreshing()){
                                        mPullToRefreshListView.onRefreshComplete();//暂停刷新
                                    }
                                    //重置尾布局
                                    foot_LoadMore.setVisibility(TextView.VISIBLE);
                                    foot_Loading.setVisibility(LinearLayout.GONE);
                                }
                                break;
                        }
                    }
                });
            }

        }else {
            Log.d("flag", "---------->initData: path为空");
        }
    }

    private void initView(View ret) {
        mPullToRefreshListView = (PullToRefreshListView) ret.findViewById(R.id.list_view);
        mListView = mPullToRefreshListView.getRefreshableView();
        mAdapter = new MyBaseAdapter(mData,getContext());
        mListView.setAdapter(mAdapter);

        //设置下拉刷新监听
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                isPullDownToRefresh=true;
                initData();
            }
        });

        //listView设置条目点击监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<mData.size()){//添加尾布局后  尾部点击事件会触发该点击
                     mCallback.callItem(mData.get(position));
                }
            }
        });

        //listView设置条目长按删除点击监听
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                new AlertDialog.Builder(getContext())
                        .setTitle("温馨提示")
                        .setMessage("是否要删除该条目？")
                        .setIcon(R.mipmap.icon_dialog)
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if (mType==0){
                                mData.remove(position-2);
                            }else {
                                mData.remove(position-1);
                            }
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
                        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();


                return true;
            }
        });

        //listView设置滑动到底监听监听
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private boolean mIsScroll;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState==SCROLL_STATE_IDLE){//滑动停止
                    mIsScroll = false;
                }else {
                    mIsScroll=true;
                }
            }
            private int currentLastItem=1;
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (lastVisibleItem !=totalItemCount
                        ||totalItemCount<=6//否则第一页加载会触发
                        ||lastVisibleItem==currentLastItem){//防止到底多次刷新
                    return;
                }
                if (!mIsScroll){//没有在滑动才触发
                    currentLastItem=lastVisibleItem;
                    if (currentLastItem==totalItemCount&&currentLastItem!=0){

                        //设置尾布局
                        foot_LoadMore.setVisibility(TextView.GONE);
                        foot_Loading.setVisibility(LinearLayout.VISIBLE);
                        //刷新数据
                        page++;
                        Log.d("flag", "---------->onScroll: " +currentLastItem+"  ,"+page+", type"+mType);
                        isPullDownToRefresh=false;
                        initData();
                    }

                }
            }
        });

    }
    private ListFrm_mainActCallback mCallback;

    public void setCallback(ListFrm_mainActCallback callback) {
        mCallback = callback;
    }

    public interface ListFrm_mainActCallback{
        void callItem(News.DataBean dataBean);
    }

}
