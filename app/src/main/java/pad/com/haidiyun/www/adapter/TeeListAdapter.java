package pad.com.haidiyun.www.adapter;

import java.util.List;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.bean.TeeBean;
import pad.com.haidiyun.www.inter.SelectTable;
import pad.com.haidiyun.www.inter.SelectTee;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TeeListAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<TeeBean> results;
	private SelectTee selectTee;
	private SelectTee close;
	public TeeListAdapter(Context context,List<TeeBean> results,SelectTee selectTee,SelectTee close) {
		 inflater = LayoutInflater.from(context);
		 this.results = results;
		this.selectTee = selectTee;
		this.close = close;
	}
	@Override
	public int getCount() {
		return results.size();
	}
	 public void updata(List<TeeBean> results){
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
		 final TeeBean result = results.get(position);
		 viewHolder.item0.setText(result.getName());
		 viewHolder.item0.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectTee.select(result);
				close.select(result);
			}
		});
		return convertView;
	}

}
