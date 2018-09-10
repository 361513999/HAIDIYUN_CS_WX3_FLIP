package pad.com.haidiyun.www.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.json.JSONException;
import org.json.JSONObject;

import pad.com.haidiyun.www.common.P;

/**
 * TCP Socket客户端
 */
public abstract class TcpClient implements Runnable {

	private int port;
	private String hostIP;
	private boolean connect = false;
	private SocketAddress socketAddress;
	private StringBuffer builder;
	private SocketTransceiver transceiver;
	private int TIME_OUT= 6000;

	/**
	 * 建立连接
	 * <p>
	 * 连接的建立将在新线程中进行
	 * <p>
	 * 连接建立成功，回调{@code onConnect()}
	 * <p>
	 * 连接建立失败，回调{@code onConnectFailed()}
	 *
	 * @param hostIP
	 *            服务器主机IP
	 * @param port
	 *            端口
	 */
	private String endTag[];
	private String error;
	public void connect(String hostIP, int port,StringBuffer builder,String ...endTag) {
		this.hostIP = hostIP;
		this.port = port;
		this.builder  = builder;
		this.endTag = endTag;
		new Thread(this).start();
	}
	public void connect(String hostIP, int port,StringBuffer builder) {
		this.hostIP = hostIP;
		this.port = port;
		this.builder  = builder;
		new Thread(this).start();
	}
	private boolean flag = false;
	public void connect(String hostIP, int port,StringBuffer builder,boolean flag) {
		this.hostIP = hostIP;
		this.port = port;
		this.builder  = builder;
		this.flag = flag;
		new Thread(this).start();
	}
	private JSONObject o = null;
	@Override
	public void run() {
		try {

			Socket socket = new Socket();
//			socket.setReuseAddress(true);
			//socket.setSoLinger(true, 0);
			//socket.setTcpNoDelay(true);
			//socket.setPerformancePreferences(0, 1, 2);
			socketAddress = new InetSocketAddress(hostIP, port);
			socket.connect(socketAddress,TIME_OUT);
			P.c("连接"+hostIP+"==="+port);
			transceiver = new SocketTransceiver(socket) {

				@Override
				public void onReceive(InetAddress addr, String s) {

//					P.c("--"+s);
					builder.append(s);
					if(flag){
						try {
							o = new JSONObject(builder.toString());
							o = null;
							TcpClient.this.onReceive(this, builder.toString());
							disconnect();
							return;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							o = null;

						}
					}else{
						if(endTag!=null){

							for(int i=0;i<endTag.length;i++){
								if(builder.toString().endsWith(endTag[i])){
									TcpClient.this.onReceive(this, builder.toString());
									disconnect();
								}
							}


						}else{

							TcpClient.this.onReceive(this, builder.toString());
							disconnect();
						}
					}

				}
				@Override
				public void onDisconnect(InetAddress addr) {
					connect = false;
					TcpClient.this.onDisconnect(this);
					disconnect();
				}
				@Override
				public void readTimeOut(InetAddress addr) {
					// TODO Auto-generated method stub
					connect = false;
					TcpClient.this.readTimeOut(this);
					disconnect();
				}
			};
			transceiver.start();
			connect = true;
			this.onConnect(transceiver);
		} catch (Exception e) {
			e.printStackTrace();
			this.onConnectFailed();
		}
	}

	/**
	 * 断开连接
	 * <p>
	 * 连接断开，回调{@code onDisconnect()}
	 */
	public void disconnect() {
		if (transceiver != null) {
			transceiver.stop();
			transceiver = null;
		}
	}

	/**
	 * 判断是否连接
	 *
	 * @return 当前处于连接状态，则返回true
	 */
	public boolean isConnected() {
		return connect;
	}

	/**
	 * 获取Socket收发器
	 *
	 * @return 未连接则返回null
	 */
	public SocketTransceiver getTransceiver() {
		return isConnected() ? transceiver : null;
	}

	/**
	 * 连接建立
	 *
	 * @param transceiver
	 *            SocketTransceiver对象
	 */
	public abstract void onConnect(SocketTransceiver transceiver);

	/**
	 * 连接建立失败
	 */
	public abstract void onConnectFailed();

	/**
	 * 接收到数据
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 *
	 * @param transceiver
	 *            SocketTransceiver对象
	 * @param s
	 *            字符串
	 *            flag 结束状态
	 */
	public abstract void onReceive(SocketTransceiver transceiver, String buffer);

	/**
	 * 连接断开
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 *
	 * @param transceiver
	 *            SocketTransceiver对象
	 */
	public abstract void onDisconnect(SocketTransceiver transceiver);
	/**
	 * 读取超时
	 */
	public abstract void readTimeOut(SocketTransceiver transceiver);
}
