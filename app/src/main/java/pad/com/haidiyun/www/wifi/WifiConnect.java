package pad.com.haidiyun.www.wifi;

import java.util.List;

import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.WifiC;
import pad.com.haidiyun.www.widget.NewDataToast;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiConnect {
	private WifiManager mWifiManager;
	private WifiLock mWifiLock;
	private SharedUtils utils;
	public WifiConnect(Context context) {
		// TODO Auto-generated constructor stub
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mWifiLock = mWifiManager.createWifiLock(Common.LOCKED_WIFI_NAME);
		utils = new SharedUtils(Common.SHARED_WIFI);
	}

	// 锁定WifiLock
	private void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	private void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock != null) {
			if (mWifiLock.isHeld()) {
				mWifiLock.release();
			}
		}

	}
	/**
	 * 获得wifi列表
	 * @return
	 */
	public List<ScanResult> getWifiList(){
		mWifiManager.startScan();
		if(!mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(true);
		}
		return mWifiManager.getScanResults();


	}
	private void clearAll(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		// 按照networkId从大到小排序

		for (WifiConfiguration existingConfig : existingConfigs) {
			if (!existingConfig.SSID.equals("\"" + SSID + "\"")) {
				mWifiManager.disableNetwork(existingConfig.networkId);
				mWifiManager.removeNetwork(existingConfig.networkId);
			}
		}
		mWifiManager.saveConfiguration();
	}

	public boolean reset(Context context,String WIFI_NAME,String WIFI_PASS,WifiC connect) {
		boolean flh = false;
		releaseWifiLock();
		if (WIFI_PASS.length()!=0) {
			clearAll(WIFI_NAME);
			WifiConfiguration con = CreateWifiInfo(
					WIFI_NAME,
					WIFI_PASS, 3);
			int newID = mWifiManager.addNetwork(con);
			flh = mWifiManager.enableNetwork(newID, true);
			if (flh) {

				if(connect!=null){
					connect.connected();

					utils.setStringValue(Common.LOCKED_WIFI_NAME, WIFI_NAME);
					utils.setStringValue(Common.LOCKED_WIFI_PASS, WIFI_PASS);
					NewDataToast.makeText( "配置成功");
				}else {
//					NewDataToast.makeText(context, "重置WIFI");
					//NewDataToast.makeText(context, "配置失败,请检查密码");
				}

			} else {
				NewDataToast.makeText( "配置失败,请检查密码");


			}
			mWifiManager.updateNetwork(con);
			mWifiManager.saveConfiguration();
			acquireWifiLock();
		} else {
			NewDataToast.makeText( "请输入WIFI密码");
		}
		return flh;
	}
	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration CreateWifiInfo(String SSID, String Password,
											 int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == 1) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
			config.priority = 10000;
		}
		return config;
	}

}
