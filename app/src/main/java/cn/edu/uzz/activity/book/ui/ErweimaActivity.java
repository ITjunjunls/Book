package cn.edu.uzz.activity.book.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.entity.ZXing;

/**
 * Created by 10616 on 2017/12/11.
 */

public class ErweimaActivity extends Activity{
	private ImageView ewm;
	private ImageView ewm_return;
	/*private String typelist;
	private String idlist;*/
	String jsonString;
	List<ZXing> books=new ArrayList<>();
	String bookString;
	Gson gson=new Gson();
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_erweima);
		initView();

	}

	private void initView() {
		ewm= (ImageView) findViewById(R.id.ewm);
		ewm_return= (ImageView) findViewById(R.id.ewm_return);
		ewm_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		/*typelist=getIntent().getStringExtra("typelist");
		idlist=getIntent().getStringExtra("idlist");*/
		jsonString=getIntent().getStringExtra("booklist");
		makeJson(jsonString);
		make();
	}

	private void makeJson(String jsonString) {
		try {
			JSONObject jsonObject=new JSONObject(jsonString);
			JSONArray jsonArray=jsonObject.getJSONArray("json");
			int length=jsonArray.length();
			for (int i=0;i<length;i++){
				jsonObject=jsonArray.getJSONObject(i);
				ZXing book=new ZXing();
				book.setBooktype(jsonObject.getInt("booktype"));
				book.setBookid(jsonObject.getInt("bookid"));
				books.add(book);
			}
			bookString = gson.toJson(books);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void make(){
		Bitmap bitmap= EncodingUtils.createQRCode(bookString,256,256,null);
		ewm.setImageBitmap(bitmap);
		BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background);
	}
}
