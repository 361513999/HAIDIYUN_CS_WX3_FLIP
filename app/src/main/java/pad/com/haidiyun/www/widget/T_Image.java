package pad.com.haidiyun.www.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public final class T_Image extends ImageView {
	public T_Image(Context context) {
		super(context);
	}

	public T_Image(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
	}
}