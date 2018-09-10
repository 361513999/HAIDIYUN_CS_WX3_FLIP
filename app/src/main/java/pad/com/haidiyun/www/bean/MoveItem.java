package pad.com.haidiyun.www.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/4/004.
 */

public class MoveItem implements Serializable {
    private String txt;
    private String color;
    private String tag;

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
