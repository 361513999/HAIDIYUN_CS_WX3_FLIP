package pad.com.haidiyun.www.bean;

import java.io.Serializable;


public class FouceBean implements Serializable{
	private String key;
	private int []points;
	private String classcode;
	private String code;
	private String name;
	private String help;
	private String unit;
	private double price;
	private boolean price_modify;
	private boolean suit;
	private String image;
	private int page;
	private boolean temp;
	private boolean more;
	private boolean free;
	
	
	public boolean isFree() {
		return free;
	}
	public void setFree(boolean free) {
		this.free = free;
	}
	public boolean isTemp() {
		return temp;
	}
	public void setTemp(boolean temp) {
		this.temp = temp;
	}
	public boolean isMore() {
		return more;
	}
	public void setMore(boolean more) {
		this.more = more;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int[] getPoints() {
		return points;
	}
	public void setPoints(int[] points) {
		this.points = points;
	}
	public String getClasscode() {
		return classcode;
	}
	public void setClasscode(String classcode) {
		this.classcode = classcode;
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
	public String getHelp() {
		return help;
	}
	public void setHelp(String help) {
		this.help = help;
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
	public boolean isPrice_modify() {
		return price_modify;
	}
	public void setPrice_modify(boolean price_modify) {
		this.price_modify = price_modify;
	}
	 
	public boolean isSuit() {
		return suit;
	}
	public void setSuit(boolean suit) {
		this.suit = suit;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	 
}
