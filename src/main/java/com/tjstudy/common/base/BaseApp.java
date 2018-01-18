package com.tjstudy.common.base;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tjstudy on 2018/1/12.
 */

public class BaseApp extends Application {
    public BaseApp() {
        instance = this;
    }

    public static BaseApp instance;
    private Map<String, String> mParams = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mParams.put("start", "0");
        mParams.put("count", "1");
    }

    /**
     * 获取缓存文件路径
     *
     * @return
     */
    public String getCacheFileDir() {
        return getCacheDir().getAbsolutePath();
    }

    /**
     * 获取参数
     *
     * @return
     */
    public Map<String, String> getParams() {
        return mParams;
    }

    /**
     * 设置参数
     *
     * @param params
     */
    public void addParams(Map<String, String> params) {
        mParams = params;
    }
}
