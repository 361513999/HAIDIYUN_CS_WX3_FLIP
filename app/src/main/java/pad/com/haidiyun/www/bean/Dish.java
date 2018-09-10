package pad.com.haidiyun.www.bean;

import java.io.Serializable;

public class Dish implements Serializable{
	private String id;
	private String code;
	private String name;
	private String type;
	private String unit;
	private String coords;
	private double price;
	private String unitId;
	private int page;
	private String shortCode;
	private String ReasonCode;
	private String reasonName;
	private String reasonPrice;
	
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public String getReasonCode() {
		return ReasonCode;
	}
	public void setReasonCode(String reasonCode) {
		ReasonCode = reasonCode;
	}
	public String getReasonName() {
		return reasonName;
	}
	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
	}
	public String getReasonPrice() {
		return reasonPrice;
	}
	public void setReasonPrice(String reasonPrice) {
		this.reasonPrice = reasonPrice;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getCoords() {
		return coords;
	}
	public void setCoords(String coords) {
		this.coords = coords;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	
}
