package pad.com.haidiyun.www.net;

/**

 * 
 * @author Administrator
 * 
 */
public class RxMsg {

	// 枚举消息类型,成功，失败，进度
	public enum RxS {
		SUCCESS, FAIL, PROGRESS,TIMEOUT;
	}

	private RxS rxS;
	private String msg;

	RxMsg() {

	}

	RxMsg(RxS rxS, String msg) {
		this.rxS = rxS;
		this.msg = msg;
	}

	public RxS getRxS() {
		return rxS;
	}

	public void setRxS(RxS rxS) {
		this.rxS = rxS;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
