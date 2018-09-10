package pad.com.haidiyun.www.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.service.FloatService;
import pad.com.haidiyun.www.widget.CommonMovePop;
import pad.com.haidiyun.www.widget.NewDataToast;

/**
 * Created by Administrator on 2017/9/6/006.
 */

public class MainFragmentActivity extends BaseFragmentActivity {
    private FragmentManager fragmentManager;
    private int CONTENT = R.id.main_content;
    private DataReceiver dataReceiver;
    /**
     * 发送一次点击广播
     */
    private void down() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_DOWN);
        sendBroadcast(intent);
    }
    private int mode = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

       /* switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = 1;
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                mode += 1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == 3) {
                    Intent intent = new Intent(this,
                            CardValActivity.class);
                    startActivityForResult(intent, ENTER_BUY_VAL);

                }
                break;
        }*/

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
//			toBack(true);
                down();
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("app.data.updata")) {
                //数据变化

            } else if (intent.getAction().equals("app.fc.ud")) {


            }  else if(intent.getAction().equals("KeyEvent.KEYCODE_BACK")){
                //切换到
               
                select(KeyEvent.KEYCODE_BACK);
            } 
        }
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (dataReceiver != null) {
            unregisterReceiver(dataReceiver);
        }
    }
    private FlipNextActivity flipNextActivity;
    private PjCommonActivity pjCommonActivity;
    private FwCommonActivity fwCommonActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(8);
        setContentView(R.layout.main_layout);
        fragmentManager = getSupportFragmentManager();
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("app.data.updata");
        //模式
      //  filter.addAction("app.fc.gr");
        filter.addAction("KeyEvent.KEYCODE_BACK");
        //上下
        filter.addAction("app.fc.ud");
       
        registerReceiver(dataReceiver, filter);
        Intent intent = getIntent();
        if(intent.hasExtra("key")) {
            select(intent.getIntExtra("key", KeyEvent.KEYCODE_CAMERA));
        }
        P.c( intent.getIntExtra("key",0)+"action0:"+intent.getAction());

    }
    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 2:
                    select(KeyEvent.KEYCODE_CAMERA);
                    break;
            }
        }
    };
    private void select(int KEYCODE){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Intent intent = new Intent();
	    intent.setAction(Common.SERVICE_ACTION);
        switch (KEYCODE) {
            case KeyEvent.KEYCODE_CAMERA:
            	intent.putExtra("view_v", 0);
                flipNextActivity = new FlipNextActivity(MainFragmentActivity.this);
                transaction.replace(CONTENT,flipNextActivity);
                break;
            case KeyEvent.KEYCODE_BACK:
            	intent.putExtra("view_gone", 0);
//                fwCommonActivity = new FwCommonActivity(MainFragmentActivity.this,handler);
//                transaction.replace(CONTENT,fwCommonActivity);

                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
            	intent.putExtra("view_gone", 0);
//                pjCommonActivity = new PjCommonActivity(MainFragmentActivity.this,handler);
//                transaction.replace(CONTENT,pjCommonActivity);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                intent.putExtra("view_v", 0);
                flipNextActivity = new FlipNextActivity(MainFragmentActivity.this);
                transaction.replace(CONTENT,flipNextActivity);
                CommonMovePop movePop = new CommonMovePop(MainFragmentActivity.this);
                movePop.showSheet();
                break;
        }
        INDEX = KEYCODE;
        transaction.commitAllowingStateLoss();
        
		
		startService(intent);
    }

    int INDEX = -1;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 自己记录fragment的位置,防止activity被系统回收时，fragment错乱的问题
        // super.onSaveInstanceState(outState);
        outState.putInt("index", INDEX);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);
        INDEX = savedInstanceState.getInt("index");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        P.c("action:"+intent.getAction());

        if(intent.hasExtra("key")){
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            select(intent.getIntExtra("key",KeyEvent.KEYCODE_CAMERA));
        }
    }
    /*  @Override
    public void keyEvent(KeyEvent event) {
        select(event.getKeyCode());
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0
                    || index >= fm.getFragments().size()) {

                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {

            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
            return;
        }

    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode,
                              Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }
}
