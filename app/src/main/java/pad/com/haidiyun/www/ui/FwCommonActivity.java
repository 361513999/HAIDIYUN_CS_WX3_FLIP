package pad.com.haidiyun.www.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.MoveAdapter;
import pad.com.haidiyun.www.bean.MoveItem;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.SetI;
import pad.com.haidiyun.www.service.FloatService;
import pad.com.haidiyun.www.widget.Common433SendPop;
import pad.com.haidiyun.www.widget.CommonLoadSendPop;
import pad.com.haidiyun.www.widget.NewDataToast;

/**
 * Created by Administrator on 2017/9/6/006.
 */

@SuppressLint("ValidFragment")
public class FwCommonActivity extends Fragment {
    private Activity activity;
    private Handler parentHandler;
    private SharedUtils sharedUtils;
    public FwCommonActivity(Activity activity,Handler parentHandler){
        this.activity = activity;
        this.parentHandler = parentHandler;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }
    private ArrayList<MoveItem> moveItems = new ArrayList<MoveItem>();
    {
        add("打包","#EB6483");
        add("加茶水","#25DB61");
        add("买单","#DAD966");
        add("清洁","#5AABF6");
        add("纸巾","#F42CE8");
        add("服务","#8D44F0");
        add("解锁","#ECBE64");
        add("催菜","#30E5D6");
        add("打印账单","#3809F7");

    }
    private void add(String txt,String color){
        MoveItem i = new MoveItem();
        i.setTxt(txt);
        i.setColor(color);
        moveItems.add(i);

    }
    private Handler handler = new Handler(){
    	public void dispatchMessage(android.os.Message msg) {
    		switch (msg.what) {
			case 2:
				NewDataToast.makeText("已解锁");
				if(loadSendPop!=null){
					loadSendPop.cancle();
					loadSendPop = null;
				}
				break;

			default:
				break;
			}
    	};
    };
    private CommonLoadSendPop loadSendPop;
    private Common433SendPop sendPop;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final GridView views = (GridView) view.findViewById(R.id.views);
        final LinearLayout content = (LinearLayout) view.findViewById(R.id.content);
        content.post(new Runnable() {
            @Override
            public void run() {
                MoveAdapter moveAdapter = new MoveAdapter(activity,moveItems,(content.getWidth()-4)/3);
                views.setAdapter(moveAdapter);
                views.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    	String tag = moveItems.get(position).getTxt().toString();
                    	if(tag.equals("解锁")){
                    		if(loadSendPop==null){
            					loadSendPop = new CommonLoadSendPop(activity, "解锁桌台");//解锁桌台
            					loadSendPop.setCancel(new SetI() {
            						
            						@Override
            						public void click() {
            							// TODO Auto-generated method stub
            							loadSendPop.cancle();
            							loadSendPop = null;
            						}
            					});
            					loadSendPop.showSheet(false);
            				}
            				// TODO Auto-generated method stub
            				String ip = sharedUtils.getStringValue("IP");
            				String port = sharedUtils.getStringValue("port");
            				//connectOd(ip, port);
            				  final String table = sharedUtils.getStringValue("table_code");
            				  if(table==null||table.length()==0){
            					  NewDataToast.makeText("请先选择桌台");
            					  return;
            				  }
            				  if(table.length()!=0){
            					  new Thread(){
            							@Override
            							public void run() {
            								// TODO Auto-generated method stub
            								super.run();
            								Connection con = openConnection();
            								execSQL(con, "UPDATE storetables_mis SET USESTATE=3 WHERE TABLENUM="+table);
            								execSQL(con, "update handevtableorder_relation set tablestate=0,PREPRINTYN='2' where tablenum="+table);
            								closeMysql(con);
            								handler.sendEmptyMessage(2);
            							}
            						}.start();
            				  }
                    	}else if(tag.equals("打印账单")){
                    		//打印账单
                    		/*Intent intent = new Intent(activity,CardValActivity.class);
                    		
                    		intent.putExtra("print", true);
                    		startActivity(intent);*/
                    		print();
                    		NewDataToast.makeText("发送打印请求");
                    	}else{
                    		 sendPop = new Common433SendPop(activity,tag,null);
                             sendPop.showSheet();
                    	}
                       

                    }
                });
            }
        });
        	view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					parentHandler.sendEmptyMessage(2);
				}
			});
    }
    private void addItem(JSONArray array,String key,Object value) throws JSONException{
		JSONObject o1 = new JSONObject();
		o1.put("Key", key);
		o1.put("Value", value);
		array.put(o1);
	}
    private void print(){
		String dev = sharedUtils.getStringValue("dev");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			addItem(jsonArray, "MchID", FileUtils.getDeviceId());
			addItem(jsonArray, "BillNo", sharedUtils.getStringValue("billId"));

			addItem(jsonArray, "TableNo", sharedUtils.getStringValue("table_code"));


		   jsonObject.put("DYDC", jsonArray);

			 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(activity,FloatService.class);
		intent.putExtra("var", jsonObject.toString());
		activity.startService(intent);	
		
	}
    
	public   Connection openConnection() {
		final String URL = "jdbc:mysql://" + sharedUtils.getStringValue("IP") + "/mis";
		final String USER = "misuser";
		final String PASSWORD = "misuserchoice";
		Connection conn = null;
		try {
			final String DRIVER_NAME = "com.mysql.jdbc.Driver";
			Class.forName(DRIVER_NAME);
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			conn = null;
		} catch (SQLException e) {
			conn = null;
		}
		return conn;
	}
	public   boolean execSQL(Connection conn, String sql) {
		boolean execResult = false;
		if (conn == null) {
			return execResult;
		}
		Statement statement = null;
		try {
			statement = conn.createStatement();
			if (statement != null) {
				execResult = statement.execute(sql);
			}
		} catch (SQLException e) {
			execResult = false;
		}
		
		return execResult;
	}
	private void closeMysql(Connection conn){
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				conn = null;
			} finally {
				conn = null;
			}
		}
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.float_service_foc, container, false);
        return view;

    }
}
