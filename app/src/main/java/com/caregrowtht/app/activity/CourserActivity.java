package com.caregrowtht.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.MyApplication;
import com.android.library.utils.SystemUtils;
import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourserReleaseAdapter;
import com.caregrowtht.app.adapter.MomentAdapter;
import com.caregrowtht.app.adapter.RootListViewAdapter;
import com.caregrowtht.app.adapter.SubListViewAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.MomentCommentEntity;
import com.caregrowtht.app.model.MomentMessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.FilePickerUtils;
import com.caregrowtht.app.uitil.ImgLabelUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ThreadPoolManager;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.uitil.alicloud.oss.AliYunOss;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.zxy.tiny.Tiny;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-25 18:01:14
 * ????????????
 */
public class CourserActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.nested_scroll)
    NestedScrollView nestedScroll;
    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_course_time)
    TextView tvCourseTime;
    @BindView(R.id.tv_student_count)
    TextView tvStudentCount;
    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.iv_pending)
    ImageView ivPending;
    @BindView(R.id.tv_upload_sign)
    TextView tvUploadSign;
    @BindView(R.id.tv_teach_lib)
    TextView tvTeachLib;
    @BindView(R.id.tv_course_task)
    TextView tvCourseTask;
    @BindView(R.id.recycler_view_mutil)
    RecyclerView xRecyclerView_mutill;
    @BindView(R.id.recycler_view_single)
    RecyclerView xRecyclerView_single;
    @BindView(R.id.iv_emtity)
    ImageView ivEmtity;
    @BindView(R.id.iv_empty)
    TextView ivEmpty;
    @BindView(R.id.rl_yes_data)
    RelativeLayout rlYesData;

    private CourseEntity courseData;
    private String orgId;
    private String courseId;
    //??????????????????
    private List<CourseEntity> courseLeftMenu = new ArrayList<>();
    // ???????????????
    private List<CourseEntity.TopicsBean> courseRightMenu = new ArrayList<>();
    //?????????popupWindow??????
    private LinearLayout popupLayout;
    //???????????????????????????
    private RootListViewAdapter adapter;
    private SubListViewAdapter subAdapter;
    private String themeName;//??????????????????
    private String topicId;//????????????Id
    private String libItemName;//??????????????????
    private String topicContent;//??????????????????
    //?????????????????? ??????
    private ArrayList<MomentMessageEntity> mutileData = new ArrayList<>();
    //?????????????????? ??????
    private ArrayList<MomentMessageEntity> singleData = new ArrayList<>();
    private MomentAdapter mutileAdapter;
    private MomentAdapter singleAdapter;
    ArrayList<StudentEntity> studentList = new ArrayList<>();

    private Dialog dialog;//?????????????????? dialog
    private boolean isPend = true;//???????????? ??????????????????

    String destPath;
    String mImageName = "";//user/avatar/?????????+??????id.jpg
    AliYunOss mOssClient;

    private String headImage;
    private boolean isUploadTable = true;//???????????????
    private boolean isRefesh = true;
    private int stuNum = 0;// ????????????
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_courser;
    }

    @Override
    public void initView() {
        tvTitle.setText("????????????");
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.ic_see_stu);

        mOssClient = new AliYunOss(this);

        courseId = getIntent().getStringExtra("courseId");
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
            // ???????????????????????????????????? ??????????????????
            if (!msgEntity.getType().equals("10") && !msgEntity.getType().equals("12")) {
                isPend = false;// ????????????????????????????????????
            }
        } else {
            orgId = UserManager.getInstance().getOrgId(); //getIntent().getStringExtra("orgId");
        }

        String str = "<font color='#333333'>??????????????????????????????</font>";
        ivEmpty.setText(Html.fromHtml(str));

        initRecyclerView(xRecyclerView_mutill, true);
        mutileAdapter = new MomentAdapter(mutileData, this, null, null, "2", "");//type:1.????????? 2.????????????
        xRecyclerView_mutill.setAdapter(mutileAdapter);
        initRecyclerView(xRecyclerView_single, true);
        singleAdapter = new MomentAdapter(singleData, this, null, null, "2", "");//type:1.????????? 2.????????????
        xRecyclerView_single.setAdapter(singleAdapter);

        refreshLayout.setRefreshHeader(new ClassicsHeader(CourserActivity.this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            isRefesh = false;
            teacherLessonDetail();
        });
        mutileAdapter.setCommentListener((view, adapterPosition, position1, position2) -> {
            showInputDialog(view, adapterPosition, position1, position2, 2);//??????
        });
        singleAdapter.setCommentListener((view, adapterPosition, position1, position2) -> {
            showInputDialog(view, adapterPosition, position1, position2, 1);//??????
        });
        mutileAdapter.setMomentListener((pData, position) -> {
            checkDelDialog(pData.getCircleId(), position, 2);//??????
        });
        singleAdapter.setMomentListener((pData, position) -> {
            checkDelDialog(pData.getCircleId(), position, 1);//??????
        });
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this::handleWindowChange);
    }

    /**
     * ????????????
     *
     * @param circleId
     * @param position
     * @param type
     */
    private void checkDelDialog(String circleId, int position, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_org, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("????????????????");
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setVisibility(View.GONE);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_ok.setOnClickListener(v -> {
            delCircle(circleId, position, type);
            dialog.dismiss();
        });
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * ??????????????????
     *
     * @param circleId
     * @param position
     * @param type
     */
    private void delCircle(String circleId, int position, int type) {
        HttpManager.getInstance().doDelCircle("CourserActivity", circleId,
                new HttpCallBack<BaseDataModel>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        if (type == 2) {//??????
                            removeMoment(mutileData, mutileAdapter, position);
                        } else if (type == 1) {//??????
                            removeMoment(singleData, singleAdapter, position);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MomentAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MomentAdapter onError", throwable.getMessage());
                    }
                });
    }

    private void removeMoment(ArrayList<MomentMessageEntity> datas, MomentAdapter adapter, int position) {
        //??????
        datas.remove(position);
        adapter.listModel.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, datas.size());
    }

    private void teacherLessonDetail() {
        //haoruigang on 2018-7-6 17:23:39 4. ???????????????????????????
        HttpManager.getInstance().doTeacherLessonDetail("CourserActivity"
                , courseId, new HttpCallBack<BaseDataModel<CourseEntity<String, String, String>>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity<String, String, String>> data) {
                        courseData = data.getData().get(0);
                        getCourseData();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserActivity Throwable", throwable.getMessage());
                    }
                });
    }

    private void getCourseData() {
        tvCourseName.setText(courseData.getCourseName());
        long startTime = Long.parseLong(courseData.getStartAt());
        long endTime = Long.parseLong(courseData.getEndAt());
        tvCourseTime.setText(String.format("%s-%s", TimeUtils.getDateToString(startTime, "MM???dd??? HH:mm"),
                TimeUtils.getDateToString(endTime, "HH:mm")));
        tvStudentCount.setText(Html.fromHtml(String.format("????????????\t<font color='#69ace5'>%s</font>\t??????\t<font color='#69ace5'>%s</font>\t??????\t<font color='#69ace5'>%s</font>", courseData.getStudentCount(), courseData.getSign(), courseData.getLeave())));

        headImage = courseData.getSignSheet();
        isUploadTable = TextUtils.isEmpty(headImage);
        tvUploadSign.setText(isUploadTable ? "???????????????" : "???????????????");

        if (TextUtils.isEmpty(courseData.getCourseTheme())) {
            tvTeachLib.setText(Html.fromHtml("????????????:???<font color='#69ace5'><u>???????????????</u></font>?????????"));
        } else {
            tvTeachLib.setText(Html.fromHtml("????????????:<font color='#69ace5'><u>" + courseData.getCourseTheme() + "</u></font>"));
            tvCourseTask.setVisibility(TextUtils.isEmpty(courseData.getCourseThemeContent()) ? View.GONE : View.VISIBLE);
            ImgLabelUtils.getInstance().htmlThree(tvCourseTask, courseData.getCourseThemeContent());//??????Html??????????????????
        }
        getLessonV2();//????????????????????????
        if (isRefesh) {
            lessonChild();//??????????????????
        }
        refreshLayout.finishLoadmore();
        refreshLayout.finishRefresh();
    }

    private void setSignSheet(String signSheet) {
        if (!TextUtils.isEmpty(signSheet)) {// ??????????????????
            String[] circlePicture = {};
            if (!TextUtils.isEmpty(signSheet)) {
                circlePicture = signSheet.split("#");
            }
            ArrayList<String> arrImageList = new ArrayList<>();
            Collections.addAll(arrImageList, circlePicture);//???????????????
            Intent intent = new Intent(MyApplication.getAppContext(), SpaceImageDetailActivity.class);
            intent.putExtra("images", arrImageList);//?????????
            intent.putExtra("position", 0);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        }
    }

    /**
     * ??????????????????????????????
     */
    private void handleWindowChange() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);//??????????????????????????????
        Log.i("display  ", "top = " + rect.top);
        Log.i("display  ", "bottom = " + rect.bottom);
        int displayHeight = rect.bottom - rect.top;//app????????????????????????????????????-???????????????-????????????
        int totalHeight = getWindow().getDecorView().getHeight();
        //????????????????????????????????????????????? 0.8 ??????dismiss dialog
        if ((float) displayHeight / totalHeight > 0.8)//0.8??????????????????????????????????????????
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                //????????????????????? item ??????????????? item
//                if (singleAdapter.listModel.get(singleAdapter.listModel.size() - 1) instanceof MomentMessageEntity) {
//                    singleAdapter.listModel.remove(singleAdapter.listModel.size() - 1);
//                    singleAdapter.notifyDataSetChanged();
//                }
            }
    }

    @Override
    public void initData() {
        teacherLessonDetail();
    }

    private void lessonChild() {
        //5. ???????????????????????????
        HttpManager.getInstance().doLessonChild("PendingActivity",
                courseId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        stuNum = data.getData().size();
                        studentList.clear();
                        for (StudentEntity stu : data.getData()) {
                            //???????????? 1????????????????????????????????? 2???????????? 3???????????? /????????????????????????????????????
                            if (TextUtils.equals(stu.getStatus(), "1")) {
                                studentList.add(stu);
                            }
                        }
                        tvPending.setText(Html.fromHtml(String.format("?????????\t<font color='#69ace5'>%s</font>", studentList.size())));
                        if (studentList.size() > 0) {
                            ivPending.setVisibility(View.VISIBLE);
                            if (isPend) {
                                startActivity(new Intent(CourserActivity.this, PendingActivity.class)
                                        .putExtra("courseId", courseId)
                                        .putExtra("courseData", courseData)
                                        .putExtra("studentList", studentList));
                                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                            }
                        } else {
                            ivPending.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        }  //                    U.showToast(errorMsg);//????????????

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    /**
     * ????????????????????????
     */
    private void getLessonV2() {
        HttpManager.getInstance().doGetLessonV2("CourserActivity", courseId,
                new HttpCallBack<BaseDataModel<MomentMessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentMessageEntity> data) {
                        mutileData.clear();
                        singleData.clear();
                        for (MomentMessageEntity mutile_single : data.getData()) {
                            String childName = mutile_single.getChildName();
                            String[] childNames = childName.split("#");//
                            if (childNames.length == 1) {//??????
                                singleData.add(mutile_single);
                                getContacts(singleData);//??????
                            } else if (childNames.length > 1) {//??????
                                mutileData.add(mutile_single);
                            }
                        }
                        if (mutileData.size() > 0 || singleData.size() > 0) {
                            rlYesData.setVisibility(View.GONE);
                            ivEmtity.setVisibility(View.VISIBLE);
                            if (mutileData.size() > 0) {
                                xRecyclerView_mutill.setVisibility(View.VISIBLE);
                            }
                            if (singleData.size() > 0) {
                                xRecyclerView_single.setVisibility(View.VISIBLE);
                            }
                        }

                        mutileAdapter.setData(mutileData);
                        singleAdapter.setData(singleData);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        U.showToast(errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserActivity Throwable", throwable.getMessage());
                    }
                });

    }

    /**
     * ?????????????????????????????????
     */
    public static void getContacts(ArrayList<MomentMessageEntity> data) {
        //?????????id??????
        Collections.sort(data, (o1, o2) -> o1.getChildId().compareToIgnoreCase(o2.getChildId()));
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.tv_teach_lib, R.id.iv_pending,
            R.id.tv_upload_sign, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, StudentSituatActivity.class)
                        .putExtra("courseData", courseData));
                break;
            case R.id.iv_pending:
                startActivity(new Intent(this, PendingActivity.class)
                        .putExtra("courseId", courseId)
                        .putExtra("courseData", courseData)
                        .putExtra("studentList", studentList));
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                break;
            case R.id.tv_teach_lib:
                getResourceLie();//?????????????????????
                break;
            case R.id.tv_upload_sign:
                if (isUploadTable) {
                    showPhotoDialog();//???????????????
                } else {
                    //???????????????
                    setSignSheet(headImage);
                }
                break;
            case R.id.iv_add:
                if (courseData != null) {
                    long startTime = Long.parseLong(courseData.getStartAt());
                    Log.d("CourserActivity", String.valueOf(startTime));
                    long today = System.currentTimeMillis() / 1000;//??????????????????
                    if (today < startTime) {// ??????????????????
                        U.showToast("??????????????????");
                    } else {
                        String Uid = UserManager.getInstance().userData.getUid();
                        String isRol = "zy_5";
                        if (Uid.equals(courseData.getTeacherId())) {// zy_4 ??????????????????????????????
                            isRol = "zy_4";
                        }
                        // zy_5?????????????????????????????????
                        if (!UserManager.getInstance().isTrueRole(isRol)) {
                            UserManager.getInstance().showSuccessDialog(this
                                    , getString(R.string.text_role));
                            break;
                        } else {
                            if (stuNum > 0) {// ??????????????????0????????????????????????
                                CourserReleaseAdapter.img = 0;
                                startActivity(new Intent(this, CourserReleaseActivity.class)
                                        .putExtra("courseData", courseData));
                            } else {
                                U.showToast("?????????????????????,????????????????????????");
                            }
                        }
                    }
                }
                break;
        }
    }

    //?????????????????????
    public PictureSelector getPicture() {
        return PictureSelector.create(this);
    }

    private void openImageOr(int type) {
        // ????????????
        int maxSelectNum = 1;
        getPicture()
                .openGallery(type)// ??????.ofImage()?????????.ofVideo()?????????
                .theme(R.style.picture_white_style)// ??????????????????
                .maxSelectNum(maxSelectNum)// ????????????????????????
                .minSelectNum(1)// ??????????????????
                .imageSpanCount(4)// ??????????????????
                .selectionMode(PictureConfig.SINGLE)// ??????
                .previewImage(true)// ?????????????????????
                .isCamera(false)// ????????????????????????
                .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                .glideOverride(160, 160)// glide ???????????????????????????????????????????????????????????????????????????????????????
                .isGif(true)// ????????????gif??????
                .openClickSound(true)// ????????????????????????
                .previewEggs(true)// ??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
    }

    private void openCamera() {
        // ????????????
//        getPicture()
//                .openCamera(PictureMimeType.ofImage())// ????????????
//                .theme(R.style.picture_white_style)// ??????????????????
//                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
        destPath = FilePickerUtils.getInstance().getImageCachePath()
                + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
        LogUtils.d("CourserActivity === ??????????????????", destPath);
        SystemUtils.imageCapture(this, destPath, PictureConfig.CAMERA);
    }

    private void showPhotoDialog() {
        final String items[] = {"??????", "??????"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("???????????????");
        builder.setItems(items, (dialog, which) -> {
            dialog.dismiss();
            switch (which) {
                case 0:
                    if (!selfPermissionGranted(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(CourserActivity.this,
                                new String[]{Manifest.permission.CAMERA}, 120);
                    } else {
                        openCamera();//??????
                    }
                    break;
                case 1:
                    openImageOr(PictureMimeType.ofImage());//????????????
                    break;
                default:
                    break;
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // ????????????????????????
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    String mHeaderAbsolutePath = selectList.get(0).getPath();
                    Logger.d(mHeaderAbsolutePath);

                    CompressImg(mHeaderAbsolutePath);// ????????????
                    break;
                case PictureConfig.CAMERA:
                    File file = new File(destPath);
                    Uri uri = Uri.fromFile(file);
                    LogUtils.d("CourserActivity === ????????????", destPath + ":" + uri.getPath());
                    CompressImg(uri.getPath());// ????????????
                    break;
            }
        }
    }

    private void CompressImg(String mHeaderAbsolutePath) {
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(mHeaderAbsolutePath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
            //return the compressed file path
            mImageName = Constant.photo + TimeUtils.getCurTimeLong("yyyyMMddHHmmss") + UserManager.getInstance().userData.getUid() + ".jpg";
            //Url
            if (mImageName != null && !mImageName.equals("")) {
                headImage = Constant.OSS_URL + mImageName;
            }
            //?????????
            mOssClient.upload(mImageName, outfile, null);
            uploadSignSheetImg();
        });
    }

    private void uploadSignSheetImg() {
        HttpManager.getInstance().doUploadSignSheetImg("CourserActivity", courseId, headImage,
                new HttpCallBack<CourseEntity>() {
                    @Override
                    public void onSuccess(CourseEntity data) {
                        isUploadTable = false;
                        tvUploadSign.setText("???????????????");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserActivity Throwable", throwable.getMessage());
                    }
                });
    }

    /**
     * ???????????????
     */
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CourserActivity.this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(CourserActivity.this, R.layout.dialog_teach_lib, null);
        TextView tvOk = view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * ????????????????????????
     */
    private void showPopBtn() {
        LayoutInflater inflater = LayoutInflater.from(CourserActivity.this);
        popupLayout = (LinearLayout) inflater.inflate(
                R.layout.activity_course_resource, null, false);
        ListView rootListView = popupLayout.findViewById(R.id.root_listview);
        // ???popupWindow
        FrameLayout subLayout = popupLayout.findViewById(R.id.sub_popupwindow);
        //?????????subListview
        ListView subListView = popupLayout.findViewById(R.id.sub_listview);
        if (adapter == null) {
            adapter = new RootListViewAdapter(
                    CourserActivity.this);
        }
        rootListView.setAdapter(adapter);
        if (subAdapter == null) {
            subAdapter = new SubListViewAdapter(
                    CourserActivity.this);
        }
        subListView.setAdapter(subAdapter);
        adapter.setItems(courseLeftMenu);
        getResourceContent(0);//?????????????????????????????????
        //??????popupwindow???????????????????????????????????????????????????????????????????????????
//        subLayout.setVisibility(View.INVISIBLE);
        //?????????Dialog
        Dialog dialog = new Dialog(CourserActivity.this, R.style.dialog);
        dialog.setContentView(popupLayout);
        dialog.show();
        rootListView.setOnItemClickListener((parent, view, position, id) -> {
            themeName = courseLeftMenu.get(position).getThemeName();
            getResourceContent(position);
        });
        subListView
                .setOnItemClickListener((parent, view, position1, id) -> {
                    /*
                     * ??????root??????????????????ListView item????????????
                     */
                    subAdapter.setSelectedPosition(position1);
                    subAdapter.notifyDataSetChanged();
                    topicId = courseRightMenu.get(position1).getTopicId();
                    libItemName = courseRightMenu.get(position1).getTopicName();
                    topicContent = courseRightMenu.get(position1).getTopicContent();
                });
        popupLayout.findViewById(R.id.tv_canel).setOnClickListener(v -> {
            dialog.dismiss();
            popupLayout.setVisibility(View.GONE);
        });
        popupLayout.findViewById(R.id.tv_confirm).setOnClickListener(v -> {
            dialog.dismiss();
            popupLayout.setVisibility(View.GONE);
            setLessonV2();//8.???????????????????????????
        });
        Window window = dialog.getWindow();
        //????????????????????????
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //??????????????????
        window.setGravity(Gravity.BOTTOM);
        //??????????????????
        window.setWindowAnimations(R.style.Popupwindow);
    }

    private void setLessonV2() {
        if (TextUtils.isEmpty(topicId)) {
//            U.showToast("?????????????????????");
            return;
        }
        //haoruigang on 2018-7-12 17:58:50
        HttpManager.getInstance().doSetLessonV2("CourserActivity", courseId, topicId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
            @Override
            public void onSuccess(BaseDataModel<CourseEntity> data) {
                if (!TextUtils.isEmpty(libItemName)) {
                    tvTeachLib.setText(Html.fromHtml("????????????:<font color='#69ace5'><u>" + libItemName + "</u></font>"));
                    tvCourseTask.setVisibility(TextUtils.isEmpty(topicContent) ? View.GONE : View.VISIBLE);
                    ImgLabelUtils.getInstance().htmlThree(tvCourseTask, topicContent);//??????Html??????????????????
                }
                U.showToast("????????????!");
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                U.showToast(errorMsg);
                if (statusCode == 1002 || statusCode == 1011) {//????????????
                    U.showToast("????????????????????????!");
                    HttpManager.getInstance().dologout(CourserActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void getResourceLie() {
        //haoruigang on 2018-4-27 11:52:33 ?????????????????????????????????
        HttpManager.getInstance().doResourceLie("CourserActivity", orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseLeftMenu.clear();
                        courseLeftMenu.addAll(data.getData());
                        if (courseLeftMenu.size() == 0) {
                            showSuccessDialog();// ??????????????????
                        } else {
                            showPopBtn();//?????????????????????
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        U.showToast(errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }


    private void getResourceContent(int position) {
        adapter.setSelectedPosition(position);//?????????????????????????????????
        adapter.notifyDataSetChanged();

        if (courseLeftMenu.size() > 0) {
            courseRightMenu = courseLeftMenu.get(position).getTopic();
            subAdapter.setItems(courseRightMenu);
            if (courseRightMenu.size() > 0) {
                //?????????????????????????????????
                subAdapter.setSelectedPosition(0);
                subAdapter.notifyDataSetChanged();
                topicId = courseRightMenu.get(0).getTopicId();
                libItemName = courseRightMenu.get(0).getTopicName();
                topicContent = courseRightMenu.get(0).getTopicContent();
            }
            /**
             * ????????????????????????????????????????????????????????????
             */
//                subLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ?????????????????? dialog
     *
     * @param itemView
     * @param adapterPosition
     * @param position1       item
     * @param position2       ??????item
     */
    @SuppressLint("ClickableViewAccessibility")
    public void showInputDialog(View itemView, final int adapterPosition, final int position1, final int position2, int type) {
        final int itemBottomY = getCoordinateY(itemView) + itemView.getHeight();//item ??????y??????
        dialog = new Dialog(CourserActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = LayoutInflater.from(CourserActivity.this).inflate(R.layout.bottom_entry_fram, null);
        dialog.setContentView(view);
        //scrollView ??????????????????????????? dialog dismiss????????? onClick ????????????
        dialog.findViewById(R.id.scrollView).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                dialog.dismiss();
            return true;
        });
        dialog.show();
        itemView.postDelayed(() -> {
            LinearLayout llCommentInput = dialog.findViewById(R.id.ll_comment_input);
            int y = getCoordinateY(llCommentInput);
            Log.i("display", "itemBottomY = " + itemBottomY + "  input text y = " + y);

            //???????????? item ???????????????????????????????????????????????? item?????? RecyclerView ????????????????????????
//            if (adapterPosition == singleAdapter.listModel.size() - 1) {
//                singleAdapter.listModel.add(new MomentMessageEntity());
//                singleAdapter.bottomHeight = llCommentInput.getHeight();
//                singleAdapter.notifyDataSetChanged();
//            }
            //?????? RecyclerView???????????? item ??????????????????????????????
            nestedScroll.scrollBy(0, itemBottomY - y);

            EditText friendsInput = llCommentInput.findViewById(R.id.friends_input);
            friendsInput.setOnKeyListener((v, keyCode, event) -> {
                // ??????????????????????????????????????????????????????enter???????????????????????????
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // ???????????????????????????
                    SubmitComment(friendsInput.getText().toString(), position1, position2, type);
                    return true;
                }
                return false;
            });
            TextView friendSend = llCommentInput.findViewById(R.id.friends_send);
            friendSend.setOnClickListener(v -> {
                // ???????????????????????????
                SubmitComment(friendsInput.getText().toString(), position1, position2, type);
            });

        }, 300);
    }

    /**
     * ???????????????????????? y ??????
     *
     * @param view
     * @return
     */
    private int getCoordinateY(View view) {
        int[] coordinate = new int[2];
        view.getLocationOnScreen(coordinate);
        return coordinate[1];
    }

    private void SubmitComment(String s, final int position1, final int position2, int type) {
        String mReplyCommentId = null;
        if (position2 != -1) {
            if (type == 2) {//??????
                mReplyCommentId = mutileData.get(position1).getComments().get(position2).getCommentId();
            } else if (type == 1) {//??????
                mReplyCommentId = singleData.get(position1).getComments().get(position2).getCommentId();
            }
        }
        String CircleId = null;
        if (type == 2) {//??????
            CircleId = mutileData.get(position1).getCircleId();
        } else if (type == 1) {//??????
            CircleId = singleData.get(position1).getCircleId();
        }
        Log.e("------------", "SubmitComment");

        HttpManager.getInstance().doComment("MomentActivity", CircleId, s, mReplyCommentId,
                new HttpCallBack<BaseDataModel<MomentCommentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentCommentEntity> data) {
                        if (null != dialog && dialog.isShowing()) {
                            dialog.dismiss();
                            ExecutorService executorService = Executors.newSingleThreadExecutor();//???????????????????????????????????????????????????????????????????????????(FIFO, LIFO, ?????????)??????(??????)
                            Runnable syncRunnable = () -> {
                                getLessonV2();
                            };
                            executorService.execute(syncRunnable);
                            executorService.shutdown();
                            Thread thread = new Thread() {
                                @Override
                                public synchronized void run() {
                                    super.run();
                                    while (true) {// ???????????????????????????????????????
                                        if (executorService.isTerminated()) {
                                            if (type == 2) {//??????
                                                xRecyclerView_mutill.scrollToPosition(position1 + 2);
                                            } else if (type == 1) {//??????
                                                xRecyclerView_single.scrollToPosition(position1 + 2);
                                            }
//                                    MoveToPosition(layoutManager, position1 + 2);//???????????????????????????
                                            break;
                                        }
                                    }
                                }
                            };
                            ThreadPoolManager.getInstance().execute(new FutureTask<Object>(thread, null), null);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        dismissLoadingDialog();
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.SIGN_SUCCESS:
                isPend = false;
                teacherLessonDetail();
                lessonChild();//??????????????????
                break;
            case ToUIEvent.COURSE:
                getLessonV2();//????????????????????????
                break;
            case ToUIEvent.REFERSH_SIGN_TABLE:
                isUploadTable = true;
                tvUploadSign.setText("???????????????");
                break;
        }
    }

}
