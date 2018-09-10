package pad.com.haidiyun.www.ui;


import pad.com.haidiyun.www.base.AppManager;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity{
	SharedUtils sharedUtils ;
	/*@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		final int action = event.getAction();
		final boolean isDown = action == KeyEvent.ACTION_DOWN;
		if(isDown&&(event.getRepeatCount() == 0)){
			keyEvent(event);
		}
		return  super.dispatchKeyEvent(event);
	}
	public abstract  void keyEvent(KeyEvent event);*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sharedUtils = new SharedUtils(Common.CONFIG);
		if(sharedUtils.getBooleanValue("screen_keep")){
			P.c("常亮模式");
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}else{
			P.c("系统熄屏策略");
		}
		AppManager.getAppManager().addActivity(this);


	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		AppManager.getAppManager().finishActivity(this);
	}

}
