package pad.com.haidiyun.www.db;


import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	/**
	 * 数据库操作
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	private String DATABASE_PATH ;
	@SuppressWarnings("unused")
	private Context context;
	public DBHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, Common.SOURCE_DB, null, Common.DB_VERSION);

	}
	public DBHelper(Context context, String name, CursorFactory factory,
					int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		this.context = context;
		DATABASE_PATH ="/data/data/"+context.getPackageName()+"/databases/";
		P.c("数据库版本"+version);
	}
	private void create (SQLiteDatabase db){
		db.beginTransaction();
		//菜品
		//db.execSQL("create table dish(i integer primary key autoincrement,id int,classcode varchar,code varchar,name varchar,name_en varchar,help varchar,unit varchar,price double,price_modify boolean,weigh boolean,discount boolean,temp boolean,suit boolean,require_cook boolean, description varchar,locked boolean,uniqueid varchar,timestamp long,del boolean)");
		//菜品图片
//	db.execSQL("create table images(i integer primary key autoincrement,id int,code varchar,path varcahr,sort int,uniqueid varchar,timestamp long,del boolean)");
		//口味
		//db.execSQL("create table remark(i integer primary key autoincrement,id int,code varchar,name varchar,help varchar,sort int,uniqueid varchar,timestamp long,del boolean)");
		//营业点
		//db.execSQL("create table site(i integer primary key autoincrement,id int,code varchar,name varchar,help varchar,sort int,description varchar,uniqueid varchar,timestamp long,del boolean)");
		//区域
		//db.execSQL("create table area(i integer primary key autoincrement,id int,code varchar,name varchar,help varchar,site varchar,sort int,description varchar,uniqueid varchar,timestamp long,del boolean)");
		//桌台
		db.execSQL("create table  board(i integer primary key autoincrement,code varchar,name varchar)");
		//分类
		//db.execSQL("create table menu (i integer primary key autoincrement,id int,parent_code varchar,code varchar,name varchar,help varchar,name_en varchar,level int,sitecode varchar, description varchar,uniqueid varchar,timestamp long,del boolean)");
		//口味
		//db.execSQL("create table cook(i integer primary key autoincrement,id int,cookclass varchar,code varchar,name varchar,help varchar,public boolean,price double,sort int,description varchar,uniqueid varchar,timestamp long,del boolean)");
		//口味类别
		//db.execSQL("create table cook_class(i integer primary key autoincrement,id int,code varchar,name varchar,sort int,description varchar,uniqueid varchar,timestamp long,del boolean)");
		//口味关系
		//db.execSQL("create table menu_cook(i integer primary key autoincrement,id int,menucode varchar,cookcode varchar,require boolean,price double,uniqueid varchar,timestamp long,del boolean)");
		//用户
		//db.execSQL("create table user(i integer primary key autoincrement,code varchar,pwd varchar,locked boolean,uniqueid varchar,timestamp long,del boolean)");
		//套餐
		//db.execSQL("create table detail(i integer primary key autoincrement,id int ,suitcode varchar,code varchar,number int,price double,require boolean,uniqueid varchar,timestamp long,del boolean)");
		//菜篮子
		db.execSQL("CREATE TABLE dish_table(i integer primary key autoincrement,code varchar,name varchar,unit varchar,price double,more boolean,price_modify boolean,temp boolean,suit boolean,count int,tags varchar,cook_codes varchar,cook_names varchar,cook_prices varchar,mark_tag varchar,mark_codes varchar,mark_names varchar,mark_prices varchar,detail_code varchar,detail_i,flag long,free boolean);");
		//坐标关系
		//db.execSQL("create table fg(i integer primary key autoincrement,menucode varchar,coordinate varchar,page int,image varchar);");
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	private void drop(SQLiteDatabase db){
		db.beginTransaction();
//		db.execSQL("DROP TABLE IF EXISTS dish");
//		db.execSQL("DROP TABLE IF EXISTS images");
//		db.execSQL("DROP TABLE IF EXISTS remark");
//		db.execSQL("DROP TABLE IF EXISTS site");
//		db.execSQL("DROP TABLE IF EXISTS area");
		db.execSQL("DROP TABLE IF EXISTS board");
//		db.execSQL("DROP TABLE IF EXISTS menu");
//		db.execSQL("DROP TABLE IF EXISTS cook");
//		db.execSQL("DROP TABLE IF EXISTS cook_class");
//		db.execSQL("DROP TABLE IF EXISTS menu_cook");
//		db.execSQL("DROP TABLE IF EXISTS user");
//		db.execSQL("DROP TABLE IF EXISTS detail");
		db.execSQL("DROP TABLE IF EXISTS dish_table");
//		db.execSQL("DROP TABLE IF EXISTS fg");
		db.setTransactionSuccessful();
		db.endTransaction();
		//此处是删除数据表，在实际的业务中一般是需要数据备份的
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		create(db);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		P.c("oldVersion"+oldVersion+"newVersion"+newVersion);
		drop(db);
		create(db);
	}


	public boolean checkDataBase() {
		SQLiteDatabase db = null;
		try {
			String databaseFilename = DATABASE_PATH + Common.SOURCE_DB;
			db = SQLiteDatabase.openDatabase(databaseFilename, null,SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {

		}
		if (db != null) {
			db.close();
		}
		return db != null ? true : false;
	}


}
