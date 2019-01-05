package cn.edu.uzz.activity.book.util;

import com.hyphenate.EMCallBack;

/**
 * Created by fullcircle on 2017/7/22.
 */

public abstract class MyCallBack implements EMCallBack {
    public abstract void success();
    public abstract void error(int i, String s);
    public void progress(int i, String s){

    }

    @Override
    public void onSuccess() {
        //把环信的EMCallBack的方法 通过线程工具类 发送到主线程执行
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                success();
            }
        });
    }

    @Override
    public void onError(final int i, final String s) {
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                error(i,s);
            }
        });
    }

    @Override
    public void onProgress(final int i, final String s) {
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
               progress(i,s);
            }
        });
    }
}
