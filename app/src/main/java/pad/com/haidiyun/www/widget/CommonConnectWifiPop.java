package pad.com.haidiyun.www.widget;


import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.inter.WifiC;
import pad.com.haidiyun.www.wifi.WifiConnect;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CommonConnectWifiPop {
	private Context context;
	/**
	 * 删除弹出框
	 */
	private EditText edit_pass;
	private TextView close,connect,wifi;
	private   IDialog dlg;
	private String wifi_name;
	private WifiC wifiC;
	public CommonConnectWifiPop(Context context,String wifi_name,WifiC wifiC) {
		this.context = context;
		this.wifi_name = wifi_name;
		this.wifiC = wifiC;
	}

	public  void showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.setting_connect_wifi, null);
		final int cFullFillWidth = 600;
		layout.setMinimumWidth(cFullFillWidth);
		wifi = (TextView) layout.findViewById(R.id.wifi);
		edit_pass = (EditText) layout.findViewById(R.id.edit_pass);
		close = (TextView) layout.findViewById(R.id.close);
		connect = (TextView) layout.findViewById(R.id.connect);
		wifi.setText("准备连接【"+wifi_name+"】");
		connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String pass = edit_pass.getText().toString();
				WifiConnect connect = new WifiConnect(context);
				boolean flag = connect.reset(context, wifi_name, pass, wifiC);
				if(flag){
					if(dlg!=null&&dlg.isShowing()){
						dlg.cancel();
					}
				}

			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(dlg!=null&&dlg.isShowing()){
					dlg.cancel();
				}
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
	}

}
