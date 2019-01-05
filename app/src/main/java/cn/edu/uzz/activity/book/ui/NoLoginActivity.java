package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.uzz.activity.book.R;

/**
 * Created by 10616 on 2017/12/10.
 */

public class NoLoginActivity extends Activity{
	private TextView title;
	private ImageView faile_return;
	private Button goto_login;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_fail);
		title= (TextView) findViewById(R.id.change_title);
		String chage_t=getIntent().getStringExtra("title");
		title.setText(chage_t);

		goto_login=findViewById(R.id.goto_login);
		goto_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(NoLoginActivity.this,LoginActivity.class));
				finish();
				overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
			}
		});

		faile_return= (ImageView) findViewById(R.id.fail_return);
		faile_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
