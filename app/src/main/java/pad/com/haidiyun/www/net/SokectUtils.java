package pad.com.haidiyun.www.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;


/*
 * 
 * @author tt
 *1.builder模式的构造器
 *2.线程池来处理数据
 *3.单例模式方便使用
 */
public class SokectUtils {

	// 定长线程�?,根据机器的cpu决定线程数，4�?
	private static int threaCount = Runtime.getRuntime().availableProcessors();
	private static ExecutorService msgThreadPool = Executors
			.newFixedThreadPool(threaCount);

	// �?单单例模�?
	private static SokectUtils instance;

	private SokectUtils() {
	}

	//
	public static SokectUtils getInstance() {
		if (instance == null) {
			instance = new SokectUtils();
		}
		return instance;
	}

	// 参数建�?�内部类，方便构�?
	public static class Builder {
		private final String ip;
		private final int port;
		//
		private boolean isLimitTime = false;
		private long timeOut = 0;
		private byte[] sendMsg = null;

		//
		public Builder(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		public Builder isLimitTime(boolean isLimitTime) {
			this.isLimitTime = isLimitTime;
			return this;
		}

		// 如果调了这个那么就是限制时间
		public Builder timeOut(long timeOut) {
			isLimitTime(true);
			this.timeOut = 10000;
			return this;
		}

		public Builder setMsg(byte[] sendMsg) {
			this.sendMsg = sendMsg;
			return this;
		}

		// 发�??
		public void send(Respon respon,String tip,String replc) {
			SokectConnect sokectConnect = new StringSokConnect(ip, port,
					isLimitTime, timeOut, sendMsg, respon,tip,replc);
			msgThreadPool.execute(sokectConnect);
		}
		/**
		 * 
		 * @param respon
		 * @param tip  发送结束符
		 * @param port
		 * @param replc 结果分隔符
		 */
		public void send(Respon respon,String tip,int port,String replc) {
			SokectConnect sokectConnect = new StringSokConnect(ip, port,
					isLimitTime, timeOut, sendMsg, respon,tip,replc);
			msgThreadPool.execute(sokectConnect);
		}
	}

	public static Builder Builder(String ip, int port) {
		return new Builder(ip, port);
	}

	public static Builder Builder() {
		SharedUtils utils = new SharedUtils(Common.CONFIG);
		String ip = utils.getStringValue("IP");
		return new Builder(ip, Common.PORT433);
	}
}
