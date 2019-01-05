package cn.edu.uzz.activity.book.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by beibei on 2017/9/9.
 */

public class MyDbHelper extends SQLiteOpenHelper {
    //Android使用的是SQLite数据库

    public MyDbHelper(Context context) {
        //参数1 上下文 参数2 数据库的名字 参数3 游标工厂 传入null就是使用系统默认的游标工厂
        //参数4 数据库的版本号
        super(context, "emclient.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库的时候  会执行这个方法
        //在这个方法中 要做的是表结构的初始化 以及初始数据的填充
        db.execSQL("create table contact_info (_id integer primary key autoincrement, username varchar(20), contact varchar(20))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //处理数据库 升级的时候的业务逻辑
    }
}
