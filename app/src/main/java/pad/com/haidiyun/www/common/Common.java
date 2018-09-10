package pad.com.haidiyun.www.common;

import java.util.HashMap;
import java.util.Map;
import pad.com.haidiyun.www.base.BaseApplication;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Environment;

public class Common {
	public static boolean IS_SHOW = false;
	public static boolean TEMP_PRINT = false;
	public static int COMMON_TIME_OUT = 6000;
	public static String BASE_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static String SD = BASE_DIR+"/HAIDIYUN/";
	public static String SOURCE = SD+"IMAGES/";
	public static String SOURCE_VIDEO = SD+"VIDEOS/";
	public static String SOURCE_ZIP = SD+"LOCAL/";
	public static String SOURCE_APK = SD+"APK/";
	public static String SOURCE_ADVER = SD+"ADVER/";
	public static String SOURCE_DB = "BookSystem.sqlite";
	public static String tables = "tables.json";
	public static final String  USERS= "users";
	public static final String CONFIG = "config";
	public final static String DB_DIR = "data/data/"+BaseApplication.application.getPName()+"/databases/";
	public static final String SERVICE_ACTION = "pad.com.haidiyun.www.float";
	//判断是否是切换到广告页
	public static int DOING = -1;
	public static String APK_LOG = SD+"LOG/";
	public static final String LOCKED_WIFI_NAME = "wifi_lock_name";
	public static final String LOCKED_WIFI_PASS = "wifi_lock_pass";
	public static final int PORT433 = 11017;
	public static final String SHARED_WIFI = "wifi_lock_config";
	public static final int DB_VERSION = 1;

	//---------------------
	public static final String FOC_CHANGE = "org.wifi.reset";
	public static int LAST_PAGE = 0;
	//-----------------
	//引导点击
	public static final String TIP_CONFIG = "tip_config";
	public static final String tip_click = "tip_click";
	public static final String tip_first = "tip_first";
	public static final String tip_buy = "tip_buy";
	//视频索引
	public static final String VIDEO_TAG="video_tag";
	//控制返回,默认是可以返回的
	public static boolean CAN_BACK = true;
	public static String TOUCH_DOWN = "pad.tuch.screen";
	public static String TOUCH_PAUSE = "pad.tuch.screen.pause";
	public static Map<String,Integer> guKeys = new HashMap<String, Integer>();
	//-------------NFC属性
	public static String[][] TECHLISTS;
	public static IntentFilter[] FILTERS;

	static {
		try {
			TECHLISTS = new String[][] { { IsoDep.class.getName() },
					{ NfcV.class.getName() }, { NfcF.class.getName() },{MifareClassic.class.getName()} ,{MifareUltralight.class.getName()}};

			//			FILTERS = new IntentFilter[] { new IntentFilter(
			//					NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
			FILTERS = new IntentFilter[] { new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
		} catch (Exception e) {
		}
	}
}
