package pad.com.haidiyun.www.bean;

import pad.com.haidiyun.www.common.P;
import android.content.Context;
import android.graphics.Path;

//区域对象
public class MapArea {
	private FouceBean fouceBean;
	private Path mPath;
	public MapArea(FouceBean fouceBean,Context context) {
		super();
		this.fouceBean = fouceBean;
		mPath = new Path();
		int paths[] = fouceBean.getPoints();
		int len = paths==null?0:paths.length;
		//每两个点做一个坐标
		for(int i = 0; i < len ; i = i + 2) {
			if(i == 0) {
				//P.c(paths[i]+"第一==>"+paths[i+1]);
				mPath.moveTo(toDip(context, paths[i]), toDip(context, paths[i + 1]));
			}else {
				//P.c(paths[i]+"第二==>"+paths[i+1]);
				mPath.lineTo(toDip(context, paths[i]), toDip(context, paths[i + 1]));
			}
		}
		mPath.close();
	}

	public FouceBean getPtKeys() {
		return fouceBean;
	}

	public void setPtKeys(FouceBean fouceBean) {
		this.fouceBean = fouceBean;
	}

	public Path getPath() {
		return mPath;
	}
	private float toDip(Context context,float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue * scale + 0.5f);
	}
}