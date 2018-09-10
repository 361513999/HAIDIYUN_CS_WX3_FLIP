package pad.com.haidiyun.www.widget;

import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.inter.UD;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 自定义上下滚动视图
 *
 * @author cloor
 *
 */
public class UDlayout extends LinearLayout {
	private UD ud;
	private boolean isOpen = true;
	private Scroller scroller = null;
	private float x, y;

	public UDlayout(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		// TODO Auto-generated constructor stub
		scroller = new Scroller(context);
		// setLongClickable(true);
	}

	/**
	 * 下滑到指定位置
	 *
	 * @param height
	 */
	public void smoothScrollTo(int width,int height) {
		// int scrollHeight = getHeight()*44/50;
		int scrollHeight = height;
		int scroolWidth = width;
		COMPUT = 0;
		if (!isOpen) {
			scroller.startScroll(-scroolWidth, -scrollHeight, scroolWidth, scrollHeight, 1000);
			isOpen = true;// 记录是否开启
		} else {
			scroller.startScroll(0, 0, -scroolWidth, -scrollHeight, 1000);
			isOpen = false;
		}
		invalidate();// 刷新
	}
	/**
	 * 视图状态
	 * @return
	 */
	public boolean isOpen(){
		return isOpen;
	}
	public void setUD(UD ud){
		this.ud = ud;
	}
	private int COMPUT = 0;
	private boolean ds = true;//默认是完成
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();
		// 先判断mScroller滚动是否完成
		if (scroller.computeScrollOffset()) {
			if(++COMPUT==1){
				ud.change(isOpen, true);
			}
			// 这里调用View的scrollTo()完成实际的滚动
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			// 必须调用该方法，否则不一定能看到滚动效果
			postInvalidate();
		}

		if(scroller.isFinished()){
			COMPUT = 0;
			if(!ds){
				//证明是滚动出来的结果
				ud.change(isOpen, false);
			}
		}
		ds = scroller.isFinished();

		/*if(scroller.isFinished()){
			COMPUT = 0;
			//初始化
			ud.change(isOpen, false);
		}*/
	}


}
