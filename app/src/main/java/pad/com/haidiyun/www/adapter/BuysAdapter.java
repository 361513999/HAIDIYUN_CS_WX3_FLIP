package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.inter.BuyClick;
public class BuysAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<DishTableBean> dishTableBeans;
	private Context context;
	private BuyClick buyClick;
	public BuysAdapter(Context context,ArrayList<DishTableBean> dishTableBeans,BuyClick buyClick) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.buyClick = buyClick;
		this.dishTableBeans = dishTableBeans;
	}
	@Override
	public int getCount() {
		//能设置的最大值
		return dishTableBeans.size();
	}
	private boolean isNet = false;
	public void updata(ArrayList<DishTableBean> dishTableBeans,boolean isNet){
		this.isNet = isNet;
		this.dishTableBeans = dishTableBeans;
		notifyDataSetChanged();
	}
	private int  status=-1;
	private boolean tui;
	public void updata(ArrayList<DishTableBean> dishTableBeans,boolean isNet,int status){
		this.isNet = isNet;
		this.status = status;
		this.dishTableBeans = dishTableBeans;
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
		TextView price;
		TextView delete;
		TextView add;
		ImageView remove;
		TextView view;
		TextView mark;
		TextView res;
		TextView tag;
		TextView reason_name;
		TextView tui;
		TextView zeng;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.buy_item, null);
			viewHolder.add = (TextView) convertView.findViewById(R.id.add);
			viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			viewHolder.view = (TextView) convertView.findViewById(R.id.view);
			viewHolder.remove = (ImageView) convertView.findViewById(R.id.remove);
			viewHolder.res = (TextView) convertView.findViewById(R.id.res);
			viewHolder.tag = (TextView) convertView.findViewById(R.id.tag);
			viewHolder.mark = (TextView) convertView.findViewById(R.id.mark);
			viewHolder.reason_name = (TextView) convertView.findViewById(R.id.reason_name);
			viewHolder.tui = (TextView) convertView.findViewById(R.id.tui);
			viewHolder.zeng = (TextView) convertView.findViewById(R.id.zeng);

			convertView.setTag(R.drawable.ic_launcher + position);
		}else {
			viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
		}

		final DishTableBean obj = dishTableBeans.get(position);
		if(obj.isMore()){
			viewHolder.res.setVisibility(View.VISIBLE);
		}else{
			viewHolder.res.setVisibility(View.GONE);
		}

		if(isNet){
			viewHolder.add.setVisibility(View.INVISIBLE);
			viewHolder.delete.setVisibility(View.INVISIBLE);
			viewHolder.remove.setVisibility(View.GONE);
			if(status==1){
				viewHolder.tui.setVisibility(View.INVISIBLE);
				viewHolder.zeng.setVisibility(View.VISIBLE);

			}else if (status==0) {
				viewHolder.tui.setVisibility(View.VISIBLE);
				viewHolder.zeng.setVisibility(View.INVISIBLE);
			}else {
				viewHolder.zeng.setVisibility(View.VISIBLE);
				viewHolder.tui.setVisibility(View.VISIBLE);
			}
			if(obj.isZeng()){
				viewHolder.zeng.setBackgroundResource(R.drawable.shape_common_ban_btn);
			}else{
				viewHolder.zeng.setBackgroundResource(R.drawable.shape_common_cwhite);
			}
			if(obj.isTui()){
				viewHolder.tui.setBackgroundResource(R.drawable.shape_common_ban_btn);
			}else{
				viewHolder.tui.setBackgroundResource(R.drawable.shape_common_cwhite);
			}

			viewHolder.mark.setVisibility(View.INVISIBLE);
		}else{
			//未下单
			viewHolder.add.setVisibility(View.VISIBLE);
			viewHolder.delete.setVisibility(View.VISIBLE);
			viewHolder.remove.setVisibility(View.VISIBLE);
			viewHolder.tui.setVisibility(View.GONE);
			viewHolder.zeng.setVisibility(View.GONE);
			viewHolder.mark.setVisibility(View.VISIBLE);
		}
		if(obj.isTemp()){
			viewHolder.tag.setVisibility(View.VISIBLE);
			viewHolder.tag.setText("时");
		}else{
			viewHolder.tag.setVisibility(View.GONE);
		}
		if(obj.isSuit()){
			viewHolder.tag.setText("套");
			viewHolder.tag.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tag.setVisibility(View.GONE);
		}
		viewHolder.res.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				buyClick.res(obj);
			}
		});
		viewHolder.mark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				buyClick.mark(obj);
			}
		});
		viewHolder.view.setText(String.valueOf(obj.getCount()));
		viewHolder.name.setText(obj.getName());
		viewHolder.price.setText(obj.getPrice()+"元\n("+obj.getUnit()+")");
		if(isNet){
			viewHolder.reason_name.setText(obj.getNotes());
		}else{
			String v = getRes(obj);
			String b = getMarks(obj);
			viewHolder.reason_name.setText(v.length()==0?b:v+"|"+b);
		}

		viewHolder.add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!obj.isFree()){
					buyClick.add(obj);
				}

			}
		});
		viewHolder.remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				buyClick.remove(obj);
			}
		});
		viewHolder.delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				buyClick.delete(obj);
			}
		});
		viewHolder.tui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//			dishTableBeans.get(position).setTui(true);
			/*if(obj.isTui()){
				obj.setTui(false);
			}else{
				obj.setTui(true);
			}*/
				for(int i=0;i<dishTableBeans.size();i++){
					dishTableBeans.get(i).setTui(false);
				}
				obj.setTui(true);

				buyClick.change(0,dishTableBeans);
			}
		});
		viewHolder.zeng.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

//			dishTableBeans.get(position).setZeng(true);
			/*if(obj.isZeng()){
				obj.setZeng(false);
			}else{
				obj.setZeng(true);
			}*/
				for(int i=0;i<dishTableBeans.size();i++){
					dishTableBeans.get(i).setZeng(false);
				}
				obj.setZeng(true);
				buyClick.change(1,dishTableBeans);
			}
		});


		return convertView;
	}
	/**
	 * 计算已选做法
	 * @param obj
	 * @return
	 */
	private String getRes(DishTableBean obj){
		StringBuilder builder = new StringBuilder();
		ArrayList<ReasonBean> rbs =  obj.getReasonBeans();
		int len = rbs==null?0:rbs.size();
		for(int i=0;i<len;i++){
			ReasonBean rb = rbs.get(i);
			builder.append(rb.getName());
			if(rb.getPrice()!=0){
				builder.append(rb.getPrice());
			}
			if(i!=len-1){
				builder.append(",");
			}
		}
		P.c("做法："+builder.toString());
		return builder.toString();
	}
	private String getMarks(DishTableBean obj){
		StringBuilder builder = new StringBuilder();
		ArrayList<ReasonBean> rbs =  obj.getMarkBeans();
		int len = rbs==null?0:rbs.size();
		for(int i=0;i<len;i++){
			ReasonBean rb = rbs.get(i);
			builder.append(rb.getName());
			if(rb.getPrice()!=0){
				builder.append(rb.getPrice());
			}
			if(i!=len-1){
				builder.append(",");
			}
		}
		P.c("备注："+builder.toString());
		return builder.toString();
	}



}
