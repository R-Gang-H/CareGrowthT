package com.caregrowtht.app.okhttp.callback;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.caregrowtht.app.Constant;
import com.caregrowtht.app.okhttp.progress.MyProgressDialog;
import com.caregrowtht.app.uitil.LogUtils;
import com.google.gson.Gson;
import com.lzy.okhttputils.callback.AbsCallback;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 作者： haoruigang on 2017-11-28 11:11:51
 * 类描述：网络回调类
 */
public abstract class HttpCallBack<T> extends AbsCallback implements IHttpCallBack<T>, MyProgressDialog.OnDialogCancel {
    private MyProgressDialog dialog;

    public HttpCallBack() {
    }

    // 是否可取消请求，默认可取消  haoruigang  2017-11-28 11:12:09
    public HttpCallBack(Activity activity, boolean isDismiss) {
        if (isDismiss)
            dialog = new MyProgressDialog(activity, isDismiss);
        else
            dialog = new MyProgressDialog(activity, this);
        dialog.show();
    }

    public HttpCallBack(Activity activity) {
        dialog = new MyProgressDialog(activity, this);
        dialog.show();
    }

    @Override
    public void setOnDialogCancel() {
        dismiss();
    }

    private void dismiss() {
        if (null != dialog && dialog.isShowing())
            dialog.dismiss();
    }

    private Type getTType(Class<?> clazz) {
        Type mySuperClassType = clazz.getGenericSuperclass();
        Type[] types = ((ParameterizedType) mySuperClassType).getActualTypeArguments();
        if (types != null && types.length > 0) {
            return types[0];
        }
        return null;
    }

    //----------引入之前的代码-------------

    private int statusCode;
    private String data;
    private String errorMsg;

    @Override
    public Object parseNetworkResponse(Response response) throws Exception {
        return response.body().string();
    }

    //  成功回调
    @Override
    public void onSuccess(Object o, Call call, Response response) {

        if (Constant.isTest) {
            String data = response.toString();
            LogUtils.e("lt---", data);
            Logger.e("接口返回数据 " + o.toString());
            Logger.json(o.toString());
        }

        try {
            JSONObject jsonObject = new JSONObject(o.toString());
            statusCode = jsonObject.getInt("status");
            data = jsonObject.getString("data");
            errorMsg = jsonObject.getString("errorMsg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            dismiss();

            Type gsonType = getTType(this.getClass());
            if (statusCode == 0 && !TextUtils.isEmpty(o.toString())) {
                if ("class java.lang.String".equals(gsonType.toString())) {
                    onSuccess((T) o.toString());
                } else {
                    T o1 = new Gson().fromJson(o.toString(), gsonType);
                    onSuccess(o1);
                }
            } else if (!TextUtils.isEmpty(o.toString()) && o.toString().contains("conflict")) {// 排课有冲突
                if ("class java.lang.String".equals(gsonType.toString())) {
                    onSuccess((T) o.toString());
                } else {
                    T o1 = new Gson().fromJson(o.toString(), gsonType);
                    onSuccess(o1);
                }
            } else {
                onFail(statusCode, errorMsg);
            }
        } catch (Exception e) {
            LogUtils.e("Exception", e.getMessage());
            onError(e);
        }
    }


    // 失败后的回调
    @Override
    public void onError(Call call, Response response, Exception e) {
        Log.e("lt---", "error response" + response);
        super.onError(call, response, e);
        onError(e);
    }
}
