package com.tjstudy.common.http.response;

import com.google.gson.annotations.SerializedName;

/**
 * base 网络数据实体
 * Created by tjstudy on 2018/1/12.
 */

public class BaseResponse<T> {
    /**
     * status : true
     * msg : 获取成功
     * timestamp : 2017-08-08 22:36:06
     * data : {}
     */

    private boolean status;
    private String msg;
    private String timestamp;
    @SerializedName("data")
    private T t;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
