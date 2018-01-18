package com.tjstudy.common.http.response;

/**
 * base 网络数据实体
 * Created by tjstudy on 2018/1/12.
 */

public class BaseResponse<T> {
    private int code;//网络返回码
    private String msg;//返回码对应数据

    private T t;//其他内容？

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg=" + msg +
                ", t=" + t +
                '}';
    }
}
