package pad.com.haidiyun.www.widget;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.TeeBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.LoginS;
import pad.com.haidiyun.www.inter.SelectTable;
import pad.com.haidiyun.www.inter.SelectTee;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommonRegisterPop {
	private Context context;
	public CommonRegisterPop(Context context) {
		this.context = context;
	}
	private SharedUtils utils;
	private IDialog dlg;
	private CommonRegisterSendPop registerPop;
	private TextView  save, cancle;
	private EditText ip, port,user;
	public void showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.setting_register, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		ip = (EditText) layout.findViewById(R.id.ip);
		port = (EditText) layout.findViewById(R.id.port);
		save = (TextView) layout.findViewById(R.id.save);
		user = (EditText) layout.findViewById(R.id.user);
		cancle = (TextView) layout.findViewById(R.id.cancle);
		utils = new SharedUtils(Common.CONFIG);
		if (utils.getStringValue("IP").length() != 0) {
			ip.setText(utils.getStringValue("IP"));
		}
		if (utils.getStringValue("port").length() != 0) {
			port.setText(utils.getStringValue("port"));
		}
		if(utils.getStringValue("sNa").length()!=0){
			user.setText(utils.getStringValue("sNa"));
		}


		ip.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				// 只要EditText变化 就将桌台变掉
			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				close();
			}
		});
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				utils.setStringValue("IP", ip.getText().toString());
				utils.setStringValue("port", port.getText().toString());
				utils.setStringValue("sNa", user.getText().toString());
				close();
				/*if(ip.getText().toString().length()!=0||port.getText().toString().length()!=0){
					registerPop = new CommonRegisterSendPop(context, "正在注册", ip.getText().toString(),port.getText().toString(), loginS);
					registerPop.showSheet();
				}else{
					NewDataToast.makeText("请检查信息");
				}*/
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
//	private LoginS loginS = new LoginS() {
//
//		@Override
//		public void success() {
//			close();
//			//这里成功了，保存一下手动信息
//			utils.setStringValue("sNa", user.getText().toString().trim());
//			utils.setStringValue("dev",dev.getText().toString().trim());
//			
//			if(registerPop!=null){
//				registerPop = null;
//			}
//		}
//	};

	private void close() {
		if (dlg != null && dlg.isShowing()) {
			dlg.cancel();
			dlg = null;
		}
	}
}
