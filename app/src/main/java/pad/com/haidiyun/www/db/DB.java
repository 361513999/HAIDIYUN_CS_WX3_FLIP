package pad.com.haidiyun.www.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.Dish;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.FlipBean;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.MenuBean;
import pad.com.haidiyun.www.bean.Pbean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.bean.ResonMenuBean;
import pad.com.haidiyun.www.bean.SuitBean;
import pad.com.haidiyun.www.bean.SuitMenuBean;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.bean.TablesBean;
import pad.com.haidiyun.www.bean.TcBean;
import pad.com.haidiyun.www.bean.TzBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
	public static DB dao;
	private static DBHelper dbHelper;
	private static SQLiteDatabase db;
	private void DB() {
	}

	/**
	 * 单列数据库操作对象
	 *
	 * @return
	 */
	public static synchronized DB getInstance() {
		if (dao == null) {
			synchronized (DB.class) {
				if (dao == null) {
					dao = new DB();
					dbHelper = new DBHelper(BaseApplication.application);
					db = dbHelper.getWritableDatabase();
				}
			}
		}
		return dao;
	}

	/**
	 * 根据字段名字获得数据
	 *
	 * @param cursor
	 * @param indexName
	 * @return
	 */
	// --------------------------
	// ---------------获取数据处理
	private String getString(Cursor cursor, String indexName) {
		return cursor.getString(cursor.getColumnIndex(indexName));
	}

	private int getInt(Cursor cursor, String indexName) {
		return cursor.getInt(cursor.getColumnIndex(indexName));
	}

	private double getDouble(Cursor cursor, String indexName) {
		return cursor.getDouble(cursor.getColumnIndex(indexName));
	}

	private long getLong(Cursor cursor, String indexName) {
		return cursor.getLong(cursor.getColumnIndex(indexName));
	}

	private boolean getBoolean(Cursor cursor, String indexName) {
		return cursor.getInt(cursor.getColumnIndex(indexName)) == 1 ? true
				: false;
	}

	// ------------------------------
	// -------------------json处理
	private String getJsonString(JSONObject json, String element)
			throws JSONException {
		return json.getString(element).trim();
	}

	private int getJsonInt(JSONObject json, String element)
			throws JSONException {
		return json.getInt(element);
	}

	private double getJsonDouble(JSONObject json, String element)
			throws JSONException {
		return json.getDouble(element);
	}

	private long getJsonLong(JSONObject json, String element)
			throws JSONException {
		return json.getLong(element);
	}

	private boolean getJsonBoolean(JSONObject json, String element)
			throws JSONException {
		return json.getBoolean(element);
	}
//------------------------------------------------------------------------------------------
	// 添加新数据
	/**
	 * 添加菜品
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addDish(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			// 解析数据并添加到数据库
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into dish(id,classcode,code,name,name_en,help,unit,price,price_modify,weigh,discount,temp,suit,require_cook,description,locked,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { getJsonInt(json, "Id"),
							getJsonString(json, "ClassCode"),
							getJsonString(json, "Code"),
							getJsonString(json, "Name"),
							getJsonString(json, "EName"),
							getJsonString(json, "HelpCode"),
							getJsonString(json, "Unit"),
							json.getDouble("Price"),
							getJsonBoolean(json, "PriceModify"),
							getJsonBoolean(json, "NeedWeigh"),
							getJsonBoolean(json, "AllowDiscount"),
							getJsonBoolean(json, "IsTmp"),
							getJsonBoolean(json, "IsSuit"),
							getJsonBoolean(json, "RequireCook"),
							getJsonString(json, "Description"),
							getJsonBoolean(json, "Locked"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 添加分类
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addCate(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into menu(id,parent_code,code,name,help,name_en,level,sitecode,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { getJsonInt(json, "Id"),
							getJsonString(json, "ParentCode"),
							getJsonString(json, "Code"),
							getJsonString(json, "Name"),
							getJsonString(json, "HelpCode"),
							getJsonString(json, "EName"),
							getJsonString(json, "Level"),
							getJsonString(json, "SiteCode"),
							getJsonString(json, "Description"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 添加用户组
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addUser(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into user(code,pwd,locked,uniqueid,timestamp,del) values(?,?,?,?,?,?)",
					new Object[] { getJsonString(json, "Code"),
							getJsonString(json, "Pwd"),
							getJsonBoolean(json, "Locked"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 餐台区域
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addArea(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL("insert into area(id,code,name,help,site,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?)",new Object[]{getJsonInt(json, "Id"),getJsonString(json, "Code"),getJsonString(json, "Name"),getJsonString(json, "HelpCode"),getJsonString(json, "Site"),getJsonInt(json, "Sort"),getJsonString(json, "Description"),getJsonString(json, "UniqueId"),json.getLong("timestamp"),json.getBoolean("DelStatus")});
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 桌台
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addBoard(ArrayList<TableBean> tbs) throws JSONException {
		int len = tbs != null ? tbs.size() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			TableBean tb = tbs.get(i);
			db.execSQL("insert into board(code,name) values(?,?);", new Object[]{tb.getCode(),tb.getName()});

		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 营业点
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addSite(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into site(id,code,name,help,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?);",
					new Object[] { getJsonInt(json, "Id"),
							getJsonString(json, "Code"),
							getJsonString(json, "Name"),
							getJsonString(json, "HelpCode"),
							getJsonInt(json, "Sort"),
							getJsonString(json, "Description"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 做法类别
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addCookCls(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into cook_class(id,code,name,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?);",
					new Object[] { getJsonInt(json, "Id"),
							getJsonString(json, "Code"),
							getJsonString(json, "Name"),
							getJsonInt(json, "Sort"),
							getJsonString(json, "Description"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 做法
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addCook(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into cook(id,cookclass,code,name,help,public,price,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?,?,?);",
					new Object[] { getJsonInt(json, "Id"),
							getJsonString(json, "CookCls"),
							getJsonString(json, "Code"),
							getJsonString(json, "Name"),
							getJsonString(json, "HelpCode"),
							getJsonBoolean(json, "Public"),
							getJsonDouble(json, "Price"),
							getJsonInt(json, "Sort"),
							getJsonString(json, "Description"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 做法关系
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addMenuCook(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into menu_cook(id,menucode,cookcode,require,price,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?)",
					new Object[] { getJsonInt(json, "Id"),
							getJsonString(json, "MenuCode"),
							getJsonString(json, "CookCode"),
							getJsonBoolean(json, "Require"),
							getJsonDouble(json, "Price"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 图片组
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addImages(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into images(id,code,path,sort,uniqueid,timestamp,del) values(?,?,?,?,?,?,?);",
					new Object[] {
							getJsonInt(json, "Id"),
							getJsonString(json, "MenuCode"),
							getJsonString(json, "ImagePath").replace("\\", "/"),
							getJsonInt(json, "Sort"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 套餐
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addDetail(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into detail(id,suitcode,code,number,price,require,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?);",
					new Object[] { getJsonInt(json, "Id"),
							getJsonString(json, "SuitMenuCode"),
							getJsonString(json, "MenuCode"),
							getJsonInt(json, "Number"),
							getJsonDouble(json, "Price"),
							getJsonBoolean(json, "Require"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}




	/**
	 * 口味
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addRemark(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
			db.execSQL(
					"insert into remark(id,code,name,help,sort,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?);",
					new Object[] { getJsonInt(json, "Id"),
							getJsonString(json, "Code"),
							getJsonString(json, "Name"),
							getJsonString(json, "HelpCode"),
							getJsonInt(json, "Sort"),
							getJsonString(json, "UniqueId"),
							json.getLong("timestamp"),
							json.getBoolean("DelStatus") });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	/**
	 * 口味
	 *
	 * @param array
	 * @throws JSONException
	 */
	public void addFg(JSONArray array) throws JSONException {
		int len = array != null ? array.length() : 0;
		db.beginTransaction();
		for (int i = 0; i < len; i++) {
			JSONObject json = array.getJSONObject(i);
//				String image = getJsonString(json, "ImageUrl");
			db.execSQL(
					"insert into fg(menucode,coordinate,page,image) values(?,?,?,?);",
					new Object[] {
							getJsonString(json, "MenuCode"),
							getJsonString(json, "Coordinate"),
							getJsonInt(json, "Page"),
							getJsonString(json, "ImageUrl").replace("\\", "/")
//								image.substring(image.lastIndexOf("\\")+1)
					});
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}




	/**
	 * 获取桌台区域菜单
	 *
	 * @return
	 */
	public ArrayList<TablesBean> getTablesAreas(
			ArrayList<TablesBean> tablesAreas,String site) {
		// select code,name from area order by sort
		String sql = "select code,name from area where del=0 and site=? order by sort ";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, new String[] {site});
			while (cursor.moveToNext()) {
				//
				if (!cursor.isNull(0)) {
					// 基本菜品
					TablesBean bean = new TablesBean();
					bean.setCode(getString(cursor, "code"));
					bean.setName(getString(cursor, "name"));
					tablesAreas.add(bean);
				}
			}
			cursor.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return tablesAreas;
	}
	/**
	 * 获取营业点
	 *
	 * @return
	 */
	public ArrayList<TablesBean> getTablesSites(
			ArrayList<TablesBean> tablesAreas) {
		// select code,name from area order by sort
		String sql = "select code,name from site where del=0 order by sort ";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, new String[] {});
			while (cursor.moveToNext()) {
				//
				if (!cursor.isNull(0)) {
					// 基本菜品
					TablesBean bean = new TablesBean();
					bean.setCode(getString(cursor, "code"));
					bean.setName(getString(cursor, "name"));
					tablesAreas.add(bean);
				}
			}
			cursor.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return tablesAreas;
	}
	/**
	 * 根据区域获得桌台号
	 *
	 * @return
	 */


	public ArrayList<TableBean> getTableCodeBeans(){
		ArrayList<TableBean> tableBeans = new ArrayList<TableBean>();
		String sql = "select code,name from board";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				TableBean bean = new TableBean();
				bean.setCode(getString(cursor, "code"));
				bean.setName(getString(cursor, "name"));
				tableBeans.add(bean);
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return tableBeans;

	}
	public void clearAll() {
		db.beginTransaction();
		db.execSQL("delete from dish");
		db.execSQL("delete from images");
		db.execSQL("delete from remark");
		db.execSQL("delete from site");
		db.execSQL("delete from area");
		db.execSQL("delete from board");
		db.execSQL("delete from menu");
		db.execSQL("delete from cook");
		db.execSQL("delete from cook_class");
		db.execSQL("delete from menu_cook");
		db.execSQL("delete from user");
		db.execSQL("delete from detail");
		db.execSQL("delete from dish_table");
		db.execSQL("delete from fg");
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	//获得菜单页数
	public ArrayList<Pbean> getFlipPages(){
		//select count(*) from fg;
		String sql = "select il.page,CASE WHEN pl.coords is null THEN 0 ELSE COUNT(*) END count  from imagelist as il left join pricelabel as pl   on il.page=pl.page group by il.page";
		ArrayList<Pbean>  beans = new ArrayList<Pbean>();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(
					sql,
					null);
			while (cursor.moveToNext()) {
				Pbean bean = new Pbean();
				bean.setPage(getInt(cursor, "Page"));
				bean.setCount(getInt(cursor, "count"));
				beans.add(bean);
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return beans;
	}




	public ArrayList<FlipBean> getDishsToFlip() {
		ArrayList<FlipBean> flipBeans = new ArrayList<FlipBean>();
		ArrayList<FouceBean> foodsBeans = new ArrayList<FouceBean>();
		ArrayList<Pbean>  beans  = getFlipPages();
		//最大页数
		int max = beans.size();
		int INDEX = 0;//起始页
		String sql = "select il.page,il.imgname,pl.coords,pl.shortcode,f.itcode,f.price,f.des,f.unit,f.ISTC,f.fujiamode,f.istemp,f.state,f.class,f.IsFree from imagelist as il left join pricelabel as pl   on il.page=pl.page  left join food as f on pl.productcd=f.itcode order by il.page";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(
					sql,
					null);
			FlipBean fb = null;
			while (cursor.moveToNext()) {
				int page = getInt(cursor, "Page");
				String path = getString(cursor, "ImgName");
				FouceBean bean = new FouceBean();
				bean.setClasscode(getString(cursor, "CLASS"));
				bean.setCode(getString(cursor, "ITCODE"));
				bean.setHelp(getString(cursor, "ShortCode"));
				bean.setName(getString(cursor, "DES"));
				bean.setPrice(getDouble(cursor, "PRICE"));
				bean.setPrice_modify(getBoolean(cursor, "STATE"));
				bean.setSuit(getBoolean(cursor, "ISTC"));
				bean.setUnit(getString(cursor, "UNIT"));
				bean.setFree(getBoolean(cursor, "IsFree"));
				bean.setPage(page);
				//--
				//先加入
				if(fb==null){
					fb = new FlipBean();
				}
				String set = getString(cursor, "Coords");
				if(set!=null){
					String vars[] =  set.split(",");
					if(vars.length==4){
						//只接受正常数值
						int co[] = new int[vars.length];
						for (int j = 0; j < vars.length; j++) {
							co[j] = Integer.parseInt(vars[j]);
						}
						bean.setPoints(co);
						bean.setKey(set.replace(",", ""));
						foodsBeans.add(bean);
					}
				}
				if(beans.get(INDEX).getCount()==foodsBeans.size()){
					//如果数量已满，就清空
					INDEX++;
					fb.setPath(path);
					fb.setFouceBeans(foodsBeans);
					flipBeans.add(fb);
					foodsBeans = new ArrayList<FouceBean>();
					fb = null;
				}
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
			P.c("出现问题");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor= null;
			}
		}
		P.c("获取的长度"+flipBeans.size());
		return flipBeans;
	}
	/**
	 * 添加菜品到购物篮【没有附加条件的添加】 cook_codes cook_names cook_prices
	 *
	 * @param bean
	 */
	public boolean addDishToPad(FouceBean bean, ArrayList<ReasonBean> resons,
								ArrayList<ReasonBean> marks) {
		StringBuilder builder_tag = new StringBuilder();
		StringBuilder builder_code = new StringBuilder();
		StringBuilder builder_name = new StringBuilder();
		StringBuilder builder_price = new StringBuilder();
		if (resons != null) {
			// 对按code进行排序
			Collections.sort(resons, new Comparator<ReasonBean>() {
				@Override
				public int compare(ReasonBean rb0, ReasonBean rb1) {
					String code0 = rb0.getCode();
					String code1 = rb1.getCode();
					// 可以按reasoncode对象的其他属性排序，只要属性支持compareTo方法
					return code0.compareTo(code1);
				}
			});
			int len = resons.size();
			for (int i = 0; i < resons.size(); i++) {
				ReasonBean rb = resons.get(i);
				if (i == len - 1) {
					builder_tag.append(rb.getTags());
					builder_code.append(rb.getCode());
					builder_name.append(rb.getName());
					builder_price.append(rb.getPrice());

				} else {
					builder_tag.append(rb.getTags()+",");
					builder_code.append(rb.getCode() + ",");
					builder_name.append(rb.getName() + ",");
					builder_price.append(rb.getPrice() + ",");
				}
			}
		}
		//口味
		StringBuilder mark_tag = new StringBuilder();
		StringBuilder mark_code = new StringBuilder();
		StringBuilder mark_name = new StringBuilder();
		StringBuilder mark_price = new StringBuilder();
		if (marks != null) {
			// 对按code进行排序
			Collections.sort(marks, new Comparator<ReasonBean>() {
				@Override
				public int compare(ReasonBean rb0, ReasonBean rb1) {
					String code0 = rb0.getCode();
					String code1 = rb1.getCode();
					// 可以按reasoncode对象的其他属性排序，只要属性支持compareTo方法
					return code0.compareTo(code1);
				}
			});
			int len = marks.size();
			for (int i = 0; i < marks.size(); i++) {
				ReasonBean rb = marks.get(i);
				if (i == len - 1) {
					mark_tag.append(rb.getTags());
					mark_code.append(rb.getCode());
					mark_name.append(rb.getName());
					mark_price.append(rb.getPrice());

				} else {
					mark_tag.append(rb.getTags()+",");
					mark_code.append(rb.getCode() + ",");
					mark_name.append(rb.getName() + ",");
					mark_price.append(rb.getPrice() + ",");
				}
			}
		}



		int []result =  Un_Save(bean.getCode(), String.valueOf(bean.getPrice()),builder_code.toString(), mark_code.toString(),bean.isFree());
		if(result==null){
			P.c("加==");
			try {
				db.execSQL("insert into dish_table(code,name,unit,price,more,price_modify,temp,suit,count,tags,cook_codes,cook_names,cook_prices,mark_tag,mark_codes,mark_names,mark_prices,flag,free) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{bean.getCode(),bean.getName(),bean.getUnit(),bean.getPrice(),bean.isMore(),bean.isPrice_modify(),bean.isTemp(),bean.isSuit(),1,builder_tag.toString(),builder_code.toString(),builder_name.toString(),builder_price.toString(),mark_tag.toString(),mark_code.toString(),mark_name.toString(),mark_price.toString(),System.currentTimeMillis(),bean.isFree()});
			} catch (Exception e) {
				// TODO: handle exception
				P.c(e.getMessage());
				return false;
			}
			resetTime();
			return true;
		}else {
			//
			P.c("不是空");
			//存在这样的那么就数量加1  并且是授权菜品
			if(result.length==2){
				//正常数据
				changeNum(result[1]+1, result[0]);
				return true;
			}
		}
		return false;
	}


	/**
	 * 加套餐
	 * @param bean
	 * @param resons
	 * @param remark
	 * @return
	 */
	public void addSuitToPad(FouceBean bean,ArrayList<SuitBean> suitBeans) {
		db.beginTransaction();
		int index  = -1;
		Cursor cursor = null;
		if(Un_SuitSave(bean.getCode(), String.valueOf(bean.getPrice()))){
			try {
				db.execSQL("insert into dish_table(code,name,unit,price,more,price_modify,temp,suit,count,tags,cook_codes,cook_names,cook_prices,mark_tag,mark_codes,mark_names,mark_prices,flag) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{bean.getCode(),bean.getName(),bean.getUnit(),bean.getPrice(),bean.isMore(),bean.isPrice_modify(),bean.isTemp(),bean.isSuit(),1,"","","","","","","","",System.currentTimeMillis()});
				cursor = db.rawQuery("select last_insert_rowid();", null);
				if (cursor.moveToFirst()) {
					index = cursor.getInt(0);
				}
				cursor.close();
			} catch (Exception e) {
				// TODO: handle exception
				P.c(e.getMessage());
			}finally{
				if(cursor!=null){
					cursor.close();
				}
			}
			resetTime();
		}
		if(index!=-1){
			//进行套餐数据加载
			for(int i=0;i<suitBeans.size();i++){
				SuitBean bn = suitBeans.get(i);
				db.execSQL("insert into dish_table(code,name,unit,price,tags,count,cook_codes,cook_names,cook_prices,mark_tag,mark_codes,mark_names,mark_prices,detail_code,detail_i,flag) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{bn.getCode(),bn.getName(),bn.getUnit(),bn.getPrice(),1,"","","","","","","","",bean.getCode(),index,System.currentTimeMillis()});
			}
			resetTime();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 做法菜单
	 *
	 * @param id
	 * @return
	 */
	public ArrayList<ResonMenuBean> getResonMenuBeans(	ArrayList<ResonMenuBean> resonMenuBeans,String code){
		String is = "select PCODE,FCODE,FNAME,FPRICE,PRODUCTTC_ORDER,MINCNT,MAXCNT,ISADDPROD,ISMUST FROM FOODFUJIA WHERE PCODE=? AND DEFUALTS=0";
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(is, new String[]{code});
			while(cursor.moveToNext()){
				//
				ResonMenuBean bean = new ResonMenuBean();
				bean.setId(getString(cursor, "PCODE"));
				bean.setCode(getString(cursor, "FCODE"));
				bean.setName(getString(cursor, "FNAME"));
				bean.setTag(getString(cursor, "PRODUCTTC_ORDER"));
				bean.setMin(getInt(cursor, "MINCNT"));
				bean.setMax(getInt(cursor, "MAXCNT"));
				resonMenuBeans.add(bean);
			}
			cursor.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return resonMenuBeans;
	}
	/**
	 * 备注
	 * @param resonMenuBeans
	 * @param code
	 * @return
	 */
	public ArrayList<ResonMenuBean> getMarkMenuBeans(){
		ArrayList<ResonMenuBean> resonMenuBeans = new ArrayList<ResonMenuBean>();
		String is = "select name,code,pk_redefine_type FROM REDEFINE_TYPE ORDER BY SORTNO";
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(is, null);
			while(cursor.moveToNext()){
				ResonMenuBean bean = new ResonMenuBean();
//				P.c(cursor.getColumnIndex("code")+"--"+cursor.getColumnIndex("NAME")+"---"+cursor.getColumnIndex("pk_redefine_type"));
				bean.setCode(cursor.getString(1));
				bean.setName(cursor.getString(0));
				bean.setTag(cursor.getString(2));
				resonMenuBeans.add(bean);
			}
			cursor.close();
		} catch (Exception e) {
			P.c(e.getLocalizedMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return resonMenuBeans;
	}

	/**
	 * 套餐菜单
	 */
	public ArrayList<SuitMenuBean> getSuitMenuBeans(ArrayList<SuitMenuBean> resonMenuBeans,String code){
		String is = "select PCODE,PCODE1,PNAME,UNIT,CNT,PRICE1,PRODUCTTC_ORDER,MINCNT,MAXCNT,GROUPTITLE FROM PRODUCTS_SUB WHERE PCODE=? AND DEFUALTS=0";
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(is, new String[]{code});
			while(cursor.moveToNext()){
				//
				SuitMenuBean bean = new SuitMenuBean();
				bean.setId(getString(cursor, "PCODE"));
				bean.setCode(getString(cursor, "PCODE1"));
				bean.setName(getString(cursor, "GROUPTITLE"));
				bean.setTag(getString(cursor, "PRODUCTTC_ORDER"));
				bean.setMin(getInt(cursor, "MINCNT"));
				bean.setMax(getInt(cursor, "MAXCNT"));
				resonMenuBeans.add(bean);
			}
			cursor.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return resonMenuBeans;
	}
	/**
	 * 是否不存在这样id
	 *
	 * @param id
	 * @return
	 */

	public int[]  Un_Save(String code,String price,String cook_codes,String mark_codes,boolean free) {
		String s_ = "select i,count from dish_table where code=? and price=? and cook_codes=? and mark_codes=? and detail_code isnull";
		int result[] = null;
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(s_, new String[] {code,price,cook_codes,mark_codes});
			int count = cursor.getCount();

			if(count!=0){
				if(free){
					result = new int[2];
					if(cursor.moveToFirst()){
						result[0] = getInt(cursor, "i");
						result[1]  =getInt(cursor, "count");
						P.c(result[0]+"已点"+result[1]);
					}
				}else{
					throw new Exception();
				}

			}
			cursor.close();

		}catch(Exception e){
			//-2就是抛弃这个值
			return new int[3];
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		// db.close();
		return result;
	}
	public boolean Un_SuitSave(String code,String price ) {
		String s_ = "select * from dish_table where code=? and price=?   and detail_code==null and suit==1";
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(s_, new String[] {code,price});
			int count = cursor.getCount();
			P.c(code+"已点数量"+count);
			cursor.close();
			if (count != 0) {
				return false;
			}
		}catch(Exception e){
			return false;
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		// db.close();
		return true;
	}

	/**
	 * 校验账户密码
	 */
	public boolean isVal(String code, String pwd) {
		// select pwd from user where code='Admin1'
		boolean flag = false;
		String sql = "select pwd from user where code=?";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, new String[] { code });
			int count = cursor.getCount();
			if (count != 0) {
				if (cursor.moveToNext()) {
					if (getString(cursor, "pwd").equals(pwd)) {
						// 账户存在，密码一样
						flag = true;
					} else {
						flag = false;
					}

				}
			}

		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor = null;
			}
		}
		return flag;

	}
	/**
	 * 获得菜篮子
	 *
	 * @return
	 */
	public ArrayList<DishTableBean> getTableBeans() {
		ArrayList<DishTableBean> dishTableBeans = new ArrayList<DishTableBean>();
		Cursor cursor = null;
		String sql = "select i,code,name,unit,price,more,price_modify,temp,suit,count,cook_codes,cook_names,cook_prices,mark_codes,mark_names,mark_prices,flag,free from dish_table where detail_code is null or suit==1";
		try {
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				DishTableBean tableBean = new DishTableBean();
				tableBean.setI(getInt(cursor, "i"));
				tableBean.setCode(getString(cursor, "code"));
				tableBean.setName(getString(cursor, "name"));
				tableBean.setUnit(getString(cursor, "unit"));
				tableBean.setPrice(getDouble(cursor, "price"));
				tableBean.setMore(getBoolean(cursor, "more"));
				tableBean.setPrice_modify(getBoolean(cursor, "price_modify"));
				tableBean.setTemp(getBoolean(cursor, "temp"));
				tableBean.setSuit(getBoolean(cursor, "suit"));
				tableBean.setCount(getInt(cursor, "count"));
				String cook_codes = getString(cursor, "cook_codes");
				tableBean.setCook_codes(cook_codes);
				tableBean.setCook_names(getString(cursor, "cook_names"));
				tableBean.setCook_prices(getString(cursor, "cook_prices"));
				String mark_codes = getString(cursor, "mark_codes");
				tableBean.setMark_codes(mark_codes);
				tableBean.setMark_names(getString(cursor, "mark_names"));
				tableBean.setMark_prices(getString(cursor, "mark_prices"));
				tableBean.setFlag(getString(cursor, "flag"));
				tableBean.setFree(getBoolean(cursor, "free"));
				// 默认给菜篮子0数量的口味
				ArrayList<ReasonBean> reasons = new ArrayList<ReasonBean>();
				if (cook_codes.length() != 0) {
					// 证明有做法选择
					// P.c(cursor.getString(6));
					if (cook_codes.contains(",")) {
						// 价格不会出现“，”
						String[] codes = cook_codes.split(",");
						String[] names = getString(cursor, "cook_names").split(
								",");
						String[] prices = getString(cursor, "cook_prices")
								.split(",");
						for (int i = 0; i < codes.length; i++) {
							//
							ReasonBean ba = new ReasonBean();
							ba.setCode(codes[i]);
							ba.setName(names[i]);
							ba.setPrice(Double.parseDouble(prices[i]));
							reasons.add(ba);
						}
					} else {
						// 不包含,那么就只有一个
						ReasonBean ba = new ReasonBean();
						ba.setCode(cook_codes);
						ba.setName(getString(cursor, "cook_names"));
						ba.setPrice(Double.parseDouble(getString(cursor,
								"cook_prices")));
						reasons.add(ba);
					}
				}
				tableBean.setReasonBeans(reasons);
				//口味
				ArrayList<ReasonBean> marks = new ArrayList<ReasonBean>();
				if (mark_codes.length() != 0) {
					// 证明有做法选择
					// P.c(cursor.getString(6));
					if (mark_codes.contains(",")) {
						// 价格不会出现“，”
						String[] codes = mark_codes.split(",");
						String[] names = getString(cursor, "mark_names").split(
								",");
						String[] prices = getString(cursor, "mark_prices")
								.split(",");
						for (int i = 0; i < codes.length; i++) {
							//
							ReasonBean ba = new ReasonBean();
							ba.setCode(codes[i]);
							ba.setName(names[i]);
							ba.setPrice(Double.parseDouble(prices[i]));
							marks.add(ba);
						}
					} else {
						// 不包含,那么就只有一个
						ReasonBean ba = new ReasonBean();
						ba.setCode(cook_codes);
						ba.setName(getString(cursor, "mark_names"));
						ba.setPrice(Double.parseDouble(getString(cursor,
								"mark_prices")));
						marks.add(ba);
					}
				}
				tableBean.setMarkBeans(marks);

				// -------临时
				dishTableBeans.add(tableBean);
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
			P.c("异常问题" + e.getLocalizedMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return dishTableBeans;
	}
	/**
	 * 修改菜篮子数量
	 *
	 * @param num
	 * @param id
	 */
	public void changeNum(int count, int i) {
		String sql = "update dish_table set count = ? where i = ?";
		db.execSQL(sql, new Object[] { count, i });
		resetTime();
	}
	public void delete(int i) {
		String s_ = "delete from dish_table where i=?";
		db.execSQL(s_, new Object[] { i });
		resetTime();
	}


	public void clear(String tableName) {
		String sql = "delete from "+tableName;
		db.execSQL(sql);
	}
	public void clear() {
		String sql = "delete from dish_table";
		db.execSQL(sql);
		resetTime();

	}
	private void resetTime(){
		SharedUtils utils = new SharedUtils(Common.CONFIG);
		utils.clear("order_time");
	}
	/**
	 * 获得多规格code
	 * @param id
	 * @return
	 */
	public Map<String, Map<String, ReasonBean>> getSelectedRessons(int index) {
		String sql = "select tags,cook_codes,cook_names,cook_prices from dish_table where i=?";
		Cursor cursor = null;
		Map<String, Map<String, ReasonBean>> resMap = new HashMap<String, Map<String,ReasonBean>>();
		try {
			cursor = db.rawQuery(sql, new String[] { String.valueOf(index) });
			while (cursor.moveToNext()) {
				if (cursor.isNull(0)) {

				} else {
					// 不等于空就进行解析处理
					String[] tags = getString(cursor, "tags").split(",");
					String[] codes = getString(cursor, "cook_codes").split(",");
					String[] names = getString(cursor, "cook_names").split(",");
					String[] prices = getString(cursor, "cook_prices").split(",");

					for (int i = 0; i < codes.length; i++) {
						ReasonBean bean = new ReasonBean();
						bean.setName(names[i]);
						bean.setCode(codes[i]);
						bean.setPrice(Double.parseDouble(prices[i]));
						bean.setTags(tags[i]);

						if(!resMap.containsKey(tags[i])){
							Map<String, ReasonBean> rbs = new HashMap<String, ReasonBean>();
							rbs.put(bean.getCode(), bean);
							resMap.put(tags[i], rbs);
						}else{
							resMap.get(tags[i]).put(bean.getCode(), bean);
						}
					}
				}
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return resMap;
	}
	public Map<String, Map<String, ReasonBean>> getSelectedMarks(int index) {
		String sql = "select mark_tag,mark_codes,mark_names,mark_prices from dish_table where i=?";
		Cursor cursor = null;
		Map<String, Map<String, ReasonBean>> resMap = new HashMap<String, Map<String,ReasonBean>>();
		try {
			cursor = db.rawQuery(sql, new String[] { String.valueOf(index) });
			while (cursor.moveToNext()) {
				if (cursor.isNull(0)) {

				} else {
					// 不等于空就进行解析处理
					String[] tags = getString(cursor, "mark_tag").split(",");
					String[] codes = getString(cursor, "mark_codes").split(",");
					String[] names = getString(cursor, "mark_names").split(",");
					String[] prices = getString(cursor, "mark_prices").split(",");

					for (int i = 0; i < codes.length; i++) {
						ReasonBean bean = new ReasonBean();
						bean.setName(names[i]);
						bean.setCode(codes[i]);
						bean.setPrice(Double.parseDouble(prices[i]));
						bean.setTags(tags[i]);

						if(!resMap.containsKey(tags[i])){
							Map<String, ReasonBean> rbs = new HashMap<String, ReasonBean>();
							rbs.put(bean.getCode(), bean);
							resMap.put(tags[i], rbs);
						}else{
							resMap.get(tags[i]).put(bean.getCode(), bean);
						}
					}
				}
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return resMap;
	}
	/**
	 * 获得此分类的和该菜品 相关的所有可选做法
	 * @return
	 * pbc 1是公共   0是私有
	 */
	public ArrayList<ReasonBean> getCpBeans(String tag, String code) {
		ArrayList<ReasonBean> rs = new ArrayList<ReasonBean>();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select PCODE,FCODE,FNAME,FPRICE,PRODUCTTC_ORDER,MINCNT,MAXCNT,ISADDPROD,ISMUST FROM FOODFUJIA WHERE PCODE=? AND PRODUCTTC_ORDER=? AND DEFUALTS!=0", new String[]{code,tag});
			while(cursor.moveToNext()){
				//增加菜品做法
				ReasonBean bean = new ReasonBean();
				bean.setCode(getString(cursor, "FCODE"));
				bean.setName(getString(cursor, "FNAME"));
				bean.setPrice(getDouble(cursor, "FPRICE"));
				rs.add(bean);
			}
			cursor.close();
		} catch (Exception e) {
			P.c("测试");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return rs;
	}
	/**
	 * 备注详情
	 * @param tag
	 * @param code
	 * @return
	 */
	public ArrayList<ReasonBean> getMarkBeans(String tag) {
		ArrayList<ReasonBean> rs = new ArrayList<ReasonBean>();
		Cursor cursor = null;
		P.c("备注详情");
		try {
			cursor = db.rawQuery("select FCODE,FNAME,FPRICE,MINCNT,MAXCNT,ISADDPROD,ISMUST FROM FOODFUJIA WHERE rgrp=?", new String[]{tag});
			while(cursor.moveToNext()){
				//增加菜品做法
				ReasonBean bean = new ReasonBean();
				bean.setCode(getString(cursor, "FCODE"));
				bean.setName(getString(cursor, "FNAME"));
				bean.setPrice(getDouble(cursor, "FPRICE"));
				rs.add(bean);
			}
			cursor.close();
		} catch (Exception e) {
			P.c("测试");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return rs;
	}
	/**
	 * 获得退增
	 * @param mbs
	 * @param status
	 */
	public void getTzBeans(ArrayList<TzBean> mbs,int status) {
		Cursor cursor = null;
		try {
			String sql = null;
			if(status == 1){
				sql = "select vcode,vname from presentreason;";
			}else if(status ==0){
				sql = "select id,des from errorcustom;";
			}
			cursor = db.rawQuery(sql,null);
			while(cursor.moveToNext()){
				//增加菜品做法
				TzBean bean = new TzBean();
				bean.setCode(cursor.getString(0));
				bean.setName(cursor.getString(1));
				mbs.add(bean);
			}
			cursor.close();
		} catch (Exception e) {
			P.c("测试");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 获得套餐
	 * @param tag
	 * @param code
	 * @return
	 */
	public ArrayList<SuitBean> getSuitBeans(String tag, String code) {
		ArrayList<SuitBean> rs = new ArrayList<SuitBean>();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("select PCODE,PCODE1,PNAME,UNIT,CNT,PRICE1,PRODUCTTC_ORDER,MINCNT,MAXCNT FROM PRODUCTS_SUB WHERE PCODE=? AND PRODUCTTC_ORDER=? AND DEFUALTS!=0", new String[]{code,tag});
			while(cursor.moveToNext()){
				//增加菜品做法
				SuitBean bean = new SuitBean();
				bean.setCode(getString(cursor, "PCODE1"));
				bean.setName(getString(cursor, "PNAME"));
				bean.setPrice(getDouble(cursor, "PRICE1"));
				bean.setUnit(getString(cursor, "UNIT"));
				rs.add(bean);
			}
			cursor.close();
		} catch (Exception e) {
			P.c("测试");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return rs;
	}


	public void updateDishInit(DishTableBean tb) {
		String sql = "update dish_table set cook_codes='',cook_names='',cook_prices='' where i=?";
		db.execSQL(sql, new Object[] { tb.getI() });
		resetTime();
	}
	public void updateMarkInit(DishTableBean tb) {
		String sql = "update dish_table set mark_codes='',mark_names='',mark_prices='' where i=?";
		db.execSQL(sql, new Object[] { tb.getI() });
		resetTime();
	}
	/**
	 * 更新数据的规格参数
	 *
	 * @param bean
	 * @param id
	 */
	public void updateDishTable(ArrayList<ReasonBean> bean, DishTableBean tb) {
		String sql = "update dish_table set tags=? ,cook_codes=?,cook_names=?,cook_prices=? where i = ?";
		StringBuilder builder = new StringBuilder();
		StringBuilder builder1 = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		StringBuilder builder3 = new StringBuilder();

		int size = bean.size();
		for (int i = 0; i < size; i++) {
			if (i != size - 1) {
				builder.append(bean.get(i).getTags()+",");
				builder1.append(bean.get(i).getCode() + ",");
				builder2.append(bean.get(i).getName() + ",");
				builder3.append(bean.get(i).getPrice() + ",");
			} else {
				builder.append(bean.get(i).getTags());
				builder1.append(bean.get(i).getCode());
				builder2.append(bean.get(i).getName());
				builder3.append(bean.get(i).getPrice());
			}
		}
		db.execSQL(sql, new Object[] {builder.toString(), builder1.toString(),
				builder2.toString(), builder3.toString(), tb.getI() });
		resetTime();
	}
	public void updateMarkTable(ArrayList<ReasonBean> bean, DishTableBean tb) {
		String sql = "update dish_table set mark_tag = ?, mark_codes=?,mark_names=?,mark_prices=? where i = ?";
		StringBuilder builder = new StringBuilder();
		StringBuilder builder1 = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		StringBuilder builder3 = new StringBuilder();
		int size = bean.size();
		for (int i = 0; i < size; i++) {
			if (i != size - 1) {
				builder.append(bean.get(i).getTags() + ",");
				builder1.append(bean.get(i).getCode() + ",");
				builder2.append(bean.get(i).getName() + ",");
				builder3.append(bean.get(i).getPrice() + ",");
			} else {
				builder.append(bean.get(i).getTags());
				builder1.append(bean.get(i).getCode());
				builder2.append(bean.get(i).getName());
				builder3.append(bean.get(i).getPrice());
			}
		}
		db.execSQL(sql, new Object[] {builder.toString(), builder1.toString(),
				builder2.toString(), builder3.toString(), tb.getI() });
		resetTime();
	}

	/**
	 * 菜品数量
	 *
	 * @return
	 */
	public int getCount() {
		int i = 0;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from dish_table where detail_code is null or suit==1", null);
			i = cursor.getCount();
			cursor.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return i;
	}

	//是否有附加项
	public int isFujia(FouceBean bean) {
		int i = 0;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from foodfujia where pcode=?;", new String[]{bean.getCode()});
			i = cursor.getCount();
			while(cursor.moveToNext()){
				if(getString(cursor, "ISADDPROD").equals("1")){
					bean.setMore(true);
				}
				if(getString(cursor, "ISMUST").equals("1")){
					bean.setTemp(true);
				}
			}
			cursor.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return i;
	}


	//---------------------------------------------------------------------------------------------
	//分界线
	//---------------------------------------------------------------------------------------------
	public boolean Un_Save(String id,String reasoncode) {
		P.c("点选的"+reasoncode);
		String s_ = "select * from dish_table where id=? and reasoncode=?";
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(s_, new String[] { id ,reasoncode});
			int count = cursor.getCount();
			cursor.close();

			if (count != 0) {
				return false;
			}
		}catch(Exception e){

		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		// db.close();
		return true;
	}


	private String reason(ArrayList<ReasonBean> bns){
		StringBuilder builder = new StringBuilder();
		int len = bns!=null?bns.size():0;
		for(int i=0;i<len;i++){
			ReasonBean rb = bns.get(i);
			if(i!=len-1){
				builder.append(rb.getCode()+",");
			}else{
				builder.append(rb.getCode());
			}
		}
		P.c(builder.toString());
		return builder.toString();

	}



	public void updateDishInit(String id,String reasoncode) {
		String sql = "update dish_table set reasoncode='',reasonname='',reasonprice='' where id = ? and reasoncode = ?";
		db.execSQL(sql, new Object[] { id,reasoncode });
		resetTime();
	}




	/**
	 * 对菜品的操作
	 */
	public boolean Un_SaveDish(String id, String coords) {
		Cursor cursor = null;
		String s_ = "select * from dish where id=? and coords=?";
		try {
			cursor = db.rawQuery(s_, new String[] { id });
			int count = cursor.getCount();
			cursor.close();
			if (count != 0) {
				return false;
			}
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		// db.close();
		return true;
	}
	/**
	 * 是否存在当前无菜品页
	 * @param page
	 * @return
	 */
	public boolean Un_SaveDishNo(int page) {
		Cursor cursor = null;
		String s_ = "select * from dish where page=?";
		try {
			cursor = db.rawQuery(s_, new String[] { String.valueOf(page) });
			int count = cursor.getCount();
			cursor.close();
			if (count != 0) {
				return false;
			}
			// db.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return true;
	}

	/**
	 * 添加菜品
	 *
	 * @param dish
	 */
	public void insertDish(Dish dish) {
		String sql = "insert into dish values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		if (Un_SaveDish(dish.getId(), dish.getCoords())) {
			db.execSQL(sql,
					new Object[] { dish.getId(), dish.getName(),
							dish.getCode(), dish.getType(), dish.getUnit(),
							dish.getCoords(), dish.getPrice(), dish.getPage(),dish.getShortCode(),dish.getReasonCode(),dish.getReasonName(),dish.getReasonPrice(),dish.getUnitId()});
		}
	}
	/**
	 * 获取菜品数量
	 * @return
	 */
	public int getDishCount() {
		int count = 0;
		Cursor cursor = null;
		String sql = "select page from dish group by page";
		try {
			cursor = db.rawQuery(sql, null);
			count = cursor.getCount();
			cursor.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return count;
	}

	public String getTeeUnit(String code) {
		String  unit = "";
		Cursor cursor = null;
		String sql = "select unitId from dish where code=? group by code";
		try {
			cursor = db.rawQuery(sql, new String[]{code});
			while(cursor.moveToNext()){
				unit = cursor.getString(0);
			}
			cursor.close();
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return unit;
	}

	/**
	 * 保存空数据页码
	 *
	 * @param dish
	 */
	public void insertDishNo(Dish dish) {
		String sql = "insert into dish(page) values(?)";
		if (Un_SaveDishNo(dish.getPage())) {
			db.execSQL(sql, new Object[] { dish.getPage() });
		}
	}

	/**
	 * 清除菜品
	 */
	public void clearDish() {
		String sql = "delete from dish";
		db.execSQL(sql);
	}





	/**
	 * 获取目录
	 */
	public ArrayList<MenuBean> getMenus(){
		Cursor cursor = null;
		String sql = "SELECT   GRPTYP,MIN(PAGE) AS PAGE FROM FOOD WHERE COORDS IS NOT NULL GROUP BY GRPTYP";
		ArrayList<MenuBean> menus = new ArrayList<MenuBean>();
		try{
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				if(!cursor.isNull(0)){
					//存在这样的值,那么就进行菜品整理
					MenuBean bean = new MenuBean();
					bean.setMenuTitle(getString(cursor, "GRPTYP"));
					bean.setPage(getInt(cursor, "PAGE"));
					menus.add(bean);
				}
			}
		}catch(Exception e){

		}finally{
			if(cursor!=null){

				cursor.close();
			}
		}
		return menus;
	}
	/**
	 * 根据菜品获取页数
	 */
	public int getPage(String id){
		int page = 0;
		String sql = "select page from dish where id=?";
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(sql, new String[]{id});
			while(cursor.moveToNext()){
				page = cursor.getInt(0);
			}
			cursor.close();
		}catch(Exception e){

		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		return page;
	}
	/**
	 * 菜品检索
	 */
	public ArrayList<FouceBean> getSearchDish(String param){
		ArrayList<FouceBean> fouceBeans  = new ArrayList<FouceBean>();
		String sql = "select f.des, f.page,pl.shortcode from food as f join  pricelabel as pl on pl.coords = f.coords where f.coords is not null and pl.shortcode like ?";
		Cursor cursor = null;
		try{
			//解析数据
			cursor = db.rawQuery(sql, new String[]{param});
			while(cursor.moveToNext()){
				int page = getInt(cursor, "Page");
				FouceBean bean = new FouceBean();
//				bean.setClasscode(getString(cursor, "classcode"));
//				bean.setCode(getString(cursor, "code"));
//				bean.setHelp(getString(cursor, "help"));
				bean.setName(getString(cursor, "DES"));
//				bean.setPrice(getDouble(cursor, "price"));
//				bean.setPrice_modify(getBoolean(cursor, "price_modify"));
//				bean.setSuit(getBoolean(cursor, "suit"));
//				bean.setUnit(getString(cursor, "unit"));
				bean.setPage(page);
				fouceBeans.add(bean);
			}
			cursor.close();
		}catch(Exception e){
			P.c("模糊查询异常");
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		return fouceBeans;
	}


}
