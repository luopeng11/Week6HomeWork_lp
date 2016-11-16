package com.luopeng.week6homework_lp;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.luopeng.week6homework_lp.beans.WebContent;
import com.luopeng.week6homework_lp.urls.Urls;
import com.luopeng.week6homework_lp.utils.HttpsUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment implements View.OnClickListener {


    private ImageView collect;
    private WebView mWebView;

    public ContentFragment() {
        // Required empty public constructor

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("bag", "---------->onDestroyView: ");
        mWebView.loadDataWithBaseURL(null, "。。。","text/html","utf-8",null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_content, container, false);

        initView(ret);
        return ret;
    }

    private void initView(View ret) {
        ImageView back = (ImageView) ret.findViewById(R.id.content_back);
        collect = ((ImageView) ret.findViewById(R.id.content_collect));
        mWebView = ((WebView) ret.findViewById(R.id.content_webView));
        back.setOnClickListener(this);
        collect.setOnClickListener(this);
    }
    private MessageCallback mCallback;

    public void setCallback(MessageCallback callback) {
        mCallback = callback;
    }

    public void setId(String id1) {
        mWebView.loadDataWithBaseURL(null, "正在加载。。。","text/html","utf-8",null);
        HttpsUtils.loadBytes(getActivity(),Urls.CONTENT_URL+id1,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==110){
                    byte[]bytes= (byte[]) msg.obj;
                    if (bytes != null) {
                        String data = new String(bytes);
                        WebContent webContent = JSON.parseObject(data, WebContent.class);
                        mWebView.loadDataWithBaseURL(null, webContent.getData().getWap_content(),"text/html","utf-8",null);
                    }

                }
            }
        });
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.content_back:
                mCallback.backToActivity();
                break;
            case R.id.content_collect:
                mCallback.collect();

                break;
        }
    }

    public void setCleanContent() {
        mWebView.loadDataWithBaseURL(null, "正在加载。。。","text/html","utf-8",null);
    }

    public interface MessageCallback{
        void backToActivity();
        void collect();
    }
}
