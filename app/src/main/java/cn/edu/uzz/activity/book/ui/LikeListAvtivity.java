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
import cn.edu.uzz.activity.book.adapter.LikeAdapter;
import cn.edu.uzz.activity.book.entity.Like;
import xyz.bboylin.universialtoast.UniversalToast;


/**
 * Created by 10616 on 2017/12/3.
 */

public class LikeListAvtivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener {

    List<Like> book_list;
    private static  String URL="http://123.206.230.120/Book/getlikeServ?account=";
    String account;
    private ImageView like_return;
	LikeAdapter adapter;
	private TextView like_fail_text;
	private ImageView like_fail_img;
	private ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylike);
        initView();
        SharedPreferences pre=getSharedPreferences("user",MODE_PRIVATE);
		account=pre.getString("account","");
        if(account.equals("")){
        	Intent intent=new Intent();
        	intent.putExtra("title","我的收藏");
        	intent.setClass(LikeListAvtivity.this,NoLoginActivity.class);
        	startActivity(intent);
        	finish();
		}
        new NewsAsyncTask().execute(URL+account);
        listView.setOnItemClickListener(this);
    }

	private void initView() {
    	like_return= (ImageView) findViewById(R.id.like_return);
		listView= (ListView) findViewById(R.id.likelist);
		like_fail_text=findViewById(R.id.like_fail_text);
		like_fail_img=findViewById(R.id.like_fail_img);
		like_return.setOnClickListener(this);
	}

	@Override
    public void onClick(View view) {
		switch (view.getId()){
			case R.id.like_return:
				finish();
				overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
				break;
			default:
				break;
		}
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {//点击进入书籍详情界面
		Like obj=book_list.get(position);
		Intent intent=new Intent();
		intent.putExtra("type",obj.getBooktype());
		intent.putExtra("id",obj.getBookid());
		intent.setClass(LikeListAvtivity.this,SpecialItemActivity.class);//后期改成特殊的 book页面
		startActivity(intent);
		overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
	}

	class NewsAsyncTask extends AsyncTask<String, Void, List<Like>> implements LikeAdapter.CallBack ,AdapterView.OnItemClickListener{

        @Override
        protected List<Like> doInBackground(String... strings) {
            return getJsonDate(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Like> book) {
            super.onPostExecute(book);
            adapter=new LikeAdapter(LikeListAvtivity.this,book,listView, this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
			setListViewHeightBasedOnChildren(listView);
        }

		@Override
		public void click(View v) {//点击取消收藏按钮，通过getTag（）获得position
			int position=(Integer) v.getTag();
			int type=book_list.get(position).getBooktype();
			int id=book_list.get(position).getBookid();
			cancelLikeBook(account,type,id,position);
		}

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {//点击item，通过position获取type和id，进入详情页后加载信息
			Like obj=book_list.get(position);
			Intent intent=new Intent();
			intent.putExtra("type",obj.getBooktype());
			intent.putExtra("id",obj.getBookid());
			intent.setClass(LikeListAvtivity.this,SpecialItemActivity.class);//后期改成特殊的 book页面
			startActivity(intent);
			overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
		}
	}

    private List<Like> getJsonDate(String URL) {
        book_list=new ArrayList<>();
		if (account.equals("")){
			return book_list;
		}
        try {
            String jsonString=readStream(new URL(URL).openStream());
            JSONObject jsonObject;
            Like likes;
            try {
				jsonObject=new JSONObject(jsonString);
                JSONArray jsonArray=jsonObject.getJSONArray("json");
                if (jsonArray.length()!=0){
					new Thread(new Runnable() {
						@Override
						public void run() {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									like_fail_img.setVisibility(View.INVISIBLE);
									like_fail_text.setVisibility(View.INVISIBLE);
								}
							});
						}
					}).start();
				}
                for (int i=0;i<jsonArray.length();i++){
                    jsonObject=jsonArray.getJSONObject(i);
                    likes=new Like();
                    likes.setAccount(jsonObject.getString("account"));
                    likes.setBookname(jsonObject.getString("bookname"));
                    likes.setBooktype(jsonObject.getInt("booktype"));
                    likes.setBookid(jsonObject.getInt("bookid"));
                    likes.setPicture(jsonObject.getString("picture"));
                    book_list.add(likes);
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

	private void cancelLikeBook(String account, int type, int id, final int position) {
		//1创建请求队列
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		//2创建请求
		JsonObjectRequest request = new JsonObjectRequest(
				"http://123.206.230.120/Book/cancellikeServ?account=" + account +  "&bookid=" +id+ "&booktype=" + type, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					int resultCode = response.getInt("resultCode");
					if (resultCode == 1||resultCode==3) {
						UniversalToast.makeText(LikeListAvtivity.this, "取消收藏成功", UniversalToast.LENGTH_SHORT).showSuccess();
						book_list.remove(position);
						adapter.refresh(book_list);
					}else if (resultCode==2){
						UniversalToast.makeText(LikeListAvtivity.this, "取消收藏失败，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				UniversalToast.makeText(LikeListAvtivity.this, "网络异常，请稍后再试", UniversalToast.LENGTH_SHORT).showError();
			}
		});
		//3请求加入队列
		request.setTag("likelist");
		queue.add(request);
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 100) {
			}
		}
	};
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
		params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() +1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
}
