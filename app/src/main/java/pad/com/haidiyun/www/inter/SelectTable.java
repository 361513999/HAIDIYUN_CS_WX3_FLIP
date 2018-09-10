package pad.com.haidiyun.www.inter;

import pad.com.haidiyun.www.bean.TableBean;


public interface SelectTable {
	public void select(TableBean bean, String optName, String optPass);
	public void isLocked();
	
}
