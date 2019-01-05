package cn.edu.uzz.activity.book.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import cn.edu.uzz.activity.book.R;
import cn.edu.uzz.activity.book.util.GuideView;


public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private LinearLayout mTabWeixin;
    private LinearLayout mTabFrd;
    private LinearLayout mTabAddress;

    private ImageButton mImgWeixin;
    private ImageButton mImgFrd;
    private ImageButton mImgAddress;

    private Fragment mTab01;
    private Fragment mTab02;
    private Fragment mTab03;

    private GuideView guideView1;
	private GuideView guideView2;
	private GuideView guideView3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
		File file= new File("/data/data/cn.edu.uzz.activity.book/shared_prefs","wel.xml");
		if(!file.exists())
		{
			initYD();
		}
        initEvent();
        setSelect(0);
	}

	private void initYD() {

		SharedPreferences pre=getSharedPreferences("wel",MODE_PRIVATE);
		SharedPreferences.Editor editor=pre.edit();
		editor.putString("username","123");
		editor.commit();

		final ImageView iv3 = new ImageView(this);
		iv3.setImageResource(R.drawable.img_new_task_guide);
		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		iv3.setLayoutParams(params3);

		final ImageView iv2 = new ImageView(this);
		iv2.setImageResource(R.drawable.img_new_task_search);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		iv2.setLayoutParams(params2);

		final ImageView iv1 = new ImageView(this);
		iv1.setImageResource(R.drawable.img_new_task_main);
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		iv1.setLayoutParams(params1);

		guideView1 = GuideView.Builder
				.newInstance(this)
				.setTargetView(mTabWeixin)//设置目标
				.setCustomGuideView(iv1)
				.setDirction(GuideView.Direction.TOP)
				.setShape(GuideView.MyShape.ELLIPSE)   // 设置圆形显示区域，
				.setBgColor(getResources().getColor(R.color.shadow))
				.setOnclickListener(new GuideView.OnClickCallback() {
					@Override
					public void onClickedGuideView() {
						guideView1.hide();
						guideView2.show();
					}
				})
				.build();

		guideView2 = GuideView.Builder
				.newInstance(this)
				.setTargetView(mTabFrd)//设置目标
				.setCustomGuideView(iv2)
				.setDirction(GuideView.Direction.TOP)
				.setShape(GuideView.MyShape.ELLIPSE)   // 设置圆形显示区域，
				.setBgColor(getResources().getColor(R.color.shadow))
				.setOnclickListener(new GuideView.OnClickCallback() {
					@Override
					public void onClickedGuideView() {
						guideView2.hide();
						guideView3.show();
					}
				})
				.build();



		guideView3 = GuideView.Builder
				.newInstance(this)
				.setTargetView(mTabAddress)//设置目标
				.setCustomGuideView(iv3)
				.setDirction(GuideView.Direction.TOP)
				.setShape(GuideView.MyShape.ELLIPSE)   // 设置圆形显示区域，
				.setBgColor(getResources().getColor(R.color.shadow))
				.setOnclickListener(new GuideView.OnClickCallback() {
					@Override
					public void onClickedGuideView() {
						guideView3.hide();
					}
				})
				.build();

		guideView1.show();
	}

	private void initEvent() {
        mTabWeixin.setOnClickListener(this);
        mTabFrd.setOnClickListener(this);
        mTabAddress.setOnClickListener(this);
    }

    private void initView() {
        mTabWeixin = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);

        mImgWeixin = (ImageButton) findViewById(R.id.id_tab_weixin_img);
        mImgFrd = (ImageButton) findViewById(R.id.id_tab_frd_img);
        mImgAddress = (ImageButton) findViewById(R.id.id_tab_address_img);
    }

    private void setSelect(int i)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        // 把图片设置为亮的
        // 设置内容区域
        switch (i)
        {
            case 0:
                if (mTab01 == null)
                {
                    mTab01 = new Tab01();
                    transaction.add(R.id.id_content, mTab01);
                } else
                {
                    transaction.show(mTab01);
                }
                mImgWeixin.setImageResource(R.drawable.book_onetab_selected);
                break;
            case 1:
                if (mTab02 == null)
                {
                    mTab02 = new Tab02();
                    transaction.add(R.id.id_content, mTab02);
                } else
                {
                    transaction.show(mTab02);

                }
                mImgFrd.setImageResource(R.drawable.book_twotab_selected);
                break;
            case 2:
                if (mTab03 == null)
                {
                    mTab03 = new Tab03();
                    transaction.add(R.id.id_content, mTab03);
                } else
                {
                    transaction.show(mTab03);
                }
                mImgAddress.setImageResource(R.drawable.book_threetab_selected);
                break;


            default:
                break;
        }

        transaction.commit();
    }
    private void hideFragment(FragmentTransaction transaction)
    {
        if (mTab01 != null)
        {
            transaction.hide(mTab01);
        }
        if (mTab02 != null)
        {
            transaction.hide(mTab02);
        }
        if (mTab03 != null)
        {
            transaction.hide(mTab03);
        }
    }


    @Override
    public void onClick(View view) {
        resetImgs();
        switch (view.getId())
        {
            case R.id.id_tab_weixin:
                setSelect(0);
                break;
            case R.id.id_tab_frd:
                setSelect(1);
                break;
            case R.id.id_tab_address:
                setSelect(2);
                break;


            default:
                break;
        }
    }
    private void resetImgs()
    {
        mImgWeixin.setImageResource(R.drawable.book_tabone_normal);
        mImgFrd.setImageResource(R.drawable.book_twotab_normal);
        mImgAddress.setImageResource(R.drawable.book_tabthree_normal);

    }

}
