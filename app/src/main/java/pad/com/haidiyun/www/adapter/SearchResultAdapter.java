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
import pad.com.haidiyun.www.bean.FouceBean;

public class SearchResultAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private ArrayList<FouceBean> searchDishs;
	private Context context;

	public SearchResultAdapter(Context context,ArrayList<FouceBean> searchDishs) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.searchDishs = searchDishs;
	}

	@Override
	public int getCount() {
		// 能设置的最大值
		return searchDishs.size();
	}

	public void updata(ArrayList<FouceBean> searchDishs) {
		this.searchDishs = searchDishs;
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

		final FouceBean obj = searchDishs.get(position);
		viewHolder.name.setText(String.valueOf(obj.getName()));

		return convertView;

	}

}
