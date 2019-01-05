package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.entity.CategoryBean;
import cn.edu.uzz.activity.book.adapter.HomeAdapter;
import cn.edu.uzz.activity.book.adapter.MenuAdapter;

/**
 * Created by 10616 on 2017/11/23.
 */

public class TypesActivity extends Activity{
	private List<String> menuList = new ArrayList<>();
	private List<String> urlList=new ArrayList<>();
	private List<CategoryBean.DataBean> homeList = new ArrayList<>();
	private List<Integer> showTitle;

	private ListView lv_menu;
	private ListView lv_home;

	private MenuAdapter menuAdapter;
	private HomeAdapter homeAdapter;
	private int currentItem;

	private TextView tv_title;
	private ImageView returnBtn;
	int po=0;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_types);
		Fresco.initialize(this);
		initview();
		loadData();
	}

	private void loadData() {
		String json = getJson(this, "category.json");
		CategoryBean categoryBean = JSONObject.parseObject(json, CategoryBean.class);
		showTitle = new ArrayList<>();
		for (int i = 0; i < categoryBean.getData().size(); i++) {
			CategoryBean.DataBean dataBean = categoryBean.getData().get(i);
			menuList.add(dataBean.getModuleTitle());
			showTitle.add(i);
			homeList.add(dataBean);
		}
		tv_title.setText(categoryBean.getData().get(0).getModuleTitle());

		menuAdapter.notifyDataSetChanged();
		homeAdapter.notifyDataSetChanged();
	}

	private void initview() {
		lv_menu = (ListView) findViewById(R.id.lv_typemenu);
		tv_title = (TextView) findViewById(R.id.tv_titile);
		lv_home = (ListView) findViewById(R.id.lv_home);
		returnBtn= (ImageView) findViewById(R.id.book_return);
		menuAdapter = new MenuAdapter(this,menuList);
		Log.e("BBBB",this+"");
		lv_menu.setAdapter(menuAdapter);

		homeAdapter = new HomeAdapter(this, homeList);
		lv_home.setAdapter(homeAdapter);
		returnBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});

		lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				po=position;
				menuAdapter.setSelectItem(position);
				menuAdapter.notifyDataSetInvalidated();
				tv_title.setText(menuList.get(position));
				lv_home.setSelection(showTitle.get(position));
			}
		});




		lv_home.setOnScrollListener(new AbsListView.OnScrollListener() {
			private int scrollState;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				this.scrollState = scrollState;
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
					return;
				}
				int current = showTitle.indexOf(firstVisibleItem);
//				lv_home.setSelection(current);
				if (currentItem != current && current >= 0) {
					currentItem = current;
					tv_title.setText(menuList.get(currentItem));
					menuAdapter.setSelectItem(currentItem);
					menuAdapter.notifyDataSetInvalidated();
				}
			}
		});

	}
	public static String getJson(Context context, String fileName) {
		StringBuilder stringBuilder = new StringBuilder();
		//获得assets资源管理器
		AssetManager assetManager = context.getAssets();
		//使用IO流读取json文件内容
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					assetManager.open(fileName), "utf-8"));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

}
