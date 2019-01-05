package cn.edu.uzz.activity.book.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.widget.ImageView;


import cn.edu.uzz.activity.book.R;

public class SplashActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//通过这个方法把xml文件描述的布局加载到界面上
		//这一行代码执行之后 activity_splash.xml 中所描述的 控件 都已经转换成java对象了
		setContentView(R.layout.activity_splash);
		//操作Imageview 添加一个动画的效果
		//先要找到这个ImageView
		ImageView iv_splash = (ImageView) findViewById(R.id.iv_splash);
		//findViewById 这个方法的返回值是View  View是所有Android控件的父类
		//创建一个属性动画  参数1 要使用属性动画的控件  参数2 通过动画要修改这个控件的哪个属性
		// 参数.... 可变参数传入 变化的过程 的值
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv_splash, "alpha", 0, 1);
		animator.setDuration(2000); //设置动画的执行时间长度
		animator.start();
		//当动画执行结束之后 要决定跳转到什么页面上
		//给动画添加一个监听器
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				//onAnimationEnd 这个方法不是立刻执行的
				//当动画执行结束的时候 会执行这个方法
				//获取是否和服务器链接的状态
				//boolean connected = EMClient.getInstance().isConnected();
				//之前是否登陆过
				//boolean loggedInBefore = EMClient.getInstance().isLoggedInBefore();
				/*if(connected&&loggedInBefore){
					// 说明已经登录过了 跳转到主页面
					//startActivity 开启一个新的页面 传入的参数 intent 意图
					Intent intent = new Intent();
					//intent描述了要打开哪一个Activity   activity就是上下文
					intent.setClass(SplashActivity.this,MainActivity.class);
					startActivity(intent);
					finish();  //结束当前的页面
				}else{
					//说明没有登陆 来到登陆页面
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this,LoginActivity.class);
					startActivity(intent);
					finish();
				}*/
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
			}
		});


	}
}







