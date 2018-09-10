package pad.com.haidiyun.www.widget;


import java.io.File;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.inter.FtpListener;
import pad.com.haidiyun.www.inter.UpApk;
import pad.com.haidiyun.www.utils.FTPDownloadFile;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
 * 同步资源
 * @author Administrator
 *
 */
public class CommonApkPop {
	private Context context;
	private TextView load_tv;
	private   IDialog dlg;
	private String msg;
	private ImageView load_img,load_close;
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case -1:
					NewDataToast.makeText( "请检查与主机的连接配置");
					cancle();
					break;
				case -2:
					NewDataToast.makeText( "FTP登录失败,请检查FTP配置");
					cancle();
					break;
				case -3:
					NewDataToast.makeText( "取消升级");
					cancle();
					break;
				case -4:
					NewDataToast.makeText( "FTP数据同步失败,请检查FTP目录结构");
					cancle();
					break;
				case -5:
					NewDataToast.makeText( "请检查服务端版本号格式");
					cancle();
					break;
				case 1:
					NewDataToast.makeText( "下载完成");
					cancle();
					break;
				case 2:
					if(load_tv!=null){
						load_tv.setText((String) msg.obj);
					}
					break;
				case 3:
					NewDataToast.makeText( "无最新版本");
					cancle();
					break;
				case 4:
					String path = (String) msg.obj;
					P.c("安装新版本");
					installApk(new File(path));
					NewDataToast.makeText( "请退出设置界面进行新版本升级");
					cancle();
					break;
			}
		};
	};

	protected void installApk(File file) {
		Intent intent = new Intent();
		//执行动作
		intent.setAction(Intent.ACTION_VIEW);
		//执行的数据类型
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);

	}

	public CommonApkPop(Context context,String msg) {
		this.context = context;
		this.msg = msg;
	}
	public  Dialog showSheet() {
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
		new Thread(){
			public void run() {
				try {
					FTPDownloadFile df = new FTPDownloadFile(listener, handler);
					df.setUpListen(upApk);
					df.downloadApk();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}

			};
		}.start();


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
		return dlg;
	}
	private FtpListener listener = new FtpListener() {

		@Override
		public void login_status(boolean is) {
			// TODO Auto-generated method stub
			if(!is){
				handler.sendEmptyMessage(-2);
			}
		}

		@Override
		public void down_success(boolean is) {
			// TODO Auto-generated method stub

		}


	};
	private UpApk upApk = new UpApk() {

		@Override
		public void success(String path) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = 4;
			msg.obj = path;
			handler.sendMessage(msg);
		}

		@Override
		public void info() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(3);
		}



		@Override
		public void ex() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(-4);
		}

		@Override
		public void fx() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(-5);
		}
	};
	private void cancle(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg = null;
		}
	}
}
