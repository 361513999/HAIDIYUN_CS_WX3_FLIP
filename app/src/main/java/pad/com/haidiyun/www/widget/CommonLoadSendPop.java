package pad.com.haidiyun.www.widget;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.inter.SetI;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 公共加载
 * @author Administrator
 *
 */
public class CommonLoadSendPop {
	private Context context;
	private TextView load_tv;
	private   IDialog dlg;
	private String msg;
	private ImageView load_img,load_close;
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {

				case -3:
					NewDataToast.makeText( "取消获取");
					cancle();
					if(setI!=null){
						setI.click();
					}

				default:
					break;
			}
		};
	};
	private SetI setI;
	public void setCancel(SetI setI){
		this.setI = setI;
	}
	public CommonLoadSendPop(Context context,String msg) {
		this.context = context;
		this.msg = msg;

	}
	private CountDownTimer timer;
	public  Dialog showSheet(boolean flag) {
		dlg = new IDialog(context, R.style.buy_pop_style);
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout layout = (FrameLayout) inflater.inflate(
				R.layout.loading, null);
		load_tv = (TextView) layout.findViewById(R.id.load_tv);
		load_tv.setText(msg);
		load_img = (ImageView) layout.findViewById(R.id.load_img);
		load_close = (ImageView) layout.findViewById(R.id.load_close);
		if(flag){
			load_close.setVisibility(View.GONE);
		}

		// TODO Auto-generated catch block
		load_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				handler.sendEmptyMessage(-3);
			}
		});
		// 动画
		Animation operatingAnim = AnimationUtils.loadAnimation(context,
				R.anim.anim_load);
		// 匀速旋转
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		load_img.startAnimation(operatingAnim);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
		timer = new CountDownTimer(20000,1000) {

			@Override
			public void onTick(long arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				NewDataToast.makeText("请求超时");
				cancel();
			}
		};
		timer.start();


		return dlg;
	}


	public void cancle(){
		if(dlg!=null){
			dlg.cancel();
			dlg = null;
		}
		if(timer!=null){
			timer.cancel();
		}
	}

}
