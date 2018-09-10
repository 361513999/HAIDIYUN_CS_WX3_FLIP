package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.FlipBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.inter.AreaTouch;
import pad.com.haidiyun.www.widget.MapAreaView;
public class FlipnNextAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<FlipBean> flipBeans;
	private Context context;
	private AreaTouch areaTouch;
	public FlipnNextAdapter(Context context,ArrayList<FlipBean> flipBeans,AreaTouch areaTouch) {
		this.context = context;
		this.areaTouch = areaTouch;
		inflater = LayoutInflater.from(context);
		this.flipBeans = flipBeans;
	}

	@Override
	public int getCount() {
		//能设置的最大值
		return 99999999 ;
	}
	public void updata(ArrayList<FlipBean> flipBeans){
		this.flipBeans = flipBeans;
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
		MapAreaView img;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.flip_item, null);
			viewHolder.img = (MapAreaView) convertView.findViewById(R.id.item);
			convertView.setTag(R.drawable.ic_launcher + position);
		}else {
			viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
		}
		int INDEX = position%flipBeans.size();
		FlipBean flipBean = flipBeans.get(INDEX);
		viewHolder.img.initDatas(flipBean.getFouceBeans());
//	  ImageLoader.getInstance().displayImage("file://"+flipBeans.get(INDEX).getPath(), viewHolder.img);
		viewHolder.img.setAreaTouch(areaTouch);
//	  System.out.println(flipBeans.get(position).getPath());
//	  Picasso.with(BaseApplication.application).load(new File(flipBeans.get(position).getPath())).noFade().into(viewHolder.img);
//	  viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(flipBeans.get(position).getPath()));
		Glide.with(BaseApplication.application).load("file://"+Common.SOURCE+flipBean.getPath()).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(viewHolder.img);
		return convertView;

	}



}
