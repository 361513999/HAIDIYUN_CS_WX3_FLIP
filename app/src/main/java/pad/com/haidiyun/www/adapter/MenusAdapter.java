package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.MenuBean;

public class MenusAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private ArrayList<MenuBean> menuBeans;
	private Context context;

	public MenusAdapter(Context context, ArrayList<MenuBean> menuBeans) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.menuBeans = menuBeans;
	}

	@Override
	public int getCount() {
		// 能设置的最大值
		return menuBeans.size();
	}

	public void updata(ArrayList<MenuBean> menuBeans) {
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

		final MenuBean obj = menuBeans.get(position);
		viewHolder.name.setText(String.valueOf(obj.getMenuTitle()));

		return convertView;

	}

}
