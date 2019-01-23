package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.edu.uzz.activity.book.R;
import cn.jpush.android.api.JPushInterface;
import xyz.bboylin.universialtoast.UniversalToast;

/**
 * Created by 10616 on 2017/11/2.
 */

public class RegistActivity extends Activity{
    private EditText reg_account;
    private  EditText reg_pwd;
    private EditText reg_confirm_pwd;
    private Button registBtn;
    private  String tel;
    private String account;
    private String sex;
    private String truthname;
    private String address;
    private String age;
	private ProgressDialog mDialog;
	private String character_num="100";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        Intent intent = getIntent();
		account=intent.getStringExtra("account");
        tel= intent.getStringExtra("tel");
		sex=intent.getStringExtra("sex");
		truthname=intent.getStringExtra("truthname");
		address=intent.getStringExtra("address");
		age=intent.getStringExtra("age");
        //Toast.makeText(RegistActivity.this,tel,Toast.LENGTH_SHORT).show();
        init();
    }

    private void init() {
        reg_account= (EditText) findViewById(R.id.input_account);
        reg_pwd= (EditText) findViewById(R.id.input_pwd);
        reg_confirm_pwd= (EditText) findViewById(R.id.repeat_pwd);
        registBtn= (Button) findViewById(R.id.regist);
        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIsLegal()==true) {
                    regist();
                }
            }
        });
    }

    private void regist() {
        final String user_account=reg_account.getText().toString();
        String user_pwd=reg_pwd.getText().toString();
        String con_pwd=reg_confirm_pwd.getText().toString();
        if(!user_pwd.equals(con_pwd)){
			UniversalToast.makeText(this, "两次密码输入不相符", UniversalToast.LENGTH_SHORT).showError();
        }else {
			commitInfor();
        }
    }

	private void commitInfor() {
		RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
		StringRequest stringrequest=new StringRequest(Request.Method.POST, "http://192.168.1.108/Book/registServ", new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				try {
					JSONObject jsonObject=new JSONObject(s);
					int resultCode=jsonObject.getInt("resultCode");
					if (resultCode==1){
						Infor();
						signin(account,"123");
					}else if(resultCode==2){
						UniversalToast.makeText(RegistActivity.this, "已注册，请登录", UniversalToast.LENGTH_SHORT).showWarning();
						startActivity(new Intent(RegistActivity.this,LoginActivity.class));
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(RegistActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
				Log.e("VolleyError---", volleyError.getMessage(), volleyError);
				byte[] htmlBodyBytes = volleyError.networkResponse.data;  //回应的报文的包体内容
				Log.e("VolleyError body---->", new String(htmlBodyBytes), volleyError);
			}
		}){
			protected Map<String,String> getParams(){
				Map<String,String> map=new HashMap<String, String>();
				map.put("account",account);//账号
				map.put("username",reg_account.getText().toString());//昵称
				map.put("sex",sex);//性别
				map.put("tel",tel);//电话号码
				map.put("address",address);//住址
				map.put("age",age);//年龄
				map.put("password",reg_pwd.getText().toString());//密码
				map.put("truthname",truthname);//真实姓名
				map.put("character_num",character_num);//信用积分
				Log.e("num error",character_num);
				return map;
			}
		};
		stringrequest.setTag("reg_infor");
		requestQueue.add(stringrequest);
	}

	private boolean checkIsLegal() {
        String user_account=reg_account.getText().toString();
        String user_pwd=reg_pwd.getText().toString();
        String con_pwd=reg_confirm_pwd.getText().toString();
        if (user_account.equals("")) {
			UniversalToast.makeText(this, "请填写昵称", UniversalToast.LENGTH_SHORT).showWarning();
            return false;
        }
        if (user_pwd.equals("")) {
			UniversalToast.makeText(this, "请填写密码", UniversalToast.LENGTH_SHORT).showWarning();
            return false;
        }
        if (con_pwd.equals("")) {
			UniversalToast.makeText(this, "请填写确认密码", UniversalToast.LENGTH_SHORT).showWarning();
            return false;
        }

        return true;
    }

	private void Infor() {
		SharedPreferences pre=getSharedPreferences("user",MODE_PRIVATE);
		SharedPreferences.Editor editor=pre.edit();
		editor.putString("username",reg_account.getText().toString());
		editor.putString("account",account);
		editor.putString("sex",sex);
		editor.putString("truthname",truthname);
		editor.putString("tel",tel);
		editor.putString("address",address);
		editor.putString("age",age);
		editor.putString("character_num",character_num);
		editor.commit();
	}
	private void signin(final String username,final String password) {
		// 注册是耗时过程，所以要显示一个dialog来提示下用户
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("注册中，请稍后...");
		mDialog.show();


		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					EMClient.getInstance().createAccount(username, password);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (!RegistActivity.this.isFinishing()) {
								mDialog.dismiss();
							}
							EMClient.getInstance().contactManager().aysncAddContact("abcde", "AAAAAA", new EMCallBack() {
								@Override
								public void onSuccess() {
									Log.d("BBBB", "添加成功");
								}

								@Override
								public void onError(int i, String s) {
									Log.d("BBBB", "加好友失败" + s);
								}

								@Override
								public void onProgress(int i, String s) {

								}
							});
							UniversalToast.makeText(RegistActivity.this, "注册成功", UniversalToast.LENGTH_SHORT).showSuccess();
							login(username);

						}
					});
				} catch (final HyphenateException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (!RegistActivity.this.isFinishing()) {
								mDialog.dismiss();
							}
							/**
							 * 关于错误码可以参考官方api详细说明
							 * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
							 */
							int errorCode = e.getErrorCode();
							String message = e.getMessage();
							Log.d("lzan13", String.format("sign up - errorCode:%d, errorMsg:%s", errorCode, e.getMessage()));
							switch (errorCode) {
								// 网络错误
								case EMError.NETWORK_ERROR:
									UniversalToast.makeText(RegistActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
									//Toast.makeText(RegistActivity.this, "网络错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
									break;
								// 用户已存在
								case EMError.USER_ALREADY_EXIST:
									UniversalToast.makeText(RegistActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
									//Toast.makeText(RegistActivity.this, "用户已存在 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
									break;
								// 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
								case EMError.USER_ILLEGAL_ARGUMENT:
									UniversalToast.makeText(RegistActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
									//Toast.makeText(RegistActivity.this, "参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
									break;
								// 服务器未知错误
								case EMError.SERVER_UNKNOWN_ERROR:
									UniversalToast.makeText(RegistActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
									//Toast.makeText(RegistActivity.this, "服务器未知错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
									break;
								case EMError.USER_REG_FAILED:
									UniversalToast.makeText(RegistActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
									//Toast.makeText(RegistActivity.this, "账户注册失败 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
									break;
								default:
									UniversalToast.makeText(RegistActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
									//Toast.makeText(RegistActivity.this, "ml_sign_up_failed code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
									break;
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void login(final String account){
		EMClient.getInstance().login(account, "123", new EMCallBack() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						startActivity(new Intent(RegistActivity.this, MainActivity.class));
						JPushInterface.setAlias(RegistActivity.this,account,null);
						Log.e("BBBB","修改成功");
					}
				});
				Intent intent=new Intent();
				intent.setClass(RegistActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String error) {
				runOnUiThread(new Runnable() {
					public void run() {
						UniversalToast.makeText(RegistActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
					}
				});
			}
		});
	}
}
