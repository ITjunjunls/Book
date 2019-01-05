package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.util.mSearchLayout;

/**
 * Created by 10616 on 2017/11/17.
 */

public class Tab02 extends Fragment{
	private mSearchLayout msearchLy;
	private static  String URL="http://123.206.230.120/Book/searchServ?searchName=";
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab02, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		msearchLy= (mSearchLayout) getView().findViewById(R.id.msearchlayout);
		initData();
	}

	protected void initData() {
		String shareData = "";
		List<String> skills = Arrays.asList(shareData.split(","));

		String shareHotData ="使女的故事,基因传,2018年养生台历";
		List<String> skillHots = Arrays.asList(shareHotData.split(","));

		msearchLy.initData(skills, skillHots, new mSearchLayout.setSearchCallBackListener() {
			@Override
			public void Search(String str) {
				//进行或联网搜索
				Intent intent=new Intent();
				intent.putExtra("searchName",str);
				intent.setClass(getActivity(),SearchResultActivity.class);
				startActivity(intent);
				((Activity) getContext()).overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
			}
			@Override
			public void Back() {
				//finish();
			}

			@Override
			public void ClearOldData() {
				//清除历史搜索记录  更新记录原始数据
			}
			@Override
			public void SaveOldData(ArrayList<String> AlloldDataList) {
				//保存所有的搜索记录
			}
		});
	}
}
