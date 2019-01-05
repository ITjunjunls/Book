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
import cn.edu.uzz.activity.book.entity.Subscribe;
import cn.edu.uzz.activity.book.loader.SubImageLoader;

/**
 * Created by 10616 on 2017/12/9.
 */

public class SubAdapter extends BaseAdapter implements AbsListView.OnScrollListener,View.OnClickListener{
private List<Subscribe> mList;
private LayoutInflater inflater;
private ViewHolder viewHolder;
private SubImageLoader mImageLoader;
private int mStart,mEnd;
public static String[] URLS;
private boolean mFirstIn;
private CallBack mcallBack;

public SubAdapter(Context context, List<Subscribe> data, ListView listView, CallBack callBack) {
		this.mList = data;
		mcallBack=callBack;
		this.inflater = LayoutInflater.from(context);
		mImageLoader=new SubImageLoader(listView);
		URLS=new String[data.size()];
		for (int i=0;i<data.size();i++){
		URLS[i]=data.get(i).getPicture();
		}
		mFirstIn=true;
		//注册对应的事件
		listView.setOnScrollListener(this);
		}

public void refresh(List<Subscribe> list) {
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

	public void onDateChange(ArrayList<Subscribe> sub_list) {
		this.mList = sub_list;
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
		Subscribe subscribe = mList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_sub, null);
			viewHolder.book_name= (TextView) convertView.findViewById(R.id.sub_name);
			viewHolder.book_pic= (ImageView) convertView.findViewById(R.id.sub_pic);
			viewHolder.cancelBtn= (TextView) convertView.findViewById(R.id.cancel_sub);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.book_pic.setImageResource(R.mipmap.ic_launcher);
		String url=mList.get(position).getPicture();
		viewHolder.book_pic.setTag(url);
		//new ImageLoader().showImageByAsyncTask(viewHolder.ivIcon,url);
		mImageLoader.showImageByAsyncTask(viewHolder.book_pic,url);
		viewHolder.book_name.setText(mList.get(position).getBookname());
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
	private TextView book_name;
	/*private TextView book_writer;
	private TextView book_publish;
	private TextView book_version;*/
	private ImageView book_pic;
	private TextView cancelBtn;
}



}