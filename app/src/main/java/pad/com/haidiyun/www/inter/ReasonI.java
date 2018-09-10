package pad.com.haidiyun.www.inter;

import java.util.ArrayList;

import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.ReasonBean;

public interface ReasonI {
	public void select(ArrayList<ReasonBean> beans, DishTableBean bean);
	public void insert(ArrayList<ReasonBean> beans, FouceBean bean);
	public void init(DishTableBean bean);
	public void init(FouceBean bean);
}
