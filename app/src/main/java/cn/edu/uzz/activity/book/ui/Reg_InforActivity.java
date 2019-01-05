package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import cn.edu.uzz.activity.book.R;

/**
 * Created by 10616 on 2017/11/4.
 */

public class Reg_InforActivity extends Activity {
	private EditText user_name;
	private String user_tel;
	private RadioGroup sex_user;
	private EditText user_address;
	private EditText user_age;
	String user_sex = "男";
	private Button confirmBtn;
	String user_account;
	private String username;
	private String useraddress;
	private String userage;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reginfor);
		user_name = (EditText) findViewById(R.id.input_name);
		user_address = (EditText) findViewById(R.id.input_address);
		user_age = (EditText) findViewById(R.id.input_age);
		sex_user = (RadioGroup) findViewById(R.id.input_sex);
		confirmBtn = (Button) findViewById(R.id.confirm_infor);
		Intent intent = getIntent();
		user_account = intent.getStringExtra("phone");
		user_tel = intent.getStringExtra("phone");
		sex_user.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				if (checkedId == R.id.radio_male) {
					user_sex = "男";
				} else if (checkedId == R.id.radio_female) {
					user_sex = "女";
				}
			}
		});
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				next();
			}
		});
	}

	private void next() {
		check();
		Intent intent = new Intent();
		intent.putExtra("account", user_account);
		intent.putExtra("tel", user_tel);
		intent.putExtra("sex", user_sex);
		intent.putExtra("truthname", username);
		intent.putExtra("address", useraddress);
		intent.putExtra("age", userage);
		intent.setClass(Reg_InforActivity.this, RegistActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}

	private void check() {
		if (user_name.getText().toString().equals("")) {
			username = "无";
		} else {
			username = user_name.getText().toString();
		}
		if (user_address.getText().toString().equals("")) {
			useraddress = "无";
		} else {
			useraddress = user_address.getText().toString();
		}
		if (user_age.getText().toString().equals("")) {
			userage = "0";
		} else {
			userage = user_age.getText().toString();
		}
	}
}
