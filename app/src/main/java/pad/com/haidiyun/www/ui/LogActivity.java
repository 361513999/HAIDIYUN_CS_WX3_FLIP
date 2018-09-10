package pad.com.haidiyun.www.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.base.AppManager;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.TimeUtil;

/**
 * Created by Administrator on 2017/11/11/011.
 */

public class LogActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);
        TextView view = (TextView) findViewById(R.id.view);
        String childFileName = TimeUtil.getTimeLog(System.currentTimeMillis());
        view.setText(FileUtils.read(Common.APK_LOG+childFileName+".txt"));
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(LogActivity.this);
            }
        });
    }
}
