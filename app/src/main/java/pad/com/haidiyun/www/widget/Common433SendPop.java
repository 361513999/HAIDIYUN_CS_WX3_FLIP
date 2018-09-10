package pad.com.haidiyun.www.widget;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.ConnectTimeOut;
import pad.com.haidiyun.www.inter.LoginS;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.TcpClient;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
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
 * 发送下单
 * @author Administrator
 *
 */
public class Common433SendPop {
	private Context context;
	private TextView load_tv;
	private   IDialog dlg;
	private String msg;
	private ImageView load_img,load_close;
	private SharedUtils utils;
	private LoginS loginS;
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					NewDataToast.makeText("已通知");
					close();
					if(loginS!=null){
						loginS.success();
					}

					break;
				case 0:
					NewDataToast.makeText((String)msg.obj);
					close();
					break;
				case -1:
					NewDataToast.makeText( "请检查与主机的连接配置");
					close();
					break;
				case -2:
					NewDataToast.makeText( "操作失败,请检查WIFI环境和主机配置");
					close();
					break;
				case -3:
//				NewDataToast.makeText(context, "再试一次");
					cancle();
					break;
				case -4:
					close();
					break;
				case -5:
					NewDataToast.makeText( "获取响应数据超时");
					close();
					break;

				default:
					break;
			}
		};
	};
	public Common433SendPop(Context context,String msg, LoginS loginS) {
		this.context = context;
		this.loginS = loginS;
		this.msg = msg;
		utils = new SharedUtils(Common.CONFIG);
	}
	private TcpClient client =new TcpClient() {

		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			// TODO Auto-generated method stub
			System.out.println(buffer);
			String json = buffer.substring(0,buffer.length()-5);
			Message msg = new Message();
			msg.what = 1;
			msg.obj = json;
			handler.sendMessage(msg);
			//----------
			if(client!=null){
				client.disconnect();
			}
		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectFailed() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(-2);
		}

		@Override
		public void onConnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			transceiver.send(utils.getStringValue("table_name")+"~$~"+msg,30000);
			//~$~
			P.c(utils.getStringValue("table_name")+"~$~"+msg);
		}

		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			//读取超时
			if(client!=null){
				client.disconnect();
			}
			handler.sendEmptyMessage(-5);

		}
	};
	/**
	 * 连接socket
	 */
	private void connect(String ip,String pt){
		P.c("发送----------------------------");
		if (client.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			client.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				client.connect(ip, port,buffer);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {
				handler.sendEmptyMessage(-1);
				e.printStackTrace();
			}
		}

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
		try {
			final String ip = utils.getStringValue("sNa");
//				int port = Integer.parseInt(utils.getStringValue("port"));
			String port = "11011";
			connect(ip, port);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(-1);
		}
		load_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(client!=null){
					client.disconnect();
				}
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
	private ConnectTimeOut timeOut = new ConnectTimeOut() {
		@Override
		public void recyle() {
			// TODO Auto-generated method stub
			//多次连接超时
			handler.sendEmptyMessage(-2);
		}
	};
	private void cancle(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg = null;
		}
	}
	private void close(){
		if(dlg!=null&&dlg.isShowing()){
			if(client!=null){
				client.disconnect();
			}
			dlg.cancel();
			dlg = null;
		}
	}

}
