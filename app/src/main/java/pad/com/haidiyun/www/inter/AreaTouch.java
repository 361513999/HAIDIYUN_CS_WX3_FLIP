package pad.com.haidiyun.www.inter;

import android.graphics.RectF;
import pad.com.haidiyun.www.bean.FouceBean;

/**
 * ͼƬ����¼�
 * @author Administrator
 *
 */
public interface AreaTouch {
	/**
	 * �������ӿ�
	 * @param bean
	 */
	public void init(RectF downF);
	public void click(FouceBean bean, RectF downF);
	public void up(float x, float y, boolean mIsFlipping);
	public void down();
	
}
