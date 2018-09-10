package pad.com.haidiyun.www.widget;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.MediaType;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.SelectPresonAdapter;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.inter.BuyClick;
import pad.com.haidiyun.www.inter.LoadBuy;
import pad.com.haidiyun.www.net.Respon;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.SokectUtils;
import pad.com.haidiyun.www.net.TcpClient;
import pad.com.haidiyun.www.service.FloatService;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommonPersonPop {
	private Context context;
	/**
	 * 下单提示
	 */
	private   IDialog dlg;
	private BuyClick buyClick;
	private SharedUtils sharedUtils;
	private   TextView search_txt;
	private LoadBuy loginBuy;
	private String optName,optPass;

	public CommonPersonPop(Context context,BuyClick buyClick,LoadBuy loginBuy,String optName,String optPass ) {
		this.context = context;
		this.buyClick = buyClick;
		this.loginBuy = loginBuy;
		this.optName = optName;
		this.optPass = optPass;
		sharedUtils = new SharedUtils(Common.CONFIG);
	}

	private long lastClick;
	private String zm[] = new String[]{"1","2","3","4","5","6","7","8","9"};
	public  Dialog showSheet() {


		dlg = new IDialog(context, R.style.menu_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.select_num, null);
		SelectPresonAdapter searchAdapter = new SelectPresonAdapter(context, zm);
		GridView select_num_view= (GridView) layout.findViewById(R.id.select_num_view);
		TextView cancle = (TextView) layout.findViewById(R.id.cancle);
		TextView send = (TextView) layout.findViewById(R.id.send);
		search_txt = (TextView) layout.findViewById(R.id.search_txt);
		TextView zero = (TextView) layout.findViewById(R.id.zero);
		ImageView sear_del = (ImageView) layout.findViewById(R.id.sear_del);
		select_num_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				try{
					int num =  Integer.parseInt(search_txt.getText().toString()
							+ zm[arg2]);
					if(num<=100){
						search_txt.setText(String.valueOf(num));
					}
				}catch(NumberFormatException e){
					NewDataToast.makeText( "输入错误");
				}


			}
		});
		zero.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				try{
					int num =  Integer.parseInt(search_txt.getText().toString()
							+  "0");
					if(num<=100){
						search_txt.setText(String.valueOf(num));
					}
				}catch(NumberFormatException e){
					NewDataToast.makeText( "输入错误");
				}
			}
		});
		sear_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/**
				 * 删除数据
				 */
				if(search_txt.getText().toString().length() != 0){
					String temp = search_txt.getText().toString();
					search_txt.setText(temp.subSequence(0, temp.length() - 1));
				}else{

				}
			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				close();
			}
		});

		//防止快速点击
		send.setOnClickListener(new OnClickListener() {



			@Override
			public void onClick(View arg0) {
				//防止快速点击
				if (System.currentTimeMillis() - lastClick <= 2000) {
					return;
				}
				lastClick = System.currentTimeMillis();
				// TODO Auto-generated method stub

				try{
					int person = Integer.parseInt(search_txt.getText().toString());
					if(person==0){
						NewDataToast.makeText( "请输入就餐人数");
						return;
					}
				}catch(NumberFormatException e){
					NewDataToast.makeText( "输入错误");
					return;
				}

				try{
					if(loadSendPop==null){
						loadSendPop = new CommonLoadSendPop(context, "正在为您开台");
						loadSendPop.showSheet(false);
					}
					//二次桌台
					//find();

					String ip = sharedUtils.getStringValue("IP");
					String pt = sharedUtils.getStringValue("port");
					//connect(ip, pt);
//					commonLogin(optName,optPass);
					commonKT();
				}catch(NumberFormatException e){

				}

			}
		});
		select_num_view.setAdapter(searchAdapter);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
	private void addItem(JSONArray array,String key,Object value) throws JSONException{
		JSONObject o1 = new JSONObject();
		o1.put("Key", key);
		o1.put("Value", value);
		array.put(o1);
	}
	/**
	 * 用户登录操作
	 * @param u
	 * @param p
	 */
	private void commonLogin(String u,String p){
		//登录操作

		String ip = sharedUtils.getStringValue("IP");
		String dev = sharedUtils.getStringValue("dev");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			addItem(jsonArray, "MchID", FileUtils.getDeviceId());
			addItem(jsonArray, "User", u);
			addItem(jsonArray, "Pwd", p);
			jsonObject.put("DL", jsonArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer builder = new StringBuffer();

		FileUtils.writeLog(jsonObject.toString(), "开台登录结果");
		SokectUtils.Builder().timeOut(6000).setMsg(EncodingUtils.getBytes(jsonObject.toString()+"<EOF>", "UTF-8")).send(new Respon() {

			@Override
			public void onSuccess(String buffer) {
				// TODO Auto-generated method stub
				FileUtils.writeLog(buffer, "开台登录结果");
				try {
					JSONObject object = new JSONObject(buffer.substring(0,buffer.length()-5));
					String msg = object.getString("value");
					if(msg.contains("登录成功")){
						//登录成功,执行开台
						commonKT();

					}else{
						handler.sendEmptyMessage(37);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("解析异常"+buffer);
				}
			}

			@Override
			public void onProgress(String progress) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFail() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(-1);
			}

			@Override
			public void timeOut() {
				// TODO Auto-generated method stub

			}
		},"<EOF>","");


	}

	//开台操作
	private void commonKT(){
		String dev = sharedUtils.getStringValue("dev");
		String ip = sharedUtils.getStringValue("IP");
		String table = sharedUtils.getStringValue("table_code");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			addItem(jsonArray, "TableNo", table);
			addItem(jsonArray, "User", optName);
			addItem(jsonArray, "Heads", Integer.parseInt(search_txt.getText().toString()));
			//  boolean flag = transceiver.send("startc="+deviceId+"$"+optName+"$"+table+"$"+Integer.parseInt(search_txt.getText().toString())+"$"+0+"$"+1+"$"+0);
			jsonObject.put("KT", jsonArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileUtils.writeLog(jsonObject.toString(), "开台提交");
		Intent intent = new Intent(context,FloatService.class);
		intent.putExtra("var", jsonObject.toString()+"<EOF>");

		sharedUtils.setIntValue("person", Integer.parseInt(search_txt.getText().toString()));
//		intent.putExtra("person", Integer.parseInt(search_txt.getText().toString()));
		context.startService(intent);





	}



	private void find(){
		//{'CMD':'TB','CONTENT':{'MhtTableArea':[{"TableAreaId":1},…]
		if(sharedUtils.getStringValue("table_name").length()!=0&&sharedUtils.getStringValue("IP").length()!=0){
			/*
				{
					  "Cmd":"CXWJZD",
					  "Lid":"001",
					  "Pid":"1C-6F-65-7D-24-61",
					  "Lan":1,
					  "BillName":"101台",
					}*/
			String mac = FileUtils.getDeviceId();
			String ip = sharedUtils.getStringValue("IP");
			OkHttpUtils .postString()
					.url(U.VISTER(ip, U.URL))
					.mediaType(MediaType.parse("application/json; charset=gb2312"))
					.content("{\"Cmd\":\"CXWJZD\",\"Lid\":\""+mac+"\",\"Pid\":\""+mac+"\",\"Lan\":1,\"BillName\":\""+sharedUtils.getStringValue("table_name")+"\"}")
					.build()
					.execute(tableCallback);
		}else{
			//什么都没有那么就把桌台状态清掉

		}
	}
	private StringCallback tableCallback  =new StringCallback() {

		@Override
		public void onResponse(String response, int id) {
			// TODO Auto-generated method stub


			try {
				JSONObject jsonObject = new JSONObject(response);
				int status = jsonObject.getInt("Code");
				switch (status) {
					case 1:
						JSONArray json = jsonObject.getJSONArray("Bills");
						int len = json.length();
						if(len==1){
							JSONObject obj = json.getJSONObject(0);
							NewDataToast.makeTextL( "【"+obj.getString("BillName")+"】已开台,请联系服务员或几秒后重新下单", 3000);
						}else{
							String ip = sharedUtils.getStringValue("IP");


						}
						break;
					case 0:
						//异常
						break;
					default:
						break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onError(Call call, Exception e, int id) {
			// TODO Auto-generated method stub
			NewDataToast.makeText( "连接失败");
		}
	};
	private CommonLoadSendPop loadSendPop;



	/**
	 * 数据库连接
	 */

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

				execResult = statement.execute(sql );

			}
		} catch (SQLException e) {
			execResult = false;
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
		return execResult;
	}
	public   Connection execSQLNot(Connection conn, String  sql) {
		boolean execResult = false;
		if (conn == null) {
			return null;
		}
		Statement statement = null;
		try {
			statement = conn.createStatement();
			if (statement != null) {
				P.c("执");

				statement.executeUpdate(sql);

				P.c("执行情况"+execResult);
			}
		} catch (SQLException e) {
			execResult = false;
		}

		return conn;
	}

	/** 执行一批SQL语句 */
	public static int[] goBatch(Connection con, String[] sqls) throws Exception {
		if (sqls == null) {
			return null;
		}
		Statement sm = null;
		try {
			sm = con.createStatement();
			for (int i = 0; i < sqls.length; i++) {
				P.c("执行吗"+sqls[i]);
				sm.addBatch(sqls[i]);// 将所有的SQL语句添加到Statement中
			}
			// 一次执行多条SQL语句
			return sm.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sm.close();
		}
		return null;
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
	public   void query(Connection conn, String sql) {
		if (conn == null) {
			return;
		}
		Statement statement = null;
		ResultSet result = null;
		try {
			statement = conn.createStatement();
			result = statement.executeQuery(sql);
			if (result != null && result.first()) {

				int idColumnIndex = result.findColumn("login_name");
				int nameColumnIndex = result.findColumn("password");
				int usernameColumnIndex = result.findColumn("user_name");
				System.out
						.println("id\t\t" + "password\t\t\t\t\t" + "username");
				while (!result.isAfterLast()) {
					System.out.print(result.getString(idColumnIndex) + "\t\t");
					System.out
							.print(result.getString(nameColumnIndex) + "\t\t");
					System.out.println(result.getString(usernameColumnIndex));
					result.next();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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

		}
	}
	/**
	 * 操作完毕
	 */
//	{"method": "logout","device_id": "1234","user_code": "2"};

	/**
	 * 连接socket
	 */
	private void connectOut(String ip,String pt){
		P.c("发送----------------------------");
		if (Outclient.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			Outclient.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				Outclient.connect(ip, port,buffer,true);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {

				e.printStackTrace();
			}
		}
	}
	TcpClient Outclient= new TcpClient() {

		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(-5);
		}

		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			handler.sendEmptyMessage(0);
			if(Outclient!=null&&Outclient.isConnected()){
				Outclient.disconnect();
			}

		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectFailed() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(-1);
		}

		@Override
		public void onConnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("method", "logout");
				jsonObject.put("device_id", sharedUtils.getStringValue("dev"));
				jsonObject.put("user_code",
						optName);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			transceiver.send(jsonObject.toString());

		}
	};
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case 37:
					NewDataToast.makeText("登录失败");
					if(loadSendPop!=null){
						loadSendPop.cancle();
						loadSendPop = null;
					}
					close();

					break;
				case -1:
					NewDataToast.makeTextL("开台失败",2000);
					if(loadSendPop!=null){
						loadSendPop.cancle();
						loadSendPop = null;
					}
					close();
					break;
				case 0:
					if(loginBuy==null){
						buyClick.person();
					}else{
						buyClick.person(loginBuy,optName,optPass);
					}
					close();
					break;
				case 2:
					String ip = sharedUtils.getStringValue("IP");
					String pt = sharedUtils.getStringValue("port");
					connectOut(ip, pt);
					break;
				case 3:
					NewDataToast.makeTextL("茶位费自动添加失败,请重新开台",2000);
					if(loadSendPop!=null){
						loadSendPop.cancle();
						loadSendPop = null;
					}
					break;
				case 4:
					NewDataToast.makeTextL("自动开台失败，请重试",2000);
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
	public void close(){
		if(loadSendPop!=null){
			loadSendPop.cancle();
			loadSendPop = null;
		}
		if(dlg!=null){
			dlg.cancel();
			dlg = null;
		}
	}
}
