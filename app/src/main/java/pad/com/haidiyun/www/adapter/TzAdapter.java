package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.TzBean;

public class TzAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<TzBean> menuBeans;
	private Context context;
	private Handler handler;
	private int status;
	public TzAdapter(Context context, ArrayList<TzBean> menuBeans,Handler handler,int status) {
		this.context = context;
		this.handler = handler;
		this.status = status;
		inflater = LayoutInflater.from(context);
		this.menuBeans = menuBeans;
	}

	@Override
	public int getCount() {
		// 能设置的最大值
		return menuBeans.size();
	}

	public void updata(ArrayList<TzBean> menuBeans) {
		this.menuBeans = menuBeans;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		Button name;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.tz_item_layout, null);
			viewHolder.name = (Button) convertView
					.findViewById(R.id.menu_title);

			convertView.setTag(R.drawable.ic_launcher + position);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
					+ position);
		}

		final TzBean obj = menuBeans.get(position);
		viewHolder.name.setText(String.valueOf(obj.getName()));
		viewHolder.name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = 30;
				msg.obj = obj;
				msg.arg1 = status;
				handler.sendMessage(msg);
			}
		});
		return convertView;

	}

}
