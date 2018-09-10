package pad.com.haidiyun.www.widget;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.TeeListAdapter;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.bean.TeeBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.inter.SelectTable;
import pad.com.haidiyun.www.inter.SelectTee;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CommonTeePop {
	private Context context;
	/**
	 * 删除弹出框
	 */
	public CommonTeePop(Context context,SelectTee selectTee) {
		this.context = context;
		this.selectTee = selectTee;
	}
	private SelectTee selectTee;
	private   IDialog dlg ;
	private GridView tablesView;
	private TextView cancle;
	private TeeListAdapter tablesListAdapter;
	private List<TeeBean> teeBeans;
	private SelectTee close = new SelectTee() {



		@Override
		public void select(TeeBean bean) {
			// TODO Auto-generated method stub
			if (bean != null) {
				close();
			}
		}
	};



	public  void showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.setting_tables, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		cancle = (TextView) layout.findViewById(R.id.cancle);
		tablesView = (GridView) layout.findViewById(R.id.tables);
		teeBeans = new ArrayList<TeeBean>();
		tablesListAdapter = new TeeListAdapter(context, teeBeans,selectTee,close);
		tablesView.setAdapter(tablesListAdapter);
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				close();
			}
		});
		new Thread(){
			public void run() {
				try {
					String result = FileUtils.read(Common.SD+Common.SOURCE_DB);

					JSONObject obj = new JSONObject(URLDecoder.decode(result, "UTF-8"));
					JSONArray jsonArray = obj.getJSONArray("CoverCharge");
					int len = jsonArray.length();
					/*
					 *  "ProductID": "128",
      "ProductName": "包房茶位",
      "ProductCode": "00128",
      "Price": "3",
      "Unit": "位"
					 */
					teeBeans.clear();
					TeeBean tb = new TeeBean();
					tb.setCode("");
					tb.setPrice("");
					tb.setName("无茶位费");
					teeBeans.add(tb);
					for(int i=0;i<len;i++){
						JSONObject oj = jsonArray.getJSONObject(i);
						TeeBean teeBean = new TeeBean();
						teeBean.setCode(oj.getString("ProductCode"));
						teeBean.setPrice(oj.getString("Price"));
						teeBean.setName(oj.getString("ProductName"));
						teeBeans.add(teeBean);
					}
					handler.sendEmptyMessage(1);


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(0);
				}

			};
		}.start();
		/*	 */
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();

	}

	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					if(tablesListAdapter!=null){
						tablesListAdapter.updata(teeBeans);
					}
					break;
				case 0:
					NewDataToast.makeText( "数据解析异常");
					close();
					break;

				default:
					break;
			}
		};
	};
	public void close(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg.dismiss();
			dlg = null;

		}
	}
}
