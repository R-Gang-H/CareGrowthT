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

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-25 18:01:14
 * 课程反馈
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
    //根目录的节点
    private List<CourseEntity> courseLeftMenu = new ArrayList<>();
    // 子目录节点
    private List<CourseEntity.TopicsBean> courseRightMenu = new ArrayList<>();
    //弹出的popupWindow布局
    private LinearLayout popupLayout;
    //根目录被选中的节点
    private RootListViewAdapter adapter;
    private SubListViewAdapter subAdapter;
    private String themeName;//一级菜单名称
    private String topicId;//二级课程Id
    private String libItemName;//二级课程名称
    private String topicContent;//二级课程内容
    //课程反馈记录 集体
    private ArrayList<MomentMessageEntity> mutileData = new ArrayList<>();
    //课程反馈记录 单个
    private ArrayList<MomentMessageEntity> singleData = new ArrayList<>();
    private MomentAdapter mutileAdapter;
    private MomentAdapter singleAdapter;
    ArrayList<StudentEntity> studentList = new ArrayList<>();

    private Dialog dialog;//包含输入框的 dialog
    private boolean isPend = true;//是否跳转 学员批量签到

    String destPath;
    String mImageName = "";//user/avatar/时间戳+用户id.jpg
    AliYunOss mOssClient;

    private String headImage;
    private boolean isUploadTable = true;//上传签到表
    private boolean isRefesh = true;
    private int stuNum = 0;// 学员人数
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_courser;
    }

    @Override
    public void initView() {
        tvTitle.setText("课程反馈");
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.ic_see_stu);

        //解决NetworkOnMainThreadException异常
        ImgLabelUtils.getInstance().struct();
        mOssClient = new AliYunOss(this);

        courseId = getIntent().getStringExtra("courseId");
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        } else {
            orgId = UserManager.getInstance().getOrgId(); //getIntent().getStringExtra("orgId");
        }

        String str = "<font color='#333333'>没有记录，看不到成长</font>";
        ivEmpty.setText(Html.fromHtml(str));

        initRecyclerView(xRecyclerView_mutill, true);
        mutileAdapter = new MomentAdapter(mutileData, this, null, null, "2", "");//type:1.兴趣圈 2.课程反馈
        xRecyclerView_mutill.setAdapter(mutileAdapter);
        initRecyclerView(xRecyclerView_single, true);
        singleAdapter = new MomentAdapter(singleData, this, null, null, "2", "");//type:1.兴趣圈 2.课程反馈
        xRecyclerView_single.setAdapter(singleAdapter);

        refreshLayout.setRefreshHeader(new ClassicsHeader(CourserActivity.this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            teacherLessonDetail();
            getLessonV2();//获取课程反馈详情
            isRefesh = false;
            refreshLayout.finishLoadmore();
            refreshLayout.finishRefresh();
        });
        mutileAdapter.setCommentListener((view, adapterPosition, position1, position2) -> {
            showInputDialog(view, adapterPosition, position1, position2, 2);//集体
        });
        singleAdapter.setCommentListener((view, adapterPosition, position1, position2) -> {
            showInputDialog(view, adapterPosition, position1, position2, 1);//单个
        });
        mutileAdapter.setMomentListener((pData, position) -> {
            checkDelDialog(pData.getCircleId(), position, 2);//集体
        });
        singleAdapter.setMomentListener((pData, position) -> {
            checkDelDialog(pData.getCircleId(), position, 1);//单个
        });
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this::handleWindowChange);
    }

    /**
     * 确定删除
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
        tvTitle.setText("确定删除吗?");
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
     * 删除课程反馈
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
                        if (type == 2) {//集体
                            removeMoment(mutileData, mutileAdapter, position);
                        } else if (type == 1) {//单个
                            removeMoment(singleData, singleAdapter, position);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MomentAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
        //移除
        datas.remove(position);
        adapter.listModel.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, datas.size());
    }

    private void teacherLessonDetail() {
        //haoruigang on 2018-7-6 17:23:39 4. 获取课程的基本信息
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
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
        tvCourseTime.setText(String.format("%s-%s", TimeUtils.getDateToString(startTime, "MM月dd日 HH:mm"),
                TimeUtils.getDateToString(endTime, "HH:mm")));
        tvStudentCount.setText(Html.fromHtml(String.format("班级人数\t<font color='#69ace5'>%s</font>\t签到\t<font color='#69ace5'>%s</font>\t请假\t<font color='#69ace5'>%s</font>", courseData.getStudentCount(), courseData.getSign(), courseData.getLeave())));

        headImage = courseData.getSignSheet();
        isUploadTable = TextUtils.isEmpty(headImage);
        tvUploadSign.setText(isUploadTable ? "上传签到表" : "查看签到表");

        if (TextUtils.isEmpty(courseData.getCourseTheme())) {
            tvTeachLib.setText(Html.fromHtml("教学主题:从<font color='#69ace5'><u>教学大纲库</u></font>中选择"));
        } else {
            tvTeachLib.setText(Html.fromHtml("教学主题:<font color='#69ace5'><u>" + courseData.getCourseTheme() + "</u></font>"));
            tvCourseTask.setVisibility(TextUtils.isEmpty(courseData.getCourseThemeContent()) ? View.GONE : View.VISIBLE);
            ImgLabelUtils.getInstance().htmlThree(tvCourseTask, courseData.getCourseThemeContent());//加载Html富文本及图片
        }

        getLessonV2();//获取课程反馈详情
        if (isRefesh) {
            lessonChild();//待处理的学员
        }
    }

    private void setSignSheet(String signSheet) {
        if (!TextUtils.isEmpty(signSheet)) {// 不为空可点击
            String[] circlePicture = {};
            if (!TextUtils.isEmpty(signSheet)) {
                circlePicture = signSheet.split("#");
            }
            ArrayList<String> arrImageList = new ArrayList<>();
            Collections.addAll(arrImageList, circlePicture);//转化为数组
            Intent intent = new Intent(MyApplication.getAppContext(), SpaceImageDetailActivity.class);
            intent.putExtra("images", arrImageList);//非必须
            intent.putExtra("position", 0);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        }
    }

    /**
     * 监听键盘的显示和隐藏
     */
    private void handleWindowChange() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);//获取当前界面显示范围
        Log.i("display  ", "top = " + rect.top);
        Log.i("display  ", "bottom = " + rect.bottom);
        int displayHeight = rect.bottom - rect.top;//app内容显示高度，即屏幕高度-状态栏高度-键盘高度
        int totalHeight = getWindow().getDecorView().getHeight();
        //显示内容的高度和屏幕高度比大于 0.8 时，dismiss dialog
        if ((float) displayHeight / totalHeight > 0.8)//0.8只是一个大致的比例，可以修改
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                //如果添加了空白 item ，移除空白 item
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
        //5. 获取参与课程的学员
        HttpManager.getInstance().doLessonChild("PendingActivity",
                courseId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        stuNum = data.getData().size();
                        studentList.clear();
                        for (StudentEntity stu : data.getData()) {
                            //学员状态 1：既没有请假也没有签到 2：已签到 3：已请假 /过滤只显示没有请假签到的
                            if (TextUtils.equals(stu.getStatus(), "1")) {
                                studentList.add(stu);
                            }
                        }
                        tvPending.setText(Html.fromHtml(String.format("待处理\t<font color='#69ace5'>%s</font>", studentList.size())));
                        if (studentList.size() > 0) {
                            ivPending.setVisibility(View.VISIBLE);
                            if (isPend) {
                                startActivity(new Intent(CourserActivity.this, PendingActivity.class)
                                        .putExtra("courseId", courseId)
                                        .putExtra("courseData", courseData)
                                        .putExtra("studentList", studentList));
                                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                            }
                        } else {
                            ivPending.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        }  //                    U.showToast(errorMsg);//不能打开

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    /**
     * 获取课程反馈详情
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
                            if (childNames.length == 1) {//单个
                                singleData.add(mutile_single);
                                getContacts(singleData);//排序
                            } else if (childNames.length > 1) {//集体
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

                        mutileAdapter.setData(mutileData, true);
                        singleAdapter.setData(singleData, true);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        U.showToast(errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
     * 从服务器获取数据并排序
     */
    public static void getContacts(ArrayList<MomentMessageEntity> data) {
        //按孩子id排序
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
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.tv_teach_lib:
                getResourceLie();//获取教学主题库
                break;
            case R.id.tv_upload_sign:
                if (isUploadTable) {
                    showPhotoDialog();//上传签到表
                } else {
                    //查看签到表
                    setSignSheet(headImage);
                }
                break;
            case R.id.iv_add:
                if (courseData != null) {
                    long startTime = Long.parseLong(courseData.getStartAt());
                    Log.d("CourserActivity", String.valueOf(startTime));
                    long today = System.currentTimeMillis() / 1000;//当前的时间戳
                    if (today < startTime) {// 课程还未开始
                        U.showToast("课程还未开始");
                    } else {
                        String Uid = UserManager.getInstance().userData.getUid();
                        String isRol = "zy_5";
                        if (Uid.equals(courseData.getTeacherId())) {// zy_4 为我的课发布课程反馈
                            isRol = "zy_4";
                        }
                        // zy_5给所有的课发布课程反馈
                        if (!UserManager.getInstance().isTrueRole(isRol)) {
                            UserManager.getInstance().showSuccessDialog(this
                                    , getString(R.string.text_role));
                            break;
                        } else {
                            if (stuNum > 0) {// 学生人数大于0可以发布课程反馈
                                CourserReleaseAdapter.img = 0;
                                startActivity(new Intent(this, CourserReleaseActivity.class)
                                        .putExtra("courseData", courseData));
                            } else {
                                U.showToast("该课程没有学员,不能发布课程反馈");
                            }
                        }
                    }
                }
                break;
        }
    }

    //创建相册选择器
    public PictureSelector getPicture() {
        return PictureSelector.create(this);
    }

    private void openImageOr(int type) {
        // 进入相册
        int maxSelectNum = 1;
        getPicture()
                .openGallery(type)// 图片.ofImage()、视频.ofVideo()、音频
                .theme(R.style.picture_white_style)// 主题样式设置
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 单选
                .previewImage(true)// 是否可预览图片
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .isGif(true)// 是否显示gif图片
                .openClickSound(true)// 是否开启点击声音
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void openCamera() {
        // 单独拍照
//        getPicture()
//                .openCamera(PictureMimeType.ofImage())// 单独拍照
//                .theme(R.style.picture_white_style)// 主题样式设置
//                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        destPath = FilePickerUtils.getInstance().getImageCachePath()
                + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
        LogUtils.d("CourserActivity === 开始拍照路径", destPath);
        SystemUtils.imageCapture(this, destPath, PictureConfig.CAMERA);
    }

    private void showPhotoDialog() {
        final String items[] = {"拍照", "相册"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择头像");
        builder.setItems(items, (dialog, which) -> {
            dialog.dismiss();
            switch (which) {
                case 0:
                    if (!selfPermissionGranted(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(CourserActivity.this,
                                new String[]{Manifest.permission.CAMERA}, 120);
                    } else {
                        openCamera();//拍照
                    }
                    break;
                case 1:
                    openImageOr(PictureMimeType.ofImage());//选择图片
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
                    // 拍照或者选图成功
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    String mHeaderAbsolutePath = selectList.get(0).getPath();
                    Logger.d(mHeaderAbsolutePath);

                    CompressImg(mHeaderAbsolutePath);// 压缩图片
                    break;
                case PictureConfig.CAMERA:
                    File file = new File(destPath);
                    Uri uri = Uri.fromFile(file);
                    LogUtils.d("CourserActivity === 压缩路径", destPath + ":" + uri.getPath());
                    CompressImg(uri.getPath());// 压缩图片
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
            //文件名
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
                        tvUploadSign.setText("查看签到表");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
     * 课程库为空
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
     * 二级菜单的根目录
     */
    private void showPopBtn() {
        LayoutInflater inflater = LayoutInflater.from(CourserActivity.this);
        popupLayout = (LinearLayout) inflater.inflate(
                R.layout.activity_course_resource, null, false);
        ListView rootListView = popupLayout.findViewById(R.id.root_listview);
        // 子popupWindow
        FrameLayout subLayout = popupLayout.findViewById(R.id.sub_popupwindow);
        //初始化subListview
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
        getResourceContent(0);//默认显示二级菜单第一个
        //弹出popupwindow时，二级菜单默认隐藏，当点击某项时，二级菜单再弹出
//        subLayout.setVisibility(View.INVISIBLE);
        //初始化Dialog
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
                     * 选中root某项时改变该ListView item的背景色
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
            setLessonV2();//8.设置课程的教学主题
        });
        Window window = dialog.getWindow();
        //设置弹出窗口大小
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //设置显示位置
        window.setGravity(Gravity.BOTTOM);
        //设置动画效果
        window.setWindowAnimations(R.style.Popupwindow);
    }

    private void setLessonV2() {
        if (TextUtils.isEmpty(topicId)) {
//            U.showToast("教学主题未选择");
            return;
        }
        //haoruigang on 2018-7-12 17:58:50
        HttpManager.getInstance().doSetLessonV2("CourserActivity", courseId, topicId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
            @Override
            public void onSuccess(BaseDataModel<CourseEntity> data) {
                if (!TextUtils.isEmpty(libItemName)) {
                    tvTeachLib.setText(Html.fromHtml("教学主题:<font color='#69ace5'><u>" + libItemName + "</u></font>"));
                    tvCourseTask.setVisibility(TextUtils.isEmpty(topicContent) ? View.GONE : View.VISIBLE);
                    ImgLabelUtils.getInstance().htmlThree(tvCourseTask, topicContent);//加载Html富文本及图片
                }
                U.showToast("设置成功!");
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                U.showToast(errorMsg);
                if (statusCode == 1002 || statusCode == 1011) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(CourserActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void getResourceLie() {
        //haoruigang on 2018-4-27 11:52:33 获取机构课程库全部课程
        HttpManager.getInstance().doResourceLie("CourserActivity", orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseLeftMenu.clear();
                        courseLeftMenu.addAll(data.getData());
                        if (courseLeftMenu.size() == 0) {
                            showSuccessDialog();// 教学主题为空
                        } else {
                            showPopBtn();//课程库底部弹出
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        U.showToast(errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }


    private void getResourceContent(int position) {
        adapter.setSelectedPosition(position);//默认选中一级菜单第一个
        adapter.notifyDataSetChanged();

        if (courseLeftMenu.size() > 0) {
            courseRightMenu = courseLeftMenu.get(position).getTopic();
            subAdapter.setItems(courseRightMenu);
            if (courseRightMenu.size() > 0) {
                //默认选中二级菜单第一个
                subAdapter.setSelectedPosition(0);
                subAdapter.notifyDataSetChanged();
                topicId = courseRightMenu.get(0).getTopicId();
                libItemName = courseRightMenu.get(0).getTopicName();
                topicContent = courseRightMenu.get(0).getTopicContent();
            }
            /**
             * 选中某个根节点时，使显示相应的子目录可见
             */
//                subLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示评论输入 dialog
     *
     * @param itemView
     * @param adapterPosition
     * @param position1       item
     * @param position2       评论item
     */
    @SuppressLint("ClickableViewAccessibility")
    public void showInputDialog(View itemView, final int adapterPosition, final int position1, final int position2, int type) {
        final int itemBottomY = getCoordinateY(itemView) + itemView.getHeight();//item 底部y坐标
        dialog = new Dialog(CourserActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = LayoutInflater.from(CourserActivity.this).inflate(R.layout.bottom_entry_fram, null);
        dialog.setContentView(view);
        //scrollView 点击事件，点击时将 dialog dismiss，设置 onClick 监听无效
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

            //最后一个 item 特殊处理，添加一个和输入框等高的 item，使 RecyclerView 有足够的空间滑动
//            if (adapterPosition == singleAdapter.listModel.size() - 1) {
//                singleAdapter.listModel.add(new MomentMessageEntity());
//                singleAdapter.bottomHeight = llCommentInput.getHeight();
//                singleAdapter.notifyDataSetChanged();
//            }
            //滑动 RecyclerView，是对应 item 底部和输入框顶部对齐
            nestedScroll.scrollBy(0, itemBottomY - y);

            EditText friendsInput = llCommentInput.findViewById(R.id.friends_input);
            friendsInput.setOnKeyListener((v, keyCode, event) -> {
                // 这两个条件必须同时成立，如果仅仅用了enter判断，就会执行两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 执行发送消息等操作
                    SubmitComment(friendsInput.getText().toString(), position1, position2, type);
                    return true;
                }
                return false;
            });
            TextView friendSend = llCommentInput.findViewById(R.id.friends_send);
            friendSend.setOnClickListener(v -> {
                // 执行发送消息等操作
                SubmitComment(friendsInput.getText().toString(), position1, position2, type);
            });

        }, 300);
    }

    /**
     * 获取控件左上顶点 y 坐标
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
            if (type == 2) {//集体
                mReplyCommentId = mutileData.get(position1).getComments().get(position2).getCommentId();
            } else if (type == 1) {//单个
                mReplyCommentId = singleData.get(position1).getComments().get(position2).getCommentId();
            }
        }
        String CircleId = null;
        if (type == 2) {//集体
            CircleId = mutileData.get(position1).getCircleId();
        } else if (type == 1) {//单个
            CircleId = singleData.get(position1).getCircleId();
        }
        Log.e("------------", "SubmitComment");

        HttpManager.getInstance().doComment("MomentFragment", CircleId, s, mReplyCommentId,
                new HttpCallBack<BaseDataModel<MomentCommentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentCommentEntity> data) {
                        if (null != dialog && dialog.isShowing()) {
                            dialog.dismiss();
                            ExecutorService executorService = Executors.newSingleThreadExecutor();//唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行(同步)
                            Runnable syncRunnable = () -> {
                                getLessonV2();
                            };
                            executorService.execute(syncRunnable);
                            executorService.shutdown();
                            Thread thread = new Thread() {
                                @Override
                                public synchronized void run() {
                                    super.run();
                                    while (true) {// 实时检测线程池是否上传完成
                                        if (executorService.isTerminated()) {
                                            if (type == 2) {//集体
                                                xRecyclerView_mutill.scrollToPosition(position1 + 2);
                                            } else if (type == 1) {//单个
                                                xRecyclerView_single.scrollToPosition(position1 + 2);
                                            }
//                                    MoveToPosition(layoutManager, position1 + 2);//提交完跳转指定位置
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
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
                lessonChild();//待处理的学员
                break;
            case ToUIEvent.COURSE:
                getLessonV2();//获取课程反馈详情
                break;
            case ToUIEvent.REFERSH_SIGN_TABLE:
                isUploadTable = true;
                tvUploadSign.setText("上传签到表");
                break;
        }
    }

}
