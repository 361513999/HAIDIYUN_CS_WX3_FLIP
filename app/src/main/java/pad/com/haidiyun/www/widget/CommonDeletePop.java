package pad.com.haidiyun.www.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.Remove;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CommonDeletePop {
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					remove.remove();
					dlg.cancel();
					dlg = null;
					break;

				default:
					break;
			}
		};
	};
	private Context context;
	/**
	 * 删除弹出框
	 */
	private TextView tip,txt,cancle,sure;
	private Remove remove;
	private DishTableBean oj ;
	private   IDialog dlg;
	private View parent_d;
	public CommonDeletePop(Context context,DishTableBean oj ,Remove remove) {
		this.context = context;
		this.oj = oj;
		this.remove = remove;
	}

	public  Dialog showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.common_tip, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		parent_d = layout.findViewById(R.id.parent_d);
		tip = (TextView) layout.findViewById(R.id.tip);
		txt = (TextView) layout.findViewById(R.id.txt);
		cancle = (TextView) layout.findViewById(R.id.cancle);
		sure = (TextView) layout.findViewById(R.id.sure);
		tip.setText("提示");
		try {
			String str = "是否移除【"+URLDecoder.decode(oj.getName(), "UTF-8")+"】";
			SpannableStringBuilder sbBuilder=new SpannableStringBuilder(str);

			sbBuilder.setSpan
					(new ForegroundColorSpan(Color.RED), 5, str.length()-1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

			txt.setText(sbBuilder);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		dlg.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {
				// TODO Auto-generated method stub
				FileUtils.start(Effectstype.Flipv, parent_d);
			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dlg.cancel();
			}
		});
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DB.getInstance().delete(oj.getI());
				handler.sendEmptyMessage(1);
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
