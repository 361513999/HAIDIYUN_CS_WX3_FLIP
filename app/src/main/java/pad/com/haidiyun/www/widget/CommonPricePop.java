package pad.com.haidiyun.www.widget;

import java.util.ArrayList;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.db.DB;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommonPricePop {
	private Context context;
	/**
	 * 删除弹出框
	 */
	private FouceBean va;
	private Handler pHandler;
	public CommonPricePop(Context context,FouceBean va,Handler pHandler) {
		this.context = context;
		this.va = va;
		this.pHandler = pHandler;
	}

	private SharedUtils utils;
	private IDialog dlg;
	private TextView  price_save, price_cancle;
	private EditText txt;
	private TextView tip;
	public void showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.modify_price_pop, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		//----修改时价
		price_save = (TextView) layout.findViewById(R.id.price_sure);
		price_cancle = (TextView) layout.findViewById(R.id.price_cancle);
		txt = (EditText) layout.findViewById(R.id.txt);
		tip = (TextView) layout.findViewById(R.id.tip);
		tip.setText(va.getName()+"【原价】"+va.getPrice());
		utils = new SharedUtils(Common.CONFIG);
		/*if (utils.getStringValue("pass").length() != 0) {
			pass.setText(utils.getStringValue("pass"));
		}*/

		price_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				close();
			}
		});
		price_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(txt.getText().toString().length()!=0){

					new Thread(){
						public void run() {
							va.setPrice(Double.parseDouble(txt.getText().toString()));
							ArrayList<ReasonBean> resons = new ArrayList<ReasonBean>();
							boolean flag = DB.getInstance().addDishToPad(va,resons,null);
							handler.sendEmptyMessage(1);
						};
					}.start();


				}
			}
		});

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
					close();
					pHandler.sendEmptyMessage(4);
					break;

				default:
					break;
			}
		};
	};

	private void close() {
		if (dlg != null && dlg.isShowing()) {
			dlg.cancel();
			dlg = null;
		}
	}
}
