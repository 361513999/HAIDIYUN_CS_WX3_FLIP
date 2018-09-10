package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.MoveItem;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;

public class MoveAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private SharedUtils sharedUtils;
	private ArrayList<MoveItem> results;
	private int W = 0;
	public MoveAdapter(Context context, ArrayList<MoveItem> results,int W) {
		 inflater = LayoutInflater.from(context);
		 this.results = results;
		this.W = W;
		 sharedUtils = new SharedUtils(Common.SHARED_WIFI);
	}
	@Override
	public int getCount() {
		return results.size();
//	return  12;
	}
	 public void updata(ArrayList<MoveItem> results){
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
		 TextView txt;

		}
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		 if (convertView == null
					|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
				viewHolder = new ViewHolder();
					convertView = inflater.inflate(R.layout.move_item, null);
					 viewHolder.txt  = (TextView) convertView.findViewById(R.id.txt);

					convertView.setTag(R.drawable.ic_launcher + position);
		  }else {
				viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
		 }
		 MoveItem result = results.get(position);
		viewHolder.txt.setLayoutParams(new LinearLayout.LayoutParams(W,W));
		 viewHolder.txt.setText(result.getTxt());
		viewHolder.txt.setBackgroundColor(Color.parseColor(result.getColor()));

		return convertView;
	}

}
