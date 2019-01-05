package cn.edu.uzz.activity.book.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beibei on 2017/9/9.
 */

public class DbUtils {
    public static Context context = null;

    public static void InitDbUtils(Context context){
        DbUtils.context = context;
    }

    /**
     * 从本地数据库获取联系人
     * @param username
     * @return
     */
    public static List<String> getContactFromDb(String username){

        if(context == null){
            throw new IllegalStateException("InitDbUtils没有调用 工具未初始化！！");
        }
        //创建openHelper
        MyDbHelper helper = new MyDbHelper(context);
        //通过openHelper 获取sqliteDatabase对象
        SQLiteDatabase db = helper.getReadableDatabase();
        //select contact from contact_info where username = username;
        //query方法 传入参数 参数1 表名字 参数2要查询的列名 参数3 要满足的条件(条件 = 后面的值要用? 代替) 参数4 从来替换? 的内容的String数组
        //参数5 分组 参数6 分组的条件 参数7 排序
        Cursor cursor = db.query("contact_info", new String[]{"contact"}, "username = ?", new String[]{username}, null, null, null);
        if(cursor!=null){
            List<String> contacts = new ArrayList<>();
            //不断的移动游标
            while (cursor.moveToNext()){
                //
                contacts.add(cursor.getString(0));//cursor.getString(0) 获取结果中的第0列数据 添加到集合中
            }
            cursor.close();//查询之后关闭游标
            db.close(); //数据库也要关闭
            return contacts;
        }else {
            db.close();
            return null;
        }
    }

    /**
     * 从服务器获取到最新的数据之后 要更新本地数据库
     * @param username
     * @param newContacts 从服务器获取到的最新的数据
     */
    public static void updateLocalContact(String username,List<String> newContacts){
        if(context == null){
            throw new IllegalStateException("InitDbUtils没有调用 工具未初始化！！");
        }
        //创建openHelper
        MyDbHelper helper = new MyDbHelper(context);
        //通过openHelper 获取sqliteDatabase对象
        SQLiteDatabase db = helper.getReadableDatabase();
        db.beginTransaction(); //开启数据库事务
        try {
            //先删除当前用户的所有好友
            db.delete("contact_info","username = ?", new String[]{username});
            //准备ContentValues 遍历传入的集合 把这些好友插入到表中 作为当前用户的最新的联系人
            ContentValues values = new ContentValues();
            //由于所有的好友都是当前Username的好友 所以每一次插入的Username都相同
            values.put("username",username);
            for (String newContact : newContacts) {
                //遍历集合 放入新的好友名字
                values.put("contact",newContact);
                //插入一条数据
                db.insert("contact_info",null,values);
            }
           //设置事物成功
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();//数据库事务结束  如果执行这一句的时候 发现事务已经成功了
            //所有的关于数据库的变化都会提交保存 如果发现事务没成功 所有的数据都会回滚到
            //db.beginTransaction()之前
        }
        db.close();
    }
}
