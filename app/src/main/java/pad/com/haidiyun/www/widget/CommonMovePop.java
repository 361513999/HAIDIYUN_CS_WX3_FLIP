package pad.com.haidiyun.www.widget;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.LoginS;
import pad.com.haidiyun.www.inter.SetI;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.TcpClient;
import pad.com.haidiyun.www.service.FloatService;
import pad.com.haidiyun.www.ui.MainFragmentActivity;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CommonMovePop {
	private Context context;
	/**
	 * 删除弹出框
	 */
	private IDialog dlg;
	private LinearLayout   par;
	private View move_layout;

	private ImageView qr;
	private ImageButton it10, it11;
	private SharedUtils sharedUtils;

	public CommonMovePop(Context context) {
		this.context = context;
		sharedUtils = new SharedUtils(Common.CONFIG);
	}

	private Bitmap drawableToBitamp(Drawable drawable) {
		Bitmap bitmap;
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		bitmap = Bitmap.createBitmap(w, h, config);
		// 注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	private LoginS ls = new LoginS() {

		@Override
		public void success() {
			// TODO Auto-generated method stub
			sendPop = null;
			close();
		}
	};
	public   Connection openConnection() {
		final String URL = "jdbc:mysql://" + sharedUtils.getStringValue("IP") + "/mis";
		final String USER = "misuser";
		final String PASSWORD = "misuserchoice";
		Connection conn = null;
		try {
			final String DRIVER_NAME = "com.mysql.jdbc.Driver";
			Class.forName(DRIVER_NAME);
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			conn = null;
		} catch (SQLException e) {
			conn = null;
		}
		return conn;
	}
	public   boolean execSQL(Connection conn, String sql) {
		boolean execResult = false;
		if (conn == null) {
			return execResult;
		}
		Statement statement = null;
		try {
			statement = conn.createStatement();
			if (statement != null) {
				execResult = statement.execute(sql);
			}
		} catch (SQLException e) {
			execResult = false;
		}

		return execResult;
	}
	private void closeMysql(Connection conn){
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				conn = null;
			} finally {
				conn = null;
			}
		}
	}
	private Common433SendPop sendPop;
	public Dialog showSheet() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		dlg = new IDialog(context, R.style.config_pop_style1);
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.move_pop, null);
		par = (LinearLayout) layout.findViewById(R.id.par);
		move_layout = layout.findViewById(R.id.move_layout);

		qr = (ImageView) layout.findViewById(R.id.qr);
		it10 = (ImageButton) layout.findViewById(R.id.it10);
		it11 = (ImageButton) layout.findViewById(R.id.it11);

		dlg.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {
				// TODO Auto-generated method stub
				FileUtils.start(Effectstype.Flipv, par);
			}
		});
		par.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				close();
			}
		});



		it10.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(loadSendPop==null){
					loadSendPop = new CommonLoadSendPop(context, "解锁桌台");//解锁桌台
					loadSendPop.setCancel(new SetI() {

						@Override
						public void click() {
							// TODO Auto-generated method stub
							loadSendPop.cancle();
							loadSendPop = null;
						}
					});
					loadSendPop.showSheet(false);
				}
				// TODO Auto-generated method stub
				String ip = sharedUtils.getStringValue("IP");
				String port = sharedUtils.getStringValue("port");
				//connectOd(ip, port);
				final String table = sharedUtils.getStringValue("table_code");
				if(table==null||table.length()==0){
					NewDataToast.makeText("请先选择桌台");
					return;
				}
				if(table.length()!=0){
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							Connection con = openConnection();
							execSQL(con, "UPDATE storetables_mis SET USESTATE=3 WHERE TABLENUM="+table);
							execSQL(con, "update handevtableorder_relation set tablestate=0,PREPRINTYN='2' where tablenum="+table);
							closeMysql(con);
							handler.sendEmptyMessage(2);
						}
					}.start();
				}


			}
		});
		it11.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				P.c("启动结账");

				if(sharedUtils.getStringValue("billId").length()!=0){
					print();
					NewDataToast.makeText("通知打印账单");
					close();
				}else{
					NewDataToast.makeText("请先开台");
				}

			}
		});



		move_layout = layout.findViewById(R.id.move_layout);
		final int cFullFillWidth = dm.widthPixels;
		layout.setMinimumWidth(cFullFillWidth);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
	private void addItem(JSONArray array, String key, Object value) throws JSONException{
		JSONObject o1 = new JSONObject();
		o1.put("Key", key);
		o1.put("Value", value);
		array.put(o1);
	}
	private void print(){
		String dev = sharedUtils.getStringValue("dev");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			addItem(jsonArray, "MchID", FileUtils.getDeviceId());
			addItem(jsonArray, "BillNo", sharedUtils.getStringValue("billId"));
			addItem(jsonArray, "TableNo", sharedUtils.getStringValue("table_code"));
			addItem(jsonArray,"User",sharedUtils.getStringValue("optName"));
			jsonObject.put("DYDC", jsonArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(context,FloatService.class);
		intent.putExtra("var", jsonObject.toString());
		context.startService(intent);

	}

	private void close() {
		if (dlg != null && dlg.isShowing()) {
			dlg.cancel();
			dlg = null;
		}
	}
	private CommonLoadSendPop loadSendPop;




	// 查询账单
	private void connectOd(String ip, String pt) {
		if (odClient.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			odClient.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				odClient.connect(ip, port, buffer,true);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

	}

	TcpClient odClient = new TcpClient() {

		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			// TODO Auto-generated method stub
			P.c(buffer);
			// 查询到价格就开始生成二维码
			try {
				JSONObject jsonObject = new JSONObject(buffer);
				if (jsonObject.getString("return").equals("10000")) {
					//
					String ip = sharedUtils.getStringValue("IP");
					String port = sharedUtils.getStringValue("port");
					connectPay(ip, port, jsonObject.getString("yfamount"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if (odClient != null) {
					odClient.disconnect();
				}
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
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("method", "querybillsdetail");
				jsonObject.put("device_id", sharedUtils.getStringValue("dev"));
				jsonObject.put("user_code",
						sharedUtils.getStringValue("optName"));
				jsonObject.put("orderid", sharedUtils.getStringValue("billId"));
				jsonObject.put("tablenum",
						sharedUtils.getStringValue("table_code"));
				jsonObject.put("querytype", "1");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			transceiver.send(jsonObject.toString());

		}
	};

	// 支付
	// 查询账单
	private void connectPay(String ip, String pt, final String price) {

		try {
			int port = Integer.parseInt(pt);
			StringBuffer buffer = new StringBuffer();
			new TcpClient() {
				@Override
				public void readTimeOut(SocketTransceiver transceiver) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onReceive(SocketTransceiver transceiver,
									  String buffer) {
					// TODO Auto-generated method stub
					// 查询到价格就开始生成二维码
					P.c(buffer);
					try {
						JSONObject jsonObject = new JSONObject(buffer);
						if (jsonObject.getString("return").equals("10000")) {
							Message msg = new Message();
							msg.what = 1;
							msg.obj = jsonObject.getString("urlcode");
							handler.sendMessage(msg);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						this.disconnect();
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
					TelephonyManager tm = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					String deviceid = tm.getDeviceId();
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("method", "thirdonlinepay");
						jsonObject.put("device_id",
								sharedUtils.getStringValue("dev"));
						jsonObject.put("user_code",
								sharedUtils.getStringValue("optName"));
						jsonObject.put("auth_code", "");
						jsonObject.put("amount", price);
						jsonObject.put("orderid",
								sharedUtils.getStringValue("billId"));
						jsonObject.put("paytype", "2");
						jsonObject.put("actiontype", "2");
						jsonObject.put("timestamp",
								String.valueOf(System.currentTimeMillis()));
						jsonObject.put("out_trade_no", "");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					transceiver.send(jsonObject.toString());

				}
			}.connect(ip, port, buffer,true);

			P.c("连接后台----------------------------");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
				case 1:
					createImage((String) msg.obj);
					if(loadSendPop!=null){
						loadSendPop.cancle();
						loadSendPop = null;
					}
					break;
				case 2:
					NewDataToast.makeText("已解锁");
					if(loadSendPop!=null){
						loadSendPop.cancle();
						loadSendPop = null;
					}
					close();
					break;
				case 3:
					NewDataToast.makeText("已打印");
					if(loadSendPop!=null){
						loadSendPop.cancle();
						loadSendPop = null;
					}
					break;
				default:
					break;
			}
		};
	};

	// 生成QR图
	private void createImage(String formatValue) {
		try {
			// 需要引入core包
			int QR_WIDTH = 150;
			int QR_HEIGHT = 150;
			QRCodeWriter writer = new QRCodeWriter();

			if (formatValue == null || "".equals(formatValue)
					|| formatValue.length() < 1) {
				return;
			}

			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(formatValue,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
			// 二维码的code
			System.out.println("w:" + martix.getWidth() + "h:"
					+ martix.getHeight());

			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 0);
			BitMatrix bitMatrix = new QRCodeWriter().encode(formatValue,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			qr.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
