package com.tjstudy.common.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * toast工具类
 * Created by tjstudy on 2018/1/17.
 */

public class ToastUtils {
    public static void show(Context context, String mess) {
        Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
    }
}
