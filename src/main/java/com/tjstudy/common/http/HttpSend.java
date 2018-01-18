package com.tjstudy.common.http;

import java.util.HashMap;

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
        APIService apiService = RetrofitBuilder.builder()
                .baseUrl("")//网络地址前缀
                .connectTimeout(12)//连接超时时间 单位/秒
                .readTimeout(12)//读取时间 单位/秒
                .retry(false)//连接失败是否重连 默认进行重连
                .cache(true)//默认不缓存 缓存默认值，大小20M 时间20s,缓存的设置只有GET请求有效
                .cacheSize(40, 60)//缓存大小单位;缓存时间单位秒
                .cacheTime(60)//缓存时间
                .commonParams(new HashMap<String, String>())//设置公共参数 例如设置接口版本号
                .build()
                .create(APIService.class);


        return RetrofitBuilder.builder().build().create(APIService.class);
    }
}
