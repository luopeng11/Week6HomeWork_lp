package com.luopeng.week6homework_lp.sqlhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by my on 2016/11/12.
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context) {
        super(context, "news.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists  tb_collection(_id integer primary key ," +
                "id varchar(10) ,title varchar(10) ,source varchar(10) ,description varchar(10) ," +
                "wap_thumb varchar(10) ,create_time varchar(10) ,nickname varchar(10));");
        db.execSQL("create table if not exists  tb_history(_id integer primary key ," +
                "id varchar(10) ,title varchar(10) ,source varchar(10) ,description varchar(10) ," +
                "wap_thumb varchar(10) ,create_time varchar(10) ,nickname varchar(10));");
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
