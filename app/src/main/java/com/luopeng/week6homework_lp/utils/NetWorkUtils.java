package com.luopeng.week6homework_lp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by my on 2016/11/5.
 */
public class NetWorkUtils {
    public static boolean isConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }else if (info.getType()==ConnectivityManager.TYPE_WIFI){
            return true;
        }else if (info.getType()==ConnectivityManager.TYPE_MOBILE){
            return true;
        }
        return false;
    }
}
