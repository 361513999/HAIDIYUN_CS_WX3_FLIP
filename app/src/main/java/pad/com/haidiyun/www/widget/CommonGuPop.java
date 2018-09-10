package pad.com.haidiyun.www.widget;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.LoadBuy;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CommonGuPop {
	private Context context;
	/**
	 * 下单提示
	 */
	private TextView tip,txt,cancle;
	private   IDialog dlg;

	private SharedUtils sharedUtils;
	private String text;
	public CommonGuPop(Context context, String text) {
		this.context = context;
		this.text = text;
		sharedUtils = new SharedUtils(Common.CONFIG);
	}
	public  Dialog showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.common_gu_tip, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		tip = (TextView) layout.findViewById(R.id.tip);
		txt = (TextView) layout.findViewById(R.id.txt);
		cancle = (TextView) layout.findViewById(R.id.cancle);
		tip.setText("温馨提示");
		txt.setText(text);
		txt.setTextSize(20);
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.cancel();
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
