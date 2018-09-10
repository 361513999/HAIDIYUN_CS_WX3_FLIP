package pad.com.haidiyun.www.widget;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.inter.BeDish;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CommonBePop {
	private Context context;
	/**
	 * 下单提示
	 */
	private TextView item0,item1;
	private   IDialog dlg;
	private BeDish beDish;
	private Typeface tf;
	private View parent_d;
	public CommonBePop(Context context,BeDish beDish) {
		this.context = context;
		this.beDish = beDish;
		tf = Typeface.createFromAsset(context.getAssets(), "font/mb.ttf");
	}
	public  Dialog showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.be_dish_layout, null);
		final int cFullFillWidth = 420;
		layout.setMinimumWidth(cFullFillWidth);
		item0 = (TextView) layout.findViewById(R.id.item0);
		item1 = (TextView) layout.findViewById(R.id.item1);
		parent_d = layout.findViewById(R.id.parent_d);
		item0.setTypeface(tf);
		item1.setTypeface(tf);
		dlg.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {
				// TODO Auto-generated method stub
				FileUtils.start(Effectstype.Fliph, parent_d);
			}
		});
		item0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				beDish.add();
				if(dlg!=null){
					dlg.cancel();
					dlg = null;
				}

			}
		});
		item1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//下单
				beDish.dish();
				//确定下单
				if(dlg!=null){
					dlg.cancel();
					dlg = null;
				}

			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
