package com.zc.http.okhttp.builder;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.request.OtherRequest;
import com.zc.http.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
