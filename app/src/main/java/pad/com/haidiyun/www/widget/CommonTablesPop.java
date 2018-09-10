package pad.com.haidiyun.www.widget;

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
import pad.com.haidiyun.www.adapter.TablesListAdapter;
import pad.com.haidiyun.www.bean.BillsBean;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.SelectTable;
import pad.com.haidiyun.www.inter.SetI;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.TcpClient;
import pad.com.haidiyun.www.service.FloatService;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class CommonTablesPop {
	private Context context;
	/**
	 * 删除弹出框
	 */
	private SharedUtils sharedUtils;
	private String optPass;
	public CommonTablesPop(Context context,SelectTable selectTable,String optName,String optPass) {
		this.context = context;
		this.selectTable = selectTable;
		this.optName = optName;
		this.optPass = optPass;
		sharedUtils = new SharedUtils(Common.CONFIG);
	}
	private String optName;
	private SelectTable selectTable;
	private  IDialog dlg ;
	private GridView tablesView;
	private TextView cancle,ref;
	private TablesListAdapter tablesListAdapter;
	private ArrayList<TableBean> tableBeans;
	private SelectTable close = new SelectTable() {
		@Override
		public void select(TableBean bean,String optName,String optPass) {
			// TODO Auto-generated method stub
			if (bean != null) {
				close();
			}
		}
		@Override
		public void isLocked() {
			// TODO Auto-generated method stub
			NewDataToast.makeText("此桌台已被锁定");
		}
	};

	public  void showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
//		 dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.setting_tables, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		ref = (TextView) layout.findViewById(R.id.ref);
		cancle = (TextView) layout.findViewById(R.id.cancle);
		tablesView = (GridView) layout.findViewById(R.id.tables);
		tableBeans = new ArrayList<TableBean>();
		tablesListAdapter = new TablesListAdapter(context, tableBeans,handler);
		tablesView.setAdapter(tablesListAdapter);

		new Thread(){
			public void run() {
				tableBeans = DB.getInstance().getTableCodeBeans();
				handler.sendEmptyMessage(2);
			};
		}.start();

		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				close();
			}
		});
		ref.setOnClickListener(new OnClickListener() {



			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			new Thread(){
				@Override
				public void run() {
					super.run();
					String ip = sharedUtils.getStringValue("IP");
					String port = sharedUtils.getStringValue("port");
				connect(ip, port);

				}
			}.start();

			}
		});
		/*	 */
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
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




	private TcpClient client = new TcpClient() {

		@Override
		public void onReceive(SocketTransceiver transceiver, final String buffer) {
			// TODO Auto-generated method stub
			//FileUtils.write(Common.SD+Common.tables+"12553", false, buffer);
			try {
				P.c("桌台返回:"+buffer);
				JSONObject jsonObject = new JSONObject(buffer);
				if(jsonObject.getString("return").equals("10000")){
					//成功
					JSONArray array = jsonObject.getJSONArray("table_detail");
					int len = array.length();
					if(len<=0){
						return;
					}
					tableBeans.clear();
					for(int i=0;i<len;i++){
						TableBean bean = new TableBean();
						JSONObject o = array.getJSONObject(i);
						bean.setName(o.getString("tablename"));
						bean.setCode(o.getString("tablenum"));
						tableBeans.add(bean);
					}
					DB.getInstance().clear("board");
					DB.getInstance().addBoard(tableBeans);
					handler.sendEmptyMessage(2);
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

		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onConnectFailed() {
			handler.sendEmptyMessage(-2);
		}

		@Override
		public void onConnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			// 连接成功开始登陆

			String dev = sharedUtils.getStringValue("dev");
			String opt= optName;
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

			/*boolean flag = transceiver.send("listTables=" + FileUtils.mac()
					+ "$$$$$");
			if (!flag) {
				handler.sendEmptyMessage(-1);
			}*/
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
	private void addItem(JSONArray array,String key,Object value) throws JSONException{
		JSONObject o1 = new JSONObject();
		o1.put("Key", key);
		o1.put("Value", value);
		array.put(o1);
	}
	private TableBean tableBean;
	private CommonLoadSendPop sendPop;
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case 3:

					if(sendPop!=null){
						sendPop.cancle();
						sendPop = null;
					}
					CommonBillsPop billsPop = new CommonBillsPop(context, handler, billsBean);
					billsPop.putObj(msg.obj);
					billsPop.setWhat(0);
					billsPop.showSheet();
					break;
				case 0:
					TableBean bean = (TableBean) msg.obj;
					selectTable.select(bean, optName,optPass);
					if(sendPop!=null){
						sendPop.cancle();
						sendPop = null;
					}
					close();
					break;
				case 1:
					if(sendPop==null){
						sendPop = new CommonLoadSendPop(context, "检查桌台状态");
						sendPop.showSheet(false);
						sendPop.setCancel(new SetI() {

							@Override
							public void click() {
								// TODO Auto-generated method stub
								sendPop.cancle();
								sendPop = null;
							}
						});
					}
					if(tableBean!=null){
						tableBean = null;
					}
					tableBean = (TableBean) msg.obj;

					String dev = sharedUtils.getStringValue("dev");

					final JSONObject jsonObject = new JSONObject();
					JSONArray jsonArray = new JSONArray();
					try {
						addItem(jsonArray, "DeviceNo", dev);
						addItem(jsonArray, "TableNo", tableBean.getCode());
						jsonObject.put("ZDCX", jsonArray);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent intent = new Intent(context,FloatService.class);
					intent.putExtra("var", jsonObject.toString()+"<EOF>");
 					context.startService(intent);
					//connectO(ip, port, be);
					break;
				case 2:
					if(tablesListAdapter!=null){
						tablesListAdapter.updata(tableBeans);
					}
					break;
				case -4:
					close();
					break;
				case -5:
					NewDataToast.makeText("获取超时");
					break;
				case -6:
					NewDataToast.makeText((String)msg.obj);
					break;
				default:
					break;
			}
		};
	};
	private ArrayList<BillsBean> billsBean = new ArrayList<BillsBean>();

	public void close(){

		if(sendPop!=null){
			sendPop.cancle();
			sendPop = null;
		}

		if(dlg!=null){
			dlg.cancel();
			dlg.dismiss();
			dlg = null;
		}
	}
	public void closeBySocket(){

	 if(tableBean!=null&&selectTable!=null){
		 selectTable.select(tableBean, optName,optPass);
	 }
		close();
	}
}
