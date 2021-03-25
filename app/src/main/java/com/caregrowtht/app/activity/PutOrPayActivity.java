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
 * 收一笔
 * 支一笔
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

    ArrayList<StudentEntity> officialData = new ArrayList<>();//正式教师
    private String type;
    private String orgId, sumMoney, dealType, agentManId;
    private PutPayEntity.TableData tableData;
    private boolean isSys = false, isSelect = true;// 是否是系统生成的 / 编辑是否重新选择了凭证

    @Override
    public int getLayoutId() {
        return R.layout.activity_put_or_pay;
    }

    @Override
    public void initView() {
        type = getIntent().getStringExtra("type");//1：收一笔 2：支一笔
        String titleName = "";
        if (type.equals("1")) {
            titleName = "收一笔";
            dealType = "4";
            tvDealDate.setText("试听课");
        } else if (type.equals("2")) {
            titleName = "支一笔";
            dealType = "7";
            tvDealDate.setText("采购教学用品");
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
                        // 预览图片 可自定长按保存路径
                        //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                        PictureSelector.create(this).themeStyle(themeId).openExternalPreview(position, adapter.selectList);
                        break;
                    case 2:
                        // 预览视频
                        PictureSelector.create(this).externalPictureVideo(media.getPath());
                        break;
                    case 3:
                        // 预览音频
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
        if (StrUtils.isNotEmpty(tableData)) {// 编辑收与支
            String titleName = "";
            if (type.equals("1")) {
                titleName = "编辑收入记录";
            } else if (type.equals("2")) {
                titleName = "编辑支出记录";
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
            isSys = tableData.getIsInits().equals("系统") || tableData.getIsInits().equals("初始化");
            etSumMoney.setFocusable(!isSys);
            tvDealDate.setClickable(!isSys);
            tvTeacherAll.setClickable(!isSys);
            if (StrUtils.isNotEmpty(path)) {
                //准备上传工作
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
     * 48.获取机构的教师
     *
     * @param status 教师的状态 1：正式 2：待审核
     */
    private void getOrgTeachers(String status) {
        HttpManager.getInstance().doGetOrgTeachers("PutOrPayActivity",
                orgId, status, "1", new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (TextUtils.equals("1", status)) {//正式
                            officialData.clear();
                            officialData.addAll(data.getData());
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("PutOrPayActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
            // 进入相册
            PictureSelector.create(PutOrPayActivity.this)
                    .openGallery(PictureMimeType.ofImage())// 全部、图片、视频、音频
                    .theme(themeId)// 主题样式设置
                    .maxSelectNum(maxSelectNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(3)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .enableCrop(false)// 是否裁剪
                    .compress(false)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .isGif(true)// 是否显示gif图片
                    .openClickSound(true)// 是否开启点击声音
                    .selectionMedia(adapter.selectList)// 是否传入已选图片
                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    this.isSelect = false;
                    // 图片选择结果回调
                    adapter.pngOravis.clear();
                    adapter.uploadModules.clear();
                    adapter.selectList = PictureSelector.obtainMultipleResult(data);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();//避免文件路径重复
                    for (int i = 0; i < adapter.selectList.size(); i++) { // 高并发
                        int finalI = i;//避免文件名称覆盖
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
                            //准备上传工作
                            readyUpload(path, pictureType, mImageName);
                        };
                        executorService.execute(syncRunnable);
                    }
                    break;
            }
            adapter.notifyDataSetChanged();
        }

    }

    //准备上传参数
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
                    if (StrUtils.isNotEmpty(tableData) && isSelect) {// 编辑收与支
                        String[] voucher = tableData.getVoucher().split("#");
                        if (StrUtils.isNotEmpty(voucher[0])) {
                            for (int i = 0; i < voucher.length; i++) {
                                adapter.uploadModules.remove(i);
                            }
                        }
                    }
                    if (adapter.uploadModules.size() > 0) {// 有图片,视频或附件
                        Thread thread = new Thread() {
                            @Override
                            public synchronized void run() {
                                super.run();//子线程
                                for (UploadModule media : adapter.uploadModules) { // 高并发
                                    LogUtils.e("CourserReleaseActivity",
                                            "----->路径:" + media.getUploadObject());
                                    uploadManage(media.getPicPath(), media.getPictureType(), media.getUploadObject());
                                }
                            }
                        };
                        ThreadPoolManager.getInstance().execute(new FutureTask<>(thread, null), null);
                    } else {
                        if (StrUtils.isEmpty(tableData)) {
                            saveBillRecord();
                        } else {
                            // 编辑收与支
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
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_PUTPAY, true));// obj 是编辑
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("PutOrPayActivity onFail", statusCode + ":" + errorMsg);
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        rlBackButton.setClickable(true);
                        isBackPressed = true;
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
        //判断条件
        sumMoney = etSumMoney.getText().toString();
        if (StrUtils.isEmpty(sumMoney)) {
            U.showToast("金额不能为空");
            return false;
        }
        String detail = etDetail.getText().toString();
        if (StrUtils.isEmpty(detail)) {
            U.showToast("请输入详情");
            return false;
        }
//        if (StrUtils.isEmpty(agentManId)) {
//            U.showToast("请选择经办人");
//            return false;
//        }
        /*if (adapter.selectList.size() == 0) {
            U.showToast("请选择图片");
            return false;
        }*/
        return true;
    }

    /**
     * 选择交易类型
     */
    private void selectDealType() {
        // 1：购买收入 2：充值续费收入 3：退费支出 4：试听收入 5：商品销售收入 6：其他收入 7.采购教学用品支出 8.杂费支出 9.其他支出
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
                    case 0:// 4：试听收入
                        dealType = "4";
                        break;
                    case 1:// 5：商品销售收入
                        dealType = "5";
                        break;
                    case 2:// 6：其他收入
                        dealType = "6";
                        break;
                }
            } else if (type.equals("2")) {
                switch (position) {
                    case 0:// 7.采购教学用品支出
                        dealType = "7";
                        break;
                    case 1:// 8.杂费支出
                        dealType = "8";
                        break;
                    case 2:// 9.其他支出
                        dealType = "9";
                        break;
                }
            }
            tvDealDate.setText(argValue);
            return null;
        });
    }

    /**
     * 选择经办人
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
    boolean isBackPressed = true;// 控制返回键是否能受控制

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
                //全部上传完成后提交接口并刷新
                if (adapter.uploadModules.size() == position) {
                    LogUtils.e("CourserReleaseActivity",
                            "----->上传:" + adapter.pngOravis.toString());
                    runOnUiThread(() -> {
                        if (StrUtils.isEmpty(tableData)) {
                            saveBillRecord();
                        } else {
                            // 编辑收与支
                            editBillRecordSave();
                        }
                    });//主线程
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

            float numb = (float) (Math.random() * 100);// 100以内的随机数
            int itemNum = 3;//小数点前的位数
            float totalNumb = numb * itemNum;
            index = (float) (Math.round(totalNumb * 100) / 100);//如果要求精确小数点前4位就*10000然后/10000

            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(picPath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
                //return the compressed file path
                mOssClient.upload(uploadName, outfile, getRrogressCallback());
                isProgress = false;
                mCircleProgress.setProgress(100);
            });
            while (isProgress) {// 图片进度条
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
        // 这儿做返回键的控制，如果自己处理返回键逻辑就返回true，如果返回false,代表继续向下传递back事件，由系统去控制
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
