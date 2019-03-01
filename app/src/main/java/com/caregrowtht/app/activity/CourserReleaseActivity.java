package com.caregrowtht.app.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.library.utils.SystemUtils;
import com.android.library.utils.U;
import com.android.library.view.MyGridView;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourserReleaseAdapter;
import com.caregrowtht.app.adapter.StudentCardAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.FilePickerUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.ThreadPoolManager;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.uitil.alicloud.oss.AliYunOss;
import com.caregrowtht.app.uitil.alicloud.oss.callback.ProgressCallback;
import com.caregrowtht.app.uitil.videocompress.VideoCompress;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UploadModule;
import com.caregrowtht.app.user.UserManager;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.white.progressview.CircleProgressView;
import com.zxy.tiny.Tiny;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-7-11 17:34:24
 * 发布课程反馈
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class CourserReleaseActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_course_intro)
    EditText etCourseIntro;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.iv_file)
    ImageView ivFile;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;
    @BindView(R.id.ll_atter)
    LinearLayout llAtter;
    @BindView(R.id.tv_select_stu)
    CheckBox tvSelectStu;
    @BindView(R.id.gv_tch)
    MyGridView gvTch;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.circle_progress_fill_in)
    CircleProgressView mCircleProgress;

    private String courseId;
    private StudentCardAdapter couStuAdapter;
    List<StudentEntity> studentList = new ArrayList<>();

    private int themeId;
    private List<LocalMedia> selectList = new ArrayList<>();
    private CourserReleaseAdapter courReleAdapter;
    private String content;

    AliYunOss mOssClient;
    private StringBuffer studentIds = new StringBuffer();

    String destPath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_courser_release;
    }

    @Override
    public void initView() {
        tvTitle.setText("发布课程反馈");

        initRecyclerView(recyclerView, false);

        themeId = R.style.picture_white_style;
        tvSelectStu.setSelected(true);
        tvSelectStu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvSelectStu.setSelected(isChecked);
            couStuAdapter.isAll = isChecked;
            couStuAdapter.initDate();
            couStuAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void initData() {
        CourseEntity courseData = (CourseEntity) getIntent().getSerializableExtra("courseData");
        courseId = courseData.getCourseId();

        courReleAdapter = new CourserReleaseAdapter(new ArrayList(), this);
        recyclerView.setAdapter(courReleAdapter);

        couStuAdapter = new StudentCardAdapter(this, R.layout.item_stu_situat, studentList, null);
        gvTch.setAdapter(couStuAdapter);
        mOssClient = AliYunOss.getInstance(this);
        lessonChild();
    }

    private void lessonChild() {
        //5. 获取参与课程的学员
        HttpManager.getInstance().doLessonChild("PendingActivity", courseId,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        studentList.clear();
                        studentList.addAll(data.getData());
                        couStuAdapter.setData(data.getData(), tvSelectStu);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserReleaseActivity.this);
                        }  //  U.showToast(errorMsg);//不能打开
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.iv_image, R.id.iv_file, R.id.iv_video, R.id.iv_camera, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_image:
                openImageOr(PictureMimeType.ofImage());//选择图片
                break;
            case R.id.iv_file:
                openFile();//选择文件
                break;
            case R.id.iv_video:
                openImageOr(PictureMimeType.ofVideo());//选择视频
                break;
            case R.id.iv_camera:
                if (!selfPermissionGranted(Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(CourserReleaseActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 120);
                } else {
                    openCamera();//拍照
                }
                break;
            case R.id.btn_submit:
                if (!UserManager.getInstance().isTrueRole("zy_4")) {
                    U.showToast(getString(R.string.text_role));
                    break;
                } else {
                    btnSubmit.setEnabled(false);
                    int index = 0;
                    for (int i = 0; i < couStuAdapter.getStudentIds().size(); i++) {
                        if (!TextUtils.isEmpty(couStuAdapter.getStudentIds().get(i))) {
                            if (index > 0) {
                                studentIds.append(",");
                            }
                            studentIds.append(couStuAdapter.getStudentIds().get(i));
                            index++;
                        }
                    }
                    if (validation()) {
                        mCircleProgress.setVisibility(View.VISIBLE);
                        rlBackButton.setClickable(false);
                        isBackPressed = false;
                        if (courReleAdapter.uploadModules.size() > 0) {// 有图片,视频或附件
                            Thread thread = new Thread() {
                                @Override
                                public synchronized void run() {
                                    super.run();
                                    for (UploadModule media : courReleAdapter.uploadModules) { // 高并发
                                        LogUtils.e("CourserReleaseActivity",
                                                "----->路径:" + media.getUploadObject());
                                        uploadManage(media.getPicPath(), media.getPictureType(), media.getUploadObject());
                                    }
                                    //子线程
                                }
                            };
                            ThreadPoolManager.getInstance().execute(new FutureTask<>(thread, null), null);
                        } else {
                            getAddPerfomV2();
                        }
                    }
                }
                break;
        }
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
                if (courReleAdapter.uploadModules.size() == position) {
                    LogUtils.e("CourserReleaseActivity",
                            "----->上传:" + courReleAdapter.pngOravis.toString());
                    runOnUiThread(() -> getAddPerfomV2());//主线程
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
        if (pictureType.equalsIgnoreCase("image/png") || pictureType.equalsIgnoreCase("image/jpeg")) {
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
            final String destPath = FilePickerUtils.getInstance().getImageCachePath()
                    + File.separator + "VID_" + System.currentTimeMillis() + ".mp4";
            VideoCompress.compressVideoLow(picPath, destPath, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {
                    LogUtils.d("VideoCompress", "开始");
                }

                @Override
                public void onSuccess() {
                    LogUtils.d("VideoCompress", "结束");
                    mOssClient.upload(uploadName, destPath, getRrogressCallback());
                }

                @Override
                public void onFail() {
                    LogUtils.d("VideoCompress", "失败");
                }

                @Override
                public void onProgress(float percent) {
                    LogUtils.d("VideoCompress", String.valueOf(percent) + "%");
                    mCircleProgress.setProgress((int) (percent));
                }
            });
        } else {
            mOssClient.upload(uploadName, picPath, getRrogressCallback());
        }
    }

    private boolean validation() {
        if (StrUtils.isEmpty(courseId)) {
            U.showToast("courseId");
            btnSubmit.setEnabled(true);
            return false;
        }
        content = etCourseIntro.getText().toString().trim();
//        if (StrUtils.isEmpty(content)) {
//            U.showToast("写点什么吧");
//            btnSubmit.setEnabled(true);
//            return false;
//        }
        if (StrUtils.isEmpty(content) && courReleAdapter.uploadModules.size() == 0) {
            U.showToast("请编写文字或选择照片/视频/文件!");
            btnSubmit.setEnabled(true);
            return false;
        }
        String[] student = studentIds.toString().split(",");
        if (student.length > 0 && student[0].isEmpty()) {
            U.showToast("请选择要发送的学员!");
            btnSubmit.setEnabled(true);
            return false;
        }
        return true;
    }

    //创建相册选择器
    public PictureSelector getPicture() {
        return PictureSelector.create(this);
    }

    private void openImageOr(int type) {
        // 进入相册
        int maxSelectNum = 9;
        getPicture()
                .openGallery(type)// 图片.ofImage()、视频.ofVideo()、音频
                .theme(themeId)// 主题样式设置
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .isGif(true)// 是否显示gif图片
                .openClickSound(true)// 是否开启点击声音
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void openFile() {
        //选择文件
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType(“image/*”);//选择图片
        //intent.setType(“audio/*”); //选择音频
        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
        //intent.setType(“video/*;image/*”);//同时选择视频和图片
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
    }

    private void openCamera() {
        // 单独拍照
        destPath = FilePickerUtils.getInstance().getImageCachePath()
                + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
        LogUtils.d("CourserActivity === 开始拍照路径", destPath);
        SystemUtils.imageCapture(this, destPath, PictureConfig.CAMERA);
    }

    int i = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();//避免文件路径重复
                    for (int i = 0; i < selectList.size(); i++) { // 高并发
                        int finalI = i;//避免文件名称覆盖
                        Runnable syncRunnable = () -> {
                            LocalMedia media = selectList.get(finalI);
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
                            LogUtils.d("CourserReleaseActivity", path + "===" + mImageName);
                            //准备上传工作
                            readyUpload(path, pictureType, mImageName);
                        };
                        executorService.execute(syncRunnable);
                    }
                    break;
                case PictureConfig.CAMERA:
                    File file = new File(destPath);
                    LogUtils.d("CourserActivity === 压缩路径", destPath + ":" + Uri.fromFile(file).getPath());
                    String curTime = TimeUtils.getCurTimeLong("yyyyMMddHHmmss") + "/" + System.currentTimeMillis();
                    String mImageName = Constant.picture + curTime + ".jpg";
                    //准备上传工作
                    readyUpload(Uri.fromFile(file).getPath(), "image/jpeg", mImageName);
                    break;
                case Constant.REQUEST_CODE_PICK_FILE:
                    Uri uri = data.getData();
                    String path;
                    if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                        path = uri.getPath();
                    } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                        path = FilePickerUtils.getInstance().getPath(this, uri);
                    } else {//4.4以下下系统调用方法
                        path = FilePickerUtils.getInstance().getRealPathFromURI(this, uri);
                    }
                    if (path != null) {
                        String pictureType = path.substring(path.lastIndexOf("."));
                        //截取最后一级文件及目录、并去除特殊字符或替换为英文标号
                        String fileName = U.stringFilter(path.substring(path.lastIndexOf("/")));
                        String mFileName = Constant.accessory + TimeUtils.getCurTimeLong("yyyyMMddHHmmss") + "/"
                                + System.currentTimeMillis() + "_" + UserManager.getInstance().userData.getUid() + fileName;
                        LogUtils.d("CourserReleaseActivity", path + "===" + mFileName);
                        //准备上传工作
                        readyUpload(path, pictureType, mFileName);
                    }
                    break;
            }
            courReleAdapter.notifyDataSetChanged();
        }
    }

    //准备上传参数
    private void readyUpload(String path, String pictureType, String mImageName) {
        courReleAdapter.pngOravis.add(Constant.OSS_URL + mImageName);
        UploadModule uploadModule = new UploadModule();
        uploadModule.setPicPath(path);
        uploadModule.setPictureType(pictureType);
        uploadModule.setUploadObject(mImageName);
        courReleAdapter.uploadModules.add(uploadModule);
    }

    private void getAddPerfomV2() {
        StringBuilder pngOravis = new StringBuilder();
        boolean isExcut = false;
        for (String url : courReleAdapter.pngOravis) {
            if (isExcut) {
                pngOravis.append("#");
            }
            pngOravis.append(url);
            isExcut = true;
        }
        //haoruigang on 2018-7-20 17:05:52 19.发布内容
        HttpManager.getInstance().doAddPerfomV2("CourserReleaseActivity",
                courseId, pngOravis.toString(), content, studentIds.toString(),
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        LogUtils.d("CourserReleaseActivity", "onSuccess");
                        btnSubmit.setEnabled(true);
                        mCircleProgress.setVisibility(View.GONE);
                        U.showToast("发布成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.COURSE));
                        startActivity(new Intent(CourserReleaseActivity.this, CourserActivity.class));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserReleaseActivity onFail", statusCode + ":" + errorMsg);
                        btnSubmit.setEnabled(true);
                        mCircleProgress.setVisibility(View.GONE);
                        rlBackButton.setClickable(true);
                        isBackPressed = true;
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserReleaseActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserReleaseActivity onError", throwable.getMessage());
                        btnSubmit.setEnabled(true);
                        mCircleProgress.setVisibility(View.GONE);
                        rlBackButton.setClickable(true);
                        isBackPressed = true;
                    }
                });
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

}
