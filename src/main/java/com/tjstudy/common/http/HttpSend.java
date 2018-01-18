package com.tjstudy.common.http;

/**
 * 网络请求整合类
 * Created by tjstudy on 2018/1/12.
 */

public class HttpSend {
    private static HttpSend ins;

    private HttpSend() {
    }

    public static HttpSend getIns() {
        if (ins == null) {
            synchronized (HttpSend.class) {
                if (ins == null) {
                    ins = new HttpSend();
                }
            }
        }
        return ins;
    }

    private APIService getBaseApi() {
        return RetrofitBuilder.builder().build().create(APIService.class);
    }
}
