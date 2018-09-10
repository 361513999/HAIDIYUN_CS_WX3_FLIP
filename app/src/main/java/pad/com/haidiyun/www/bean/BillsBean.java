package pad.com.haidiyun.www.bean;

import java.io.Serializable;

public class BillsBean implements Serializable{
	private String name;
	private int person;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPerson() {
		return person;
	}
	public void setPerson(int person) {
		this.person = person;
	}
	
}
