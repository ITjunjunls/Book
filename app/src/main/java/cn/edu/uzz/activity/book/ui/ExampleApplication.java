package cn.edu.uzz.activity.book.ui;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.dao.DbUtils;
import cn.edu.uzz.activity.book.push.Logger;
import cn.edu.uzz.activity.book.util.GetMessageEvent;
import cn.jpush.android.api.JPushInterface;

/**
 * For developer startup JPush SDK
 * 
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class ExampleApplication extends Application {
    private static final String TAG = "JIGUANG-Example";
	private SoundPool soundPool;
	private int foreground;
    @Override
    public void onCreate() {    	     
    	 Logger.d(TAG, "[ExampleApplication] onCreate");
         super.onCreate();

         JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 JPush
		 Logger.d(TAG, "[ExampleApplication] ok");
		initHX();
//		initLeanCloud();
		DbUtils.InitDbUtils(this);
		initSoundPool();
    }

//	private void initLeanCloud() {
//		AVOSCloud.initialize(this,"uegGFiDbKaCaF0r66bDDo6iy-gzGzoHsz","st9EHtxdfyR4eSGW1dalVQPn");
//	}
	private void initSoundPool() {
		//参数1 当前声音池最多缓存多少声音
		//参数2 声音的类型 通过AudioManager封装  STREAM_MUSIC走媒体声音（音乐 视频 游戏）
		//STREAM_ALARM 闹钟的 STREAM_NITIFICATION 通知  STREAM_RING 电话铃声
		//参数3  设计的目的是控制声音的采样率（音质） 当前没有用 传入0 作为默认值
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
		//向声音池中添加声音  参数1 上下文 参数2 声音的资源id 一般放到res->raw目录下 参数3 没用 传入1作为默认值
		foreground = soundPool.load(getApplicationContext(), R.raw.duan, 1);
//        background = soundPool.load(getApplicationContext(), R.raw.duan, 1);
	}

	private void initHX() {
		EMOptions options = new EMOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		//初始化
		EMClient.getInstance().init(this, options);
		//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
		EMClient.getInstance().setDebugMode(true);
		//给环信添加收到消息的监听器
		EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
			@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onMessageReceived(List<EMMessage> list) {
				//收到消息了
				EventBus.getDefault().post(new GetMessageEvent());
				//play方法播放声音池中的声音
				//参数1 声音的id load方法的返回值
				//参数2 参数3  左右声道的音量 取值范围0~1
				//参数4 优先级 0优先级最低
				//参数5 是否循环 0 不循环 -1 不停的循环
				//参数6 播放的速度 取值范围0.5~2 1是正常速度
				soundPool.play(foreground,1,1,1,0,1);
//                    soundPool.play(background,1,1,1,0,1);
				if (isForegound()){
					sendNotification(list.get(0));
				}
			}

			@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
			private void sendNotification(EMMessage emMessage) {
				Notification.Builder bulider=new Notification.Builder(getApplicationContext());
				//设置通知点击后自动消失
				bulider.setAutoCancel(true);
				//设置通知的小图标
				bulider.setSmallIcon(R.mipmap.book_icon);
				//设置通知的大标题
				bulider.setContentTitle("您收到一条未读消息");
				//消息的内容
				EMTextMessageBody body= (EMTextMessageBody) emMessage.getBody();
				//把消息的内容设置到通知的内容中
				bulider.setContentText(body.getMessage());
//                设置一个大图标---下拉之后展示的图标
				bulider.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.book_icon));
				bulider.setContentInfo("来自"+emMessage.getUserName());
				//给图标设置点击事件
				Intent mainActivityIntent=new Intent(getApplicationContext(), MainActivity.class);
				//Intent chatActivityIntent=new Intent(getApplicationContext(), ChatActivity.class);
				//chatActivityIntent.putExtra("contact",emMessage.getUserName());
				//Intent[] intents=new Intent[]{mainActivityIntent,chatActivityIntent};
				//PendingIntent pendingIntent=PendingIntent.getActivities(getApplicationContext(),1,intents,PendingIntent.FLAG_UPDATE_CURRENT);
				//bulider.setContentIntent(pendingIntent);
				Notification notifition=bulider.build();
				NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				manager.notify(1,notifition);
			}

			@Override
			public void onCmdMessageReceived(List<EMMessage> list) {

			}

			@Override
			public void onMessageRead(List<EMMessage> list) {

			}

			@Override
			public void onMessageDelivered(List<EMMessage> list) {

			}

			@Override
			public void onMessageChanged(EMMessage emMessage, Object o) {

			}
		});


	}
	/**
	 * 判断当前应用是否处于后台
	 * @return
	 */
	private boolean isForegound(){
		//获取Activity管理器
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//获取正在运行的100个应用信息
		List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(100);
		//拿到第一个正在运行的应用信息
		ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
		//获取最上面的Activity
		ComponentName topActivity = runningTaskInfo.topActivity;
		//判断当前应用的包名和 这个Activity的包名 是否相同
		if(topActivity.getPackageName().equals(getPackageName())){
			return false;
		}else{
			return true;
		}
	}
}
