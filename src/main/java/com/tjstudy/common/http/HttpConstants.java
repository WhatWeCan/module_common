package com.tjstudy.common.http;

import java.net.UnknownHostException;

/**
 * 网络相关常量
 * Created by tjstudy on 2018/1/12.
 */

class HttpConstants {
    static final String BASE_URL = "";//基础网址

    static final int CODE_OK = 200;//表示访问成功
    static final int CODE_TOKEN_EXPIRED = 300;//token过期

    /**
     * 获取异常对应的文本提示
     *
     * @param e
     * @return
     */
    public String getExceptionMsg(Throwable e) {
        if (e instanceof UnknownHostException) {
            return "无网络";
        }
        return "";
    }
}
