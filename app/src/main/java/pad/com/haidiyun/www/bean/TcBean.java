package pad.com.haidiyun.www.bean;

import java.io.Serializable;

/**

 * 
 * @author tt
 * 
 */
public class TcBean implements Serializable {
	private String code;
	private String name;
	private int num;
	private double price;
	private String path;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
