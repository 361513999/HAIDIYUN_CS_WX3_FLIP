package pad.com.haidiyun.www.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.FtpListener;
import pad.com.haidiyun.www.inter.UpApk;

/**
 * A program demonstrates how to upload files from local computer to a remote
 * FTP server using Apache Commons Net API.
 */
public class FTPDownloadFile {

	private static int port = 21;
	private static String user = "jmj";
	private static String pass = "jmj";
	private SharedUtils utils;
	private FtpListener ftpListener;
	// 定义下载文件是BookSystem.sqlite
	private String CONFIG = "BookSystem.sqlite";
	// 定义下载文件资源是source
	private String SOURCE = "source";
	// 服务器端的source
	private String VIDEO = "video";
	private String APK = "apk";
	private String ADVER = "adver";
	private Handler downMsg;







	public FTPDownloadFile(FtpListener ftpListener, Handler downMsg) {
		// TODO Auto-generated constructor stub
		this.ftpListener = ftpListener;
		this.downMsg = downMsg;
		utils = new SharedUtils(Common.CONFIG);
		ftpClient = new FTPClient();
	}

	/**
	 * 上传报错日志
	 */
	public void uploadBugLog() {

		String server = utils.getStringValue("IP");
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if (!log) {
				ftpListener.login_status(log);
				return;
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 下载视频
	 */
	private UpApk upApk;

	public void setUpListen(UpApk upApk) {
		this.upApk = upApk;
	}

	/**
	 * 下载广告
	 */
	public void downloadAdver() {
		String server = utils.getStringValue("IP");
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if (!log) {
				ftpListener.login_status(log);
				return;
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			/*
			 * if(getExistFile(file)){ //存在这样的配置文件 }
			 */
			File base = new File(Common.SD);
			if (!base.exists()) {
				base.mkdirs();
			}
			// 下载配置文件
			File source = new File(Common.SOURCE_ADVER);
			if (!source.exists()) {
				source.mkdirs();
			}
			// 切换到另一层目录下面去
			ftpClient.changeWorkingDirectory(ADVER);
			FTPFile cfile[] = ftpClient.listFiles();
			// 下载资源文件
			for (int i = 0; i < cfile.length; i++) {
				FTPFile ffi = cfile[i];
				// if (ffi.getName().endsWith(SOURCE)&&ffi.isDirectory()) {
				// 进入source文件夹
				// 下载source下面的全部文件
				File temp = new File(Common.SOURCE_ADVER + ffi.getName());
				if (temp.exists()) {
					if (temp.length() != ffi.getSize()) {
						P.c("文件名字一样，且大小不一样");
						Tip(ffi.getName());
						down1(ftpClient, ffi.getName(), Common.SOURCE_ADVER);
					}
				} else {
					// 文件不存在，需要下载
					Tip(ffi.getName());
					down1(ftpClient, ffi.getName(), Common.SOURCE_ADVER);
				}
				temp = null;
				// }
				ffi = null;
			}

			ftpListener.down_success(true);
			BaseApplication.application.resetApplicationL();
		} catch (Exception ex) {
			ftpListener.down_success(false);
			ex.printStackTrace();
		} finally {
			close(ftpClient);

		}

	}

	public void downloadApk() {

		String server = utils.getStringValue("IP");
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if (!log) {
				ftpListener.login_status(log);
				return;
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			File apk_source = new File(Common.SOURCE_APK);
			if (!apk_source.exists()) {
				apk_source.mkdirs();
			}
			// 切换到另一层目录下面去
			ftpClient.changeWorkingDirectory(APK);
			FTPFile cfile[] = ftpClient.listFiles();
			// 下载资源文件
			if (cfile.length != 0) {
				if (cfile.length == 1) {
					FTPFile ffi = cfile[0];
					String current = BaseApplication.application
							.tripLittle(BaseApplication.application
									.getVersion());
					String net = BaseApplication.application
							.tripLittle(ffi.getName().substring(0,
									ffi.getName().length() - 4));
					int cur = Integer.parseInt(current);
					int ne = Integer.parseInt(net);

					System.out.println("cur" + cur + "    " + ne);

					if (cur < ne) {
						// 需要更新下载
						downApk(ftpClient, ffi.getName());
						upApk.success(Common.SOURCE_APK + ffi.getName());
					} else {
						// 无须更新
						upApk.info();
					}
					ffi = null;
				}
			}
		} catch (Exception ex) {
			upApk.ex();
			ex.printStackTrace();
			P.c(ex.getLocalizedMessage());
		} finally {
			close(ftpClient);
		}
	}

	/**
	 * 下载视频
	 */
	public void downloadVideo() {

		String server = utils.getStringValue("IP");
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if (!log) {
				ftpListener.login_status(log);
				return;
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			File source = new File(Common.SOURCE_VIDEO);
			if (!source.exists()) {
				source.mkdirs();
			}
			// 切换到另一层目录下面去
			ftpClient.changeWorkingDirectory(VIDEO);
			FTPFile cfile[] = ftpClient.listFiles();
			// 下载资源文件
			for (int i = 0; i < cfile.length; i++) {
				FTPFile ffi = cfile[i];
				// if (ffi.getName().endsWith(SOURCE)&&ffi.isDirectory()) {
				// 进入source文件夹
				// 下载source下面的全部文件
				File temp = new File(Common.SOURCE_VIDEO + ffi.getName());
				if (temp.exists()) {
					if (temp.length() != ffi.getSize()) {
						P.c("文件名字一样，且大小不一样");
						Tip(ffi.getName());
						down(ftpClient, ffi.getName());
					}
				} else {
					// 文件不存在，需要下载
					Tip(ffi.getName());
					down(ftpClient, ffi.getName());
				}
				temp = null;
				// }
				ffi = null;
			}

			ftpListener.down_success(true);

		} catch (Exception ex) {
			ftpListener.down_success(false);
			ex.printStackTrace();
		} finally {
			close(ftpClient);
		}
	}

	private void saveDownInfo(SharedUtils sharedUtils, FTPFile ftpFile) {
		P.c("保存更新信息");
		sharedUtils.setStringValue("updata_timestamp",
				String.valueOf(ftpFile.getTimestamp().getTimeInMillis()));
		sharedUtils.setStringValue("updata_size",
				String.valueOf(ftpFile.getSize()));
	}

	private void clearDownInfo(SharedUtils sharedUtils) {
		sharedUtils.clear("updata_timestamp");
		sharedUtils.clear("updata_size");
	}

	public boolean isUpdata() {
		String server = utils.getStringValue("IP");
		  
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if (!log) {
				return false;
			}

			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			FTPFile file[] = ftpClient.listFiles();
			/*
			 * if(getExistFile(file)){ //存在这样的配置文件 }
			 */
			File base = new File(Common.SD);
			if (!base.exists()) {
				base.mkdirs();
			}
			// 下载配置文件
			for (int i = 0; i < file.length; i++) {
				FTPFile ffi = file[i];
				if (ffi.getName().endsWith(CONFIG) && ffi.isFile()) {
					close(ftpClient);
					if (String.valueOf(ffi.getTimestamp().getTimeInMillis())
							.equals(utils.getStringValue("updata_timestamp"))
							&& String.valueOf(ffi.getSize()).equals(
									utils.getStringValue("updata_size"))) {
						//
						return false;

					} else {
						//
						return true;

					}

				}
				ffi = null;
			}
		} catch (Exception ex) {

			ex.printStackTrace();
		} finally {
			close(ftpClient);
		}
		return false;

	}

	/**
	 * 下载图片
	 */
	private FTPClient ftpClient ;
	public void downloadStand() {
		String server = utils.getStringValue("IP");
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if (!log) {
				ftpListener.login_status(log);
				return;
			}
			P.c("登录成功");
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			FTPFile file[] = ftpClient.listFiles();
			/*
			 * if(getExistFile(file)){ //存在这样的配置文件 }
			 */
			File base = new File(Common.SD);
			if (!base.exists()) {
				base.mkdirs();
			}
			// 下载配置文件
			for (int i = 0; i < file.length; i++) {
				FTPFile ffi = file[i];
				if (ffi.getName().endsWith(CONFIG) && ffi.isFile()) {
					File temp = new File(Common.SD + ffi.getName());
					if (temp.exists()) {
						if (temp.length() != ffi.getSize()) {
							P.c("文件名字一样，且大小不一样");
							Tip(ffi.getName());
							down1(ftpClient, ffi.getName(), Common.SD);
							saveDownInfo(utils, ffi);
						}
					} else {
						// 文件不存在，需要下载
						Tip(ffi.getName());
						down1(ftpClient, ffi.getName(), Common.SD);
						saveDownInfo(utils, ffi);
					}
					temp = null;
				}
				ffi = null;
			}
			File source = new File(Common.SOURCE);
			if (!source.exists()) {
				source.mkdirs();
			}
			// 切换到另一层目录下面去
			ftpClient.changeWorkingDirectory(SOURCE);
			FTPFile cfile[] = ftpClient.listFiles();
			// 下载资源文件
			for (int i = 0; i < cfile.length; i++) {
				FTPFile ffi = cfile[i];
				// if (ffi.getName().endsWith(SOURCE)&&ffi.isDirectory()) {
				// 进入source文件夹
				// 下载source下面的全部文件
				File temp = new File(Common.SOURCE + ffi.getName());
				if (temp.exists()) {
					if (temp.length() != ffi.getSize()) {
						P.c("文件名字一样，且大小不一样");
						Tip(ffi.getName());
						down1(ftpClient, ffi.getName(), Common.SOURCE);
					}
				} else {
					// 文件不存在，需要下载
					Tip(ffi.getName());
					down1(ftpClient, ffi.getName(), Common.SOURCE);
				}
				temp = null;
				// }
				ffi = null;
			}
			P.c("完成");
			ftpListener.down_success(true);
		} catch (Exception ex) {
			ftpListener.down_success(false);
			clearDownInfo(utils);
		} finally {
			close(ftpClient);
		}

	}

	private void Tip(String obj) {
		Message msg = new Message();
		msg.what = 2;
		msg.obj = obj;
		downMsg.sendMessage(msg);
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param file
	 * @return
	 */
	public boolean getExistFile(FTPFile file[]) {
		for (int i = 0; i < file.length; i++) {
			FTPFile ffi = file[i];
			if (ffi.isFile() && ffi.getName().equals(CONFIG)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断文件夹是否存在
	 * 
	 * @param file
	 * @return
	 */
	public boolean getExistDirectory(FTPFile file[]) {
		for (int i = 0; i < file.length; i++) {
			FTPFile ffi = file[i];
			if (ffi.isDirectory() && ffi.getName().equals(SOURCE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 下载事件
	 */
	private void down1(FTPClient ftpClient, String ftpFile, String base) {
		try {
			String remoteFile2 = ftpFile;
			File downloadFile2 = new File(base + ftpFile);
			OutputStream outputStream2 = new BufferedOutputStream(
					new FileOutputStream(downloadFile2));
			InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
			byte[] bytesArray = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(bytesArray)) != -1) {
				outputStream2.write(bytesArray, 0, bytesRead);
			}
			boolean success = ftpClient.completePendingCommand();
			if (success) {
				P.c("成功下载" + ftpFile);
			}
			outputStream2.close();
			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void down(FTPClient ftpClient, String ftpFile) {
		try {

			String remoteFile1 = ftpFile;
			File downloadFile1 = new File(new String(
					(Common.SOURCE_VIDEO + ftpFile).getBytes("ISO-8859-1"),
					"GBK"));
			OutputStream outputStream1 = new BufferedOutputStream(
					new FileOutputStream(downloadFile1));
			boolean success = ftpClient
					.retrieveFile(remoteFile1, outputStream1);
			outputStream1.close();

			if (success) {
				// LogUtils.print("File #1 has been downloaded successfully.");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void downApk(FTPClient ftpClient, String ftpFile) {
		try {

			String remoteFile1 = ftpFile;
			File downloadFile1 = new File(
					new String((Common.SOURCE_APK + ftpFile)
							.getBytes("ISO-8859-1"), "GBK"));
			OutputStream outputStream1 = new BufferedOutputStream(
					new FileOutputStream(downloadFile1));
			boolean success = ftpClient
					.retrieveFile(remoteFile1, outputStream1);
			outputStream1.close();

			if (success) {
				// LogUtils.print("File #1 has been downloaded successfully.");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 关闭连接
		public void close() {
			try {
				if(ftpClient!=null){
					if (ftpClient.isConnected()) {
						ftpClient.logout();
						ftpClient.disconnect();
					}
				}
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	// 关闭连接
	private void close(FTPClient ftpClient) {
		try {
			if(ftpClient!=null){
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}