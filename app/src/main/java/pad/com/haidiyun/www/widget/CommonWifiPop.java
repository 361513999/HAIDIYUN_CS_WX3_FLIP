package pad.com.haidiyun.www.widget;

import java.util.List;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.WifiListAdapter;
import pad.com.haidiyun.www.inter.WifiC;
import pad.com.haidiyun.www.wifi.WifiConnect;
import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;



public class CommonWifiPop {
	private WifiC wifiC = new WifiC() {

		@Override
		public void connected() {
			// TODO Auto-generated method stub
			if(wifiListAdapter!=null){
				results = conn.getWifiList();
				wifiListAdapter.updata(results);
			}
		}
	};
	private Context context;
	/**
	 * 删除弹出框
	 */
	private ListView wifiView;
	private WifiListAdapter wifiListAdapter;
	private   IDialog dlg;
	private List<ScanResult> results;
	public CommonWifiPop(Context context) {
		this.context = context;
	}
	private WifiConnect conn;
	public  Dialog showSheet() {
		dlg = new IDialog(context,R.style.config_pop_style);
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		  d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY); 
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.setting_wifi, null);
		final int cFullFillWidth = 600;
		layout.setMinimumWidth(cFullFillWidth);
		wifiView = (ListView) layout.findViewById(R.id.wifi_list);
		conn = new WifiConnect(context);
		results = conn.getWifiList();
		wifiListAdapter = new WifiListAdapter(context, results);
		wifiView.setAdapter(wifiListAdapter);
		wifiView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				CommonConnectWifiPop connectWifiPop = new CommonConnectWifiPop(context, results.get(arg2).SSID,wifiC);
				connectWifiPop.showSheet();
			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
