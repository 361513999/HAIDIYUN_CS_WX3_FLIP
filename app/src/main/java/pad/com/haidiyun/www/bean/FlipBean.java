package pad.com.haidiyun.www.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class FlipBean  implements Serializable{
	private String path;
	private ArrayList<FouceBean> fouceBeans;
	public ArrayList<FouceBean> getFouceBeans() {
		return fouceBeans;
	}

	public void setFouceBeans(ArrayList<FouceBean> fouceBeans) {
		this.fouceBeans = fouceBeans;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
