package net.frederico.showtipsview;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.inter.BackFirst;
import pad.com.haidiyun.www.utils.Tip_Utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 */
public class ShowTipsView extends FrameLayout {
	private Point showhintPoints;
	private int radius = 0;

	private String title, description, button_text;
	private boolean custom, displayOneTime;
	private int displayOneTimeID = 0;
	private int delay = 0;

	private ShowTipsViewInterface callback;

	private View targetView;
	@SuppressWarnings("unused")
	private int screenX, screenY;

	private int title_color, description_color, background_color, circleColor;

//	private StoreUtils showTipsStore;

	private Paint paint;
	private Paint bgPaint;
	//	private Context context;
	private Tip_Utils tip_Utils;
	@SuppressWarnings("unused")
	private int sx,sy;
	public ShowTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		this.context = context;
		init();
	}
	public ShowTipsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public ShowTipsView(Context context) {
		super(context);
		init();
	}
	private void init() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		sx = dm.widthPixels;
		sy = dm.heightPixels;
		tip_Utils = new Tip_Utils();
		this.setVisibility(View.GONE);
		this.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// DO NOTHING
				// HACK TO BLOCK CLICKS

			}
		});

//		showTipsStore = new StoreUtils(getContext());

		paint = new Paint();

		bgPaint = new Paint();

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Get screen dimensions
		screenX = w;
		screenY = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int x = showhintPoints.x;
		int y = showhintPoints.y;
		canvas.save();
// ----------------------
		//剪切一个矩形
     /* int s[] = new int[]{x-80,y-20,x+80,y+20};
      Path pathC = getPath(s);
      RectF rectC = new RectF();
		// 绘制一个矩形区域
      pathC.computeBounds(rectC, true);
      canvas.clipRect(rectC, Region.Op.XOR);
      */
		//-----------
		int rad = radius+4;
		//剪切一个圆形
		Path pathCir = new Path();
		//让这个圆形更大点
		pathCir.addCircle(x, y, rad, Path.Direction.CCW);
		canvas.clipPath(pathCir, Region.Op.XOR);
//-------------------------
//画一个背景
		bgPaint.setColor(Color.BLACK);
		bgPaint.setAlpha(180);
		//注意这里设置全屏的大小
		int s1[] = new int[]{0,0,sx,800};
		Path pathA = getPath(s1);
		RectF rectA = new RectF();
		pathA.computeBounds(rectA, true);
		canvas.drawRect(rectA, bgPaint);

		//画一个边框
		paint.setStyle(Paint.Style.STROKE);
		if (circleColor != 0){
			paint.setColor(circleColor);
		}else{
			paint.setColor(Color.RED);
		}
		paint.setAntiAlias(true);
		paint.setStrokeWidth(6);
		canvas.drawCircle(x, y, rad, paint);
		canvas.restore();
	}
	private Path getPath(int []paths){
		int len =paths.length;
		Path mPath = new Path();
		for(int i = 0; i < len ; i = i + 2) {
			if(i == 0) {
				mPath.moveTo(toDip(getContext(), paths[i]), toDip(getContext(), paths[i + 1]));
			}else {
				mPath.lineTo(toDip(getContext(), paths[i]), toDip(getContext(), paths[i + 1]));
			}
		}
		mPath.close();
		return mPath;
	}
	private float toDip(Context context,float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue * scale + 0.5f);
	}
	boolean isMeasured;
	private boolean is = false;
	public void show(final Activity activity,boolean is) {
		this.is = is;
		if(backFirst!=null){
			backFirst.goBack(false);
		}
		/*if (isDisplayOneTime() && showTipsStore.hasShown(getDisplayOneTimeID())) {
			setVisibility(View.GONE);
			((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(ShowTipsView.this);
			return;
		} else {
			if (isDisplayOneTime())
				showTipsStore.storeShownId(getDisplayOneTimeID());
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				((ViewGroup) activity.getWindow().getDecorView()).addView(ShowTipsView.this);
				ShowTipsView.this.setVisibility(View.VISIBLE);
				Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
				ShowTipsView.this.startAnimation(fadeInAnimation);
				final ViewTreeObserver observer = targetView.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {

						if (isMeasured)
							return;

						if (targetView.getHeight() > 0 && targetView.getWidth() > 0) {
							isMeasured = true;

						}

						if (custom == false) {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = location[0] + targetView.getWidth() / 2;
							int y = location[1] + targetView.getHeight() / 2;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;
							radius = targetView.getWidth() / 2;
						} else {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = location[0] + showhintPoints.x;
							int y = location[1] + showhintPoints.y;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;

						}

						invalidate();
						createViews();

					}
				});
			}
		}, getDelay());*/
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				((ViewGroup) activity.getWindow().getDecorView()).addView(ShowTipsView.this);
				ShowTipsView.this.setVisibility(View.VISIBLE);
				Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
				ShowTipsView.this.startAnimation(fadeInAnimation);
				final ViewTreeObserver observer = targetView.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {

						if (isMeasured)
							return;

						if (targetView.getHeight() > 0 && targetView.getWidth() > 0) {
							isMeasured = true;

						}

						if (custom == false) {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = location[0] + targetView.getWidth() / 2;
							int y = location[1] + targetView.getHeight() / 2;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;
							radius = targetView.getWidth() / 2;
						} else {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = location[0] + showhintPoints.x;
							int y = location[1] + showhintPoints.y;
							// Log.d("FRED", "X:" + x + " Y: " + y);
							Point p = new Point(x, y);
							showhintPoints = p;
						}
						invalidate();
						createViews();

					}
				});

			}
		});
	}

	private BackFirst backFirst;
	public void setListen(BackFirst backFirst){
		this.backFirst = backFirst;
	}
	public void showBy_XY(final Activity activity,final int[] points) {
		if(backFirst!=null){
			backFirst.goBack(false);
		}

		/*if (isDisplayOneTime() && showTipsStore.hasShown(getDisplayOneTimeID())) {
			setVisibility(View.GONE);
			((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(ShowTipsView.this);
			return;
		} else {
			if (isDisplayOneTime())
				showTipsStore.storeShownId(getDisplayOneTimeID());
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				((ViewGroup) activity.getWindow().getDecorView()).addView(ShowTipsView.this);
				ShowTipsView.this.setVisibility(View.VISIBLE);
				Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
				ShowTipsView.this.startAnimation(fadeInAnimation);
				final ViewTreeObserver observer = targetView.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (isMeasured)
							return;
						int width = points[2]-points[0];
						int height = points[3]-points[1];
						int x0 = points[0];
						int y0 = points[1];

						if (height > 0 && width > 0) {
							isMeasured = true;

						}

						if (custom == false) {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = x0 + width / 2;
							int y = y0 + height / 2;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;
							radius = width / 2;
						} else {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = x0 + showhintPoints.x;
							int y = y0 + showhintPoints.y;
							// Log.d("FRED", "X:" + x + " Y: " + y);
							Point p = new Point(x, y);
							showhintPoints = p;
						}
						createViews();
						invalidate();
					}
				});
			}
		}, getDelay());
		*/
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				((ViewGroup) activity.getWindow().getDecorView()).addView(ShowTipsView.this);
				ShowTipsView.this.setVisibility(View.VISIBLE);
				Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
				ShowTipsView.this.startAnimation(fadeInAnimation);
				final ViewTreeObserver observer = targetView.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (isMeasured)
							return;
						int width = points[2]-points[0];
						int height = points[3]-points[1];
						int x0 = points[0];
						int y0 = points[1];

						if (height > 0 && width > 0) {
							isMeasured = true;

						}

						if (custom == false) {
							/*int[] location = new int[2];
							targetView.getLocationInWindow(location);*/
							int x = x0 + width / 2;
							int y = y0 + height / 2;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;
							radius = width / 2;
						} else {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = x0 + showhintPoints.x;
							int y = y0 + showhintPoints.y;
							// Log.d("FRED", "X:" + x + " Y: " + y);
							Point p = new Point(x, y);
							showhintPoints = p;
						}
						createViews();
						invalidate();
					}
				});

			}
		});
	}
	private int image_res = -1;
	public void setImageRes(int image_res){
		this.image_res = image_res;
	}
	/*
	 * Create text views and close button
	 */
	private void createViews() {
		this.removeAllViews();

		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/mb.ttf");

		FrameLayout texts_layout = new FrameLayout(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout layout = new LinearLayout(getContext());
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lparams.leftMargin = 50;
		lparams.topMargin = 70;
		lparams.gravity = Gravity.TOP|Gravity.LEFT;
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(lparams);
		texts_layout.addView(layout);
		/*
		 * Title
		 */

		TextView textTitle = new TextView(getContext());
		textTitle.setTypeface(tf);
		textTitle.setText(getTitle());
		if (getTitle_color() != 0)
			textTitle.setTextColor(getTitle_color());
		else
			textTitle.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
		textTitle.setId(R.id.tips_0);
		textTitle.setTextSize(36);

		// Add title to this view
//		texts_layout.addView(textTitle);
		layout.addView(textTitle);
		/*
		 * Description
		 */
		TextView text = new TextView(getContext());
		text.setText(getDescription());
		text.setTypeface(tf);
		if (getDescription_color() != 0)
			text.setTextColor(getDescription_color());
		else
			text.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
		text.setTextSize(26);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 8, 0, 0);
		//params.addRule(RelativeLayout.BELOW, 123);


		text.setLayoutParams(params);
//		texts_layout.addView(text);
		layout.addView(text);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams paramsTexts = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		//取消限制
		/*if (screenY / 2 > showhintPoints.y) {
			// textBlock under the highlight circle
			paramsTexts.height = (showhintPoints.y + radius) - screenY;
			paramsTexts.topMargin = (showhintPoints.y + radius);
			//texts_layout.setGravity(Gravity.START | Gravity.TOP);
			texts_layout.setPadding(50, 50, 50, 50);
		} else {
			// textBlock above the highlight circle
			paramsTexts.height = showhintPoints.y - radius;

			//texts_layout.setGravity(Gravity.START | Gravity.BOTTOM);

			texts_layout.setPadding(50, 100, 50, 50);
		}*/

		texts_layout.setLayoutParams(paramsTexts);
		this.addView(texts_layout);


		if(image_res!=-1){
			ImageView imageView = new ImageView(getContext());

			Glide.with(BaseApplication.application).load(image_res).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
			if(image_res==R.drawable.lr){
				params = new LayoutParams(600, LayoutParams.WRAP_CONTENT);
			}else{
				params = new LayoutParams(LayoutParams.WRAP_CONTENT,500);
			}

			//params.addRule(RelativeLayout.CENTER_IN_PARENT);
			imageView.setLayoutParams(params);
			this.addView(imageView);
		}



		/*
		 * Close button
		 */
		Button btn_close = new Button(getContext());
		btn_close.setId(R.id.tips_1);
//		btn_close.setText(getButtonText());
		btn_close.setTextColor(Color.WHITE);
		btn_close.setTextSize(17);
		btn_close.setGravity(Gravity.CENTER);
		btn_close.setBackgroundResource(R.drawable.tip_sure_select);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.rightMargin = 50;
		params.bottomMargin = 70;
		params.gravity = Gravity.BOTTOM|Gravity.RIGHT;
		btn_close.setLayoutParams(params);
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(is){
					tip_Utils.setBuy(true);
				}
				if(backFirst!=null){
					backFirst.goBack(true);
				}
				if (getCallback() != null)
					getCallback().gotItClicked();
				setVisibility(View.GONE);
//				removeAllViews();
				((ViewGroup) ((Activity) getContext()).getWindow().getDecorView())
						.removeView(ShowTipsView.this);
			}
		});
		this.addView(btn_close);
	}
	public void setButtonText(String text) {
		this.button_text = text;
	}

	public String getButtonText() {
		if(button_text == null || button_text.equals(""))
			return "知道了";

		return button_text;
	}

	public void setTarget(View v) {
		targetView = v;
	}

	public void setTarget(View v, int x, int y, int radius) {
		custom = true;
		targetView = v;
		Point p = new Point(x, y);
		showhintPoints = p;
		this.radius = radius;
	}

	static Point getShowcasePointFromView(View view) {
		Point result = new Point();
		result.x = view.getLeft() + view.getWidth() / 2;
		result.y = view.getTop() + view.getHeight() / 2;
		return result;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDisplayOneTime() {
		return displayOneTime;
	}

	public void setDisplayOneTime(boolean displayOneTime) {
		this.displayOneTime = displayOneTime;
	}

	public ShowTipsViewInterface getCallback() {
		return callback;
	}

	public void setCallback(ShowTipsViewInterface callback) {
		this.callback = callback;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDisplayOneTimeID() {
		return displayOneTimeID;
	}

	public void setDisplayOneTimeID(int displayOneTimeID) {
		this.displayOneTimeID = displayOneTimeID;
	}

	public int getTitle_color() {
		return title_color;
	}

	public void setTitle_color(int title_color) {
		this.title_color = title_color;
	}

	public int getDescription_color() {
		return description_color;
	}

	public void setDescription_color(int description_color) {
		this.description_color = description_color;
	}

	public int getBackground_color() {
		return background_color;
	}

	public void setBackground_color(int background_color) {
		this.background_color = background_color;
	}

	public int getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(int circleColor) {
		this.circleColor = circleColor;
	}

}
