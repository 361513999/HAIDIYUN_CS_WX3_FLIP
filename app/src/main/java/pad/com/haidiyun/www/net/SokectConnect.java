package pad.com.haidiyun.www.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;

import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.net.RxMsg.RxS;

import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
 * 运行sokect
 *使用rx回到主线�?
 */
public abstract class SokectConnect implements Runnable {
	public final String TAG = "SokectConnect";

	//
	private String ip;
	private int port;
	//
	private boolean isLimitTime;
	private long timeOut = 0;
	private byte[] sendMsg = null;
	private Respon respon = null;

	//
	private SocketAddress socketaddress;
	private Socket socket;
	// rx
	private Single<RxMsg> msgSingle;
	private String replc;
	//
	public SokectConnect(String ip, int port, boolean isLimitTime,
			long timeOut, byte[] sendMsg, Respon respon,String tip,String replc) {
		this.ip = ip;
		this.port = port;
		this.isLimitTime = isLimitTime;
		this.timeOut = timeOut;
		this.sendMsg = sendMsg;
		this.respon = respon;
		this.tip = tip;
		this.replc = replc;
	}
	private String tip;

	public void run() {
		try {
			//
			P.c("执行了没有");
			connect(4000, 30000);
			// 强制超时
			setTimeOut();
			//
			send();
			//
			String msg = receiveData(socket, respon,tip,replc);
			if (!msg.equals("")) {
				sendToSubscriber(RxS.SUCCESS, msg);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			sendToSubscriber(RxS.FAIL, "出现异常");
		}
	}

	// 连接
	private void connect(int soTimeout, int timeout) throws IOException {
		socketaddress = new InetSocketAddress(ip, port);
		socket = new Socket();
		socket.setSoTimeout(soTimeout);
		socket.setTcpNoDelay(true);
		socket.connect(socketaddress, timeout);
	}

	// 发�??
	private void send() throws IOException {
		if (socket != null && socket.isConnected()) {
			// 发�?�开�?
			DataOutputStream stream = new DataOutputStream(
					socket.getOutputStream());
			stream.write(sendMsg);
			stream.flush();
		}
	}

	// �?要实现的代码
	public abstract String receiveData(Socket socket, Respon respon,String tip,String replc)
			throws Exception;
	 
	// 定时关闭，否则网络很差的情况下，会遇到卡住的情况
	private void setTimeOut() {
		new Thread(new Runnable() {
			public void run() {
				P.c("超时监测");
				if (isLimitTime) {
					try {
						Thread.sleep(timeOut);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					P.c("00");
					sokectClose(true);
				}
			}
		}).start();
		
		
	}

	// 给外部线程停止的方法
	public void stop() {
		sokectClose(false);
		unsubRx();
	}

	// 关闭sokect
	private void sokectClose(boolean flag) {
		try {
			if (socket != null) {
				//System.out.println(socket.hashCode()+"----socket_before    "+socket.isConnected()  +"==="+socket.isClosed());
//				socket.shutdownOutput();
//				socket.shutdownInput();
				socket.close();
				 
				//System.out.println(socket.hashCode()+"-----socket_after     "+socket.isConnected()+"==="+socket.isClosed());
				socket = null;
			}
		} catch (IOException e) {
			socket = null;
		}
		if(flag){
			sendToSubscriber(RxS.FAIL, "出现异常");
		}
	}
	 
	
	private SingleSubscriber<RxMsg> msgSubscriber = new SingleSubscriber<RxMsg>() {

		@Override
		public void onSuccess(RxMsg msg) {

			sokectClose(false);
			unsubRx();

			if (msg.getRxS() == RxS.FAIL) {
				respon.onFail();
			}
			if (msg.getRxS() == RxS.SUCCESS) {
				respon.onSuccess(msg.getMsg());
			}
			if (msg.getRxS() == RxS.TIMEOUT) {
				respon.timeOut();
			}
		}

		@Override
		public void onError(Throwable error) {
			sokectClose(false);
			unsubRx();
		}
	};

	// 发�?�rx消息
	private void sendToSubscriber(final RxS rxS, final String str) {

		msgSingle = Single.fromCallable(new Callable<RxMsg>() {
			@Override
			public RxMsg call() {
				return new RxMsg(rxS, str);
			}
		});
		
		msgSingle.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(msgSubscriber);
	}

	// 注销RX接受消息
	private void unsubRx() {
		if (msgSubscriber != null && !msgSubscriber.isUnsubscribed()) {
			msgSubscriber.unsubscribe();
		}
	}
}
