package pad.com.haidiyun.www.widget;

import java.util.ArrayList;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.BillsAdapter;
import pad.com.haidiyun.www.bean.BillsBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class CommonBillsPop {
	private Context context;
	/**
	 * 下单提示
	 */
	private   IDialog dlg;
	private Handler menuSelect;
	private ArrayList<BillsBean> menuBeans;
	public CommonBillsPop(Context context,Handler menuSelect,ArrayList<BillsBean> menuBeans) {
		this.context = context;
		this.menuSelect = menuSelect;
		this.menuBeans = menuBeans;
		dlg = new IDialog(context, R.style.menu_pop_style);
	}
	private Object o;
	private SharedUtils sharedUtils;
	public void putObj(Object o){
		this.o = o;
	}
	private int what;
	public void setWhat(int what){
		this.what = what;
	}
	public void setWindow(){
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}
	public  Dialog showSheet() {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.menu_pop_view, null);
		ListView menus_list= (ListView) layout.findViewById(R.id.menus_list);

		BillsAdapter menusAdapter = new BillsAdapter(context, menuBeans);
		menus_list.setAdapter(menusAdapter);
		sharedUtils = new SharedUtils(Common.CONFIG);
		menus_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				StringBuilder builder = new StringBuilder();
				sharedUtils.clear("bills");
				if(menuBeans.size()>1){
					for(int i=0;i<menuBeans.size();i++){
						BillsBean bb = menuBeans.get(i);
						if(i!=menuBeans.size()-1){
							builder.append(bb.getName()+"_"+bb.getPerson()+",");
						}else{
							builder.append(bb.getName()+"_"+bb.getPerson());
						}

					}
					sharedUtils.setStringValue("bills", builder.toString());
				}


				BillsBean bb = menuBeans.get(arg2);
				sharedUtils.setStringValue("billId", bb.getName());
				try {
					sharedUtils.setIntValue("person", bb.getPerson());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sharedUtils.setIntValue("person", 1);
				}

				if(dlg!=null){
					dlg.cancel();
					dlg = null;
				}
				Message msg = new Message();
				msg.what = what;
				if(o!=null){
					msg.obj = o;
				}
				menuSelect.sendMessage(msg);

			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
