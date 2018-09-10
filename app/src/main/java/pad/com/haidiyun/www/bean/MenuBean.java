package pad.com.haidiyun.www.bean;

import java.io.Serializable;

public class MenuBean implements Serializable{
	private String menuTitle;
	private int page;
	public String getMenuTitle() {
		return menuTitle;
	}
	public void setMenuTitle(String menuTitle) {
		this.menuTitle = menuTitle;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	
}
