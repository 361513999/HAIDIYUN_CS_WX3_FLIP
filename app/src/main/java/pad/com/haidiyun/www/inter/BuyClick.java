package pad.com.haidiyun.www.inter;

import java.util.ArrayList;

import pad.com.haidiyun.www.bean.DishTableBean;


public interface BuyClick {
	public void add(DishTableBean obj);
	public void delete(DishTableBean obj);
	public void remove(DishTableBean obj);
	public void res(DishTableBean obj);
	public void mark(DishTableBean obj);
	public void person();
	public void person(LoadBuy buy, String optName, String optPass);
	public void change(int x, ArrayList<DishTableBean> objs);
}
