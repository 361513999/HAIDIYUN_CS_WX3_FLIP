package pad.com.haidiyun.www.bean;

import java.io.Serializable;

public class TzBean implements Serializable{
	private String code;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
