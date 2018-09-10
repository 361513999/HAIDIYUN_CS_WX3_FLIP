package pad.com.haidiyun.www.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import pad.com.haidiyun.www.R;

/**
 * Created by Administrator on 2017/9/6/006.
 */

@SuppressLint("ValidFragment")
public class PjCommonActivity extends Fragment {
    private Activity activity;
    private Handler parentHandler;
    public PjCommonActivity(Activity activity,Handler parentHandler){
        this.activity = activity;
        this.parentHandler = parentHandler;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				parentHandler.sendEmptyMessage(2);
			}
		});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pj_layout, container, false);
        return view;

    }
}
