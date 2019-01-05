package cn.edu.uzz.activity.book.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.entity.Books;
import cn.edu.uzz.activity.book.util.BitmapCache;
import xyz.bboylin.universialtoast.UniversalToast;

public class RentingItemActivity extends AppCompatActivity implements View.OnClickListener{
	private NetworkImageView book_pic;
	private TextView book_name;
	private TextView book_writer;
	private TextView book_publish;
	private TextView book_version;
	private TextView book_isbn;
	private TextView book_price;
	private TextView book_time;
	private ImageView item_return;
	private TextView like_word;
	String image_name;
	private LinearLayout book_like;
	private ImageButton book_like_image;
	private TextView book_like_word;
	private Button book_sub;
	private Button book_rent;
	int type;
	int id;
	String account;
	String bookname;
	Books book=new Books();
	private static String  substatus;
	private static String rentstatus;
	StringBuffer time;
	private TextView startCount;
	private int recLen;
	private Timer timer = new Timer();
	private String enddate;
	String t;

	Date now1=new Date();
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String now;
	Date d1;
	Date d2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_renting_item);
		initView();
	}

	private void initView() {
		book_pic = (NetworkImageView) findViewById(R.id.renting_item_image);
		book_name = (TextView) findViewById(R.id.renting_item_name);
		book_writer = (TextView) findViewById(R.id.renting_item_writer);
		book_publish = (TextView) findViewById(R.id.renting_item_publish);
		book_version = (TextView) findViewById(R.id.renting_item_version);
		book_isbn = (TextView) findViewById(R.id.renting_item_isbn);
		book_price = (TextView) findViewById(R.id.renting_item_price);
		book_time = (TextView) findViewById(R.id.renting_item_time);
		item_return = (ImageView) findViewById(R.id.renting_item_return);
		book_like = (LinearLayout) findViewById(R.id.renting_item_like);
		book_like_word = (TextView) findViewById(R.id.renting_item_like_word);
		book_like_image = (ImageButton) findViewById(R.id.renting_item_like_image);
		book_sub = (Button) findViewById(R.id.renting_item_subscribe);
		book_rent = (Button) findViewById(R.id.renting_item_rent);
		like_word= (TextView) findViewById(R.id.renting_item_like_word);
		startCount=findViewById(R.id.renting_time);



		Intent intent=getIntent();
		type=intent.getIntExtra("type",0);
		id=intent.getIntExtra("id",0);
		enddate=intent.getStringExtra("enddate");
		SharedPreferences pre=getSharedPreferences("user",MODE_PRIVATE);
		account=pre.getString("account","");

		getBook(type,id);


		//设置点击监听
		item_return.setOnClickListener(this);
		book_like.setOnClickListener(this);
		book_rent.setOnClickListener(this);
		book_sub.setOnClickListener(this);


		//1.检测该用户是否收藏此书，若已收藏，则显示已收藏
		//2.检测书籍状态，显示是否被借阅或被预定
		check();
		Time();
		timer.schedule(task, 1000, 1000);
	}

	private void Time() {
		now=df.format(now1);
		try {
			d1=df.parse(now);
			d2=df.parse(enddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(d1.getTime()<d2.getTime()){
			//说明用户借书超期
			long l=d2.getTime()-d1.getTime();
			recLen= (int) l;
		}else{
			startCount.setText("此书已到期，请尽快与管理员联系");
		}
	}

	private void getBook(final int type, final int id) {
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		final StringRequest request = new StringRequest(Request.Method.POST,
				"http://123.206.230.120/Book/getbookServ" ,  new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					int resultCode = jsonObject.getInt("resultCode");
					JSONObject books=jsonObject.getJSONObject("books");
					if (resultCode ==0) {
						book_name.setText("书名：" + books.getString("name"));
						bookname=books.getString("name");
						book_writer.setText("作者：" + books.getString("writer"));
						book_publish.setText("出版社：" + books.getString("publish"));
						book_version.setText(books.getString("version"));
						book_isbn.setText(books.getString("ISBN"));
						book_price.setText("价格：" + books.getString("price"));
						book_time.setText("日期：" + books.getString("time"));
						image_name = "http://123.206.230.120/Book/images/img/"+books.getString("picture");
						getImage(image_name);//获取照片
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(RentingItemActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		}){
			protected Map<String,String> getParams() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("bookid", id + "");
				map.put("booktype", type + "");
				return map;
			}
		};
		//3请求加入队列
		request.setTag("specialbook");
		queue.add(request);

	}

	private void getImage(String image_name) {
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
		book_pic.setDefaultImageResId(R.drawable.ic_launcher);
		book_pic.setErrorImageResId(R.drawable.ic_launcher);
		book_pic.setImageUrl(image_name,
				imageLoader);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.renting_item_return:
				timer.cancel();
				finish();
				overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
				break;
			case R.id.renting_item_like:
				if(like_word.getText().toString().equals("收藏")){getLikeBook();}
				else {cancelLikeBook();}
				break;
			case R.id.renting_item_rent:
				if (book_rent.getText().toString().equals("已借阅")){
					UniversalToast.makeText(RentingItemActivity.this, "抱歉，此书已被借阅", UniversalToast.LENGTH_SHORT).showWarning();
				}else {
					Rent();
				}
				break;
			case R.id.renting_item_subscribe:
				if(checksub()==1){
					UniversalToast.makeText(RentingItemActivity.this, "预定成功", UniversalToast.LENGTH_SHORT, UniversalToast.CLICKABLE)
							.setClickCallback("查看", new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									startActivity(new Intent(RentingItemActivity.this,MySubActivity.class));
								}
							})
							.showSuccess();
				}else if(checksub()==2){
					UniversalToast.makeText(RentingItemActivity.this, "抱歉，此书已被预订", UniversalToast.LENGTH_SHORT).showWarning();
				}else if(checksub()==3){
					UniversalToast.makeText(RentingItemActivity.this, "抱歉，此书正在被借阅", UniversalToast.LENGTH_SHORT).showWarning();
				}
				break;
			default:
				break;
		}
	}

	private void check() {
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		StringRequest request = new StringRequest(Request.Method.POST,
				"http://123.206.230.120/Book/checklikeServ",  new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					int resultCode = jsonObject.getInt("resultCode");
					substatus=jsonObject.getString("subscribe");
					rentstatus=jsonObject.getString("rentstatus");
					if (resultCode == 1) {
						book_like_image.setImageResource(R.drawable.book_like_selected);
						book_like_word.setText("已收藏");
						book_sub.setText(substatus);
						book_rent.setText(rentstatus);
					}else if (resultCode==2){
						book_like_image.setImageResource(R.drawable.book_like_normal);
						book_like_word.setText("收藏");
						book_sub.setText(substatus);
						book_rent.setText(rentstatus);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(RentingItemActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		}){
			protected Map<String,String> getParams() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("account", account);
				map.put("id", id + "");
				map.put("type", type + "");
				return map;
			}
		};
		//3请求加入队列
		request.setTag("book");
		queue.add(request);
	}

	protected void showDatePickDlg() {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog datePickerDialog = new DatePickerDialog(RentingItemActivity.this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				time=new StringBuffer().append(year).append("-").append(monthOfYear+1).append("-").append(dayOfMonth);
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date dateend=sdf.parse(String.valueOf(time));
					String now=sdf.format(new Date());
					Date datenow=sdf.parse(now);
					if (dateend.getTime()<datenow.getTime()){
						UniversalToast.makeText(RentingItemActivity.this, "请选择正确的日期", UniversalToast.LENGTH_SHORT).showWarning();
					}else{
						//Toast.makeText(RentingItemActivity.this,"您选择的时间为："+time,Toast.LENGTH_SHORT).show();//测试阶段使用 ，成功后注释掉
						rentBook();
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.show();
	}

	private void Rent() {
		if (book_sub.getText().toString().equals("预定")){
			showDatePickDlg();
		}else {
			checkRent();
		}
	}

	private void checkRent() {
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		StringRequest request = new StringRequest(Request.Method.POST,
				"http://123.206.230.120/Book/checksubServ",  new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					int resultCode = jsonObject.getInt("resultCode");
					if (resultCode == 1) {
						showDatePickDlg();
					}else if (resultCode==2){
						UniversalToast.makeText(RentingItemActivity.this, "此书已被其他用户预定", UniversalToast.LENGTH_SHORT).showWarning();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(RentingItemActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		}){
			protected Map<String,String> getParams() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("account", account);
				map.put("bookid", id + "");
				map.put("booktype", type + "");
				return map;
			}
		};
		//3请求加入队列
		request.setTag("book");
		queue.add(request);
	}

	private void rentBook() {
		final String  timeend=time.toString();
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		StringRequest request = new StringRequest(Request.Method.POST, "http://123.206.230.120/Book/rentcarServ", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					int resultCode = jsonObject.getInt("resultCode");
					if (resultCode == 1) {
						UniversalToast.makeText(RentingItemActivity.this, "添加成功", UniversalToast.LENGTH_SHORT, UniversalToast.CLICKABLE)
								.setClickCallback("查看", new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										startActivity(new Intent(RentingItemActivity.this,CarActivity.class));
									}
								})
								.showSuccess();
					}else if (resultCode==2){
						UniversalToast.makeText(RentingItemActivity.this, "添加到借阅车失败，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
					}else if (resultCode==3){
						UniversalToast.makeText(RentingItemActivity.this, "此书已被添加，勿重复添加", UniversalToast.LENGTH_SHORT).showWarning();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(RentingItemActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		}){
			protected Map<String,String> getParams() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("account", account);
				map.put("bookid", id+"");
				map.put("booktype", type+"");
				map.put("bookname", bookname);
				map.put("timeend", timeend);
				map.put("picture", image_name);
				return map;
			}
		};
		//3请求加入队列
		request.setTag("book");
		queue.add(request);
	}

	private int checksub() {
		if(book_sub.getText().toString().equals("已预订")){
			return 2;
		}else if (book_rent.getText().toString().equals("已借阅")){
			return 3;
		}else {
			getSubscribe();//此书未被预定，进行预定操作
			return 1;
		}
	}

	private void getSubscribe() {
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		StringRequest request = new StringRequest(Request.Method.POST,
				"http://123.206.230.120/Book/subscribeServ" ,  new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					int resultCode = jsonObject.getInt("resultCode");
					if (resultCode == 1||resultCode==2) {
						book_sub.setText("已预订");
					}else if (resultCode==3){
						UniversalToast.makeText(RentingItemActivity.this, "预定失败，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(RentingItemActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		}){
			protected Map<String,String> getParams() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("account", account);
				map.put("bookid", id+"");
				map.put("booktype", type+"");
				map.put("bookname", bookname);
				map.put("picture", image_name);
				return map;
			}
		};
		//3请求加入队列
		request.setTag("book");
		queue.add(request);
	}

	private void cancelLikeBook() {
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		StringRequest request = new StringRequest(Request.Method.POST,
				"http://123.206.230.120/Book/cancellikeServ?account=" + account +  "&bookid=" +id+ "&booktype=" + type, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					int resultCode = jsonObject.getInt("resultCode");
					if (resultCode == 1||resultCode==3) {
						UniversalToast.makeText(RentingItemActivity.this, "取消收藏成功", UniversalToast.LENGTH_SHORT).showSuccess();
						book_like_image.setImageResource(R.drawable.book_like_normal);
						book_like_word.setText("收藏");
					}else if (resultCode==2){
						UniversalToast.makeText(RentingItemActivity.this, "取消收藏失败，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(RentingItemActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		}){
			protected Map<String,String> getParams() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("account", account);
				map.put("bookid", id+"");
				map.put("booktype", type+"");
				return map;
			}
		};
		//3请求加入队列
		request.setTag("book");
		queue.add(request);

	}

	public boolean getLikeBook() {
		if (account==""){
			UniversalToast.makeText(RentingItemActivity.this, "请先登录", UniversalToast.LENGTH_SHORT, UniversalToast.CLICKABLE)
					.setClickCallback("查看", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							startActivity(new Intent(RentingItemActivity.this,LoginActivity.class));
						}
					})
					.showWarning();
			return false;
		}
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		StringRequest request = new StringRequest(Request.Method.POST,
				"http://123.206.230.120/Book/likeServ",  new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					int resultCode = jsonObject.getInt("resultCode");
					if (resultCode == 1) {
						UniversalToast.makeText(RentingItemActivity.this, "收藏成功", UniversalToast.LENGTH_SHORT, UniversalToast.CLICKABLE)
								.setClickCallback("查看", new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										startActivity(new Intent(RentingItemActivity.this,LikeListAvtivity.class));
									}
								})
								.showSuccess();
						book_like_image.setImageResource(R.drawable.book_like_selected);
						book_like_word.setText("已收藏");
					}else if (resultCode==2){
						UniversalToast.makeText(RentingItemActivity.this, "收藏失败，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Log.e("VolleyError---", volleyError.getMessage(), volleyError);
				byte[] htmlBodyBytes = volleyError.networkResponse.data;  //回应的报文的包体内容
				Log.e("VolleyError body---->", new String(htmlBodyBytes), volleyError);
				UniversalToast.makeText(RentingItemActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		}){
			protected Map<String,String> getParams() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("account", account);
				map.put("bookid", id+"");
				map.put("booktype", type+"");
				map.put("bookname", bookname);
				map.put("picture", image_name);
				return map;
			}
		};
		//3请求加入队列
		request.setTag("book");
		queue.add(request);
		return true;
	}


	final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
				case 1:
					//startCount.setText(print(recLen));
					startCount.setText(t);
					if(recLen < 0){
						timer.cancel();
						startCount.setText("此书已到期，请尽快与管理员联系");
					}
			}
		}
	};

	private void print(int rec) {
		long day=rec/(24*60*60*1000);
		long hour=(rec/(60*60*1000)-day*24-13);
		long min=((rec/(60*1000))-day*24*60-hour*60-780);
		long s=(rec/1000-day*24*60*60-hour*60*60-min*60-780*60);
		t=""+day+"天"+hour+"小时"+min+"分"+s+"秒";
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			recLen=recLen-1000;
			print(recLen);
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};
}
