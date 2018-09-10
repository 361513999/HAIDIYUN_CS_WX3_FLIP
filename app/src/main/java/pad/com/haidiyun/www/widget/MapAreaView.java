package pad.com.haidiyun.www.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.MapArea;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.inter.AreaTouch;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 图片区域点击
 */
public class MapAreaView extends ImageView {
	private Context context;

	// 保存所有热点区域
	private Map<String, MapArea> mMapArea;

	// 保存点击的区域
	private Set<String> mFocus;

	protected Paint paint = new Paint();

	// 点击时Path区域的转换，用于触摸点的判断
	protected RectF mPathRectF = new RectF();

	public MapAreaView(Context context) {
		super(context);
		this.context = context;
		// initDatas();
	}

	public MapAreaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// initDatas();
	}

	public void initDatas(ArrayList<FouceBean> fouces) {
		mMapArea = new HashMap<String, MapArea>();
		mFocus = new HashSet<String>();
		initMapArea(fouces);
		fouceList = fouces;
	}

	private ArrayList<FouceBean> fouceList;

	/**
	 * 绘制文本区域和文字处理
	 *
	 * @param canvas
	 */
	private void drawText(Canvas canvas) {
		/**
		 * 点击区域的数量
		 */
		int len = fouceList.size();
		// 文字大小,
		paint.setTextSize(15);
		for (int i = 0; i < len; i++) {
			FouceBean bean = fouceList.get(i);
			MapArea bodyArea = new MapArea(bean, context);
			// 坐标和path的转换
			Path path = bodyArea.getPath();
			RectF rectF = new RectF();
			// 绘制一个矩形区域
			path.computeBounds(rectF, true);
			paint.setColor(Color.parseColor("#c5290b"));
//			paint.setColor(Color.parseColor("#EE82EE"));
			canvas.drawRect(rectF, paint);
			// 绘制文字信息
			paint.setStrokeWidth(3);
			String unit = bean.getName()+"|"+bean.getPrice() + "元/"+bean.getUnit();
			paint.setColor(Color.WHITE);
			FontMetricsInt fontMetrics = paint.getFontMetricsInt();
			float baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2;
			// 下面这行是实现水平居中，drawText对应改为传入rectF.centerX()
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(unit, rectF.centerX(), baseline, paint);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 进行触摸区域绘制
		/*drawText(canvas);

		for(String key : mFocus) {
			  Path path = mMapArea.get(key).getPath();
		  canvas.drawPath(path, paint);
		  if(!mPathRectF.isEmpty())
		  canvas.drawRect(mPathRectF, paint); }*/

	}

	private Path downPath;

	private Path getPath(RectF rectF, MotionEvent event) {
		for (String key : mMapArea.keySet()) {
			Path path = mMapArea.get(key).getPath();
			path.computeBounds(rectF, true);
			if (rectF.contains(event.getX(), event.getY())) {
				return path;
			}
		}
		return null;
	}

	private boolean isClick = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (areaTouch != null) {
			mPathRectF.setEmpty();
			areaTouch.init(mPathRectF);
			checkAreas(event);
			if (!mFocus.isEmpty()) {
				MapArea area = null;
				for (String key : mFocus) {
					area = mMapArea.get(key);
					invalidate();
					areaTouch.click(area.getPtKeys(), mPathRectF);
				}
			}
		}
		return super.onTouchEvent(event);
	}

	private void checkAreas(MotionEvent event) {
		mFocus.clear();
		for (String key : mMapArea.keySet()) {
			mPathRectF.setEmpty();
			Path path = mMapArea.get(key).getPath();
			path.computeBounds(mPathRectF, true);
			if (mPathRectF.contains(event.getX(), event.getY())) {
				mFocus.add(key);
				break;
			}
		}
	}

	public void initMapArea(ArrayList<FouceBean> fouces) {
		mMapArea.clear();
		mFocus.clear();
		if(fouces==null){
			return;
		}
		int len = fouces.size();
		for (int i = 0; i < len; i++) {
			P.c("加载..........");
			MapArea bodyArea = new MapArea(fouces.get(i), context);
			mMapArea.put(fouces.get(i).getKey(), bodyArea);

			// keys[i] = fouces.get(i).getId();
		}
		/*
		 * MapArea bodyArea = null; int idenId = -1; if(keys != null) {
		 * for(String key : keys) { idenId =
		 * context.getResources().getIdentifier(key, "array",
		 * context.getPackageName()); int[] paths =
		 * context.getResources().getIntArray(idenId); idenId =
		 * context.getResources().getIdentifier(key + "_tip", "array",
		 * context.getPackageName()); String[] ptKeys =
		 * context.getResources().getStringArray(idenId); bodyArea = new
		 * MapArea(ptKeys, paths,context); mMapArea.put(key, bodyArea); } }
		 */
	}

	private AreaTouch areaTouch;

	public void setAreaTouch(AreaTouch areaTouch) {
		this.areaTouch = areaTouch;
	}
}