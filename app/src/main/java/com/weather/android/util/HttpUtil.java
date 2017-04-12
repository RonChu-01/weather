package com.weather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by wb.chuyong on 2017/4/7.
 */

//与服务器交互的代码，借用Okhttp ,调用方法时，传入一个地址，注册一个回调，响应服务器返回数据

public class HttpUtil {

    public static void sendOkHttpRequest(String address , okhttp3.Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);

    }

}















