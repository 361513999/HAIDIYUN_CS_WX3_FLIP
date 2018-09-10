package pad.com.haidiyun.www;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pad.com.haidiyun.www.bean.MenuBean;

public class UsersAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private ArrayList<String> menuBeans;
	private Context context;

	public UsersAdapter(Context context, ArrayList<String> menuBeans) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.menuBeans = menuBeans;
	}

	@Override
	public int getCount() {
		// 能设置的最大值
		return menuBeans.size();
	}

	public void updata(ArrayList<String> menuBeans) {
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
		TextView name;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.menu_item_layout, null);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.menu_title);

			convertView.setTag(R.drawable.ic_launcher + position);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
					+ position);
		}

		final String obj = menuBeans.get(position);
		viewHolder.name.setText(obj);

		return convertView;

	}

}
