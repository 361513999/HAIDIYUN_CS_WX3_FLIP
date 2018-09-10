package pad.com.haidiyun.www.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.FtpListener;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.TcpClient;
import pad.com.haidiyun.www.service.FloatService;
import pad.com.haidiyun.www.utils.CopyFile;
import pad.com.haidiyun.www.utils.FTPDownloadFile;
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
 * 同步资源
 *
 * @author Administrator
 *
 */
public class CommonSnyDataPop {
	private Context context;
	private TextView load_tv;
	private IDialog dlg;
	private String msg;
	private ImageView load_img, load_close;
	private ViewHandler handler;
	private class ViewHandler extends Handler {
		WeakReference<CommonSnyDataPop> mLeakActivityRef;
		public ViewHandler(CommonSnyDataPop leakActivity) {
			mLeakActivityRef = new WeakReference<CommonSnyDataPop>(leakActivity);
		}
		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			if (mLeakActivityRef.get() != null) {
				switch (msg.what) {
					case -1:
						NewDataToast.makeText("请检查与主机的连接配置");
						cancle();
						break;
					case -2:
						NewDataToast.makeText("FTP登录失败,请检查FTP配置");
						cancle();
						break;
					case -3:
						NewDataToast.makeText("不能取消同步");
						cancle();
						break;
					case -4:
						NewDataToast.makeText("FTP数据同步失败,请检查FTP目录结构");
						cancle();
						break;
					case -5:
						NewDataToast.makeText("同步桌台出错");
						cancle();
						break;
					case 0:
						NewDataToast.makeText((String) msg.obj);
						cancle();
						break;
					case 11:
						String ip = sharedUtils.getStringValue("IP");
						String port = sharedUtils.getStringValue("port");
						//connect(ip, port);
						new Thread(){
							@Override
							public void run() {
								super.run();
								Connection conn = openConnection();
								query(conn,"select tablenum,tblname from storetables_mis order by tblname");
								closeDB(conn);
							}
						}.start();


						break;
					case 10:
						new Thread() {
							public void run() {
								String DATABASE_PATH = "/data/data/"
										+ context.getPackageName() + "/databases/";
								File file = new File(DATABASE_PATH);
								if (!file.exists()) {
									file.mkdirs();
								}
								CopyFile cf = new CopyFile();
								cf.delAllFile(DATABASE_PATH);
								cf.copyFile(Common.SD + Common.SOURCE_DB, DATABASE_PATH
										+ Common.SOURCE_DB);
								handler.sendEmptyMessage(1);
							};
						}.start();
						break;
					case 1:
						NewDataToast.makeText("同步完成");
						// 清除一些数据
						sharedUtils.clear("table_name");
						sharedUtils.clear("table_code");
						Common.LAST_PAGE = 0;
					/*
					 * Intent intent = new Intent(); //FlipNextActivity中定义的广播action
					 * intent.setAction("app.data.updata");
					 * context.sendBroadcast(intent);
					 */
						cancle();
						BaseApplication.application.resetApplicationAll();
						break;
					case 2:
						if (load_tv != null) {
							load_tv.setText((String) msg.obj);
						}
						break;
				}

			}
		}
	}



	/**
	 * 连接socket
	 */
	private void connect(String ip, String pt) {
		P.c("发送----------------------------");
		if (client.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			client.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				client.connect(ip, port, buffer,true);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {
				handler.sendEmptyMessage(-1);
				e.printStackTrace();
			}
		}

	}

	public   void query(Connection conn, String sql) {
		P.c("conn"+(conn==null));
		if (conn == null) {
            handler.sendEmptyMessage(1);
			return;
		}
		Statement statement = null;
		ResultSet result = null;
		try {
			statement = conn.createStatement();
			result = statement.executeQuery(sql);
			if (result != null && result.first()) {

				int idColumnIndex = result.findColumn("tablenum");
				int nameColumnIndex = result.findColumn("tblname");


				ArrayList<TableBean> tbs = new ArrayList<TableBean>();

				while (!result.isAfterLast()) {

					TableBean bean = new TableBean();

					bean.setName(result.getString(nameColumnIndex));
					bean.setCode(result.getString(idColumnIndex));
					tbs.add(bean);

					result.next();
				}
				DB.getInstance().clear("board");
				DB.getInstance().addBoard(tbs);

			}
		} catch (Exception e) {
			e.printStackTrace();
            handler.sendEmptyMessage(1);
		}   finally {
			try {
				if (result != null) {
					result.close();
					result = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					conn = null;
				} finally {
					conn = null;
				}
			}
			P.c("还执行吗");
            handler.sendEmptyMessage(1);
		}
	}
	public void closeDB(Connection conn)
	{
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
	public Connection openConnection() {
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



	private TcpClient client = new TcpClient() {

		@Override
		public void onReceive(SocketTransceiver transceiver, final String buffer) {
			ArrayList<TableBean> tbs = new ArrayList<TableBean>();
			// TODO Auto-generated method stub
			// FileUtils.write(Common.SD+Common.tables+"12553", false, buffer);
			/*new Thread() {
				public void run() {
					try {
					String s[] = buffer.split(";");
					ArrayList<TableBean> tbs = new ArrayList<TableBean>();
					for (int i = 0; i < s.length; i++) {
						String c[] = s[i].split("@");
						TableBean bean = new TableBean();
						bean.setName(c[5]);
						bean.setCode(c[3]);
						tbs.add(bean);
					}
						DB.getInstance().clear("board");
						DB.getInstance().addBoard(tbs);
						handler.sendEmptyMessage(1);
					} catch (Exception e) {
						// TODO: handle exception
						handler.sendEmptyMessage(-5);
					}

				};
			}.start();*/
//			System.out.println(buffer);
			try {
				JSONObject jsonObject = new JSONObject(buffer);
				if(jsonObject.getString("return").equals("10000")){
					//成功
					JSONArray array = jsonObject.getJSONArray("table_detail");
					int len = array.length();
					if(len<=0){
						return;
					}
					tbs.clear();
					for(int i=0;i<len;i++){
						TableBean bean = new TableBean();
						JSONObject o = array.getJSONObject(i);
						bean.setName(o.getString("tablename"));
						bean.setCode(o.getString("tablenum"));
						tbs.add(bean);
					}
					DB.getInstance().clear("board");
					DB.getInstance().addBoard(tbs);
					handler.sendEmptyMessage(1);
				}else{
					handler.sendEmptyMessage(-5);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(client!=null&&client.isConnected()){
					client.disconnect();
				}
			}
			// downMsg.view(buffer.substring(0,buffer.length()-5));
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
		/*	TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			// 连接成功开始登陆
			boolean flag = transceiver.send("listTables=" + FileUtils.mac()
					+ "$$$$$");
			if (!flag) {
				handler.sendEmptyMessage(-1);
			}*/

			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			// 连接成功开始登陆
			//
//			{"method":"gettablelist","device_id":"1234","user_code":"2"}
			String dev = sharedUtils.getStringValue("dev");
			String opt= sharedUtils.getStringValue("optName");
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("method", "gettablelist");
				jsonObject.put("device_id", dev);
				jsonObject.put("user_code", opt);

				boolean flag = transceiver.send(jsonObject.toString());
				if (!flag) {
					handler.sendEmptyMessage(-1);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			// 读取超时
			if (client != null) {
				client.disconnect();
			}
			handler.sendEmptyMessage(-5);
		}
	};

	private boolean deleteDir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	private static void copyFileUsingFileStreams(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	private SharedUtils sharedUtils;
	private FTPDownloadFile df;

	public CommonSnyDataPop(Context context, String msg) {
		this.context = context;
		this.msg = msg;
		handler = new ViewHandler(this);
		sharedUtils = new SharedUtils(Common.CONFIG);
		df = new FTPDownloadFile(listener, handler);
	}

	public synchronized Dialog showSheet() {
		dlg = new IDialog(context, R.style.buy_pop_style);
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.loading,
				null);
		load_tv = (TextView) layout.findViewById(R.id.load_tv);
		load_tv.setText("同步数据");
		load_img = (ImageView) layout.findViewById(R.id.load_img);
		load_close = (ImageView) layout.findViewById(R.id.load_close);
		loadImage();
		load_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						if (df != null) {
							df.close();
						}
					}
				}.start();

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

	private void cancle() {
		if (dlg != null && dlg.isShowing()) {
			dlg.cancel();
			dlg = null;
		}
	}

	private void sendMsg(int what, String tip) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = tip;
		handler.sendMessage(msg);
	}

	private FtpListener listener = new FtpListener() {

		@Override
		public void login_status(boolean is) {
			// TODO Auto-generated method stub
			if (!is) {
				handler.sendEmptyMessage(-2);
			}
		}

		@Override
		public void down_success(boolean is) {
			// TODO Auto-generated method stub
			if (is) {
				handler.sendEmptyMessage(10);
			} else {
				handler.sendEmptyMessage(-4);
			}
		}

	};

	private void loadImage() {
		new Thread() {
			public void run() {

				try {
					df.downloadStand();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}

			};
		}.start();
	}

}
