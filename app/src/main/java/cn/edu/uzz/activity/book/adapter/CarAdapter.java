package cn.edu.uzz.activity.book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.entity.RentCar;
import cn.edu.uzz.activity.book.loader.CarImageLoader;

/**
 * Created by 10616 on 2017/12/16.
 */

public class CarAdapter extends BaseAdapter implements AbsListView.OnScrollListener,View.OnClickListener{
	private List<RentCar> mList;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private CarImageLoader mImageLoader;
	private int mStart,mEnd;
	public static String[] URLS;
	private boolean mFirstIn;
	private CallBack mcallBack;

	public CarAdapter(Context context, List<RentCar> data, ListView listView, CallBack callBack) {
		this.mList = data;
		mcallBack=callBack;
		this.inflater = LayoutInflater.from(context);
		mImageLoader=new CarImageLoader(listView);
		URLS=new String[data.size()];
		for (int i=0;i<data.size();i++){
			URLS[i]=data.get(i).getPicture();
		}
		mFirstIn=true;
		//注册对应的事件
		listView.setOnScrollListener(this);
	}

	public void refresh(List<RentCar> list) {
		mList = list;//传入list，然后调用notifyDataSetChanged方法
		notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		mcallBack.click(view);
	}

	//定义一个接口用于回调点击事件
	public interface CallBack{
		public void click(View v);
	}

	public void onDateChange(ArrayList<RentCar> like_list) {
		this.mList = like_list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RentCar car = mList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_rentcar, null);
			viewHolder.car_book_name= (TextView) convertView.findViewById(R.id.car_bookname);
			viewHolder.car_bookpic= (ImageView) convertView.findViewById(R.id.car_bookpic);
			viewHolder.cancelBtn= (TextView) convertView.findViewById(R.id.cancel_car);
			viewHolder.car_endtime= (TextView) convertView.findViewById(R.id.car_endtime);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.car_bookpic.setImageResource(R.mipmap.ic_launcher);
		String url=mList.get(position).getPicture();
		viewHolder.car_bookpic.setTag(url);
		//new ImageLoader().showImageByAsyncTask(viewHolder.ivIcon,url);
		mImageLoader.showImageByAsyncTask(viewHolder.car_bookpic,url);
		viewHolder.car_book_name.setText(mList.get(position).getBookanme());
		viewHolder.car_endtime.setText("还书日期:"+mList.get(position).getEndtime());
		viewHolder.cancelBtn.setOnClickListener(this);
		viewHolder.cancelBtn.setTag(position);
		return convertView;
	}

	@Override
	public void onScrollStateChanged(AbsListView absListView, int i) {
		if (i==SCROLL_STATE_IDLE){
			//加载可见项
			mImageLoader.loadImages(mStart,mEnd);
		}else{
			//停止加载任务
			mImageLoader.cancelAllTasks();
		}
	}

	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int i2) {
		mStart=firstVisibleItem;
		mEnd=firstVisibleItem+visibleItemCount;
		//第一次现实的时候调用
		if(mFirstIn&&visibleItemCount>0){
			mImageLoader.loadImages(mStart,mEnd);
			mFirstIn=false;
		}
	}

	class ViewHolder {
		TextView car_book_name;
		TextView car_endtime;
		ImageView car_bookpic;
		private TextView cancelBtn;
	}



}