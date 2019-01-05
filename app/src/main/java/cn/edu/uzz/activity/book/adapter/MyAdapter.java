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
import cn.edu.uzz.activity.book.entity.Books;
import cn.edu.uzz.activity.book.loader.ImageLoader;

public class MyAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    private List<Books> mList;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private ImageLoader mImageLoader;
    private int mStart,mEnd;
    public static String[] URLS;
    private boolean mFirstIn;

    public MyAdapter(Context context, List<Books> data, ListView listView) {
        this.mList = data;
        this.inflater = LayoutInflater.from(context);
        mImageLoader=new ImageLoader(listView);
        URLS=new String[data.size()];
        for (int i=0;i<data.size();i++){
            URLS[i]=data.get(i).getPicture();
        }
        mFirstIn=true;
        //注册对应的事件
        listView.setOnScrollListener(this);
    }

    public void onDateChange(ArrayList<Books> book_list) {
        this.mList = book_list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Books book = mList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bookitem, null);
            viewHolder.book_name= (TextView) convertView.findViewById(R.id.bookname);
            viewHolder.book_status= (TextView) convertView.findViewById(R.id.bookstatus);
            viewHolder.book_writer= (TextView) convertView.findViewById(R.id.bookwriter);
            viewHolder.book_version= (TextView) convertView.findViewById(R.id.bookversion);
            viewHolder.book_pic= (ImageView) convertView.findViewById(R.id.bookpic);
            viewHolder.book_publish= (TextView) convertView.findViewById(R.id.bookpublish);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.book_pic.setImageResource(R.mipmap.ic_launcher);
        String url=mList.get(position).getPicture();
        viewHolder.book_pic.setTag(url);
        //new ImageLoader().showImageByAsyncTask(viewHolder.ivIcon,url);
        mImageLoader.showImageByAsyncTask(viewHolder.book_pic,url);
        viewHolder.book_publish.setText(mList.get(position).getPublish());
        viewHolder.book_version.setText(mList.get(position).getVersion());
        viewHolder.book_writer.setText(mList.get(position).getWriter());
        viewHolder.book_status.setText(mList.get(position).getRentstatus());
        viewHolder.book_name.setText(mList.get(position).getName());
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
        TextView tvTitle,tvContent;
        ImageView ivIcon;
        TextView book_name;
        TextView book_writer;
        TextView book_status;
        TextView book_publish;
        TextView book_version;
        ImageView book_pic;

    }


}
