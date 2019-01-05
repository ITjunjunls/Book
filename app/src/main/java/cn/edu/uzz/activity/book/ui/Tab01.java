package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.youth.banner.Banner;

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
import java.util.Random;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.adapter.MyAdapter;
import cn.edu.uzz.activity.book.entity.Books;
import cn.edu.uzz.activity.book.loader.GlideImageLoader;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 10616 on 2017/11/17.
 */

public class Tab01 extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{
	private ListView mListView;
	private static  String URL="http://123.206.230.120/Book/getoldlistServ?type1=";
	List<Books> book_list=new ArrayList<>();;
	private ImageView saomaBtn;
	private ImageView booktypesBtn;
	private ImageView newbookBtn;
	int type1;
	private Banner banner;
	private List<String> images=new ArrayList<>();
	private PullToRefreshLayout pullToRefreshLayout;
	private MyAdapter adapter;
	NewsAsyncTask newsAsyncTask=new NewsAsyncTask();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.tab01, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		mListView= (ListView) getView().findViewById(R.id.listviewmain);
		type1=getbookstype1();
		newsAsyncTask.execute(URL+type1);
		mListView.setOnItemClickListener(this);
	}

	private void initView() {
		saomaBtn= (ImageView) getView().findViewById(R.id.saoyisao);
		booktypesBtn= (ImageView) getView().findViewById(R.id.booktypes);
		newbookBtn= (ImageView) getView().findViewById(R.id.newbook);
		banner= (Banner) getView().findViewById(R.id.banner);
		pullToRefreshLayout = (PullToRefreshLayout) getView().findViewById(R.id.pull_main);
		saomaBtn.setOnClickListener(this);
		booktypesBtn.setOnClickListener(this);
		newbookBtn.setOnClickListener(this);
		images.add("http://123.206.230.120/Book/images/lbt/1.jpg");
		images.add("http://123.206.230.120/Book/images/lbt/3.jpg");
		images.add("http://123.206.230.120/Book/images/lbt/2.jpg");
		//设置图片加载器
		banner.setImageLoader(new GlideImageLoader());
		//设置图片集合
		banner.setImages(images);
		//banner设置方法全部调用完毕时最后调用
		banner.start();
		pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
			@Override
			public void refresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						pullToRefreshLayout.finishRefresh();
					}
				},2000);
			}

			@Override
			public void loadMore() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {

						pullToRefreshLayout.finishLoadMore();
					}
				},2000);
			}

		});
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
		Books obj=book_list.get(position);
		Intent intent=new Intent();
		intent.putExtra("book",obj);
		intent.setClass(getActivity(),BookItemActivity.class);
		startActivity(intent);
		((Activity) getContext()).overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.booktypes:
				startActivity(new Intent(getActivity(),TypesActivity.class));
				((Activity) getContext()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				((Activity) getContext()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				break;
			case R.id.newbook:
				startActivity(new Intent(getActivity(),NullActivity.class));
				((Activity) getContext()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				((Activity) getContext()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				break;
			case R.id.saoyisao:
				startActivityForResult(new Intent(getActivity(), CaptureActivity.class),0);
				((Activity) getContext()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				((Activity) getContext()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				break;
			default:
				break;
		}
	}


	class NewsAsyncTask extends AsyncTask<String, Void, List<Books>> {

		@Override
		protected List<Books> doInBackground(String... strings) {
			return getJsonDate(strings[0]);
		}

		@Override
		protected void onPostExecute(List<Books> book) {
			super.onPostExecute(book);
			adapter=new MyAdapter(getActivity(),book,mListView);
			mListView.setAdapter(adapter);
			setListViewHeightBasedOnChildren(mListView);
		}
	}

	private List<Books> getJsonDate(String URL) {
		try {
			String jsonString=readStream(new URL(URL).openStream());
			JSONObject jsonObject;
			Books books;
			try {
				jsonObject=new JSONObject(jsonString);
				JSONArray jsonArray=jsonObject.getJSONArray("json");
				for (int i=0;i<jsonArray.length();i++){
					jsonObject=jsonArray.getJSONObject(i);
					books=new Books();
					books.setId(jsonObject.getInt("id"));
					books.setName(jsonObject.getString("name"));
					books.setPublish(jsonObject.getString("publish"));
					books.setVersion(jsonObject.getString("version"));
					books.setPicture("http://123.206.230.120/Book/images/img/"+jsonObject.getString("picture"));
					books.setRentstatus(jsonObject.getString("rentstatus"));
					books.setSubstatus(jsonObject.getString("substatus"));
					books.setPrice(jsonObject.getString("price"));
					books.setTime(jsonObject.getString("time"));
					books.setISBN(jsonObject.getString("ISBN"));
					books.setWriter(jsonObject.getString("writer"));
					books.setType(jsonObject.getInt("type"));
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

	public int getbookstype1(){
		int max=10;
		int min=0;
		Random random = new Random();
		int s = random.nextInt(max)%(max-min+1) + min;
		Log.e("BBBB","type1 id "+s);
		return s;
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		JSONObject jsonObject;
		if(resultCode==RESULT_OK){
			Bundle bundle=data.getExtras();
			String result=bundle.getString("result");
			try {
				jsonObject=new JSONObject(result);
				int booktype=jsonObject.getInt("booktype");
				int bookid=jsonObject.getInt("bookid");
				Intent intent=new Intent();
				intent.putExtra("type",booktype);
				intent.putExtra("id",bookid);
				intent.setClass(getActivity(),SpecialItemActivity.class);
				startActivity(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}



