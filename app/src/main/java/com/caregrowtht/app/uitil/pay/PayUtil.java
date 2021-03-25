package com.caregrowtht.app.uitil.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.caregrowtht.app.model.PayEntity;
import com.caregrowtht.app.uitil.LogUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * 支付工具
 */
public enum PayUtil {
    PAY_UTIL;
    public final int ALIPAY_RESULT = 2;
    public OnPayResultListener mListener;
    public Context mContext;

    /**
     * 支付结果
     *
     * @param context
     * @param listener
     */
    public void payResult(String payEntity, Context context, OnPayResultListener listener) {
        mListener = listener;
        mContext = context;
        //调起支付宝
        Logger.e("Sunny", payEntity);
        aliPay(payEntity);
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALIPAY_RESULT:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    PayEntity payEntity = new Gson().fromJson(resultInfo, PayEntity.class);
                    String resultStatus = payResult.getResultStatus();
                    LogUtils.e("支付宝", resultInfo);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (mListener != null) {
                            mListener.paySuccess(PayUtil.payResult.ALIPAY_SUCCESS, payEntity);
                        }
                    } else if (TextUtils.equals(resultStatus, "8000") || TextUtils.equals(resultStatus, "6004")) {
                        // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                        // 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                        if (mListener != null) {
                            mListener.payResult(PayUtil.payResult.ALIPAY_UNKNOWN);
                        }
                    } else if (TextUtils.equals(resultStatus, "5000")) {
                        //  5000:重复请求
                        if (mListener != null) {
                            mListener.payResult(PayUtil.payResult.ALIPAY_REPEAT);
                        }
                    } else {// 4000:订单支付失败 6001:用户中途取消 6002:网络连接出错
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (mListener != null) {
                            mListener.payResult(PayUtil.payResult.ALIPAY_FAILURE);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 支付宝
     *
     * @param info
     */
    private void aliPay(String info) {
        final Runnable payRunnable = () -> {
            PayTask alipay = new PayTask((Activity) mContext);
            Map<String, String> result = alipay.payV2("" + info, true);
            Logger.e("支付宝", result.toString());
            Message msg = new Message();
            msg.what = ALIPAY_RESULT;
            msg.obj = result;
            handler.sendMessage(msg);
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 支付结果回调
     */

    public interface OnPayResultListener {

        void paySuccess(payResult result, PayEntity payEntity);

        void payResult(payResult result);
    }

    /**
     * 支付结果类型
     */
    public enum payResult {
        ALIPAY_SUCCESS, ALIPAY_UNKNOWN, ALIPAY_REPEAT, ALIPAY_FAILURE;
    }

}
