package com.luopeng.week6homework_lp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler=new Handler(){
        private int currentTime=3;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (currentTime==0){
                        //倒计时结束

                        boolean isFirst = SplashActivity.this
                                .getSharedPreferences("appConfig", Context.MODE_PRIVATE)
                                .getBoolean("isFirst", true);
                        if(isFirst){//第一次登陆--显示引导界面
                            mRelativeLayout.setVisibility(RelativeLayout.GONE);
                            mFrameLayout.setVisibility(FrameLayout.VISIBLE);
                            GuideFragment guideFragment = new GuideFragment();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.splash_frame,guideFragment)
                                    .commit();

                        }else {//不是第一次登陆--调到主界面界面
                            SplashActivity.this.startActivity(new Intent(SplashActivity.this,MainActivity.class));
                            SplashActivity.this.finish();
                        }
                        break;
                    }
                    currentTime--;
                    mTextView.setText(currentTime+"秒后进入主界面");
                    this.sendEmptyMessageDelayed(1,1000);
                    break;
            }
        }
    };
    private TextView mTextView;
    private RelativeLayout mRelativeLayout;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        showTime();
        initView();
    }

    private void showTime() {
        mHandler.sendEmptyMessageDelayed(1,1000);
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.splash_showTime);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.splash_main);
        mFrameLayout = (FrameLayout) findViewById(R.id.splash_frame);
    }
}
