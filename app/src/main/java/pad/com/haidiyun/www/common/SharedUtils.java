package pad.com.haidiyun.www.common;
import pad.com.haidiyun.www.base.BaseApplication;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SharedUtils {
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	@SuppressWarnings("deprecation")
	public SharedUtils(String NAME) {
		// TODO Auto-generated constructor stub
		preferences = BaseApplication.application.getSharedPreferences(NAME,  Context.MODE_MULTI_PROCESS|Context.MODE_WORLD_WRITEABLE);
	}


	/**
	 * 清空
	 */
	public void clear() {
		editor = preferences.edit();
		editor.clear();
		editor.commit();
		
	}
	/**
	 * 判断是否存在
	 *
	 * @param tag
	 * @return
	 */
	public boolean isHere(String tag) {
		return preferences.contains(tag);
	}

	/**
	 * 设置String类型的sharedpreferences
	 *
	 * @param key
	 * @return
	 */
	public void setStringValue(String key, String value) {
		editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	public void setLongValue(String key, long value){
		editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	public long getLong(String key){
		return preferences.getLong(key, -1);
	}
	
	public void setIntValue(String key,int value){
		editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	public int getIntValue(String key){
		return preferences.getInt(key, 0);
	}
	/**
	 * 获得String类型的sharedpreferences
	 *
	 * @param key
	 * @return
	 */
	public String getStringValue(String key) {
		return preferences.getString(key, "");
	}
	/**
	 */
	public String getValue(String key) {
		return preferences.getString(key, "0");
	}

	/**
	 * 设置boolean类型的sharedpreferences
	 *
	 * @param key
	 * @return
	 */
	public void setBooleanValue(String key, boolean value) {
		editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	public void clear(String key) {
		editor = preferences.edit();
		editor.remove(key);
		editor.commit();
	}
	/**
	 * 获得boolean类型的sharedpreferences
	 *
	 * @param key
	 * @return
	 */
	public Boolean getBooleanValue(String key) {
		// 默认为false
		return preferences.getBoolean(key, false);
	}
	public ArrayList<String> getKeys(){
		ArrayList<String> keys = new ArrayList<>();
		Map<String,String> vl  = (Map<String, String>) preferences.getAll();
		Set set = vl.entrySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String temp  =String.valueOf(it.next());
			P.c("键值"+temp);
			keys.add(temp.split("=")[0].trim());
		}
		return  keys;
	}


}
