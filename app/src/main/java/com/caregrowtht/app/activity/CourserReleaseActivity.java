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
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourserReleaseAdapter;
import com.caregrowtht.app.adapter.StudentCardAdapter;
import com.caregrowtht.app.adapter.StudentSituatAdapter;
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
import java.util.Set;
import java.util.TreeSet;
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
 * ??????????????????
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
    @BindView(R.id.rv_stu_situat)
    RecyclerView rvStuSituat;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.circle_progress_fill_in)
    CircleProgressView mCircleProgress;

    private String courseId;
    private StudentSituatAdapter studentSitAdapter;
    private ArrayList<StudentEntity> studentList = new ArrayList<>();

    private int themeId;
    private List<LocalMedia> selectList = new ArrayList<>();
    private CourserReleaseAdapter courReleAdapter;
    private String content;

    AliYunOss mOssClient;
    private StringBuffer studentIds = new StringBuffer();
    private List<StudentCardAdapter> stuCardAdapter = new ArrayList<>();
    private List<CheckBox> tvSelectCourse = new ArrayList<>();
    String destPath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_courser_release;
    }

    @Override
    public void initView() {
        tvTitle.setText("??????????????????");

        initRecyclerView(recyclerView, false);

        themeId = R.style.picture_white_style;
        tvSelectStu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvSelectStu.setSelected(isChecked);
            studentSitAdapter.isExt = false;// ????????????????????????????????????
            for (int i = 0; i < stuCardAdapter.size(); i++) {
                StudentCardAdapter adapter = stuCardAdapter.get(i);
                adapter.isAll = isChecked;
                adapter.setData(tvSelectCourse.get(i));
                adapter.checkAll();
            }
        });
    }

    @Override
    public void initData() {
        CourseEntity courseData = (CourseEntity) getIntent().getSerializableExtra("courseData");
        courseId = courseData.getCourseId();

        courReleAdapter = new CourserReleaseAdapter(new ArrayList(), this);
        recyclerView.setAdapter(courReleAdapter);

        initRecyclerView(rvStuSituat, true);
        studentSitAdapter = new StudentSituatAdapter(studentList, this, null, onStuCardClick);
        rvStuSituat.setAdapter(studentSitAdapter);

        mOssClient = AliYunOss.getInstance(this);
        lessonChild();
    }

    public StudentSituatAdapter.OnStuCardClick onStuCardClick = (stuCardAdapter, tvSelectCourse) -> {
        this.stuCardAdapter.add(stuCardAdapter);
        this.tvSelectCourse.add(tvSelectCourse);
        stuCardAdapter.setData(tvSelectCourse);
        tvSelectCourse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) return;// ?????????????????????
            studentSitAdapter.isExt = false;// ????????????????????????????????????
            tvSelectCourse.setSelected(isChecked);
            stuCardAdapter.isAll = isChecked;
            stuCardAdapter.setData(tvSelectCourse);
        });
    };

    private void lessonChild() {
        //5. ???????????????????????????
        HttpManager.getInstance().doLessonChild("PendingActivity", courseId,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        studentList.clear();
                        studentList.addAll(data.getData());
                        ArrayList<StudentEntity> stuStatus = removeDuplicateOrder(studentList);//?????????????????????
                        if (stuStatus.size() > 1 && stuStatus.get(0).getStatus().equals("1")
                                && stuStatus.get(1).getStatus().equals("2")) {
                            // ??????(??????1???2??????)
                            StudentEntity stu = stuStatus.get(0);
                            stuStatus.set(0, stuStatus.get(1));
                            stuStatus.set(1, stu);
                        }
                        studentSitAdapter.setData(stuStatus, studentList);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserReleaseActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    /**
     * ??????status??????
     *
     * @param orderList
     * @return
     * @author haoruigang
     */
    private static ArrayList<StudentEntity> removeDuplicateOrder
    (List<StudentEntity> orderList) {
        Set<StudentEntity> set = new TreeSet<>((a, b) -> {
            // ??????????????????asicc???????????????
            return a.getStatus().compareTo(b.getStatus());
        });
        set.addAll(orderList);
        return new ArrayList<>(set);
    }

    @OnClick({R.id.rl_back_button, R.id.iv_image, R.id.iv_file, R.id.iv_video, R.id.iv_camera, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_image:
                if (CourserReleaseAdapter.img == 9 || CourserReleaseAdapter.img > 9) {
                    U.showToast("?????????????????????????????????9???");
                    return;
                }
                openImageOr(PictureMimeType.ofImage());//????????????
                break;
            case R.id.iv_file:
                openFile();//????????????
                break;
            case R.id.iv_video:
                if (CourserReleaseAdapter.img == 9 || CourserReleaseAdapter.img > 9) {
                    U.showToast("?????????????????????????????????9???");
                    return;
                }
                openImageOr(PictureMimeType.ofVideo());//????????????
                break;
            case R.id.iv_camera:
                if (!selfPermissionGranted(Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(CourserReleaseActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 120);
                } else {
                    if (CourserReleaseAdapter.img == 9 || CourserReleaseAdapter.img > 9) {
                        U.showToast("?????????????????????????????????9???");
                        return;
                    }
                    openCamera();//??????
                }
                break;
            case R.id.btn_submit:
                if (!UserManager.getInstance().isTrueRole("zy_4")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    btnSubmit.setClickable(false);
                    int index = 0;
                    for (StudentCardAdapter adapter : stuCardAdapter) {
                        for (int i = 0; i < adapter.getStudentIds().size(); i++) {
                            if (!TextUtils.isEmpty(adapter.getStudentIds().get(i))) {
                                if (index > 0) {
                                    studentIds.append(",");
                                }
                                studentIds.append(adapter.getStudentIds().get(i));
                                index++;
                            }
                        }
                    }
                    if (validation()) {
                        mCircleProgress.setVisibility(View.VISIBLE);
                        rlBackButton.setClickable(false);
                        isBackPressed = false;
                        if (courReleAdapter.uploadModules.size() > 0) {// ?????????,???????????????
                            Thread thread = new Thread() {
                                @Override
                                public synchronized void run() {
                                    super.run();
                                    for (UploadModule media : courReleAdapter.uploadModules) { // ?????????
                                        LogUtils.e("CourserReleaseActivity",
                                                "----->??????:" + media.getUploadObject());
                                        uploadManage(media.getPicPath(), media.getPictureType(), media.getUploadObject());
                                    }
                                    //?????????
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
                if (courReleAdapter.uploadModules.size() == position) {
                    LogUtils.e("CourserReleaseActivity",
                            "----->??????:" + courReleAdapter.pngOravis.toString());
                    runOnUiThread(() -> getAddPerfomV2());//?????????
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
            final String destPath = FilePickerUtils.getInstance().getImageCachePath()
                    + File.separator + "VID_" + System.currentTimeMillis() + ".mp4";
            VideoCompress.compressVideoLow(picPath, destPath, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {
                    LogUtils.d("VideoCompress", "??????");
                }

                @Override
                public void onSuccess() {
                    LogUtils.d("VideoCompress", "??????");
                    mOssClient.upload(uploadName, destPath, getRrogressCallback());
                }

                @Override
                public void onFail() {
                    LogUtils.d("VideoCompress", "??????");
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
            btnSubmit.setClickable(true);
            return false;
        }
        content = etCourseIntro.getText().toString().trim();
//        if (StrUtils.isEmpty(content)) {
//            U.showToast("???????????????");
//            btnSubmit.setClickable(true);
//            return false;
//        }
        if (StrUtils.isEmpty(content) && courReleAdapter.uploadModules.size() == 0) {
            U.showToast("??????????????????????????????/??????/??????!");
            btnSubmit.setClickable(true);
            return false;
        }
        String[] student = studentIds.toString().split(",");
        if (student.length > 0 && student[0].isEmpty()) {
            U.showToast("???????????????????????????!");
            btnSubmit.setClickable(true);
            return false;
        }
        return true;
    }

    //?????????????????????
    public PictureSelector getPicture() {
        return PictureSelector.create(this);
    }

    private void openImageOr(int type) {
        // ????????????
        int maxSelectNum = 9 - CourserReleaseAdapter.img;
        getPicture()
                .openGallery(type)// ??????.ofImage()?????????.ofVideo()?????????
                .theme(themeId)// ??????????????????
                .maxSelectNum(maxSelectNum)// ????????????????????????
                .minSelectNum(1)// ??????????????????
                .imageSpanCount(4)// ??????????????????
                .selectionMode(PictureConfig.MULTIPLE)// ??????
                .previewImage(true)// ?????????????????????
                .previewVideo(true)// ?????????????????????
                .isCamera(false)// ????????????????????????
                .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                .compress(true)// ????????????
                .synOrAsy(true)//??????true?????????false ?????? ????????????
                .glideOverride(160, 160)// glide ???????????????????????????????????????????????????????????????????????????????????????
                .isGif(true)// ????????????gif??????
                .openClickSound(true)// ????????????????????????
                .previewEggs(true)// ??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
    }

    private void openFile() {
        //????????????
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType(???image/*???);//????????????
        //intent.setType(???audio/*???); //????????????
        //intent.setType(???video/*???); //???????????? ???mp4 3gp ???android????????????????????????
        //intent.setType(???video/*;image/*???);//???????????????????????????
        intent.setType("*/*");//???????????????
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
    }

    private void openCamera() {
        // ????????????
        destPath = FilePickerUtils.getInstance().getImageCachePath()
                + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
        LogUtils.d("CourserActivity === ??????????????????", destPath);
        SystemUtils.imageCapture(this, destPath, PictureConfig.CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // ????????????????????????
                    selectList = PictureSelector.obtainMultipleResult(data);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();//????????????????????????
                    for (int i = 0; i < selectList.size(); i++) { // ?????????
                        int finalI = i;//????????????????????????
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
                            //??????????????????
                            readyUpload(path, pictureType, mImageName);
                        };
                        executorService.execute(syncRunnable);
                    }
                    break;
                case PictureConfig.CAMERA:
                    File file = new File(destPath);
                    LogUtils.d("CourserActivity === ????????????", destPath + ":" + Uri.fromFile(file).getPath());
                    String curTime = TimeUtils.getCurTimeLong("yyyyMMddHHmmss") + "/" + System.currentTimeMillis();
                    String mImageName = Constant.picture + curTime + ".jpg";
                    //??????????????????
                    readyUpload(Uri.fromFile(file).getPath(), "image/jpeg", mImageName);
                    break;
                case Constant.REQUEST_CODE_PICK_FILE:
                    Uri uri = data.getData();
                    String path;
                    if ("file".equalsIgnoreCase(uri.getScheme())) {//???????????????????????????
                        path = uri.getPath();
                    } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4??????
                        path = FilePickerUtils.getInstance().getPath(this, uri);
                    } else {//4.4???????????????????????????
                        path = FilePickerUtils.getInstance().getRealPathFromURI(this, uri);
                    }
                    if (path != null) {
                        String pictureType = path.substring(path.lastIndexOf("."));
                        //?????????????????????????????????????????????????????????????????????????????????
                        String fileName = U.stringFilter(path.substring(path.lastIndexOf("/")));
                        String mFileName = Constant.accessory + TimeUtils.getCurTimeLong("yyyyMMddHHmmss") + "/"
                                + System.currentTimeMillis() + "_" + UserManager.getInstance().userData.getUid() + fileName;
                        LogUtils.d("CourserReleaseActivity", path + "===" + mFileName);
                        //??????????????????
                        readyUpload(path, pictureType, mFileName);
                    }
                    break;
            }
            courReleAdapter.notifyDataSetChanged();
        }
    }

    //??????????????????
    private void readyUpload(String path, String pictureType, String mImageName) {
        if (pictureType.contains("jpg") || pictureType.contains("png")
                || pictureType.contains("jpeg") || pictureType.contains("mp4")) {
            CourserReleaseAdapter.img++;
        }
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
        //haoruigang on 2018-7-20 17:05:52 19.????????????
        HttpManager.getInstance().doAddPerfomV2("CourserReleaseActivity",
                courseId, pngOravis.toString(), content, studentIds.toString(),
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        LogUtils.d("CourserReleaseActivity", "onSuccess");
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        U.showToast("????????????");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.COURSE));
                        startActivity(new Intent(CourserReleaseActivity.this, CourserActivity.class));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserReleaseActivity onFail", statusCode + ":" + errorMsg);
                        btnSubmit.setClickable(true);
                        mCircleProgress.setVisibility(View.GONE);
                        rlBackButton.setClickable(true);
                        isBackPressed = true;
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserReleaseActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserReleaseActivity onError", throwable.getMessage());
                        btnSubmit.setClickable(true);
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
        // ????????????????????????????????????????????????????????????????????????true???????????????false,????????????????????????back???????????????????????????
        return isBackPressed;
    }

}
