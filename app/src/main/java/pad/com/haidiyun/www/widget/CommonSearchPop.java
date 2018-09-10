package pad.com.haidiyun.www.widget;

import java.util.ArrayList;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.SearchAdapter;
import pad.com.haidiyun.www.adapter.SearchResultAdapter;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.db.DB;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CommonSearchPop {
	private Context context;
	/**
	 * 下单提示
	 */
	private   IDialog dlg;
	private Handler menuSelect;
	private String zm[] = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private ArrayList<String> search = new ArrayList<String>();
	public CommonSearchPop(Context context,Handler menuSelect) {
		this.context = context;
		this.menuSelect = menuSelect;
		search.clear();
	}
	private void get(final String param){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				searchDishs = DB.getInstance().getSearchDish(param);
				handler.sendEmptyMessage(1);
			}
		}.start();
	}
	private Handler handler = new Handler(){
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
				case 1:
					if(resultAdapter!=null){
				/*	if(searchDishs.size()==0){
						result_view.setVisibility(View.GONE);
					}else{
						result_view.setVisibility(View.VISIBLE);
					}*/
						resultAdapter.updata(searchDishs);
					}
					break;

				default:
					break;
			}
		};
	};
	private ArrayList<FouceBean> searchDishs ;
	private SearchResultAdapter resultAdapter ;
	private LinearLayout result_view;
	public  Dialog showSheet() {
		dlg = new IDialog(context, R.style.menu_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.search_view, null);
		result_view = (LinearLayout) layout.findViewById(R.id.result_view);
		final TextView search_txt = (TextView) layout.findViewById(R.id.search_txt);
		GridView menus_list= (GridView) layout.findViewById(R.id.search_tip);
		SearchAdapter searchAdapter = new SearchAdapter(context, zm);
		menus_list.setAdapter(searchAdapter);

		ImageView sear_del = (ImageView) layout.findViewById(R.id.sear_del);

		ListView search_list = (ListView) layout.findViewById(R.id.search_list);
		searchDishs = new ArrayList<FouceBean>();
		resultAdapter = new SearchResultAdapter(context, searchDishs);
		search_list.setAdapter(resultAdapter);
		search_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = searchDishs.get(arg2).getPage();
				menuSelect.sendMessage(msg);
				if(dlg!=null){
					dlg.cancel();
					dlg = null;
				}
			}
		});
		//--------------------------------------
		menus_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				search_txt.setText(search_txt.getText().toString()
						+ zm[arg2]);
				search.add(zm[arg2]);
				//
				String rs = search.toString().replace("[", "%")
						.replaceAll("]", "%").replaceAll(",", "%")
						.replaceAll(" ", "");
				if(rs.length()!=0){
					get(rs);
				}

			}
		});
		sear_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/**
				 * 删除数据
				 */
				if(search_txt.getText().toString().length() != 0){
					String temp = search_txt.getText().toString();
					search_txt.setText(temp.subSequence(0, temp.length() - 1));
					search.remove(search.size() - 1);
					String rs = search.toString().replace("[", "%")
							.replaceAll("]", "%").replaceAll(",", "%")
							.replaceAll(" ", "");
					P.c("回退"+rs);
					if(search.size()!=0){
						get(rs);
					}else{
						searchDishs.clear();
						handler.sendEmptyMessage(1);
					}
				}else{
					searchDishs.clear();
					handler.sendEmptyMessage(1);
				}
			}
		});

		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		pause();
		return dlg;
	}
	private void pause(){
		Intent intent = new Intent();
		intent.setAction(Common.TOUCH_PAUSE);
		context.sendBroadcast(intent);
	}
	private void down(){
		Intent intent = new Intent();
		intent.setAction(Common.TOUCH_DOWN);
		context.sendBroadcast(intent);
	}
}
