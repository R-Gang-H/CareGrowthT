package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.BitmapUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.UserManager;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by haoruigang on 2017/10/19.
 * 定制化显示扫描界面
 */
public class QRCodeActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    EditText tvTitle;
    @BindView(R.id.tv_scan)
    TextView tvScan;
    @BindView(R.id.tv_dist)
    TextView tvDist;
    @BindView(R.id.tv_scan_line)
    TextView tvScanLine;
    @BindView(R.id.tv_dist_line)
    TextView tvDistLine;
    @BindView(R.id.fl_my_container)
    FrameLayout flMyContainer;
    @BindView(R.id.ll_zxing_dis)
    LinearLayout llZxingDis;
    @BindView(R.id.btn_create_child)
    Button btnCreateChild;

    private final int REQUEST_IMAGE = 666;


    @Override
    public int getLayoutId() {
        return R.layout.activity_qrcode;
    }

    @Override
    public void initView() {
        rlBackButton.setOnClickListener(view -> finish());
    }

    @Override
    public void initData() {
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            getQRCode(result, "1");// 扫描
        }

        @Override
        public void onAnalyzeFailed() {
//            U.showToast("相机打开出错");
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString("invitationCode", "");
            resultIntent.putExtras(bundle);
            QRCodeActivity.this.setResult(RESULT_OK, resultIntent);
            QRCodeActivity.this.finish();
        }
    };

    /**
     * @param result     识别的参数
     * @param typeQRCode 识别类型
     */
    private void getQRCode(String result, String typeQRCode) {
        //        org=000H
        String type = "0";
        String retentStr = null;
        if (result.contains(Constant.isTest ? "http" : "https") && result.contains("s=")) {//微信能扫的
            String[] strs = result.split("=");
            retentStr = String.valueOf(BitmapUtils.decode(strs[1]));
            type = "1";
        } else if (result.contains("&")) {// org=00CC&lesson=2265
            String lessonId = result.substring(result.lastIndexOf("=") + 1);
            retentStr = lessonId;
            type = "2";
        } else {// 微信不能扫的
            //解密二维码
            retentStr = String.valueOf(BitmapUtils.decode(result));
            type = "1";
        }
        if (typeQRCode.equals("1")) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString("invitationCode", retentStr);
            bundle.putString("type", type);// type: 1: org=000H(机构Id) 2: org=00CC&lesson=2265(课程Id)
            resultIntent.putExtras(bundle);
            QRCodeActivity.this.setResult(RESULT_OK, resultIntent);
            QRCodeActivity.this.finish();
        } else {
            if (type.equals("1")) {
                if (!StrUtils.isEmpty(retentStr)) {
                    UserManager.getInstance().getOrgInfo(retentStr, this, "1");
                } else {
                    U.showToast("未识别二维码!");
                }
            } else {
                startActivity(new Intent(this, CourserActivity.class)
                        .putExtra("courseId", retentStr));
            }
        }
    }


    @OnClick({R.id.tv_title, R.id.tv_scan, R.id.tv_dist, R.id.btn_create_child})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title:
                startActivity(new Intent(QRCodeActivity.this, OrgSearchActivity.class));
                break;
            case R.id.tv_scan:
                tvScanLine.setBackgroundColor(getResources().getColor(R.color.blueLight));
                tvDistLine.setBackgroundColor(getResources().getColor(R.color.white));
                flMyContainer.setVisibility(View.VISIBLE);
                llZxingDis.setVisibility(View.GONE);
                break;
            case R.id.tv_dist:
                tvScanLine.setBackgroundColor(getResources().getColor(R.color.white));
                tvDistLine.setBackgroundColor(getResources().getColor(R.color.blueLight));
                flMyContainer.setVisibility(View.GONE);
                llZxingDis.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_create_child:
                //扫描本地图片识别二维码
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(BitmapUtils.getImageAbsolutePath(this, uri),
                            new CodeUtils.AnalyzeCallback() {
                                @Override
                                public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                                    getQRCode(result, "2");// 识别二维码
                                }

                                @Override
                                public void onAnalyzeFailed() {
                                    U.showToast("解析二维码失败");
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
