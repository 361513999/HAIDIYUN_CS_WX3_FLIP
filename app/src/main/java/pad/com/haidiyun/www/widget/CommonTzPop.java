package pad.com.haidiyun.www.widget;

import java.util.ArrayList;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.TzAdapter;
import pad.com.haidiyun.www.bean.TzBean;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
public class CommonTzPop {
	private Context context;
	/**
	 * 下单提示
	 */
	private   IDialog dlg;
	private View parent_d;
	private TzAdapter tzAdapter;
	private GridView tz_list;
	private ArrayList<TzBean> tzBeans ;
	private int status;
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					tzAdapter.updata(tzBeans);
					break;

				default:
					break;
			}
		};
	};
	private Handler flHandler;
	public CommonTzPop(Context context,int status,Handler flHandler) {
		this.context = context;
		this.status = status;
		this.flHandler = flHandler;
		tzBeans = new ArrayList<TzBean>();
	}
	public  Dialog showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.tz_dish_layout, null);
		final int cFullFillWidth = 420;
		layout.setMinimumWidth(cFullFillWidth);
		parent_d = layout.findViewById(R.id.parent_d);
		tz_list = (GridView) layout.findViewById(R.id.tz_list);
		tzAdapter = new TzAdapter(context, tzBeans, flHandler, status);
		tz_list.setAdapter(tzAdapter);
		new Thread(){
			public void run() {
				DB.getInstance().getTzBeans(tzBeans, status);
				handler.sendEmptyMessage(1);
			};
		}.start();

		dlg.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {
				// TODO Auto-generated method stub
				FileUtils.start(Effectstype.Fliph, parent_d);
			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
	public void cancle(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg = null;
		}
	}

}
