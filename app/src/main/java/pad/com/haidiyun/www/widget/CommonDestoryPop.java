package pad.com.haidiyun.www.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.LoadBuy;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CommonDestoryPop {
	private Context context;
	/**
	 * 清空菜品
	 */
	private TextView tip,txt,cancle,sure;
	private   IDialog dlg;
	private LoadBuy sureBuy;
	public CommonDestoryPop(Context context,LoadBuy sureBuy) {
		this.context = context;
		this.sureBuy = sureBuy;
	}
	public  Dialog showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.common_tip, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		tip = (TextView) layout.findViewById(R.id.tip);
		txt = (TextView) layout.findViewById(R.id.txt);
		cancle = (TextView) layout.findViewById(R.id.cancle);
		sure = (TextView) layout.findViewById(R.id.sure);
		sure.setText("确定");
		tip.setText("温馨提示");
		String str = "此操作将会清空所有列表中已点的菜品,是否清空?";
		SpannableStringBuilder sbBuilder=new SpannableStringBuilder(str);
		sbBuilder.setSpan
				(new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		txt.setText(sbBuilder);
		txt.setTextSize(20);
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.cancel();
			}
		});
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//下单
				//确定下单
				DB.getInstance().clear();
				sureBuy.success(null, null);
				dlg.cancel();
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
