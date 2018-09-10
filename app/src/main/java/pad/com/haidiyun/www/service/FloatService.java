package pad.com.haidiyun.www.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.AppManager;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.BillsBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.Led;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.net.Respon;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.SokectUtils;
import pad.com.haidiyun.www.net.TcpClient;
import pad.com.haidiyun.www.ui.CardValActivity;
import pad.com.haidiyun.www.ui.FlipNextActivity;
import pad.com.haidiyun.www.ui.LanuchActivity;
import pad.com.haidiyun.www.utils.Tip_Utils;
import pad.com.haidiyun.www.widget.CommonConfigPop;
import pad.com.haidiyun.www.widget.NewDataToast;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FloatService extends Service {

	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	private View float_service;
	private TextView   touch_view;
	private View fs;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private int state;
	private float StartX;
	private float StartY;
	private int delaytime = 500;
	private int tableDelay = 15000;
	private SharedUtils  configUtils;
	private Tip_Utils tip_Utils;
	private LinearLayout move_layout;
	private WifiManager mWifiManager;
	private ProgressBar pw_spinner;
	private ViewHandler viewHandler;
	private BatteryReceiver batteryReceiver;
	private Typeface tf;
	private LinearLayout parent;
	private RelativeLayout layout;
	@Override
	public void onCreate() {
		super.onCreate();
		configUtils = new SharedUtils(Common.CONFIG);
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		viewHandler = new ViewHandler(this);
		mWifiManager = (WifiManager)this. getSystemService(Context.WIFI_SERVICE);
		float_service = LayoutInflater.from(this).inflate(
				R.layout.float_service, null);
		createView();
		pw_spinner = (ProgressBar) float_service.findViewById(R.id.pw_spinner);
		parent = (LinearLayout) float_service.findViewById(R.id.parent);
		layout = (RelativeLayout) float_service.findViewById(R.id.layout);
		touch_view = (TextView) float_service.findViewById(R.id.touch_view);
		tf = Typeface.createFromAsset(getAssets(), "font/mb.ttf");

		touch_view.setTypeface(tf);

		touch_view.setTextColor(getResources().getColor(R.color.white));
		touch_view.setText(configUtils.getStringValue("table_name").length()==0?"餐台状态":configUtils.getStringValue("table_name"));
		tip_Utils = new Tip_Utils();
//		sharedUtils = new SharedUtils(Common.SHARED_WIFI);

		handler.post(task);
	//	tableHandler.post(tableRunnable);
//		shandler.postDelayed(stask, sdelaytime);
		batteryReceiver = new BatteryReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryReceiver, filter);
		//绑定服务
		new InitSocketThread().start();
		mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
		//默认
		layout.setBackgroundResource(R.drawable.touch_1);


	}

	private LocalBroadcastManager mLocalBroadcastManager;
	private static final long HEART_BEAT_RATE = 6 * 1000;
	private static final long HEART_READ_RATE = 15*1000;
	private  static final int CONNECT_TIME_OUT = 10*1000;
	public static String HOST = "";// "192.168.1.21";//
	public static  int PORT = 0;
	private ReadThread mReadThread;
	private WeakReference<Socket> mSocket;
	// For heart Beat
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable = new Runnable() {

		@Override
		public void run() {
			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
				boolean isSuccess = sendMsg("{\"HBT\":\"ON\"}");//就发送一个\r\n过去 如果发送失败，就重新初始化一个socket
				System.out.println("发送心跳");
				FileUtils.writeLog("发送心跳", "发送心跳");
				if (!isSuccess) {
					System.out.println("心跳发送失败");
					reset();
				}
			}
			//10秒内没有获取到任何心跳信息，那么就证明超时
			if(System.currentTimeMillis()-readTime>=HEART_READ_RATE){
				System.out.println("心跳超时");
				FileUtils.writeLog("心跳超时", "心跳超时");
				reset();
			}
			sendMsg(guVar());
			/*if(System.currentTimeMillis() - billTime >= HEART_READ_RATE){
				P.c("--发送账单检查");
				find();
			}*/
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};
	/**
	 * 构造沽清请求数据
	 * @return
	 */
	private String guVar(){
		String dev = configUtils.getStringValue("dev");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			jsonObject.put("GQ", jsonArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
//	private int person = 0;
	/**
	 * 处理沽清结果
	 * @param result
	 */
	private void processGu(String result){
		Common.guKeys.clear();
//		无估清
		try {
			if(result.contains("无沽清")){

			}else{
				String []tmep = result.split("\n");
				for(int i=0;i<tmep.length;i++){
					String s = "";

					if(tmep[i].length()>4){
						s = tmep[i].substring(0,4).replace(" ", "").trim();
					}
					if(s.length()==4){
						//就是菜品编码
						Common.guKeys.put(s, 0);
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void processBillbyTableCode(String result){


		if(result.contains("金额")){
			configUtils.setStringValue("billId","0");
			try {
				String per = result.substring(result.indexOf("人数")+2,result.indexOf("点菜员")).replace(" ","");
				int person = Integer.parseInt(per);
				configUtils.setIntValue("person",person);


			}catch (Exception E){

			}
			//就餐中
			viewHandler.sendEmptyMessage(22);



		}else{
			configUtils.clear("billId");
			configUtils.clear("order_time");
			configUtils.clear("bills");
			viewHandler.sendEmptyMessage(11);
		}
		P.c("发送的app.send.table");
		Intent open=new Intent("app.send.table");
		sendBroadcast(open);

	}
	/**
	 * 处理开台结果
	 * @param msg
	 * @return
	 */

	private void processOpenTable(String result){
		try {

			if(result.contains("成功")||result.contains("账单号")){
				//开台成功
				String billId = result.substring(result.indexOf("账单号")).split("：")[1];
				configUtils.setStringValue("billId", billId);

				//-----通知service更改
				Intent intent = new Intent();
				intent.setAction(Common.SERVICE_ACTION);
				intent.putExtra("open_table", "");
				startService(intent);

				Intent open=new Intent("app.open.table");
				open.putExtra("result", true);
				sendBroadcast(open);
			}  else{

				Intent open=new Intent("app.open.table");
				open.putExtra("result", false);
				open.putExtra("tips",result);
				sendBroadcast(open);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//handler.sendEmptyMessage(-1);
		}
	}
	/**
	 * 处理下单
	 * @param result
	 */
	private void processDish(String result){

		// TODO Auto-generated method stub

		if(result.contains("其他设备")){
			Intent open=new Intent("app.send.dish");
			open.putExtra("result", -6);
			sendBroadcast(open);
		}else if (result.contains("成功")) {
			DB.getInstance().clear();
			configUtils.clear("order_time");
			Intent open=new Intent("app.send.dish");
			open.putExtra("result", 1);
			sendBroadcast(open);
		}else if (result.contains("")) {

			Intent open=new Intent("app.send.dish");
			open.putExtra("result", -7);
			open.putExtra("obj", result);
			sendBroadcast(open);
		}


	}
	/**
	 * 处理退菜
	 * @param msg
	 * @return
	 */
	private void processTui(String result){
		Intent open=new Intent("app.send.tui");
		open.putExtra("obj", result);
		//result.substring(33,result.length())
		sendBroadcast(open);
	}
	private void processZeng(String result){
		Intent open=new Intent("app.send.zeng");
		open.putExtra("obj", result);
		sendBroadcast(open);
	}

	public boolean sendMsg(String msg) {
		if (null == mSocket || null == mSocket.get()) {
			return false;
		}
		Socket soc = mSocket.get();
		try {
			if (!soc.isClosed() && !soc.isOutputShutdown()) {
				final OutputStream os = soc.getOutputStream();
				if(!msg.endsWith("<EOF>")){
					msg = msg+"<EOF>";
				}
				final String message = msg + "\r\n";
				if(message.length()!=0){
					new Thread(){
						@Override
						public void run() {
							super.run();
							try {
								os.write(message.getBytes());
								os.flush();
								P.c("发送数据"+message);
								FileUtils.writeLog(message,"发送数据");
								sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					}.start();
				}

			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			FileUtils.writeLog(msg,"发送失败");
			return false;
		}
		return true;

	}
	private CountDownTimer downTimer;

	private void reset(){
		P.c("重启"+HOST);
        Led.reset(2);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mHandler.removeCallbacks(heartBeatRunnable);
		if(mReadThread!=null){
			mReadThread.release();
		}
		Intent intent = new Intent();
		intent.setAction(Common.SERVICE_ACTION);
		intent.putExtra("view_dx", 0);
		startService(intent);
		Led.reset(1);
		releaseLastSocket(mSocket);
		new InitSocketThread().start();


	}

	class InitSocketThread extends Thread {
		@Override
		public void run() {
			super.run();
			initSocket();
		}
	}
	private 	UdpLis lis = null;
	private void initSocket() {//初始化Socket
		try {
			FileUtils.writeLog("初始化Socket", "初始化Socket");

			HOST  = configUtils.getStringValue("IP");
			PORT = Common.PORT433;
			Socket so = new Socket();
			SocketAddress remoteAddr=new InetSocketAddress(HOST, PORT);
			so.connect(remoteAddr, CONNECT_TIME_OUT);
			//so.setSoTimeout(CONNECT_TIME_OUT);
			readTime = System.currentTimeMillis();
			mSocket = new WeakReference<Socket>(so);
			// Thread.sleep(CONNECT_TIME_OUT);
			/*if(!so.isConnected()){
				System.out.println("5s连接超时重启");
				reset();
			}*/
			if(so!=null){
				mReadThread = new ReadThread(so);
				mReadThread.start();
			}

			Led.led(3);

			/*if(lis!=null){
				lis.stop(true);
				lis = null;
			}
			lis = new UdpLis(this);
			Thread thread = new Thread(lis);
			thread.start();*/

			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
		} catch (Exception e) {

			FileUtils.writeLog(e.getMessage(), "连接超时重启");
			Intent intent = new Intent();
			intent.setAction(Common.SERVICE_ACTION);
			intent.putExtra("view_err", 0);
			startService(intent);

			reset();

		}
	}



	private void releaseLastSocket(WeakReference<Socket> mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if (sk!=null&&!sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private StringBuilder builder = new StringBuilder();
	private long sendTime = 0L;
	private long readTime = 0L;
	private long billTime = 0L;
	private final String END = "<EOF>";
	// Thread to read content from Socket
	class ReadThread extends Thread {
		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = new WeakReference<Socket>(socket);
		}

		public void release() {
			isStart = false;
			if(mWeakSocket!=null){
				releaseLastSocket(mWeakSocket);
			}

		}

		@SuppressLint("NewApi")
		@Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket.get();
			if (null != socket) {
				try {
					InputStream is = socket.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int length = 0;
					while (!socket.isClosed() && !socket.isInputShutdown()
							&& isStart && ((length = is.read(buffer)) != -1)) {
						if (length > 0) {
							String ttp = new String(Arrays.copyOf(buffer,
									length)).trim();
							builder.append( ttp);
							String message = builder.toString();
							readTime = System.currentTimeMillis();
							P.c(message);
							Map<String, String> temp = null;
							if(message.contains(END)&&message.endsWith(END)){
								FileUtils.writeLog(message, "数据结果");
								P.c("数据结果"+message);
								String temps[] = message.split(END);
								if(temps!=null){
									for(int i=0;i<temps.length;i++){
										temp = new HashMap<>();
										JSONObject jsonObject = null;
										try {
											jsonObject = new JSONObject(temps[i]);
											temp.put(jsonObject.getString("Cmd"), jsonObject.getString("Value"));
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}

								}
								builder = null;
								builder  = new StringBuilder();
								Intent intent = new Intent();
								intent.setAction(Common.SERVICE_ACTION);
								intent.putExtra("view_ok", 0);
								startService(intent);

							}
							if(temp!=null){
								if(temp.containsKey("GQ")){
									processGu(temp.get("GQ"));
								}


								if(temp.containsKey("ZDCX")){
									//解析账单号
									processBillbyTableCode(temp.get("ZDCX"));
								}
								if(temp.containsKey("YHLB")){
									//处理密码
									processMM(temp.get("YHLB"));

								}

								if (temp.containsKey("KT")) {
									processOpenTable(temp.get("KT"));
								}else if (temp.containsKey("DC")) {
									processDish(temp.get("DC"));
								}else if (temp.containsKey("TC1")) {
									processTui(temp.get("TC1"));
								}else if (temp.containsKey("ZSCP")) {
									processZeng(temp.get("ZSCP"));
								}else if (temp.containsKey("DYDC")) {
									viewHandler.sendEmptyMessage(222);
								}
								temp = null;
							}

							//收到服务器过来的消息，就通过Broadcast发送出去
							if(message.equals("ok")){//处理心跳回复
//								Intent intent=new Intent(HEART_BEAT_ACTION);
//								mLocalBroadcastManager.sendBroadcast(intent);
							}else{
								//其他消息回复
//								Intent intent=new Intent(MESSAGE_ACTION);
//								intent.putExtra("message", message);
//								mLocalBroadcastManager.sendBroadcast(intent);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
//					System.out.print("读取超时重连");
//					reset();
				}
			}
		}
	}
	private Map<String,String> parseJson(String buffer) throws JSONException{

		Map<String,String> maps = new HashMap<String, String>();
		JSONObject jsonObject = new JSONObject(buffer);
		maps.put(jsonObject.getString("Cmd"), jsonObject.getString("Value"));
		return maps;
	}
	private void processMM(String vzr){

		SharedUtils userUtils = new SharedUtils(Common.USERS);
		userUtils.clear();

		try {
			JSONObject jsonObject = new JSONObject(vzr);
			JSONArray jsonArray = jsonObject.getJSONArray("YHLB");
			int len = jsonArray.length();
			if(len!=0){
				for(int i=0;i<len;i++){
					JSONObject object = jsonArray.getJSONObject(i);
					userUtils.setStringValue(object.getString("User"),object.getString("Pwd"));
				}
				Intent open=new Intent("app.send.users");
				sendBroadcast(open);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(Common.IS_SHOW){
			viewHandler.sendEmptyMessage(223);
		}

	}




	class BatteryReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				// 获取当前电量
				int level = intent.getIntExtra("level", 0);
				// 电量的总刻度
				int scale = intent.getIntExtra("scale", 100);
				// 设置当前电量
				int progress = (level * 100) / scale;

				// 是否在充电
				// int status = intent
				// .getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				// boolean isCharging = status ==
				// BatteryManager.BATTERY_STATUS_CHARGING
				// || status == BatteryManager.BATTERY_STATUS_FULL;
				// int currPower = (progress * 36 / 10);
				P.c(level + "电量" + progress + "--" + scale);
				pw_spinner.setProgress(level);
				/*
				 * if (isCharging) {// 充电
				 * batteryHandler.sendEmptyMessage(Msg_Battery_IsCharging); }
				 * else { if (progress >= 80) {// 绿色#00FF00
				 * batteryHandler.sendEmptyMessage(Msg_Battery_IsChange80); } if
				 * (progress > 40 && progress < 80) {// 黄色#FFFF00
				 * batteryHandler.sendEmptyMessage(Msg_Battery_IsChange40); } if
				 * (progress <= 40) {// 红色#EC6841
				 * batteryHandler.sendEmptyMessage(Msg_Battery_IsChange00); } }
				 */
			}
		}
	}

	/**
	 * 透明事件穿透模式
	 */
	private long lastClick;

	private void createView() {
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参数
		wmParams = (BaseApplication.application).getMywmParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		wmParams.format = PixelFormat.RGBA_8888;
		// wmParams.format = PixelFormat.TRANSLUCENT;
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.gravity = Gravity.RIGHT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wm.addView(float_service, wmParams);
		float_service.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub

				CommonConfigPop configPop = new CommonConfigPop(
						FloatService.this);
				configPop.showSheet();
				return true;
			}
		});
		/*float_service.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (System.currentTimeMillis() - lastClick <= 1000) {
					return;
				}
				lastClick = System.currentTimeMillis();


				if(AppManager.getAppManager().currentActivity() instanceof LanuchActivity){
					Intent intent = new Intent();
					intent.setAction("KeyEvent.KEYCODE_BACK_Laucher");
					sendBroadcast(intent);
				}else{
					Intent intent = new Intent();
					intent.setAction("KeyEvent.KEYCODE_BACK");
					sendBroadcast(intent);
				}
				// 发一个时间更改的通知

			}
		});
*/
		/*
		 * float_service.setOnTouchListener(new OnTouchListener() { public
		 * boolean onTouch(View v, MotionEvent event) { // 获取相对屏幕的坐标，即以屏幕左上角为原点
		 * x = event.getRawX(); y = event.getRawY() - 25; // 25是系统状态栏的高度
		 * Log.i("currP", "currX" + x + "====currY" + y);// 调试信息 switch
		 * (event.getAction()) { case MotionEvent.ACTION_DOWN: state =
		 * MotionEvent.ACTION_DOWN; StartX = x; StartY = y; //
		 * 获取相对View的坐标，即以此View左上角为原点 mTouchStartX = event.getX(); mTouchStartY =
		 * event.getY(); Log.i("startP", "startX" + mTouchStartX + "====startY"
		 * + mTouchStartY);// 调试信息 break; case MotionEvent.ACTION_MOVE: state =
		 * MotionEvent.ACTION_MOVE; updateViewPosition(); break;
		 *
		 * case MotionEvent.ACTION_UP: state = MotionEvent.ACTION_UP;
		 * updateViewPosition(); mTouchStartX = mTouchStartY = 0; break; }
		 * return true; } });
		 */

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.hasExtra("recy_table")) {
			P.c("切换桌台");
			/*if (configUtils.getStringValue("table_name").length() != 0) {

				touch.setText(configUtils.getStringValue("table_name"));

			}*/
			find();
		} else if (intent != null && intent.hasExtra("open_table")) {
			viewHandler.sendEmptyMessage(22);
		}else if(intent!=null && intent.hasExtra("open_buy")){
			String view = intent.getStringExtra("open_buy");
			if(float_service!=null){

				if(view.equals("0")){
					wmParams.gravity = Gravity.LEFT | Gravity.TOP;
				}else{
					wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
				}
				wm.updateViewLayout(float_service, wmParams);
//				float_service.setVisibility(view.equals("0")?View.GONE:View.VISIBLE);
			}
		}else if(intent!=null&&intent.hasExtra("var")){
			//数据请求


			sendMsg(intent.getStringExtra("var"));

		}else if (intent!=null&&intent.hasExtra("view_dx")) {
			layout.setBackgroundResource(R.drawable.touch_1);
		}else if(intent!=null&&intent.hasExtra("view_ok")){

			layout.setBackgroundResource(R.drawable.touch_2);
		}else if (intent!=null&&intent.hasExtra("view_err")) {
			layout.setBackgroundResource(R.drawable.touch);
		}else if (intent!=null&&intent.hasExtra("view_gone")) {
			parent.setVisibility(View.GONE);
		}else if (intent!=null&&intent.hasExtra("view_v")) {
			parent.setVisibility(View.VISIBLE);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			//dataRefresh();
			handler.postDelayed(this, delaytime);
			wm.updateViewLayout(float_service, wmParams);
			// 在这里进行电量的控制变化
			if(float_service!=null){
				String result = getTopActivity();


				if(result.equals(BaseApplication.application.getPName())){
					float_service.setVisibility(View.VISIBLE);
				}else{
					FlipNextActivity.mHandler.removeCallbacks(FlipNextActivity.heartBeatRunnable);
					float_service.setVisibility(View.GONE);
				}
			}
		}
	};
	private ActivityManager am;
	private   String getTopActivity() {


		if (Build.VERSION.SDK_INT <= 20) {
			List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
			if (tasks != null && !tasks.isEmpty()) {
				ComponentName componentName = tasks.get(0).topActivity;
				if (componentName != null) {
					return componentName.getPackageName();
				}
			}
		} else {
			/*if(Build.VERSION.SDK_INT==24){


					BufferedReader cmdlineReader = null;
					try {
						cmdlineReader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + android.os.Process.myPid() + "/cmdline"), "iso-8859-1"));
						int c;
						StringBuilder processName = new StringBuilder();
						while ((c = cmdlineReader.read()) > 0) {
							processName.append((char) c);
						}
						return processName.toString();
					} catch (Exception ignore) {
					} finally {
						try {
							if (cmdlineReader != null) {
								cmdlineReader.close();
							}
						} catch (IOException ignore) {
						}
					}
					return "";

			}else{*/
				ActivityManager.RunningAppProcessInfo currentInfo = null;
				Field field = null;
				int START_TASK_TO_FRONT = 2;
				String pkgName = null;
				try {
					field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
				} catch (Exception e) {
					return null;
				}
				List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
				if (appList == null || appList.isEmpty()) {
					return null;
				}
				for (ActivityManager.RunningAppProcessInfo app : appList) {
					if (app != null && app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						Integer state = null;
						try {
							state = field.getInt(app);
						} catch (Exception e) {
							return null;
						}
						if (state != null && state == START_TASK_TO_FRONT) {
							currentInfo = app;
							break;
						}
					}
				}
				if (currentInfo != null) {
					pkgName = currentInfo.processName;
				}
				return pkgName;
			}



//		}
		return null;
	}

//	private static Handler tableHandler = new Handler();
//	private   Runnable tableRunnable = new Runnable() {
//		public void run() {
//			find();
//			// 获取沽清
//			String ip = configUtils.getStringValue("IP");
//			String port = configUtils.getStringValue("port");
//			P.c("检查沽清");
////			if (ip.length() != 0) {
////				P.c("检查沽清===");
////				connectGu(ip, port);
////			}
//			// HQGQ
//			tableHandler.postDelayed(this, tableDelay);
//
//		}
//	};



	int count = 0;

//	public void dataRefresh() {
//		if (wifi_status != null) {
//			if (sharedUtils.getStringValue(Common.LOCKED_WIFI_NAME).length() != 0) {
//				// 证明有数据存在
//				try {
//					WifiInfo info = mWifiManager.getConnectionInfo();
//					// P.c(info.getBSSID()+"=="+info.getSSID());
//					wifi_status.setText(info.getSSID().replace("\"", ""));
//					wifi_status.setVisibility(View.VISIBLE);
//					// P.c(info.getBSSID()+"=="+info.getSSID());
//					if (!info.getBSSID().equals("00:00:00:00:00:00")) {
//						if (!info
//								.getSSID()
//								.replace("\"", "")
//								.equals(sharedUtils
//										.getStringValue(Common.LOCKED_WIFI_NAME))) {
//							// 切换网络
//
//						}
//						count = 0;
//					} else {
//						count++;
//						if (count > 10) {
//							//sharedUtils.clear();
//						}
//					}
//				} catch (Exception e) {
//					wifi_status.setText("WIFI获取有误");
//					wifi_status.setVisibility(View.VISIBLE);
//				}
//			} else {
//				wifi_status.setText("请先配置WIFI");
//				wifi_status.setVisibility(View.VISIBLE);
//			}
//		}
//		// 进行桌台号的选取
//	/*	if (touch != null) {
//			if (configUtils.getStringValue("table_name").length() != 0) {
//
//				 * if(!touch.getText().toString().equals(configUtils.getStringValue
//				 * ("table_name"))){ FrameLayout.LayoutParams params = new
//				 * FrameLayout
//				 * .LayoutParams(float_service.getMeasuredWidth()-44,4);
//				 * params.setMargins(0, 2, 0, 0); params.gravity =
//				 * Gravity.CENTER_HORIZONTAL;
//				 * pw_spinner.setLayoutParams(params);
//				 * pw_spinner.setVisibility(View.VISIBLE);
//				 *
//				 * }
//
//				touch.setText(configUtils.getStringValue("table_name"));
//
//			} else {
//				touch.setText("请配置桌台号");
//			}
//
//		}*/
//		// 查询桌台
//
//	}

	/**
	 * 查找更新数据
	 */
	private void find() {

		// {'CMD':'TB','CONTENT':{'MhtTableArea':[{"TableAreaId":1},…]
		if(configUtils.getStringValue("table_code").length()!=0&&configUtils.getStringValue("optName").length()!=0	&& configUtils.getStringValue("IP").length() != 0){
				if(configUtils.getStringValue("billId").length()==0){
					viewHandler.sendEmptyMessage(11);

				}
			String ip = configUtils.getStringValue("IP");
			String port = configUtils.getStringValue("port");
			connect(ip, port);
		} else {
			// 什么都没有那么就把桌台状态清掉
			configUtils.clear("person");
			configUtils.clear("billId");
			configUtils.clear("order_time");
			configUtils.clear("bills");
			viewHandler.sendEmptyMessage(33);
			P.c("未知桌台");
		}
	}




	/**
	 * 连接socket
	 */
	private void connect(String ip,String pt){
		P.c("--发送账单查询");
			billTime = System.currentTimeMillis();
			String dev = configUtils.getStringValue("dev");
		    String table = configUtils.getStringValue("table_code");
			final JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			try {
				addItem(jsonArray, "DeviceNo", dev);
				addItem(jsonArray, "TableNo", table);
				jsonObject.put("ZDCX", jsonArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendMsg(jsonObject.toString());

/*


		*/
/*P.c("发送----------------------------");
		if (client.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			client.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				client.connect(ip, port,buffer,true);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {
				 handler.sendEmptyMessage(-1);
				e.printStackTrace();
			}
		}*//*


		String table_code = configUtils.getStringValue("table_code");
		String dev = configUtils.getStringValue("dev");
		String opt= configUtils.getStringValue("optName");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", "getorderbytable");
			jsonObject.put("device_id", dev);
			jsonObject.put("user_code", opt);
			jsonObject.put("tablenum", table_code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		P.c("餐台状态"+jsonObject.toString());
		FileUtils.writeLog(jsonObject.toString(), "餐台状态");
		int port = Integer.parseInt(pt);
		SokectUtils.Builder().timeOut(Common.COMMON_TIME_OUT).setMsg(EncodingUtils.getBytes(jsonObject.toString(), "UTF-8")).send(new Respon() {
			@Override
			public void onSuccess(String str) {
				// TODO Auto-generated method stub
				P.c("查询桌台"+str);
				exit();
				try {
					JSONObject jsonObject = new JSONObject(str);
					if(jsonObject.getString("return").equals("10000")){

						JSONArray array = jsonObject.getJSONArray("order_detail");
						int len = array.length();
						if(len==0){
							//configUtils.clear("person");
							configUtils.clear("billId");
							configUtils.clear("order_time");
							configUtils.clear("bills");
							viewHandler.sendEmptyMessage(11);
						}else{
							JSONObject ob = array.getJSONObject(0);
							configUtils.setStringValue("billId", ob.getString("orderid"));
							try {
								configUtils.setIntValue("person", Integer.parseInt(ob.getString("person_num")));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								configUtils.setIntValue("person", 1);
							}
							configUtils.clear("bills");

							if(len>1){

								StringBuilder builder = new StringBuilder();
								for(int i=0;i<len;i++){
									JSONObject ob0 = array.getJSONObject(i);
									if(i!=len-1){
										builder.append(ob0.getString("orderid")+"_"+ob0.getString("person_num")+",");
									}else{
										builder.append(ob0.getString("orderid")+"_"+ob0.getString("person_num"));
									}

								}
								//P.c("加入"+builder.toString());
								configUtils.setStringValue("bills", builder.toString());
							}

							viewHandler.sendEmptyMessage(22);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onProgress(String progress) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFail() {
				// TODO Auto-generated method stub
				P.c("失败");
			}

			@Override
			public void timeOut() {
				// TODO Auto-generated method stub
				P.c("超时");
			}
		}, "NULL",port,"");

*/


	}

	private void exit(){
		String pt = configUtils.getStringValue("port");
		int port = Integer.parseInt(pt);
		String dev = configUtils.getStringValue("dev");
		String opt= configUtils.getStringValue("optName");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", "logout");
			jsonObject.put("device_id", dev);
			jsonObject.put("user_code", opt);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		SokectUtils.Builder().timeOut(Common.COMMON_TIME_OUT).setMsg(EncodingUtils.getBytes(jsonObject.toString(), "UTF-8")).send(new Respon() {
			@Override
			public void onSuccess(String str) {
				// TODO Auto-generated method stub
				P.c("登出"+str);

			}

			@Override
			public void onFail() {

			}

			@Override
			public void onProgress(String progress) {
				// TODO Auto-generated method stub

			}



			@Override
			public void timeOut() {
				// TODO Auto-generated method stub
				P.c("超时");
			}
		}, "NULL",port,"");
	}


	/**
	 * 沽清
	 */

	/**
	 * 沽清
	 */
	private void addItem(JSONArray array,String key,Object value) throws JSONException{
		JSONObject o1 = new JSONObject();
		o1.put("Key", key);
		o1.put("Value", value);
		array.put(o1);
	}
	private void connectGu(String ip,String pt){
		String dev = configUtils.getStringValue("dev");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			jsonObject.put("GQ", jsonArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SokectUtils.Builder().timeOut(Common.COMMON_TIME_OUT).setMsg(EncodingUtils.getBytes(jsonObject.toString(), "UTF-8")).send(new Respon() {

			@Override
			public void onSuccess(String buffer) {
				// TODO Auto-generated method stub

				Common.guKeys.clear();
//				无估清
				P.c("返回数据。。"+buffer);
				try {
					JSONObject object = new JSONObject(buffer.substring(0,buffer.length()-5));
					String msg = object.getString("value");
					if(msg.contains("无沽清")){

					}else{
						String []tmep = msg.split(";");
						for(int i=0;i<tmep.length;i++){
							String s = "";

							if(tmep[i].length()>4){
								s = tmep[i].substring(0,4).replace(" ", "").trim();
							}


							if(s.length()==4){
								//就是菜品编码
								Common.guKeys.put(s, 0);

							}
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				/*String str[] = strs.split("@");
				if(str[0].equals("0")){
					for(int i=0;i<str.length;i++){
						if(i!=0){
							Common.guKeys.put(str[i], 0);
						}
					}
				}*/
			}

			@Override
			public void onProgress(String progress) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFail() {
				// TODO Auto-generated method stub

			}

			@Override
			public void timeOut() {
				// TODO Auto-generated method stub

			}
		}, "<EOF>",";");




	/*	P.c("发送----------------------------");
		if (guClient.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			guClient.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				guClient.connect(ip, port,buffer);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				P.c("解析异常");
			}
		}*/

	}
	TcpClient guClient = new TcpClient() {

		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			// TODO Auto-generated method stub
			Common.guKeys.clear();
			String str[] = buffer.split("@");
			if(str[0].equals("0")){
				for(int i=0;i<str.length;i++){
					if(i!=0){
						Common.guKeys.put(str[i], 0);
					}
				}
			}
			if(guClient!=null){
				guClient.disconnect();
			}
		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectFailed() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			String userCode = configUtils.getStringValue("optName");
			String dev = configUtils.getStringValue("dev");
			transceiver.send("soldOut="+dev+"$"+userCode);
			P.c("soldOut="+dev+"$"+userCode);
		}
	};


	/**
	 * 查询当前桌台的订单
	 *此接口禁止平板使用，会锁定桌台
	 */
	private void sel(){
//		getorderbytable

		String ip = configUtils.getStringValue("IP");
		String port = configUtils.getStringValue("port");
		connectSel(ip, port);
	}
	private void connectSel(String ip,String pt){
		P.c("发送----------------------------");
		if (selClient.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			selClient.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				selClient.connect(ip, port,buffer);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {
				handler.sendEmptyMessage(-1);
				e.printStackTrace();
			}
		}

	}

	private TcpClient selClient = new TcpClient() {

		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			// TODO Auto-generated method stub
			P.c("获得的桌台未结账信息"+buffer);

			if(selClient!=null&&selClient.isConnected()){
				selClient.disconnect();
			}
		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectFailed() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
//			  {"CMD":"TB","CONTENT":{"MhtTable":[{"OptimisticLockField":0,"TableId":-1}]}}
			// P.c("{\"CMD\":\"TB\",\"CONTENT\":{\"MhtTable\":[{\"OptimisticLockField\":0,\"TableId\":"+tableid+"}]}}");
			transceiver.send("getorderbytable="+FileUtils.mac()+"$"+configUtils.getStringValue("optName")+"$"+configUtils.getStringValue("table_code"));
//			 {"CMD":"TB","CONTENT":{"MhtTable":[{"OptimisticLockField":0,"TableId":22}]}}
		}
	};

	private class ViewHandler extends Handler {
		WeakReference<FloatService> mLeakActivityRef;

		public ViewHandler(FloatService leakActivity) {
			mLeakActivityRef = new WeakReference<FloatService>(leakActivity);
		}

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			if (mLeakActivityRef.get() != null) {

				switch (msg.what) {
					case 223:
						NewDataToast.makeText("用户列表已更新");
						break;
					case 222:
						NewDataToast.makeText("已通知打印");
						break;
					case 111:
						P.c("打印结账单");
						Intent intent = new Intent(FloatService.this,CardValActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("print", "");
						startActivity(intent);

						break;
					case 11:
						touch_view.setVisibility(View.VISIBLE);
						touch_view.setText("空闲");
						touch_view.setTextColor(getResources().getColor(R.color.white));
						// touch_view.setTextColor(getResources().getColor(R.color.green));
						if (tip_Utils.getBuy()) {
							tip_Utils.setFirst(false);
							tip_Utils.setWel(false);
							tip_Utils.setBuy(false);
						}
						break;
					case 22:
						touch_view.setVisibility(View.VISIBLE);
						touch_view.setText("就餐中");

						touch_view.setTextColor(getResources().getColor(R.color.red));
						// touch_view.setTextColor(getResources().getColor(R.color.red));
						break;
					case 33:
						touch_view.setVisibility(View.VISIBLE);
						touch_view.setText("未知桌台");
						// touch_view.setTextColor(getResources().getColor(R.color.grey));
						break;
					default:
						break;

				}
			}

		}
	}
	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(float_service, wmParams);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	public void onDestroy() {
		if (batteryReceiver != null) {
			unregisterReceiver(batteryReceiver);
		}


		handler.removeCallbacks(task);
//		tableHandler.removeCallbacks(tableRunnable);
		wm.removeView(float_service);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// ------------------------提交数据到服务器
	private String serverURL = "http://www.haidiyun.top";

	public String getDeviceId() {
		// 根据Wifi信息获取本地Mac
		String ANDROID = "";
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info != null) {
			ANDROID = info.getMacAddress();
		}
		return ANDROID;
	}

	public boolean isNec(String urlString) throws IOException {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		String is = pm.isScreenOn() ? "1" : "0";
		String mac = getDeviceId();
		URL url = new URL(urlString
				+ "/WebService/WebService.asmx/DeviceStatus?lcd_On=" + is
				+ "&mac=" + mac);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.connect();
		return httpUrlConnection.getResponseCode() == 200 ? true : false;
	}

	/*private int sdelaytime = 1000 * 60 * 5;
	private Handler shandler = new Handler();
	private Runnable stask = new Runnable() {
		public void run() {
			dataRefresh();
			shandler.postDelayed(this, sdelaytime);
			// 在这里进行电量的控制变化
		}

		private void dataRefresh() {
			// TODO Auto-generated method stub

			new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					try {
						// 没有“/”形式的http://域名
						for (int i = 0; i < 3; i++)
							if (isNec(serverURL)) {
								sdelaytime = 1000 * 60 * 60;
								break;
							} else {
								sdelaytime = 1000 * 60 * 5;
							}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
	};*/

}
