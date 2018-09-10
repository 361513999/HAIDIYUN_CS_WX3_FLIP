package pad.com.haidiyun.www.widget;


import org.json.JSONException;
import org.json.JSONObject;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.Status;
import pad.com.haidiyun.www.inter.ConnectTimeOut;
import pad.com.haidiyun.www.inter.LoginS;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.TcpClient;
import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
 * �����µ�
 * @author Administrator
 *
 */
public class CommonRegisterSendPop {
	 private Context context;
	private TextView load_tv;
	private   IDialog dlg;
	private String msg;
	private ImageView load_img,load_close;
	private SharedUtils utils;
	private LoginS loginS;
	private Handler handler = new Handler(){
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 1:
				NewDataToast.makeText((String)msg.obj);
				utils.setStringValue("IP", ip);
				utils.setStringValue("port", port);
				close();
				loginS.success();
				break;
			case 0:
				NewDataToast.makeText("�����˻�������");
				close();
				break;
			case -1:
				NewDataToast.makeText( "��������������������");
				close();
				break;
			case -2:
				NewDataToast.makeText( "��¼ʧ��,����WIFI��������������");
				close();
				break;
			case -3:
				NewDataToast.makeText( "ȡ����¼");
				cancle();
				break;
			case -5:
				NewDataToast.makeText( "��ȡ��Ӧ���ݳ�ʱ");
				close();
				break;
			default:
				break;
			}
		};
	};
	private TcpClient client = new TcpClient() {
		
		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			String result[] = buffer.split("@");
			if(result!=null&&result.length==2){
				Message msg = new Message();
				msg.what = 1;
				msg.obj = result[1];
				handler.sendMessage(msg);
			}
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
			//���ӳɹ���ʼ��½
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			  String deviceid = tm.getDeviceId();
				 String mac = FileUtils.getDeviceId();
				boolean flag = transceiver.send("registerDeviceId="+mac);
				if(!flag){
					handler.sendEmptyMessage(-1);
				}
		}
		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			//��ȡ��ʱ
			if(client!=null){
				 client.disconnect();
			 }
			handler.sendEmptyMessage(-5);
		}
	};
	public   String getDeviceId() {
		// ����Wifi��Ϣ��ȡ����Mac
		String ANDROID = "";
		WifiManager wifi = (WifiManager) BaseApplication.application.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info!=null) {
			ANDROID = info.getMacAddress();
		}
		return ANDROID;
	}
	/**
	 * ����socket
	 */
	private void connect(String ip,String pt){
		P.c("����----------------------------");
		if (client.isConnected()) {
			// �Ͽ�����
			P.c("�Ͽ�����----------------------------");
			client.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				String []strs = new String[]{Status.REGISTER_N_OK,Status.REGISTER_OK};
				client.connect(ip, port,buffer,strs);
				P.c("���Ӻ�̨----------------------------");
			} catch (NumberFormatException e) {
				 handler.sendEmptyMessage(-1);
				e.printStackTrace();
			}
		}
	
	}
	private String ip;
	private String port;
	
	public CommonRegisterSendPop(Context context,String msg , String ip, String port,LoginS loginS) {
		this.context = context;
		this.ip = ip;
		this.port = port;
		this.loginS = loginS;
		this.msg = msg;
		  utils = new SharedUtils(Common.CONFIG);
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
//		 String ip = utils.getStringValue("IP");
//			String port = utils.getStringValue("port");
			connect(ip, port);
				// TODO Auto-generated catch block
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
		// ����
		Animation operatingAnim = AnimationUtils.loadAnimation(context,
				R.anim.anim_load);
		// ������ת
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		load_img.startAnimation(operatingAnim);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
	
	
	@SuppressWarnings("unused")
	private ConnectTimeOut timeOut = new ConnectTimeOut() {
		
		@Override
		public void recyle() {
			// TODO Auto-generated method stub
			//������ӳ�ʱ
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
