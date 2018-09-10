package pad.com.haidiyun.www.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 菜篮子
 * @author Administrator
 *
 */
public class DishTableBean implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int i;
	private String code;
	private String name;
	private String unit;
	private double price;
	private boolean more;
	private boolean price_modify;
	private boolean temp;
	private boolean suit;
	private String SuitMenuDetail;
	private int count;
	private ArrayList<ReasonBean> reasonBeans;
	private ArrayList<ReasonBean> markBeans;
	private String cook_codes;
	private String cook_names;
	private String cook_prices;
	private String mark_codes;
	private String mark_names;
	private String mark_prices;
	private String flag;
	private boolean isFree;
	private String notes;
	private String groupid;
	private boolean zeng;
	private boolean tui;
	private String valuation_unit;

	public String getValuation_unit() {
		return valuation_unit;
	}
	public void setValuation_unit(String valuation_unit) {
		this.valuation_unit = valuation_unit;
	}
	public boolean isZeng() {
		return zeng;
	}
	public void setZeng(boolean zeng) {
		this.zeng = zeng;
	}
	public boolean isTui() {
		return tui;
	}
	public void setTui(boolean tui) {
		this.tui = tui;
	}
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getMark_codes() {
		return mark_codes;
	}
	public void setMark_codes(String mark_codes) {
		this.mark_codes = mark_codes;
	}
	public String getMark_names() {
		return mark_names;
	}
	public void setMark_names(String mark_names) {
		this.mark_names = mark_names;
	}

	public String getMark_prices() {
		return mark_prices;
	}
	public void setMark_prices(String mark_prices) {
		this.mark_prices = mark_prices;
	}
	public ArrayList<ReasonBean> getMarkBeans() {
		return markBeans;
	}
	public void setMarkBeans(ArrayList<ReasonBean> markBeans) {
		this.markBeans = markBeans;
	}
	public boolean isFree() {
		return isFree;
	}
	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public boolean isMore() {
		return more;
	}
	public void setMore(boolean more) {
		this.more = more;
	}
	public boolean isPrice_modify() {
		return price_modify;
	}
	public void setPrice_modify(boolean price_modify) {
		this.price_modify = price_modify;
	}
	public boolean isTemp() {
		return temp;
	}
	public void setTemp(boolean temp) {
		this.temp = temp;
	}
	public boolean isSuit() {
		return suit;
	}
	public void setSuit(boolean suit) {
		this.suit = suit;
	}
	public String getSuitMenuDetail() {
		return SuitMenuDetail;
	}
	public void setSuitMenuDetail(String suitMenuDetail) {
		SuitMenuDetail = suitMenuDetail;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public ArrayList<ReasonBean> getReasonBeans() {
		return reasonBeans;
	}
	public void setReasonBeans(ArrayList<ReasonBean> reasonBeans) {
		this.reasonBeans = reasonBeans;
	}
	public String getCook_codes() {
		return cook_codes;
	}
	public void setCook_codes(String cook_codes) {
		this.cook_codes = cook_codes;
	}
	public String getCook_names() {
		return cook_names;
	}
	public void setCook_names(String cook_names) {
		this.cook_names = cook_names;
	}
	public String getCook_prices() {
		return cook_prices;
	}
	public void setCook_prices(String cook_prices) {
		this.cook_prices = cook_prices;
	}


}
