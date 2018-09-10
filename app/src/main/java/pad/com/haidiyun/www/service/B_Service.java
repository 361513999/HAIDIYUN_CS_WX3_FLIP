package pad.com.haidiyun.www.service;

import com.hdy.upload.aidl.IUploadService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;

public class B_Service extends Service{
	@Override
	
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return iBackService;
	}
	IUploadService.Stub iBackService = new IUploadService.Stub() {

		@Override
		public String getMark() throws RemoteException {
			// TODO Auto-generated method stub
			SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
			return sharedUtils.getStringValue("table_name");
		}

		@Override
		public String getStatusCD() throws RemoteException {
			// TODO Auto-generated method stub
			return "";
		}
 
	};

	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

 	
}
