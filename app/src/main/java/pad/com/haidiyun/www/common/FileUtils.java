package pad.com.haidiyun.www.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.json.JSONException;
import org.json.JSONObject;

import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.widget.dialog.Animations.BaseEffects;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;

public class FileUtils {
	public static  void start(Effectstype type,View v) {
		BaseEffects animator = type.getAnimator();
		animator.setDuration(Math.abs(800));
		animator.start(v);
	}
	public static  void start(Effectstype type,View v,int time) {
		BaseEffects animator = type.getAnimator();
		animator.setDuration(Math.abs(time));
		animator.start(v);
	}
	/**
	 * 检查数据库是否存在
	 * @return
	 */
	public static boolean db_exits(){
		File file = new File(Common.DB_DIR+Common.SOURCE_DB);
		if(file.exists()){
			return true;
		}
		return false;
	}
	public static void unZipFile(String archive, String decompressDir)
			throws IOException, FileNotFoundException, ZipException {
		BufferedInputStream bi;
		ZipFile zf = new ZipFile(archive, "UTF-8");
		Enumeration e = zf.getEntries();
		Log.v("内容" ,e.hasMoreElements()+"");
		while (e.hasMoreElements()) {
			ZipEntry ze2 = (ZipEntry) e.nextElement();
			String entryName = ze2.getName();
			Log.v("内容" ,entryName);
			/*String path = decompressDir + "/" + entryName;
			P.c(ze2.isDirectory()+"--"+ze2.getName());
			if (ze2.isDirectory()) {
//				P.c("正在创建解压目录 - " + entryName);
				File decompressDirFile = new File(path);
				if (!decompressDirFile.exists()) {
					decompressDirFile.mkdirs();
				}
			} else {
				String fileDir = path.substring(0, path.lastIndexOf("/"));
//				P.c(fileDir+"正在创建解压文件 - " + entryName);
				File fileDirFile = new File(fileDir);
				if (!fileDirFile.exists()) {
					fileDirFile.mkdirs();
				}
				BufferedOutputStream bos = null;
				if(entryName.endsWith(".db")){
					File dir = new File(Common.DB_DIR);
					if(!dir.exists()){
						dir.mkdirs();
					}
					bos = new BufferedOutputStream(new FileOutputStream(Common.DB_DIR + entryName));
				}else{
					bos = new BufferedOutputStream(
							new FileOutputStream(decompressDir + "/" + entryName));
				}
				bi = new BufferedInputStream(zf.getInputStream(ze2));
				byte[] readContent = new byte[1024];
				int readCount = bi.read(readContent);
				while (readCount != -1) {
					bos.write(readContent, 0, readCount);
					readCount = bi.read(readContent);
				}
				bos.close();
			}*/
		}
		zf.close();
		// bIsUnzipFinsh = true;
	}

	/**
	 *
	 * @param zipFile
	 *            源文件
	 * @param folderPath
	 *            目标目录
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 */

	// 读取指定路径文本文件
	public static synchronized String read(String filePath) {
		StringBuilder str = new StringBuilder();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filePath));
			String s;
			try {
				while ((s = in.readLine()) != null)
					str.append(s + '\n');
			} finally {
				in.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str.toString();
	}

	// 写入指定的文本文件，append为true表示追加，false表示重头开始写，
	// text是要写入的文本字符串，text为null时直接返回
	public static void write(String filePath, boolean append, String text) {
		if (text == null)
			return;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath,
					append));
			try {
				out.write(text);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获得指定文件的byte数组
	 */
	private byte[] getBytes(String filePath){
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static void getFile(byte[] bfile, String filePath,String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath+"\\"+fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static Bitmap blurBitmap(Bitmap bitmap) {
		// Let's create an empty bitmap with the same size of the bitmap we want
		// to blur
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		// Instantiate a new Renderscript
		RenderScript rs = RenderScript.create(BaseApplication.application);
		// Create an Intrinsic Blur Script using the Renderscript
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs));
		// Create the Allocations (in/out) with the Renderscript and the in/out
		// bitmaps
		Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
		Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
		// Set the radius of the blur
		blurScript.setRadius(15.f);
		// Perform the Renderscript
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);
		// Copy the final bitmap created by the out Allocation to the outBitmap
		allOut.copyTo(outBitmap);
		// recycle the original bitmap
		bitmap.recycle();
		// After finishing everything, we destroy the Renderscript.
		rs.destroy();
		return outBitmap;
	}
	public static void writeLog(String text,String tag) {
		SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
		if(sharedUtils.getStringValue("IP").length()==0){
			//没有配置就不进行日志记录，减少无用检查数据
			return;
		}
		String childFileName = TimeUtil.getTimeLog(System.currentTimeMillis());
		String logPath = Common.APK_LOG;
		File file = new File(Common.APK_LOG);
		if(!file.exists()){
			file.mkdirs();
			//不存在就创建目录
		}
		String fileNs[] = file.list();

		if (text == null)
			return;
		try {
			String realName = name(file, fileNs, childFileName)+".txt";
			BufferedWriter out = new BufferedWriter(new FileWriter(logPath+realName,
					true));
			try {
				out.write(tag+"-----"+TimeUtil.getTime(System.currentTimeMillis())+"\n");
				out.write(text);
				out.write("\n");
			} finally {
				out.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static boolean deleteDir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	public static String name(File file,String fileNs[],String childFileName){
		for(int i=0;i<fileNs.length;i++){
			if(fileNs[i].substring(0,fileNs[i].lastIndexOf(".")).equals(childFileName)){
				return childFileName;
			}
		}
		if(fileNs.length!=0){
			deleteDir(file);
		}
		//
		return childFileName;
	}
	/**
	 * 433
	 *
	 * @param temp
	 * @return
	 */
	public static String getReals(String temp) {
		return temp.replaceAll("^(0+)", "");
	}
	public static String mac() {
		SharedUtils utils = new SharedUtils(Common.CONFIG);
//		return getDeviceId().replace(":", "");
		return utils.getStringValue("dev");
	}
	public static String getDeviceId() {
		// 根据Wifi信息获取本地Mac
		String ANDROID = "";
		WifiManager wifi = (WifiManager) BaseApplication.application
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info != null) {
			ANDROID = info.getMacAddress();
		}
		if(ANDROID!=null&&ANDROID.length()!=0){
			ANDROID = ANDROID.substring(ANDROID.length()-10,ANDROID.length());
		}
		return "hdy";
//		return "123";
	}
	/**
	 * 格式化json
	 * @return
	 * @throws JSONException
	 */
	public static String formatJson(String json) throws JSONException{
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject.getString("d");
	}
}
