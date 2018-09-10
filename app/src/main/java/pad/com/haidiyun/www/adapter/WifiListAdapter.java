package pad.com.haidiyun.www.adapter;

import java.util.List;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WifiListAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<ScanResult> results;
	private SharedUtils sharedUtils;
	public WifiListAdapter(Context context,List<ScanResult> results) {
		 inflater = LayoutInflater.from(context);
		 this.results = results;
		 sharedUtils = new SharedUtils(Common.SHARED_WIFI);
	}
	@Override
	public int getCount() {
		return results.size();
	}
	 public void updata(List<ScanResult> results){
		   this.results = results;
		   notifyDataSetChanged();
	  }
	@Override
	public Object getItem(int arg0) {
		return results.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	 private class ViewHolder {
		 TextView name;
		 TextView wifi_ss;
		 TextView wifi_con;
		}
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		 if (convertView == null
					|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
				viewHolder = new ViewHolder();
					convertView = inflater.inflate(R.layout.wifi_item, null);
					 viewHolder.name  = (TextView) convertView.findViewById(R.id.wifi_name);
					 viewHolder.wifi_ss = (TextView) convertView.findViewById(R.id.wifi_ss);
					 viewHolder.wifi_con = (TextView) convertView.findViewById(R.id.wifi_con);
					convertView.setTag(R.drawable.ic_launcher + position);
		  }else {
				viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
		 }
		 ScanResult result = results.get(position);
		 if(sharedUtils.getStringValue(Common.LOCKED_WIFI_NAME).length()!=0&&sharedUtils.getStringValue(Common.LOCKED_WIFI_NAME).equals(result.SSID)){
			 viewHolder.wifi_con.setVisibility(View.VISIBLE);
		 }else{
			 viewHolder.wifi_con.setVisibility(View.GONE);
		 }
		 viewHolder.name.setText(result.SSID);
		 viewHolder.wifi_ss.setText(result.BSSID);
		return convertView;
	}

}
