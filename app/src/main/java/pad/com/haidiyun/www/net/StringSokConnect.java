package pad.com.haidiyun.www.net;

import java.io.DataInputStream;
import java.net.Socket;
import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

public class StringSokConnect extends SokectConnect {
	private final static String STR_KT_FLAG = "KT";
	private final static String STR_TC_FLAG = "TC1";
	private final static String STR_DL_FLAG = "DL";

	public StringSokConnect(String ip, int port, boolean isLimitTime,
							long timeOut, byte[] sendMsg, Respon respon,String tip,String replc) {
		super(ip, port, isLimitTime, timeOut, sendMsg, respon,tip,replc);
	}

	@Override
	public String receiveData(Socket socket, Respon respon,String tip,String replc) throws Exception {
		String msg = "";
		if (socket != null && socket.isConnected()) {
			byte[] buffer = new byte[2048];
			DataInputStream stream = new DataInputStream(
					socket.getInputStream());
			StringBuilder builder = new StringBuilder();
			int length = 0;
			do {
				length = stream.read(buffer);
				if (length < 0) {
					break;
				}
				msg = EncodingUtils.getString(buffer, 0, length, "UTF-8").replaceAll("\n", replc);
				builder.append(msg);
				if(tip.equals("<EOF>")){
					if (msg.endsWith("<EOF>")) {
						if (voerWhile(builder.toString())) {
							break;
						}
					}
				}else if(tip.equals("json")){
					try {
						JSONObject object = new JSONObject(msg);

						break;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}else if(tip.equals("NULL")){

					break;
				}

			} while (true);
			msg = builder.toString();
			socket.close();
			System.out.println("返回"+msg);
		}
		return msg;
	}

	public static boolean voerWhile(String flag) {
		if (flag.contains(STR_KT_FLAG)) {
			return true;
		} else if (flag.contains(STR_TC_FLAG)) {
			return true;
		} else if (flag.contains(STR_DL_FLAG)) {
			return true;
		}  else {
			return false;
		}
	}


}
