package pad.com.haidiyun.www.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.http.util.EncodingUtils;
import android.os.Handler;
import android.os.Looper;

import pad.com.haidiyun.www.common.P;

/**
 * Socket收发器 通过Socket发送数据，并使用新线程监听Socket接收到的数据
 */
public abstract class SocketTransceiver implements Runnable {

	protected Socket socket;
	protected InetAddress addr;
	protected DataInputStream in;
	protected DataOutputStream out;
	private boolean runFlag;
	private Handler handler = new Handler(Looper.getMainLooper());
	/**
	 * 实例化
	 * 
	 * @param socket
	 *            已经建立连接的socket
	 */
	private Runnable runnable;
	public SocketTransceiver(Socket socket) {
		this.socket = socket;
		 		this.addr = socket.getInetAddress();
		 		runnable = new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						SocketTransceiver.this.readTimeOut(addr);
					}
				};
	}

	/**
	 * 获取连接到的Socket地址
	 * 
	 * @return InetAddress对象
	 */
	public InetAddress getInetAddress() {
		return addr;
	}

	/**
	 * 开启Socket收发
	 * <p>
	 * 如果开启失败，会断开连接并回调{@code onDisconnect()}
	 */
	public void start() {
		runFlag = true;
		new Thread(this).start();
	}

	/**
	 * 断开连接(主动)
	 * <p>
	 * 连接断开后，会回调{@code onDisconnect()}
	 */
	public void stop() {
		System.out.println("=========关闭socket");
		runFlag = false;
		try {
			/*socket.shutdownOutput();
			socket.shutdownInput();
			in.close();
			out.close();
			socket.close();
			socket = null;*/
			handler.removeCallbacks(runnable);
			
			
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (socket != null) {
					socket.close();
					socket = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				socket = null;
			}
			/*if (socket != null) {
				if (!socket.isInputShutdown()) {
					socket.shutdownInput();
				}
				if (!socket.isOutputShutdown()) {
					socket.shutdownOutput();
				}
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				socket.close();// 关闭socket
				socket = null;
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			 
		}
	}

	/**
	 * 发送字符串
	 * 
	 * @param s
	 *            字符串
	 * @return 发送成功返回true
	 */
	public boolean send(String var) {
		if (out != null) {
			try {
//				out.write(EncodingUtils.getBytes(var+"<EOF>", "UTF-16LE"));
				 
				out.write(EncodingUtils.getBytes(var, "UTF-8"));
				out.flush();
				P.c("发送成功");
				handler.postDelayed(runnable,10000);
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		P.c("发送失败");
		return false;
	}
	public boolean send433(String var) {
		if (out != null) {
			try {
			 
				out.write(EncodingUtils.getBytes(var+"<EOF>", "UTF-8"));
				//out.write(EncodingUtils.getBytes(var, "UTF-8"));
				out.flush();
				P.c("发送成功");
				handler.postDelayed(runnable,10000);
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		P.c("发送失败");
		return false;
	}
	/**
	 * 带超时
	 */
	/**
	 * 发送字符串
	 * 
	 * @param s
	 *            字符串
	 * @return 发送成功返回true
	 */
	public boolean send(String var,int TIME_OUT) {
		if (out != null) {
			try {
				out.write(EncodingUtils.getBytes(var, "UTF-8"));
//				out.write(EncodingUtils.getBytes(var+"<EOF>", "UTF-16LE"));
				out.flush();
				P.c("发送成功");
				handler.postDelayed(runnable, TIME_OUT);
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		P.c("发送失败");
		return false;
	}
	
	public boolean sendTxt(String var) {
		if (out != null) {
			try {
				out.write(EncodingUtils.getBytes(var, "UTF-8"));
				out.flush();
				P.c("发送成功");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		P.c("发送失败");
		return false;
	}
	/**
	 * 监听Socket接收的数据(新线程中运行)
	 */
	private static final int BUF_SIZE = 1024 * 10;// 一次接收缓冲区大小
	@Override
	public void run() {
		try {
			in = new DataInputStream(this.socket.getInputStream());
			out = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			runFlag = false;
		}
		System.out.println("========="+runFlag);
		while (runFlag) {
			System.out.println("=========读取");
			try {
				byte[] buffer = new byte[BUF_SIZE];
				int length = 0;
//				P.c("socket.isClosed()"+socket.isClosed());
//				P.c("socket.isInputShutdown()"+socket.isInputShutdown());
				while (socket!=null&&!socket.isClosed() && !socket.isInputShutdown()
						&& ((length = in.read(buffer)) != -1)){
//					System.out.println("=========内容"+EncodingUtils.getString(buffer, 0, length,"UTF-8"));
//					P.c(EncodingUtils.getString(buffer, 0, length,"UTF-16LE"));
					//P.c(EncodingUtils.getString(buffer, 0, length,"UTF-16LE"));
//					this.onReceive(addr, EncodingUtils.getString(buffer, 0, length,"UTF-16LE"));
					//P.c("返回数据"+EncodingUtils.getString(buffer, 0, length,"UTF-8"));
					this.onReceive(addr, EncodingUtils.getString(buffer, 0, length,"UTF-8").replaceAll("\n", "").replaceAll("\r", ""));
				}
			} catch (IOException e) {
				// 连接被断开(被动)
				runFlag = false;
			}
		}
		/*// 断开连接
		try {
			System.out.println("断开");
			in.close();
			out.close();
			socket.close();
			in = null;
			out = null;
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		this.onDisconnect(addr);
	}

	/**
	 * 接收到数据
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 * 
	 * @param addr
	 *            连接到的Socket地址
	 * @param s
	 *            收到的字符串
	 */
	public abstract void onReceive(InetAddress addr, String s);

	/**
	 * 连接断开
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 * 
	 * @param addr
	 *            连接到的Socket地址
	 */
	public abstract void onDisconnect(InetAddress addr);
	/**
	 * 读取超时
	 */
	public abstract void readTimeOut(InetAddress addr);
}
