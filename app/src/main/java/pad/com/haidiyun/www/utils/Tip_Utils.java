package pad.com.haidiyun.www.utils;

import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;

public class Tip_Utils {
	private SharedUtils sharedUtils;
	 
	public Tip_Utils() {
		// TODO Auto-generated constructor stub
		sharedUtils = new SharedUtils(Common.TIP_CONFIG);
	}
	public void setWel(boolean flg){
		sharedUtils.setBooleanValue(Common.tip_click, flg);
	}
	public void setBuy(boolean flg){
		sharedUtils.setBooleanValue(Common.tip_buy, flg);
	}
	
	
	public boolean getWel(){
		return sharedUtils.getBooleanValue(Common.tip_click);
	}
	public boolean getBuy(){
		return sharedUtils.getBooleanValue(Common.tip_buy);
	}
	
	public void setFirst(boolean flg){
		sharedUtils.setBooleanValue(Common.tip_first, flg);
	}
	public boolean getFirst(){
		return sharedUtils.getBooleanValue(Common.tip_first);
	}
	public void reset(){
		sharedUtils.clear();
	}
}
