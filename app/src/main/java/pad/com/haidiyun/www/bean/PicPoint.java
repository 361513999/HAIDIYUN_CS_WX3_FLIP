package pad.com.haidiyun.www.bean;

import java.io.Serializable;

public class PicPoint implements Serializable{
	//需要展示动画的宽度和高度
	private int width;
	private int height;
	//关于坐标点
	private int[] point;


	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int[] getPoint() {
		return point;
	}
	public void setPoint(int[] point) {
		this.point = point;
	}

}
