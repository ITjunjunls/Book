package cn.edu.uzz.activity.book.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mob.MobSDK;

import org.json.JSONObject;

import cn.edu.uzz.activity.book.R;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import xyz.bboylin.universialtoast.UniversalToast;


/**
 * Created by 10616 on 2017/11/4.
 */

public class ForgetPwdActivity extends AppCompatActivity implements View.OnClickListener{
    String APPKEY = "245253300b8c5";
    String APPSECRETE = "b3c33f3aab1477bab646dd6a516d3fda";

    // 手机号输入框
    private EditText inputaccount;

    // 验证码输入框
    private EditText inputCodeEt;

    // 获取验证码按钮
    private Button requestCodeBtn;

    // 注册按钮
    private Button commitBtn;



    //
    int i = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpwd);

        init();


        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 初始化控件
     */
    private void init() {
        inputaccount = (EditText) findViewById(R.id.login_input_account_et);
        inputCodeEt = (EditText) findViewById(R.id.login_input_code_et);
        requestCodeBtn = (Button) findViewById(R.id.login_request_code_btn);
        commitBtn = (Button) findViewById(R.id.login_commit_btn);
        requestCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

        // 启动短信验证sdk

        MobSDK.init(this, APPKEY, APPSECRETE);
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v) {

        /*String phoneNums = phonenum.getText().toString();
        Toast.makeText(ForgetPwdActivity.this,phoneNums,Toast.LENGTH_SHORT).show();*/
        switch (v.getId()) {
            case R.id.login_request_code_btn:
                String stu_id=inputaccount.getText().toString();
                findTel(stu_id);
                break;

            case R.id.login_commit_btn:
                //将收到的验证码和手机号提交再次核对
                SMSSDK.submitVerificationCode("86", inputaccount.getText().toString(), inputCodeEt
                        .getText().toString());
                break;
        }
    }

    /**
     *
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                requestCodeBtn.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                requestCodeBtn.setText("获取验证码");
                requestCodeBtn.setClickable(true);
                i = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
						UniversalToast.makeText(getApplicationContext(), "提交验证码成功", UniversalToast.LENGTH_SHORT).showSuccess();
                        Intent intent = new Intent(ForgetPwdActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        findInfor(inputaccount.getText().toString());
                        finish();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						UniversalToast.makeText(getApplicationContext(), "正在获取验证码", UniversalToast.LENGTH_SHORT).showSuccess();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
		UniversalToast.makeText(this, "手机号码输入有误", UniversalToast.LENGTH_SHORT).showError();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
    /*
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
        String telRegex = "[1][43578]\\d{9}";// "[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4，5、7，8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }



    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    public void findTel(final String account){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        //2创建请求
        JsonObjectRequest request=new JsonObjectRequest(
                "http://123.206.230.120/Book/findtelServ?account="+account , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("code");
                    if (code==1){
						UniversalToast.makeText(ForgetPwdActivity.this, "此号码未注册", UniversalToast.LENGTH_SHORT).showError();
                    	return;
					} else if(code==0){
                        if (!judgePhoneNums(account)) {
                            return;
                        } // 2. 通过sdk发送短信验证
                        SMSSDK.getVerificationCode("86", account);
                        // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                        requestCodeBtn.setClickable(false);
                        requestCodeBtn.setText("重新发送(" + i + ")");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (; i > 0; i--) {
                                    handler.sendEmptyMessage(-9);
                                    if (i <= 0) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                handler.sendEmptyMessage(-8);
                            }
                        }).start();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        //3请求加入队列
        queue.add(request);
    }

    public void findInfor(final String account){
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		JsonObjectRequest request=new JsonObjectRequest(
				"http://123.206.230.120/Book/findinforServ?account="+account , null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				try {
					int code = jsonObject.getInt("resultCode");
					if (code == 0) {
						String username = jsonObject.getString("username");
						String sex = jsonObject.getString("usersex");
						String tel = jsonObject.getString("phone");
						String address = jsonObject.getString("addr");
						String age = jsonObject.getString("userage");
						String name=jsonObject.getString("name");
						SharedPreferences pre=getSharedPreferences("user",MODE_PRIVATE);
						SharedPreferences.Editor editor=pre.edit();
						editor.putString("username",username);
						editor.putString("account",account);
						editor.putString("sex",sex);
						editor.putString("truthname",name);
						editor.putString("tel",tel);
						editor.putString("address",address);
						editor.putString("age",age);
						editor.commit();
					}else{
						UniversalToast.makeText(ForgetPwdActivity.this, "个人信息获取失败", UniversalToast.LENGTH_SHORT).showError();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(ForgetPwdActivity.this, "个人信息获取失败", UniversalToast.LENGTH_SHORT).showError();
			}
		});
		request.setTag("login");
		queue.add(request);
    }


}
