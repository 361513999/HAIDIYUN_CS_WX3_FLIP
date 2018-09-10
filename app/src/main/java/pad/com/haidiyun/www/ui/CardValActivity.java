package pad.com.haidiyun.www.ui;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.AppManager;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.DownMsg;
import pad.com.haidiyun.www.inter.SetI;
import pad.com.haidiyun.www.net.Respon;
import pad.com.haidiyun.www.net.SokectUtils;
import pad.com.haidiyun.www.service.FloatService;
import pad.com.haidiyun.www.utils.DesUtils;
import pad.com.haidiyun.www.widget.CommonLoadSendPop;
import pad.com.haidiyun.www.widget.CommonUserPop;
import pad.com.haidiyun.www.widget.NewDataToast;
/*
 * tablesPop = new CommonTablesPop(CardValActivity.this,
						selectTable, data.getStringExtra("optName"));
				tablesPop.showSheet();
				
				AppManager.getAppManager().finishActivity(CardValActivity.this);
 */
public class CardValActivity extends CaseActivity{
	private void pause(){
		Intent intent = new Intent();
		intent.setAction(Common.TOUCH_PAUSE);
		sendBroadcast(intent);
	}
	private ImageView anim_complete;
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private FrameLayout layout0;
	private LinearLayout layout1;
	private ToggleButton tbButton;
	private EditText pass;
	private TextView user;
	private TextView cancle,save;
	SharedUtils userUtils ;
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case -5:
					NewDataToast.makeText("获取响应数据超时");
					break;
				case -1:
					NewDataToast.makeText("请检查与主机的连接配置");
					break;
				case 1:
					if(print){
						P.c("开始打印");

						commonLogin(((String[])msg.obj)[0],((String[])msg.obj)[1]);
					}else{
						Intent intent = new Intent();
						intent.putExtra("result", msg.arg1);
						P.c("校验返回");
						Intent dataIntent = getIntent();
						if(dataIntent.hasExtra("obj")){
							P.c("附加对象的校验返回");
							intent.putExtra("obj", getIntent().getSerializableExtra("obj"));
						}
						if(dataIntent.hasExtra("arg1")){
							intent.putExtra("arg1", getIntent().getIntExtra("arg1", -1));
						}
						intent.putExtra("optName",  ((String[])msg.obj)[0] );
						intent.putExtra("optPass",  ((String[])msg.obj)[1] );
//					utils.setStringValue("optName", (String)msg.obj);
						setResult(1000,intent);
						P.c("校验返回123");
						AppManager.getAppManager().finishActivity(CardValActivity.this);
					}


					break;
				case 0:
					if(msg.arg1==1){
//					NewDataToast.makeTextD( "服务员:"+msg.obj+",你好!\n"+"可以在此点餐机上使用",1000);
//					Toast.makeText(context, "服务员:"+msg.obj+",你好!\n"+"可以在此点餐机上使用",1000);
					}else if(msg.arg1==0){
						NewDataToast.makeText( "不能在此点餐机上使用");
					}
					AppManager.getAppManager().finishActivity(CardValActivity.this);
					break;

				case 2:
					NewDataToast.makeText( "校验失败");

					break;
				case 4:
					NewDataToast.makeText("无员工密码");
					break;
				case 5:
					NewDataToast.makeText("权限不足");
					break;
				case 37:

					NewDataToast.makeText("登录失败");
					loadSendPop.cancle();
					loadSendPop = null;
					break;
				case 38:
					NewDataToast.makeText("已通知打印服务");
					loadSendPop.cancle();
					loadSendPop = null;
					AppManager.getAppManager().finishActivity(CardValActivity.this);
					break;
				default:
					break;
			}
		};
	};
	private CommonLoadSendPop loadSendPop;

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
		if(loadSendPop==null){
			loadSendPop = new CommonLoadSendPop(CardValActivity.this, "打印账单");//解锁桌台
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
						printOrder();

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
				P.c("超时");
			}
		},"<EOF>","");
	}

	private void printOrder() {


		String dev = sharedUtils.getStringValue("dev");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			addItem(jsonArray, "MchID", FileUtils.getDeviceId());
			addItem(jsonArray, "BillNo", sharedUtils.getStringValue("billId"));
			addItem(jsonArray, "TableNo", sharedUtils.getStringValue("table_code"));
			jsonObject.put("DYDC", jsonArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SokectUtils.Builder().timeOut(6000).setMsg(EncodingUtils.getBytes(jsonObject.toString()+"<EOF>", "UTF-8")).send(new Respon() {

			@Override
			public void onSuccess(String buffer) {
				// TODO Auto-generated method stub
				try {
					JSONObject object = new JSONObject(buffer.substring(0,buffer.length()-5));
					String msg = object.getString("value");
					handler.sendEmptyMessage(38);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onProgress(String progress) {
				// TODO Auto-generated method stub
				P.c("progress"+progress);
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

		}, "<EOF>", "");

	}

	private int timer=0;
	private SharedUtils utils;
	private void change(boolean flag){
		if(flag){
			layout1.setVisibility(View.VISIBLE);
			layout0.setVisibility(View.GONE);
		}else{
			layout0.setVisibility(View.VISIBLE);
			layout1.setVisibility(View.GONE);

		}
	}
	private   String MD5(String pw) {

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



	private boolean print = false;
	private ArrayAdapter adapter;

	class DataReceiver  extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

				if(intent.getAction().equals("app.send.users")){
					if(userPop!=null){
						userPop.lis();
					}
				}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(dataReceiver!=null){
			unregisterReceiver(dataReceiver);
		}
	}
    private void getMM(){
        String cmd = "{\"YHLB\":\"\"}";
        Intent intent = new Intent(this,FloatService.class);
        intent.putExtra("var", cmd);
        startService(intent);
    }
	private 	CommonUserPop userPop;
	private DataReceiver dataReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_val_activity);
		Common.IS_SHOW = false;
		dataReceiver = new CardValActivity.DataReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("app.send.users");
		registerReceiver(dataReceiver, filter);
		pause();
		utils = new SharedUtils(Common.CONFIG);
		userUtils = new SharedUtils(Common.USERS);


		layout0 = (FrameLayout) findViewById(R.id.layout0);
		layout1 = (LinearLayout) findViewById(R.id.layout1);
		tbButton = (ToggleButton) findViewById(R.id.tb);
		cancle = (TextView) findViewById(R.id.cancle);
		save = (TextView) findViewById(R.id.save);
		user = (TextView) findViewById(R.id.user);
		//users = userUtils.getKeys();
        getMM();
		user.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {


			 if(userPop==null){

					userPop = new CommonUserPop(CardValActivity.this, new DownMsg() {
						@Override
						public void view(String msg) {
							user.setText(msg);
							userPop = null;
						}
					}, new SetI() {
						@Override
						public void click() {
							userPop = null;
						}
					});
					userPop.showSheet();
				}


			}
		});

		pass = (EditText) findViewById(R.id.pass);
		final Intent intent = getIntent();
		if(intent.hasExtra("print")){
			print = true;
			//打印账单模式
		}



		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String pas = pass.getText().toString().trim();
				String us = user.getText().toString().trim();


				if(us.length()==0){
					NewDataToast.makeText("请输入服务员账户");
					return;
				}

				if(pas.length()==0){
					handler.sendEmptyMessage(4);
				}else{

						if(!pas.equals(userUtils.getStringValue(us))){
							NewDataToast.makeText("密码错误");
							return;
						}



					String ip = utils.getStringValue("IP");
					String port = utils.getStringValue("port");
					//无校验
					Message msg  =new Message();
					msg.what = 1;
					msg.arg1 = 1;
					msg.obj = new String[]{user.getText().toString().trim(),pas};
					if(pass.getText().toString().trim().length()==0){
						handler.sendEmptyMessage(2);
					}else{
						sharedUtils.setStringValue("optName",us);
						handler.sendMessage(msg);
					}
				}



			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AppManager.getAppManager().finishActivity(CardValActivity.this);
			}
		});
		change(tbButton.isChecked());
		tbButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				change(arg1);
			}
		});
		anim_complete = (ImageView) findViewById(R.id.write_tip);
		TranslateAnimation alphaAnimation2 = new TranslateAnimation(0, 0, -550f, 0);
		alphaAnimation2.setDuration(2000);
		alphaAnimation2.setRepeatCount(Animation.INFINITE);
		alphaAnimation2.setRepeatMode(Animation.REVERSE);
		anim_complete.setAnimation(alphaAnimation2);
		alphaAnimation2.start();
		init();
		read(getIntent(), 0);
	}
	private void init(){
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		openNFC();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(intent!=null){
			read(intent,1);
		}
	}
	private String result = null;
	private void read(final Intent intent,final int index){
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())||NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
					NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage ndefMessage = null;
			if(rawMsgs!=null){
				if(rawMsgs.length>0){
					ndefMessage = (NdefMessage) rawMsgs[0];
				}
				NdefRecord record = ndefMessage.getRecords()[0];
				result =  new String(record.getPayload());
			}
		}
		if(result!=null&&result.length()!=0){
			new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					try {
						DesUtils des = new DesUtils("haidiyun");//自定义密钥
						P.c(result);
						P.c(des.decrypt(result));
						String json = des.decrypt(result);
						JSONObject jsonObject = new JSONObject(json);
						String code = jsonObject.getString("code");
						String pwd= jsonObject.getString("pwd");
						if(pwd.length()==0){
							handler.sendEmptyMessage(4);
							return;
						}
					//P.c(pwd+"--"+pwd+"--"+sharedUtils.getStringValue("tzmm"));

							if(!pwd.equals(userUtils.getStringValue(code))){

								handler.sendEmptyMessage(5);
								return;
							}


//						boolean flag =  DB.getInstance().isVal(code, pwd);
//							if(flag){
						Message msg  =new Message();
						msg.what = index;
//								msg.arg1 = flag?1:0;
						msg.arg1 = 1;//能反解json就一定成功
						msg.obj = new String[]{code,pwd};
						if(pwd.length()==0){
							handler.sendEmptyMessage(2);
						}else{
							//每次校验都绑定用户
							sharedUtils.setStringValue("optName",code);
							handler.sendMessage(msg);
						}

//							}else{
//								handler.sendEmptyMessage(2);
//								P.c("校验失败");
//							}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}

	}
	/**
	 * 打开NFC设备
	 */
	private void openNFC() {
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent,
					Common.FILTERS, Common.TECHLISTS);
		}
	}




}
