package cn.edu.uzz.activity.book.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import cn.edu.uzz.activity.book.adapter.RentingAdapter;
import cn.edu.uzz.activity.book.entity.Rent;

public class RentingActivity extends AppCompatActivity implements View.OnClickListener{
	private String account;
	private ListView mListView;
	private List<Rent> book_list;
	private RentingAdapter rentingAdapter;
	private static  String URL="http://123.206.230.120/Book/getrentingServ?account=";
	private TextView fail_text;
	private ImageView fail_img;
	private ImageView renting_return;
	private JSONArray jsonArray;
	private String jsonString;
	private int length;
	NewsAsyncTask newsAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_renting);
		initView();
		SharedPreferences pre=getSharedPreferences("user",MODE_PRIVATE);
		account=pre.getString("account","");
		if(account.equals("")){
			Intent intent=new Intent();
			intent.putExtra("title","正在借阅");
			intent.setClass(RentingActivity.this,NoLoginActivity.class);
			startActivity(intent);
			finish();
		}
		newsAsyncTask.execute(URL+account);
	}

	private void initView() {
		renting_return= (ImageView) findViewById(R.id.renting_return);
		renting_return.setOnClickListener(this);
		mListView= (ListView) findViewById(R.id.renting_list);
		fail_text= (TextView) findViewById(R.id.renting_fail_text);
		fail_img= (ImageView) findViewById(R.id.renting_fail_img);
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
			case R.id.renting_return:
				finish();
				overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
				break;
			default:
				break;
		}
	}

	class NewsAsyncTask extends AsyncTask<String, Void, List<Rent>> implements AdapterView.OnItemClickListener{

		@Override
		protected List<Rent> doInBackground(String... strings) {
			return getJsonDate(strings[0]);
		}

		@Override
		protected void onPostExecute(List<Rent> book) {
			super.onPostExecute(book);
			rentingAdapter=new RentingAdapter(RentingActivity.this,book,mListView);
			mListView.setAdapter(rentingAdapter);
			mListView.setOnItemClickListener(this);
			setListViewHeightBasedOnChildren(mListView);
		}

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
			Rent obj=book_list.get(position);
			Intent intent=new Intent();
			intent.putExtra("type",obj.getBooktype());
			intent.putExtra("id",obj.getBookid());
			intent.putExtra("enddate",obj.getEndtime());
			intent.setClass(RentingActivity.this,RentingItemActivity.class);//后期改成特殊的 book页面
			startActivity(intent);
			overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
		}
	}

	private List<Rent> getJsonDate(String URL) {
		book_list=new ArrayList<>();
		try {
			jsonString=readStream(new URL(URL).openStream());
			JSONObject jsonObject;
			Rent books;
			try {
				jsonObject=new JSONObject(jsonString);
				jsonArray=jsonObject.getJSONArray("json");
				length=jsonArray.length();
				if (jsonArray.length()!=0){
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
					books=new Rent();
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
