package pad.com.haidiyun.www.widget;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.BaseApplication;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author
 * @version 1.0
 * @created 2012-8-30
 */
@SuppressLint("ViewConstructor")
public class NewDataToast extends Toast {
	private static NewDataToast result;
	@SuppressWarnings("unused")
	private MediaPlayer mPlayer;
	@SuppressWarnings("unused")
	public static final int PHONE = 1;
	public static final int ORDER_NULL = 2;
	public static final int NULL = 0;
	public static final int ORDER_IN = 3;
	public NewDataToast(Context context) {
		super(context);
	}
	@Override
	public void show() {
		super.show();
	}
	public static synchronized void makeText(CharSequence text){
		ViewToast.getViewToast().makeText(BaseApplication.application, text);
		ViewToast.getViewToast().show();
	}
	public static synchronized   void makeTextL( CharSequence text,int time){
		ViewToast.getViewToast().makeText(BaseApplication.application, text);
		ViewToast.getViewToast().show(time);
	}
	public static synchronized   void makeTextTop(CharSequence text,int time){
		ViewToast.getViewToast().makeTextTop(BaseApplication.application, text);
		ViewToast.getViewToast().show(time);
	}
	public static synchronized   void makeTextD(CharSequence text){
		ViewToast.getViewToastD().makeTextD(BaseApplication.application, text);
		ViewToast.getViewToastD().show();
	}
	public static synchronized   void makeTextD(CharSequence text,int time){
		ViewToast.getViewToastD().makeTextD(BaseApplication.application, text);
		ViewToast.getViewToastD().show(time);
	}
	public static NewDataToast Text(Context context, CharSequence text,int alp) {
		if (result == null) {
			result = new NewDataToast(context);
		}
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		View v = inflate.inflate(R.layout.new_data_toast, null);
		TextView tv = (TextView) v.findViewById(R.id.new_data_toast_message);
		tv.setText(text);
		result.setView(v);
		result.setDuration(Toast.LENGTH_SHORT);
		result.setGravity(Gravity.BOTTOM, 0, (int) (dm.density * 75));
		if(alp!=-1){
			result.getView().getBackground().setAlpha(alp);
		}
		return result;
	}
	public static void hodden() {
		if (result != null) {
			result.cancel();
		}
	}
}
