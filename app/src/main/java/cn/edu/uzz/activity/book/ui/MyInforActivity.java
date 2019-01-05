package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.uzz.activity.book.R;

/**
 * Created by 10616 on 2018/2/3.
 */

public class MyInforActivity extends Activity implements View.OnClickListener{
	private TextView account;
	private TextView name;
	private TextView sex;
	private TextView tel;
	private TextView age;
	private TextView addr;
	private TextView truthname;
	private TextView changePwdBtn;
	private Button changeInforBtn;
	private ImageView infor_return;
	private TextView character_num;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infor);
		initView();
	}

	private void initView() {
		account=findViewById(R.id.u_account);
		name=findViewById(R.id.u_name);
		sex=findViewById(R.id.u_sex);
		truthname=findViewById(R.id.u_truthname);
		tel=findViewById(R.id.u_tel);
		age=findViewById(R.id.u_age);
		addr=findViewById(R.id.u_addr);
		changePwdBtn=findViewById(R.id.changepwd);
		changeInforBtn=findViewById(R.id.change_my_infor);
		infor_return=findViewById(R.id.infor_return);
		character_num=findViewById(R.id.u_character_num);
		infor_return.setOnClickListener(this);
		changeInforBtn.setOnClickListener(this);
		changePwdBtn.setOnClickListener(this);
		SharedPreferences pre=getSharedPreferences("user",MODE_PRIVATE);
		if(pre.getString("account","").equals("")){
			Intent intent=new Intent();
			intent.putExtra("title","我的资料");
			intent.setClass(MyInforActivity.this,NoLoginActivity.class);
			startActivity(intent);
			finish();
		}
		account.setText(pre.getString("account",""));
		name.setText(pre.getString("username",""));
		sex.setText(pre.getString("sex",""));
		truthname.setText(pre.getString("truthname",""));
		tel.setText(pre.getString("tel",""));
		age.setText(pre.getString("age",""));
		addr.setText(pre.getString("address",""));
		character_num.setText(pre.getString("character_num",""));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.change_my_infor:
				startActivity(new Intent(MyInforActivity.this,NullActivity.class));
				break;
			case R.id.changepwd:
				startActivity(new Intent(MyInforActivity.this,NullActivity.class));
				break;
			case R.id.infor_return:
				finish();
				break;
			default:
				break;
		}
	}
}
