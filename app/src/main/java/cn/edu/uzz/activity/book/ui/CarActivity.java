package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.adapter.CarAdapter;
import cn.edu.uzz.activity.book.entity.RentCar;
import xyz.bboylin.universialtoast.UniversalToast;

/**
 * Created by 10616 on 2017/12/22.
 */

public class CarActivity extends Activity implements View.OnClickListener {

	private Button makeBtn;
	private ListView mListView;
	private List<RentCar> book_list;
	private String account;
	private CarAdapter carAdapter;
	private static  String URL="http://123.206.230.120/Book/getcarServ?account=";
	private TextView fail_text;
	private ImageView fail_img;
	NewsAsyncTask newsAsyncTask;
	private ImageView car_return;
	private int length;
	private JSONArray jsonArray;
	private String jsonString;
	private int symbol=0;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car);
		initView();
		SharedPreferences pre=getSharedPreferences("user",MODE_PRIVATE);
		account=pre.getString("account","");
		if(account.equals("")){
			Intent intent=new Intent();
			intent.putExtra("title","借阅小车");
			intent.setClass(CarActivity.this,NoLoginActivity.class);
			startActivity(intent);
			finish();
		}
		newsAsyncTask.execute(URL+account);
	}

	private void initView() {
		makeBtn= (Button) findViewById(R.id.jinruerweima);
		makeBtn.setOnClickListener(this);
		car_return= (ImageView) findViewById(R.id.car_return);
		car_return.setOnClickListener(this);
		mListView= (ListView) findViewById(R.id.car_list);
		fail_text= (TextView) findViewById(R.id.fail_text);
		fail_img= (ImageView) findViewById(R.id.fail_img);
		newsAsyncTask=new NewsAsyncTask();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 100) {
			}
		}
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.jinruerweima:
				if (Check()){
					Intent intent=new Intent();
					intent.putExtra("booklist",jsonString);
					intent.setClass(CarActivity.this,ErweimaActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
				}else {
					UniversalToast.makeText(CarActivity.this, "您的购物车还没有添加借阅哦", UniversalToast.LENGTH_SHORT).showWarning();
				}
				break;
			case R.id.car_return:
				finish();
				overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
				break;
			default:
				break;
		}
	}

	private boolean  Check() {
		if (symbol==1){
			return true;
		}else {
			return false;
		}
	}


	class NewsAsyncTask extends AsyncTask<String, Void, List<RentCar>> implements CarAdapter.CallBack ,AdapterView.OnItemClickListener{

		@Override
		protected List<RentCar> doInBackground(String... strings) {
			return getJsonDate(strings[0]);
		}

		@Override
		protected void onPostExecute(List<RentCar> book) {
			super.onPostExecute(book);
			carAdapter=new CarAdapter(CarActivity.this,book,mListView,this);
			mListView.setAdapter(carAdapter);
			mListView.setOnItemClickListener(this);
			setListViewHeightBasedOnChildren(mListView);
		}

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
			RentCar obj=book_list.get(position);
			Intent intent=new Intent();
			intent.putExtra("type",obj.getBooktype());
			intent.putExtra("id",obj.getBookid());
			intent.setClass(CarActivity.this,SpecialItemActivity.class);//后期改成特殊的 book页面
			startActivity(intent);
			overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
		}

		@Override
		public void click(View v) {//从借阅车删除
			int position= (int) v.getTag();
			int type=book_list.get(position).getBooktype();
			int id=book_list.get(position).getBookid();
			cancelCarBook(account,type,id,position);
		}
	}

	private void cancelCarBook(String account, int type, int id, final int position) {
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		JsonObjectRequest request = new JsonObjectRequest(
				"http://123.206.230.120/Book/cancelcarServ?account=" + account +  "&bookid=" +id+ "&booktype=" + type, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					int resultCode = response.getInt("resultCode");
					if (resultCode == 1||resultCode==3) {
						UniversalToast.makeText(CarActivity.this, "借阅车删除成功", UniversalToast.LENGTH_SHORT).showSuccess();
						book_list.remove(position);
						carAdapter.refresh(book_list);
					}else if (resultCode==2){
						UniversalToast.makeText(CarActivity.this, "借阅车删除失败，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(CarActivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		});
		//3请求加入队列
		request.setTag("car");
		queue.add(request);
	}

	private List<RentCar> getJsonDate(String URL) {

		book_list=new ArrayList<>();
		try {
			jsonString=readStream(new URL(URL).openStream());
			JSONObject jsonObject;
			RentCar books;
			try {
				jsonObject=new JSONObject(jsonString);
				jsonArray=jsonObject.getJSONArray("json");
				length=jsonArray.length();
				if (jsonArray.length()!=0){
					symbol=1;
					new Thread(new Runnable() {
						@Override
						public void run() {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									fail_img.setVisibility(View.INVISIBLE);
									fail_text.setVisibility(View.INVISIBLE);
								}
							});
						}
					}).start();
				}
				for (int i=0;i<jsonArray.length();i++){
					jsonObject=jsonArray.getJSONObject(i);
					books=new RentCar();
					books.setAccount(jsonObject.getString("account"));
					books.setPicture(jsonObject.getString("picture"));
					books.setBookid(jsonObject.getInt("bookid"));
					books.setBooktype(jsonObject.getInt("booktype"));
					books.setBookanme(jsonObject.getString("bookname"));
					books.setEndtime(jsonObject.getString("enddate"));
					books.setNowdate(jsonObject.getString("nowdate"));
					book_list.add(books);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return book_list;
	}

	private String readStream(InputStream is){
		InputStreamReader isr;
		String result="";
		try {
			String line="";
			isr=new InputStreamReader(is,"utf-8");
			BufferedReader br=new BufferedReader(isr);
			while ((line=br.readLine())!=null){
				result+=line;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	//计算listview高度
	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			// 计算子项View 的宽高
			listItem.measure(0, 0);
			// 统计所有子项的总高度
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() + 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
}
