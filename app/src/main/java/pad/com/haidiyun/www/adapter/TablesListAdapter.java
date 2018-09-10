package pad.com.haidiyun.www.adapter;

import java.util.List;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.inter.SelectTable;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TablesListAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<TableBean> results;
	private Context context;
	private Handler handler;
	public TablesListAdapter(Context context,List<TableBean> results, Handler handler) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.results = results;
		this.handler = handler;
	}
	@Override
	public int getCount() {
		return results.size();
	}
	public void updata(List<TableBean> results){
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
		TextView item0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.table_item, null);
			viewHolder.item0  = (TextView) convertView.findViewById(R.id.item0);
			convertView.setTag(R.drawable.ic_launcher + position);
		}else {
			viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
		}
		final TableBean result = results.get(position);
		viewHolder.item0.setText(result.getName());
//		 if(result.isLocked()){
//			 viewHolder.item0.setTextColor(context.getResources().getColor(R.color.ban));
//		 }else{
		viewHolder.item0.setTextColor(context.getResources().getColor(R.color.text_cr));
//		 }
		viewHolder.item0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				 
				Message msg = new Message();
				msg.what = 1;//检查桌台
//					msg.what = 0;
				msg.obj = result;
				handler.sendMessage(msg);

			}
		});
		return convertView;
	}

}
