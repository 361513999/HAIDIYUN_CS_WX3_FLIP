package pad.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.UsersAdapter;
import pad.com.haidiyun.www.adapter.MenusAdapter;
import pad.com.haidiyun.www.bean.MenuBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.DownMsg;
import pad.com.haidiyun.www.inter.SetI;
import pad.com.haidiyun.www.service.FloatService;

public class CommonUserPop {
	private Context context;
	/**
	 * 下单提示
	 */
	private   IDialog dlg;
	private DownMsg menuSelect;
	private SwipeRefreshLayout pull_to_refresh_list;
	private SharedUtils userUtils;
	private SetI setI;
	public CommonUserPop(Context context, DownMsg menuSelect,SetI setI) {
		this.context = context;
		this.menuSelect = menuSelect;
		this.setI =setI;
		userUtils = new SharedUtils(Common.USERS);;
	}
	public boolean isShowing(){
		if(dlg!=null){
			return dlg.isShowing();
		}
		return  false;
	}
	private void getMM(){
		String cmd = "{\"YHLB\":\"\"}";
		Intent intent = new Intent(context,FloatService.class);
		intent.putExtra("var", cmd);
		context.startService(intent);
	}
	private SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
			pull_to_refresh_list.setRefreshing(true);
			Common.IS_SHOW = true;
				getMM();

		}
	};
	private ArrayList<String> sers = new ArrayList<>();
	public void lis(){

		if(usersAdapter!=null){
			sers.clear();
			sers = userUtils.getKeys();
			usersAdapter.updata(sers);
			pull_to_refresh_list.setRefreshing(false);
		}
	}
	private UsersAdapter usersAdapter;
	public  Dialog showSheet() {
		dlg = new IDialog(context, R.style.menu_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.common_user, null);
		pull_to_refresh_list = (SwipeRefreshLayout) layout.findViewById(R.id.pull_to_refresh_list);
		ListView menus_list= (ListView) layout.findViewById(R.id.menus_list);
		sers.clear();;
		sers = userUtils.getKeys();
		usersAdapter = new UsersAdapter(context, sers);
		menus_list.setAdapter(usersAdapter);
		pull_to_refresh_list.setOnRefreshListener(listener);
		menus_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub

				menuSelect.view(sers.get(arg2));

				if(dlg!=null){
					dlg.cancel();
					dlg = null;
				}

			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				if(setI!=null){
					setI.click();
				}
			}
		});
		dlg.show();
		return dlg;
	}

}
