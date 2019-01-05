package cn.edu.uzz.activity.book.ui;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog dialog;

    /**
     * 显示进度对话框
     * @param msg
     */
    public void showProgressDialog(String msg){
        if(dialog == null){
            dialog = new ProgressDialog(this); //创建一个进度对话框
        }
        dialog.setMessage(msg); //设置对话框要显示的文字
        dialog.show();          //把这个对话框显示出来
    }

    /**
     * 取消进度对话框
     */
    public void cancelDialog(){
        if(dialog != null){
            dialog.dismiss(); //把对话框取消掉
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当activity销毁的时候就会执行这个方法
        if(dialog!=null){
            dialog.dismiss();//把对话框取消掉
            dialog =null;
        }
    }

    public void showResultMsg(String errorMsg){
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        cancelDialog();
    }
}
