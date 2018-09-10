package pad.com.haidiyun.www.widget;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import okhttp3.Call;
import okhttp3.MediaType;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.LoadBuy;
import pad.com.haidiyun.www.net.Respon;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.SokectUtils;
import pad.com.haidiyun.www.net.TcpClient;
import pad.com.haidiyun.www.service.FloatService;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
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
public class CommonSendPop {
	private Context context;
	private TextView load_tv;
	private   IDialog dlg;
	private String msg;
	private ImageView load_img,load_close;
	private ArrayList<DishTableBean> dishTableBeans;
	private LoadBuy loadBuy;
	private  SharedUtils utils;
	//操作员名字
	private String optName;
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case 2:
					for(int i=0;i<dishTableBeans.size();i++){
						DishTableBean db = dishTableBeans.get(i);
						if(Common.guKeys.containsKey(db.getCode())){
							NewDataToast.makeTextL( "【"+db.getName()+"】已沽清",5000);
							close();
							return;
						}
					}
					String ip = utils.getStringValue("IP");
					String port = utils.getStringValue("port");
					connect(ip, port);
					break;
				case 1:
					DB.getInstance().clear();
					utils.clear("order_time");
					NewDataToast.makeTextL(  "恭喜您，您已成功下单!",1500);
					loadBuy.success(null,null);
					close();
					break;
				case 0:
					utils.clear("order_time");
//				NewDataToast.makeTextL(context,(String)msg.obj,3000);
					NewDataToast.makeText((String)msg.obj);
					close();
					break;
				case -1:
					NewDataToast.makeText( "请检查与主机的连接配置");
					close();
					break;
				case -2:
					NewDataToast.makeText( "下单失败,请检查WIFI环境");
					close();
					break;
				case -3:
					if(requestCall!=null){
						requestCall.cancel();
					}
					NewDataToast.makeText( "取消下单");
					cancle();
					break;
				case -5:
					NewDataToast.makeText( "获取响应数据超时");
					close();
					break;
				case -6:
					NewDataToast.makeText( "其他设备正在操作此台位");
					close();
					break;
				case -7:
					String temp  = (String)msg.obj;
					if(temp.contains("库存不足")||temp.contains("无效的菜")){
						CommonGuPop gu = new CommonGuPop(context, temp);
						gu.showSheet();
					}else{
						NewDataToast.makeText(temp);
					}

					close();
					break;
				default:
					break;
			}
		};
	};
	private void connectGu(String ip,String pt){
		P.c("发送----------------------------");
		P.c("检查沽清");
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
			}
		}

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
			P.c("沽清结果"+buffer);
			String str[] = buffer.split("@");
			if(str[0].equals("0")){
				for(int i=0;i<str.length;i++){
					if(i!=0){
						Common.guKeys.put(str[i], 0);
					}
				}

			}else{
				//没有沽清菜品
			}
			handler.sendEmptyMessage(2);

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
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			String dev = utils.getStringValue("dev");
			transceiver.send("soldOut="+dev+"$"+optName);

		}
	};
	/**
	 * 连接socket
	 */

	private long order_Time;
	private int person;
	private String billId;
	private String optPass;
	public CommonSendPop(Context context,String msg,ArrayList<DishTableBean> dishTableBeans, LoadBuy loadBuy,int person,String optName,String optPass,String billId) {
		this.context = context;
		this.msg = msg;
		this.dishTableBeans = dishTableBeans;
		this.loadBuy = loadBuy;
		this.person = person;
		this.billId = billId;
		this.optName = optName;
		this.optPass = optPass;
		utils = new SharedUtils(Common.CONFIG);
		if(utils.getLong("order_time")==-1){
			utils.setLongValue("order_time", System.currentTimeMillis());
		}
		order_Time = utils.getLong("order_time");
		String temp = String.valueOf(order_Time);

		order_Time = Long.valueOf(temp.substring(temp.length()-10,temp.length()));
//		order_Time = System.currentTimeMillis();
	}
	public  Dialog showSheet() {
		dlg = new IDialog(context, R.style.buy_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout layout = (FrameLayout) inflater.inflate(
				R.layout.loading, null);
		load_tv = (TextView) layout.findViewById(R.id.load_tv);
		load_tv.setText(msg);
		load_img = (ImageView) layout.findViewById(R.id.load_img);
		load_close = (ImageView) layout.findViewById(R.id.load_close);

		//下单
//		send();
		String ip = utils.getStringValue("IP");
		String port = utils.getStringValue("port");
//		connectGu(ip, port);
//		connect(ip, port);
		sendDish();
		//commonLogin(optName,optPass);


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

	private void cancle(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg = null;
		}
		if(client!=null){
			client.disconnect();
		}
	}
	/**
	 * 下单
	 */
	private RequestCall requestCall;
	private String getMethodName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		StackTraceElement e = stacktrace[2];
		String methodName = e.getMethodName();
		return methodName;
	}
	private TcpClient client = new TcpClient() {

		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			// TODO Auto-generated method stub
			FileUtils.writeLog(buffer, "下单结果");
			if(client!=null){
				client.disconnect();
			}
			String strs[] = buffer.split("@");
			if(strs.length==2){
				try {
					if(strs[0].equals("0")){
						//
						handler.sendEmptyMessage(1);
						return;
					}else if(strs[0].equals("-1")){
						Message msg = new Message();
						msg.what = 0;
						msg.obj = strs[1];
						handler.sendMessage(msg);
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					P.c("解析异常");
				}
			}
			Message msg = new Message();
			msg.what = 0;
			msg.obj = "下单失败";
			handler.sendMessage(msg);
		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			FileUtils.writeLog("onDisconnect", "下单异常");
			P.c("----------断开连接---------");

		}

		@Override
		public void onConnectFailed() {
			// TODO Auto-generated method stub
			FileUtils.writeLog("onConnectFailed", "下单异常");
			P.c("----------连接失败---------");
			if(client!=null){
				client.disconnect();
			}
			handler.sendEmptyMessage(-2);
		}

		@Override
		public void onConnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			P.c("----------连接成功---------");
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			String table = utils.getStringValue("table_code");
//			String userCode = utils.getStringValue("optName");
//			String billId  = utils.getStringValue("billId");
			StringBuilder builder = new StringBuilder();
			int len = dishTableBeans.size();
			for(int i=0;i<len;i++){
				DishTableBean bean = dishTableBeans.get(i);
				ArrayList<ReasonBean> rb = bean.getReasonBeans();
				ArrayList<ReasonBean> mk = bean.getMarkBeans();
				StringBuilder rbCode = new StringBuilder();
				StringBuilder rbPrice = new StringBuilder();
				StringBuilder rbNumber = new StringBuilder();
				StringBuilder rbName = new StringBuilder();
				int jen = rb.size();
				int ken = mk.size();
				for(int j=0;j<jen;j++){
					ReasonBean r =  rb.get(j);
					rbCode.append(r.getCode());
					rbPrice.append(r.getPrice());
					rbNumber.append("1");//默认给个1
					rbName.append(r.getName());
					if(j!=jen-1){
						rbCode.append("!");
						rbPrice.append("!");
						rbNumber.append("!");
						rbName.append("!");
					}
				}
				//补充一个
				if(jen!=0){
					rbCode.append("!");
					rbPrice.append("!");
					rbNumber.append("!");
					rbName.append("!");
				}
				for(int j=0;j<ken;j++){
					ReasonBean r =  mk.get(j);
					rbCode.append(r.getCode());
					rbPrice.append(r.getPrice());
					rbNumber.append("1");//默认给个1
					rbName.append(r.getName());
					if(j!=ken-1){
						rbCode.append("!");
						rbPrice.append("!");
						rbNumber.append("!");
						rbName.append("!");
					}
				}
				builder.append("T"+FileUtils.mac()+System.currentTimeMillis()+"@"+bean.getCode()+"@"+bean.getName()+"@@@@"+bean.getCount()+"@@"+rbCode.toString()+"@"+rbName.toString()+"@"+bean.getPrice()+"@"+rbPrice.toString()+"@"+0+"@"+1+"@"+bean.getUnit()+"@"+0+"@@"+rbNumber.toString()+"@@"+0+"@");
				if(i!=len-1){
					builder.append(";");
				}
			}
			String txt = "sendc="+FileUtils.mac()+"$"+optName+"$$"+table+"$"+billId+"$"+builder.toString()+"$$"+1+"$"+"{\"timestamp\":\""+order_Time+"\",\"sendtype\":\"0\"}";
			FileUtils.writeLog(txt, "下单提交");


			//P.c(builder.toString());
			boolean flag = transceiver.send(txt);
			//P.c("sendc="+FileUtils.mac()+"$"+optName+"$$"+table+"$"+billId+"$"+builder.toString()+"$$"+1+"$"+"{\"timestamp\":\""+order_Time+"\",\"sendtype\":\"0\"}");
			if(!flag){
				handler.sendEmptyMessage(-1);
			}
		}
		public   String getDeviceId() {
			// 根据Wifi信息获取本地Mac
			String ANDROID = "";
			WifiManager wifi = (WifiManager) BaseApplication.application.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			if (info!=null) {
				ANDROID = info.getMacAddress();
			}
			return ANDROID;
		}
		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			//读取超时
			FileUtils.writeLog("readTimeOut", "下单异常");
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
	private void send(){
		String ip = utils.getStringValue("IP");
		String tableCode = utils.getStringValue("table_code");
		int person = utils.getIntValue("person");
		StringBuilder builder = new StringBuilder();
		int len = dishTableBeans.size();
		StringBuilder dish = new StringBuilder();
		for(int i=0;i<len;i++){
			DishTableBean tableBean = dishTableBeans.get(i);
			dish.append("{\"MenuCode\":\""+tableBean.getCode()+"\",\"MenuName\":\""+tableBean.getName()+"\",\"SuitMenuDetail\":\""+tableBean.getSuitMenuDetail()+"\",\"Number\":"+tableBean.getCount()+",\"Price\":"+tableBean.getPrice()+",\"Cooks\":\""+tableBean.getCook_codes()+"\",\"Remark\":\"\",\"MenuFlag\":"+tableBean.getFlag()+"}");
			if(i!=len-1){
				dish.append(",");
			}
		}
		builder.append("{\"TableNo\":\""+tableCode+"\",\"GstCount\":"+person+",\"GstSrc\":\"\",\"Waiter\":\"\",\"Remark\":\"\",\"User\":\""+optName+"\",\"List\":["+dish.toString()+"]}");
		P.c("下单==="+builder.toString());
		JSONObject jsonObject = new JSONObject();
		//http://192.168.1.147/DataService.svc/DownloadData
		try {
			jsonObject.put("data", builder.toString());
			requestCall = OkHttpUtils.postString()
					.url(U.VISTER(ip, U.URL_ORDER_SEND))
//			        .addParams("data", "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
					.mediaType(MediaType.parse("application/json; charset=utf-8"))
					.content(jsonObject.toString())
					.build();
			requestCall.execute(sendCallback);
		} catch (JSONException e) {
			e.printStackTrace();
			NewDataToast.makeText( "发送失败");
		}
	}
	private StringCallback sendCallback = new StringCallback() {
		@Override
		public void onResponse(String response, int id) {
			P.c(response);
			try {
				JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
				if(jsonObject.getBoolean("Success")){
					//成功状态
					handler.sendEmptyMessage(1);
				}else{
					handler.sendEmptyMessage(-2);
				}
			} catch (JSONException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void onError(Call call, Exception e, int id) {
			// TODO Auto-generated method stub
			P.c(e.getMessage());
			handler.sendEmptyMessage(-2);
		}
	};
	public void close(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg = null;
		}
	}

	//款易测试

	/*	private void test(){
            Map<String, String> map = new HashMap<String, String>();
            map.put("type", "4");
            map.put("storeid", "5511");
            map.put("data", "");
            JSONObject dataJsonObject = new JSONObject();
            dataJsonObject.put("orderType", "4");
            String order_id = "KY"+System.currentTimeMillis();
            dataJsonObject.put("orderId", order_id);
            dataJsonObject.put("orderIdView", order_id);
            dataJsonObject.put("", value)
        }*/
	String createSign(Map<String, String> params, boolean encode)
			throws UnsupportedEncodingException {
		Set<String> keysSet = params.keySet();
		Object[] keys = keysSet.toArray();
		Arrays.sort(keys);
		StringBuffer temp = new StringBuffer();
		boolean first = true;
		for (Object key : keys) {
			if (first) {
				first = false;
			} else {
				temp.append("&");
			}
			temp.append(key).append("=");
			Object value = params.get(key);
			String valueString = "";
			if (null != value) {
				valueString = String.valueOf(value);
			}
			if (encode) {
				temp.append(URLEncoder.encode(valueString, "UTF-8"));
			} else {
				temp.append(valueString);
			}
		}
		return MD5(temp.toString()).toUpperCase();
	}
	private String MD5(String pw) {

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = pw.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 433
	 */
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

		String ip = utils.getStringValue("IP");
		String dev = utils.getStringValue("dev");



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

		FileUtils.writeLog(jsonObject.toString(), "下单登录");
		SokectUtils.Builder().timeOut(6000).setMsg(EncodingUtils.getBytes(jsonObject.toString()+"<EOF>", "UTF-8")).send(new Respon() {

			@Override
			public void onSuccess(String buffer) {
				// TODO Auto-generated method stub
				FileUtils.writeLog(buffer, "下单登录结果");
				try {
					JSONObject object = new JSONObject(buffer.substring(0,buffer.length()-5));
					String msg = object.getString("value");
					if(msg.contains("登录成功")){
						//登录成功,执行下菜
						sendDish();

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
	private void sendDish(){
		String ip = utils.getStringValue("IP");
		String dev = utils.getStringValue("dev");
		String tableCode = utils.getStringValue("table_code");
		int person = utils.getIntValue("person");

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		int len = dishTableBeans.size();
		if(len==0){
			NewDataToast.makeText("还没有点菜呢");
			return;
		}
		StringBuffer psb = new StringBuffer();
		StringBuffer qsb = new StringBuffer();
		StringBuffer ssb = new StringBuffer();
		StringBuffer csb = new StringBuffer();
		StringBuffer tsb = new StringBuffer();
		for(int i=0;i<len;i++){
			DishTableBean tableBean = dishTableBeans.get(i);
			ArrayList<ReasonBean> rbs = tableBean.getReasonBeans();
			StringBuffer temp = new StringBuffer();
			for(int j=0;j<rbs.size();j++){
				ReasonBean rb = rbs.get(j);
				temp.append(rb.getCode());
			}

			if(i==len-1){
				//最后一个不加,
				psb.append(tableBean.getCode());
				qsb.append(tableBean.getCount());
				ssb.append("1");
				csb.append(tableBean.getMark_codes().length()==0?"null":tableBean.getMark_codes().replace(",", ""));
				tsb.append("null");


			}else{
				psb.append(tableBean.getCode()+",");
				qsb.append(tableBean.getCount()+",");
				ssb.append("1,");
				csb.append((tableBean.getMark_codes().length()==0?"null":tableBean.getMark_codes().replace(",", ""))+",");
				tsb.append("null,");

			}

		}
		try {
			addItem(jsonArray, "DeviceNo", dev);
			addItem(jsonArray, "TableNo", tableCode);


			addItem(jsonArray, "ProductCD", psb.toString());
			addItem(jsonArray,"Qty",qsb);
			addItem(jsonArray, "CookingTaste", csb);
			addItem(jsonArray, "ComboCD", tsb);
			addItem(jsonArray, "Status", ssb);
			addItem(jsonArray, "OrderTime", order_Time);
			addItem(jsonArray, "Heads",String.valueOf(person));
			addItem(jsonArray,"User",utils.getStringValue("optName"));
			jsonObject.put("DC", jsonArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileUtils.writeLog(jsonObject.toString(), "下单提交");

		Intent intent = new Intent(context,FloatService.class);
		intent.putExtra("var", jsonObject.toString()+"<EOF>");
		context.startService(intent);



	}
}
