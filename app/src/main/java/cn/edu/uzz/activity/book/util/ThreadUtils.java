package cn.edu.uzz.activity.book.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by fullcircle on 2017/12/19.
 */

public class ThreadUtils {
    //创建一个单线程的线程池
    private static Executor executor = Executors.newSingleThreadExecutor();
    //创建一个主线程的handler对象 通过这个handler把子线程的逻辑发送到主线程中执行
    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 在子线程中执行一段逻辑
     * @param runnable
     */
    public static void runOnSubThread(Runnable runnable){
        executor.execute(runnable);
    }

    /**
     * 在主线程中执行一个runnable对象
     * @param runnable
     */
    public static void runOnUIThread(Runnable runnable){
        //通过主线程的handler把runnable对象放到主线程中执行
        handler.post(runnable);
    }
}
