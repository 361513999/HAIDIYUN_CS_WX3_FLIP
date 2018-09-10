package pad.com.haidiyun.www.widget;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.BaseApplication;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;



public class ViewToast {
	private    Handler handler = new Handler();
	private  TextView tip;
	private   IDialog dlg;
	private static ViewToast viewToast;
	private ViewToast() {
		// TODO Auto-generated constructor stub
	}
	public static synchronized ViewToast getViewToast() {
		if (viewToast == null) {
		synchronized (ViewToast.class) {
			if (viewToast == null) {
				viewToast=new ViewToast();
			}
		}
	}
		return viewToast;
	}
	//全漂浮
	public static synchronized ViewToast getViewToastD() {
		if (viewToast == null) {
		synchronized (ViewToast.class) {
			if (viewToast == null) {
				viewToast=new ViewToast();
			}
		}
	}
		return viewToast;
	}
	
	public   void makeText(Context context,CharSequence txt) {
		if(dlg!=null&&dlg.isShowing()){
			return;
		}
		 dlg = new IDialog(BaseApplication.application, R.style.toast_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.new_data_toast, null);
		tip = (TextView) layout.findViewById(R.id.new_data_toast_message);
		tip.setText(txt);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
	}
	public  void   makeTextTop(Context context,CharSequence txt) {
		 dlg = new IDialog(BaseApplication.application, R.style.toast_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.new_data_toast, null);
		tip = (TextView) layout.findViewById(R.id.new_data_toast_message);
		tip.setText(txt);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
	}
	public    void makeTextD(Context context,CharSequence txt) {
		dlg = new IDialog(BaseApplication.application, R.style.d_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.new_data_toast, null);
		tip = (TextView) layout.findViewById(R.id.new_data_toast_message);
		tip.setText(txt);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
	}
	public   void show(){
		if(dlg!=null&&!dlg.isShowing()){
			dlg.show();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					close();
				}
			}, 800);
		}
	}
	public   void show(int time){
		if(dlg!=null&&!dlg.isShowing()){
			dlg.show();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					close();
				}
			}, time);
		}
	}
	
	private  void close(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg = null;
		}
	}
}
