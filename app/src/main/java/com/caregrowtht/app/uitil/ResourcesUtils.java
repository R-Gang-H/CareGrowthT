package com.caregrowtht.app.uitil;

import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import android.widget.TextView;

import com.caregrowtht.app.MyApplication;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


/**
 * 类描述：获取资源
 * 创建人：haoruigang
 * 创建时间：2017-11-20 19:08:56
 */

public class ResourcesUtils {

    /**
     * 获取字符串
     *
     * @param id
     * @param obj
     * @return
     */
    public static String getString(@StringRes int id, Object... obj) {
        String string = MyApplication.getInstance().getResources().getString(id);
        if (null != obj && obj.length > 0)
            return String.format(string, obj);
        return string;
    }

    public static String[] getStrings(@ArrayRes int id) {
        String[] stringArray = MyApplication.getInstance().getResources().getStringArray(id);
        return stringArray;
    }

    /**
     * 获取颜色
     *
     * @param color
     * @return
     */
    public static int getColor(@ColorRes int color) {
        return MyApplication.getAppContext().getResources().getColor(color);
    }

    /**
     * 获取图片
     *
     * @param drawable
     * @return
     */
    public static Drawable getDrawable(@DrawableRes int drawable) {
        return MyApplication.getAppContext().getResources().getDrawable(drawable);
    }

    /**
     * 文字中添加图片
     *
     * @param textView
     * @param imgResId
     * @param index
     * @param padding
     */
    public static void setTvaddDrawable(TextView textView, @DrawableRes int imgResId, int index, int padding) {
        if (imgResId == -1) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(index == 1 ? imgResId : 0, index == 2 ? imgResId : 0, index == 3 ? imgResId : 0, index == 4 ? imgResId : 0);
            textView.setCompoundDrawablePadding(padding);
        }
    }

    /**
     * 字符串（含中文）转16进制
     *
     * @param str
     * @return
     */
    public static byte[] SendS(String str) {
        byte[] ok = new byte[0];
        try {
            ok = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ok;
    }

    /**
     * 16进制转字符串（含中文）
     *
     * @param bytes
     * @return
     */
    public static String getString(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }

}
