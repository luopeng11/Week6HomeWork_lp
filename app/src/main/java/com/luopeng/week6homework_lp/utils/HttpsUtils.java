package com.luopeng.week6homework_lp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by my on 2016/11/9.
 */
public class HttpsUtils {

    public static void loadBytes(final Context context,final String path, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                Request.Builder builder=new Request.Builder();
                builder.url(path);
                Request request = builder.build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        byte[] bytes = response.body().bytes();

                        //保存到sd卡
                        String[] strings = path.split("/");
                        SdCardUtils.saveToCache(context.getExternalCacheDir().getAbsolutePath(),
                                strings[strings.length-1],
                                bytes);

                        Message msg=new Message();
                        msg.what=110;
                        msg.obj=bytes;

                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void loadBytes(final Context context, final String path, final int position, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                Request.Builder builder=new Request.Builder();
                builder.url(path);
                Request request = builder.build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        byte[] bytes = response.body().bytes();
                        //保存到sd卡
                        String[] strings = path.split("/");
                        SdCardUtils.saveToCache(context.getExternalCacheDir().getAbsolutePath(),
                                strings[strings.length-1],
                                bytes);

                        Message msg=new Message();
                        msg.what=110;
                        msg.arg1=position;
                        msg.obj=bytes;

                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
