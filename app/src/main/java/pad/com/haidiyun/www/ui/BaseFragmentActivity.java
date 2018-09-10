package pad.com.haidiyun.www.ui;


import pad.com.haidiyun.www.base.AppManager;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.WindowManager;



public abstract class BaseFragmentActivity extends FragmentActivity {
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
			 
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		}else{
			 
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
