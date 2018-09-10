package pad.com.haidiyun.www.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.service.FloatService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.WindowManager;
import android.widget.Toast;


public class BaseApplication extends Application {
	private static final boolean DEVELOPER_MODE = true;
	public static BaseApplication application;
	protected boolean isNeedCaughtExeption = true;// 是否捕获未知异常
	private MyUncaughtExceptionHandler uncaughtExceptionHandler;
	private static String  packgeName;
	static {
		try {
			System.loadLibrary("UsbSupply");
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();


		application = this;
		//packgeName = getPackageName();
		if (isNeedCaughtExeption) {
			cauchException();
		}
		startFloat();
	}
	public static String getPName(){
		packgeName = "pad.com.haidiyun.www";
		return  packgeName;
	}
	/**
	 * 获得应用版本
	 * @return
	 */
	public String tripLittle(String temp){
		return temp.replaceAll("\\.", "");
	}
	public String getVersion(){

		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void startFloat(){
		Intent intent = new Intent(this,FloatService.class);
		intent.putExtra("view_gone", 0);
		startService(intent);

	}
	private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();
	private PendingIntent restartIntent;
	public WindowManager.LayoutParams getMywmParams(){
		return wmParams;
	}


	private void cauchException() {
		System.out.println("-----------------------------------------------------");

		// 程序崩溃时触发线程
		uncaughtExceptionHandler = new MyUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	}
	private class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// 保存错误日志
//			saveCatchInfo2File(ex);

			FileUtils.writeLog(toTxt(ex), "程序异常");

			//如果报错就不进行重启
			resetApplicationAll();

		}
	};
	private String toTxt(Throwable ex){
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String sb = writer.toString();
		return sb;
	}
	public void resetApplication(){

		Intent intent = new Intent();
		// 参数1：包名，参数2：程序入口的activity
		intent.setClassName(packgeName, packgeName + ".ui.FlipNextActivity");
		restartIntent = PendingIntent.getActivity(getApplicationContext(), -1, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		// 1秒钟后重启应用
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
		// 关闭当前应用
	}
	public void resetApplicationL(){

		Intent intent = new Intent();
		// 参数1：包名，参数2：程序入口的activity
		intent.setClassName(packgeName, packgeName + ".ui.LanuchActivity");
		restartIntent = PendingIntent.getActivity(getApplicationContext(), -1, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		// 1秒钟后重启应用
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
		// 关闭当前应用
	}

	public void resetApplicationAll(){

		Intent intent = new Intent();
		// 参数1：包名，参数2：程序入口的activity
		intent.setClassName(packgeName, packgeName + ".ui.LanuchActivity");
		restartIntent = PendingIntent.getActivity(getApplicationContext(), -1, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		// 1秒钟后重启应用
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
		// 关闭当前应用
		AppManager.getAppManager().finishAllActivity();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	/**
	 * 保存错误信息到文件中
	 *
	 * @return 返回文件名称
	 */
	private String saveCatchInfo2File(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String sb = writer.toString();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String time = formatter.format(new Date());
			String fileName = time + ".txt";
			System.out.println("fileName:" + fileName);
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String filePath =  Common.BASE_DIR+"/HAIDIYUN/" + packgeName+ "/crash/";
				File dir = new File(filePath);
				if (!dir.exists()) {
					if (!dir.mkdirs()) {
						// 创建目录失败: 一般是因为SD卡被拔出了
						return "";
					}
				}
				P.c("filePath + fileName:" + filePath + fileName);
				FileOutputStream fos = new FileOutputStream(filePath + fileName);
				fos.write(sb.getBytes());
				fos.close();
				//文件保存完了之后,在应用下次启动的时候去检查错误日志,发现新的错误日志,就发送给开发者
			}
			return fileName;
		} catch (Exception e) {
			P.c("an error occured while writing file..." + e.getMessage());
		}
		return null;
	}
}
