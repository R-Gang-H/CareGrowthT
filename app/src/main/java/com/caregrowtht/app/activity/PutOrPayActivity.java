package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.library.utils.U;
import com.android.library.view.WheelPopup;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.GridImageAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.PutPayEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.ThreadPoolManager;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.uitil.alicloud.oss.AliYunOss;
import com.caregrowtht.app.uitil.alicloud.oss.callback.ProgressCallback;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UploadModule;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.FullyGridLayoutManager;
import com.caregrowtht.app.view.xrecyclerview.ItemOffsetDecoration;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.white.progressview.CircleProgressView;
import com.zxy.tiny.Tiny;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ?????????
 * ?????????
 */
public class PutOrPayActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_sum_money)
    EditText etSumMoney;
    @BindView(R.id.tv_deal_date)
    TextView tvDealDate;
    @BindView(R.id.et_detail)
    EditText etDetail;
    @BindView(R.id.tv_teacher_all)
    TextView tvTeacherAll;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.circle_progress_fill_in)
    CircleProgressView mCircleProgress;

    private GridImageAdapter adapter;
    private int maxSelectNum = 1;
    private int themeId;
    AliYunOss mOssClient;

    ArrayList<StudentEntity> officialData = new ArrayList<>();//????????????
    private String type;
    private String orgId, sumMoney, dealType, agentManId;
    private PutPayEntity.TableData tableData;
    private boolean isSys = false, isSelect = true;// ???????????????????????? / ?????????????????????????????????

    @Override
    public int getLayoutId() {
        return R.layout.activity_put_or_pay;
    }

    @Override
    public void initView() {
        type = getIntent().getStringExtra("type");//1???????????? 2????????????
        String titleName = "";
        if (type.equals("1")) {
            titleName = "?????????";
            dealType = "4";
            tvDealDate.setText("?????????");
        } else if (type.equals("2")) {
            titleName = "?????????";
            dealType = "7";
            tvDealDate.setText("??????????????????");
        }
        tvTitle.setText(titleName);

        themeId = R.style.picture_white_style;

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(5, 5));
        adapter.setOnItemClickListener((position, v) -> {
            if (adapter.selectList.size() > 0) {
                LocalMedia media = adapter.selectList.get(position);
                String pictureType = media.getPictureType();
                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                switch (mediaType) {
                    case 1:
                        // ???????????? ???????????????????????????
                        //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                        PictureSelector.create(this).themeStyle(themeId).openExternalPreview(position, adapter.selectList);
                        break;
                    case 2:
                        // ????????????
                        PictureSelector.create(this).externalPictureVideo(media.getPath());
                        break;
                    case 3:
                        // ????????????
                        PictureSelector.create(this).externalPictureAudio(media.getPath());
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        tableData = (PutPayEntity.TableData) getIntent().getSerializableExtra("tableData");
        adapter.setData(tableData);
        if (StrUtils.isNotEmpty(tableData)) {// ???????????????
            String titleName = "";
            if (type.equals("1")) {
                titleName = "??????????????????";
            } else if (type.equals("2")) {
                titleName = "??????????????????";
            }
            tvTitle.setText(titleName);
            etSumMoney.setText(tableData.getPrice2());
            dealType = tableData.getType();
            tvDealDate.setText(tableData.getTradeType());
            etDetail.setText(StrUtils.parseEmpty(tableData.getDetail()));
            agentManId = tableData.getAgentManId();
            tvTeacherAll.setText(tableData.getAgentManName());
            etRemark.setText(tableData.getRemark());
            String path = tableData.getVoucher();
            isSys = tableData.getIsInits().equals("??????") || tableData.getIsInits().equals("?????????");
            etSumMoney.setFocusable(!isSys);
            tvDealDate.setClickable(!isSys);
            tvTeacherAll.setClickable(!isSys);
            if (StrUtils.isNotEmpty(path)) {
                //??????????????????
                adapter.pngOravis.add(path);
                UploadModule uploadModule = new UploadModule();
                uploadModule.setPicPath(path);
                uploadModule.setPictureType("image");
                adapter.uploadModules.add(uploadModule);
            }
        }
        orgId = UserManager.getInstance().getOrgId();
        mOssClient = AliYunOss.getInstance(this);
        getOrgTeachers("1");
    }

    /**
     * 48.?????????????????????
     *
     * @param status ??????????????? 1????????? 2????????????
     */
    private void getOrgTeachers(String status) {
        HttpManager.getInstance().doGetOrgTeachers("PutOrPayActivity",
                orgId, status, "1", new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (TextUtils.equals("1", status)) {//??????
                            officialData.clear();
                            officialData.addAll(data.getData());
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("PutOrPayActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(PutOrPayActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("PutOrPayActivity throwable", throwable.getMessage());
                    }
                });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // ????????????
            PictureSelector.create(PutOrPayActivity.this)
                    .openGallery(PictureMimeType.ofImage())// ?????????????????????????????????
                    .theme(themeId)// ??????????????????
                    .maxSelectNum(maxSelectNum)// ????????????????????????
                    .minSelectNum(1)// ??????????????????
                    .imageSpanCount(3)// ??????????????????
                    .selectionMode(PictureConfig.MULTIPLE)// ??????
                    .previewImage(true)// ?????????????????????
                    .previewVideo(true)// ?????????????????????
                    .enablePreviewAudio(true) // ?????????????????????
                    .isCamera(true)// ????????????????????????
                    .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                    .enableCrop(false)// ????????????
                    .compress(false)// ????????????
                    .synOrAsy(true)//??????true?????????false ?????? ????????????
                    //.compressSavePath(getPath())//????????????????????????
                    //.sizeMultiplier(0.5f)// glide ?????????????????? 0~1?????? ????????? .glideOverride()??????
                    .glideOverride(160, 160)// glide ???????????????????????????????????????????????????????????????????????????????????????
                    .isGif(true)// ????????????gif??????
                    .openClickSound(true)// ????????????????????????
                    .selectionMedia(adapter.selectList)// ????????????????????????
                    .previewEggs(true)// ??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                    .minimumCompressSize(100)// ??????100kb??????????????????
                    .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    this.isSelect = false;
                    // ????????????????????????
                    adapter.pngOravis.clear();
                    adapter.uploadModules.clear();
                    adapter.selectList = PictureSelector.obtainMultipleResult(data);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();//????????????????????????
                    for (int i = 0; i < adapter.selectList.size(); i++) { // ?????????
                        int finalI = i;//????????????????????????
                        Runnable syncRunnable = () -> {
                            LocalMedia media = adapter.selectList.get(finalI);
                            String path = StrUtils.isEmpty(media.getCompressPath()) ? media.getPath() :
                                    media.getCompressPath();
                            String pictureType = media.getPictureType();
                            String curTime = TimeUtils.getCurTimeLong("yyyyMMddHHmmss") + "/" + System.currentTimeMillis() + finalI;
                            String mImageName = "";
                            if (pictureType.equalsIgnoreCase("image/png") || pictureType.equalsIgnoreCase("image/jpeg")) {
                                mImageName = Constant.picture + curTime + ".jpg";
                            } else if (pictureType.equalsIgnoreCase("video/mp4")) {
                                mImageName = Constant.video + curTime + ".mp4";
                            }
                            LogUtils.d("PutOrPayActivity", path + "===" + mImageName);
                            //??????????????????
                            readyUpload(path, pictureType, mImageName);
                        };
                        executorService.execute(syncRunnable);
                    }
                    break;
            }
            adapter.notifyDataSetChanged();
        }

    }

    //??????????????????
    private void readyUpload(String path, String pictureType, String mImageName) {
        adapter.pngOravis.add(Constant.OSS_URL + mImageName);
        UploadModule uploadModule = new UploadModule();
        uploadModule.setPicPath(path);
        uploadModule.setPictureType(pictureType);
        uploadModule.setUploadObject(mImageName);
        adapter.uploadModules.add(uploadModule);
    }

    @OnClick({R.id.rl_back_button, R.id.btn_cancel, R.id.tv_deal_date, R.id.tv_teacher_all, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.tv_deal_date:
                hideKeyboard();
                selectDealType();
                break;
            case R.id.tv_teacher_all:
                hideKeyboard();
                selectOperator();
                break;
            case R.id.btn_submit:
                if (validation()) {
                    btnSubmit.setEnabled(false);
                    mCircleProgress.setVisibility(View.VISIBLE);
                    rlBackButton.setClickable(false);
                    isBackPressed = false;
                    if (StrUtils.isNotEmpty(tableData) && isSelect) {// ???????????????
                        String[] voucher = tableData.getVoucher().split("#");
                        if (StrUtils.isNotEmpty(voucher[0])) {
                            for (int i = 0; i < voucher.length; i++) {
                                adapter.uploadModules.remove(i);
                            }
                        }
                    }
                    if (adapter.uploadModules.size() > 0) {// ?????????,???????????????
                        Thread thread = new Thread() {
                            @Override
                            public synchronized void run() {
                                super.run();//?????????
                                for (UploadModule media : adapter.uploadModules) { // ?????????
                                    LogUtils.e("CourserReleaseActivity",
                                            "----->??????:" + media.getUploadObject());
                                    uploadManage(media.getPicPath(), media.getPictureType(), media.getUploadObject());
                                }
                            }
                        };
                        ThreadPoolManager.getInstance().execute(new FutureTask<>(thread, null), null);
                    } else {
                        if (StrUtils.isEmpty(tableData)) {
                            saveBillRecord();
                        } else {
                            // ???????????????
                            editBillRecordSave();
                        }
                    }
                }
                break;
        }
    }

    private void editBillRecordSave() {
        GetMoneyData getMoneyData = new GetMoneyData().invoke();
        String price = getMoneyData.getPrice();
        StringBuilder pngOravis = getMoneyData.getPngOravis();
        String remark = getMoneyData.getRemark();
        String detail = getMoneyData.getDetail();
        HttpManager.getInstance().doEditBillRecordSave("PutOrPayActivity",
                tableData.getRechargeId(), orgId, price, dealType, agentManId, detail,
                pngOravis.toString(), remark, new HttpCallBack<BaseDataModel<PutPayEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<PutPayEntity> data) {
                        LogUtils.d("PutOrPayActivity", "onSuccess");
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_PUTPAY, true));// obj ?????????
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("PutOrPayActivity onFail", statusCode + ":" + errorMsg);
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        rlBackButton.setClickable(true);
                        isBackPressed = true;
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(PutOrPayActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("PutOrPayActivity onError", throwable.getMessage());
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        rlBackButton.setClickable(true);
                        isBackPressed = true;
                    }
                });
    }

    private void saveBillRecord() {
        GetMoneyData getMoneyData = new GetMoneyData().invoke();
        String price = getMoneyData.getPrice();
        StringBuilder pngOravis = getMoneyData.getPngOravis();
        String remark = getMoneyData.getRemark();
        String detail = getMoneyData.getDetail();
        HttpManager.getInstance().doSaveBillRecord("PutOrPayActivity",
                orgId, price, dealType, agentManId, detail, pngOravis.toString(), remark,
                new HttpCallBack<BaseDataModel<PutPayEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<PutPayEntity> data) {
                        LogUtils.d("PutOrPayActivity", "onSuccess");
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_PUTPAY));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("PutOrPayActivity onFail", statusCode + ":" + errorMsg);
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        rlBackButton.setClickable(true);
                        isBackPressed = true;
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(PutOrPayActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("PutOrPayActivity onError", throwable.getMessage());
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        rlBackButton.setClickable(true);
                        isBackPressed = true;
                    }
                });
    }

    private boolean validation() {
        //????????????
        sumMoney = etSumMoney.getText().toString();
        if (StrUtils.isEmpty(sumMoney)) {
            U.showToast("??????????????????");
            return false;
        }
        String detail = etDetail.getText().toString();
        if (StrUtils.isEmpty(detail)) {
            U.showToast("???????????????");
            return false;
        }
//        if (StrUtils.isEmpty(agentManId)) {
//            U.showToast("??????????????????");
//            return false;
//        }
        /*if (adapter.selectList.size() == 0) {
            U.showToast("???????????????");
            return false;
        }*/
        return true;
    }

    /**
     * ??????????????????
     */
    private void selectDealType() {
        // 1??????????????? 2????????????????????? 3??????????????? 4??????????????? 5????????????????????? 6??????????????? 7.???????????????????????? 8.???????????? 9.????????????
        String[] arr = new String[0];
        if (type.equals("1")) {
            arr = Constant.putArr;
        } else if (type.equals("2")) {
            arr = Constant.payArr;
        }
        WheelPopup pop = new WheelPopup(PutOrPayActivity.this, arr);
        pop.showAtLocation(View.inflate(PutOrPayActivity.this,
                R.layout.popup_wheel_select, null),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            if (type.equals("1")) {
                switch (position) {
                    case 0:// 4???????????????
                        dealType = "4";
                        break;
                    case 1:// 5?????????????????????
                        dealType = "5";
                        break;
                    case 2:// 6???????????????
                        dealType = "6";
                        break;
                }
            } else if (type.equals("2")) {
                switch (position) {
                    case 0:// 7.????????????????????????
                        dealType = "7";
                        break;
                    case 1:// 8.????????????
                        dealType = "8";
                        break;
                    case 2:// 9.????????????
                        dealType = "9";
                        break;
                }
            }
            tvDealDate.setText(argValue);
            return null;
        });
    }

    /**
     * ???????????????
     */
    private void selectOperator() {
        String[] arr = new String[officialData.size()];
        for (int i = 0; i < officialData.size(); i++) {
            arr[i] = officialData.get(i).getUserName();
        }
        WheelPopup pop = new WheelPopup(PutOrPayActivity.this, arr);
        pop.showAtLocation(View.inflate(PutOrPayActivity.this,
                R.layout.popup_wheel_select, null),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            agentManId = officialData.get(position).getUserId();
            tvTeacherAll.setText(argValue);
            return null;
        });
    }

    int position = 0;
    float index = 0;
    boolean isProgress = true;
    boolean isBackPressed = true;// ?????????????????????????????????

    public ProgressCallback<PutObjectRequest, PutObjectResult> getRrogressCallback() {
        return new ProgressCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtils.e("CourserReleaseActivity", "---onProgress:---" + (int) (currentSize * 100) / totalSize);
                mCircleProgress.setProgress((int) ((currentSize * 100) / totalSize));
            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtils.e("CourserReleaseActivity", "---onSuccess:---");
                position++;
                //??????????????????????????????????????????
                if (adapter.uploadModules.size() == position) {
                    LogUtils.e("CourserReleaseActivity",
                            "----->??????:" + adapter.pngOravis.toString());
                    runOnUiThread(() -> {
                        if (StrUtils.isEmpty(tableData)) {
                            saveBillRecord();
                        } else {
                            // ???????????????
                            editBillRecordSave();
                        }
                    });//?????????
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException
                    clientException, ServiceException serviceException) {
                LogUtils.e("CourserReleaseActivity", "---onFailure:---");
            }
        };
    }


    private void uploadManage(String picPath, String pictureType, String uploadName) {
        if (pictureType.equalsIgnoreCase("image/png")
                || pictureType.equalsIgnoreCase("image/jpeg")) {
            isProgress = true;

            float numb = (float) (Math.random() * 100);// 100??????????????????
            int itemNum = 3;//?????????????????????
            float totalNumb = numb * itemNum;
            index = (float) (Math.round(totalNumb * 100) / 100);//??????????????????????????????4??????*10000??????/10000

            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(picPath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
                //return the compressed file path
                mOssClient.upload(uploadName, outfile, getRrogressCallback());
                isProgress = false;
                mCircleProgress.setProgress(100);
            });
            while (isProgress) {// ???????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mCircleProgress.setProgress((int) ++index);
            }
        } else if (pictureType.equalsIgnoreCase("video/mp4")) {

        } else {
            mOssClient.upload(uploadName, picPath, getRrogressCallback());
        }
    }

    @Override
    public void onBackPressed() {
        if (!isCosumenBackKey()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean isCosumenBackKey() {
        // ????????????????????????????????????????????????????????????????????????true???????????????false,????????????????????????back???????????????????????????
        return isBackPressed;
    }

    private class GetMoneyData {
        private String price;
        private StringBuilder pngOravis;
        private String remark, detail;

        public String getPrice() {
            return price;
        }

        public StringBuilder getPngOravis() {
            return pngOravis;
        }

        public String getRemark() {
            return remark;
        }

        public String getDetail() {
            return detail;
        }

        public GetMoneyData invoke() {
            price = sumMoney;
            pngOravis = new StringBuilder();
            boolean isExcut = false;
            for (String url : adapter.pngOravis) {
                if (isExcut) {
                    pngOravis.append("#");
                }
                pngOravis.append(url);
                isExcut = true;
            }
            remark = etRemark.getText().toString();
            detail = etDetail.getText().toString();
            return this;
        }
    }
}
