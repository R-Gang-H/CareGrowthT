package com.caregrowtht.app.activity;

import android.content.ClipData;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DensityUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourserMsgTypeAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.draglistview.ItemAdapter;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-24 11:08:57
 * 课程管理
 */
public class CourserMsgActivity extends BaseActivity implements ViewOnItemClick, ViewOnItemLongClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_del_course)
    ImageView ivDelCourse;
    @BindView(R.id.iv_edit_course)
    ImageView ivEditCourse;
    @BindView(R.id.rv_board_view)
    RecyclerView rvBoardView;
    @BindView(R.id.tv_hint_drag)
    TextView tvHintDrag;
    @BindView(R.id.tv_no_course)
    TextView tvNoCourse;


    List<CourseEntity> courseTypeList = new ArrayList<>();//课程种类
    ArrayList<CourseEntity> courseList = new ArrayList<>();
    private CourserMsgTypeAdapter courseTypeAdapter;
    private ItemAdapter listAdapter;
    private String orgId;
    private CourseEntity courseType;
    private int position = 0;
    private View longView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_courser_msg;
    }

    @Override
    public void initView() {
        tvTitle.setText("课程管理");

        initRecyclerView(recyclerView, true);
        courseTypeAdapter = new CourserMsgTypeAdapter(courseTypeList, this, this);
        recyclerView.setAdapter(courseTypeAdapter);

        initRecyclerView(rvBoardView, true);
        listAdapter = new ItemAdapter(courseList, this, null, this);
        listAdapter.setHasStableIds(true);
        rvBoardView.setAdapter(listAdapter);
    }

    private static final String BLUE = "BLUE";

    @Override
    public void initData() {
        orgId = UserManager.getInstance().getOrgId();
        getCoursesType();//获取课程种类
    }

    /**
     * haoruigang on 2018-8-28 16:13:46
     * 38.获取机构的课程种类
     */
    private void getCoursesType() {
        HttpManager.getInstance().doGetCoursesType("CourserMsgActivity", orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseTypeList.clear();
                        courseTypeList.addAll(data.getData());
                        courseTypeList.add(new CourseEntity());
                        courseTypeAdapter.setData(courseTypeList);
                        if (courseTypeList.size() > 1) {
                            ivEditCourse.setVisibility(View.VISIBLE);
                            courseType = courseTypeList.get(position);//默认加载第一个课程分类内容
                            getCourseTypePlan(courseType.getClassifyId());//默认加载第一个
                        } else {
                            ivDelCourse.setVisibility(View.GONE);
                            ivEditCourse.setVisibility(View.GONE);
                            tvHintDrag.setVisibility(View.GONE);
                            tvNoCourse.setVisibility(View.VISIBLE);
                            rvBoardView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserMsgActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserMsgActivity onError", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.iv_del_course, R.id.iv_edit_course})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_del_course:
                if (courseType.getClassifyName().contains("无")) {
                    return;
                }
                if (!UserManager.getInstance().isTrueRole("pk_1")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                } else {
                    showDelDialog();
                }
                break;
            case R.id.iv_edit_course:
                if (!UserManager.getInstance().isTrueRole("pk_1")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    //编辑课程分类
                    startActivity(new Intent(this, CreateCourseTypeActivity.class)
                            .putExtra("CourseEntity", courseType));
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                }
                break;
        }
    }

    // 删除课程分类弹框
    private void showDelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定删除？")
                .setPositiveButton("确定", (d, which) -> {
                    deleteCourseType();
                })
                .setNegativeButton("取消", (d, which) -> d.dismiss())
                .create().show();
    }

    private void deleteCourseType() {
        // haoruigang on 2018-10-26 12:36:39 56.删除课程分类
        HttpManager.getInstance().doDeleteCourseType("CourserMsgActivity", orgId, courseType.getClassifyId(),
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        U.showToast("删除成功");
                        position = 0;// 加载第一课程种类
                        recyclerView.scrollToPosition(0);
                        getCoursesType();//重新同步获取课程种类
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserMsgActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserMsgActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        if (courseTypeList.size() - 1 == position) {
            if (!UserManager.getInstance().isTrueRole("pk_1")) {
                UserManager.getInstance().showSuccessDialog(this
                        , getString(R.string.text_role));
            } else {
                //添加课程分类
                startActivity(new Intent(this, CreateCourseTypeActivity.class));
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            }
        } else {
            this.position = position;
            courseTypeAdapter.setSelectItem(position);
            courseType = courseTypeList.get(position);
            getCourseTypePlan(courseType.getClassifyId());
        }
    }

    private void getCourseTypePlan(String classifyId) {
        // 54.获取课程分类下的排课
        HttpManager.getInstance().doGetCourseTypePlan("CourserMsgActivity", orgId, classifyId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseList.clear();
                        courseList.addAll(data.getData());
                        listAdapter.setData(courseList);
                        if (data.getData().size() > 0) {
                            ivDelCourse.setVisibility(View.GONE);
                            tvHintDrag.setVisibility(View.VISIBLE);
                            tvNoCourse.setVisibility(View.GONE);
                            rvBoardView.setVisibility(View.VISIBLE);
                        } else {
                            if (courseType.getClassifyName().contains("无")) {//是无课程分类，不能删除
                                ivDelCourse.setVisibility(View.GONE);
                            } else {
                                ivDelCourse.setVisibility(View.VISIBLE);
                            }
                            tvHintDrag.setVisibility(View.GONE);
                            tvNoCourse.setVisibility(View.VISIBLE);
                            rvBoardView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserMsgActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserMsgActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void setOnItemLongClickListener(View view, int postion) {
        longView = view;
        Intent intent = new Intent();
        ClipData clipData = ClipData.newIntent("label", intent);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(clipData, shadowBuilder, rvBoardView, 0);
        //震动反馈
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

        final int pos = recyclerView.getChildAdapterPosition(view);
//        final CourseEntity item = courseList.remove(pos);

        //拖拽事件
        recyclerView.setOnDragListener(new View.OnDragListener() {

            int prevPos = pos;

            @Override
            public boolean onDrag(View v, DragEvent event) {

                //v 永远是设置该监听的view，这里即fl_blue
                String simpleName = v.getClass().getSimpleName();
//                Log.w(BLUE, "view name:" + simpleName);

                //获取事件
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.i(BLUE, "开始拖拽");
                        longView.setVisibility(View.INVISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.i(BLUE, "结束拖拽");
                        longView.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.i(BLUE, "拖拽的view进入监听的view时");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.i(BLUE, "拖拽的view离开监听的view时");
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        float x = event.getX();
                        float y = event.getY();

                        View onTopOf = recyclerView.findChildViewUnder(event.getX(), event.getY());
                        int i = recyclerView.getChildAdapterPosition(onTopOf);
                        if (courseTypeList.size() - 1 != i) {// 不为最后一个（添加课程分类）
                            courseTypeAdapter.setSelItem(i);// 控制篮框显示
                        }
                        if (event.getY() > DensityUtil.getScreenHeightDp(CourserMsgActivity.this)) {// 滑到屏幕底部向下滑
                            recyclerView.scrollToPosition(i + 1);
                        } else if (event.getY() < DensityUtil.getStatusBarHeight(CourserMsgActivity.this)) {// 滑到屏幕顶部向上滑
                            if (i > 0) {
                                recyclerView.scrollToPosition(i - 1);
                            } else {
                                recyclerView.scrollToPosition(0);
                            }
                        }

                        prevPos = i;
                        Log.i(BLUE, "拖拽的view在BLUE中的位置:x =" + x + ",y=" + y + "position:" + prevPos);
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.i(BLUE, "释放拖拽的view");

                        View underView = recyclerView.findChildViewUnder(event.getX(), event.getY());
                        int underPos = recyclerView.getChildAdapterPosition(underView);
                        if (courseTypeList.size() - 1 != underPos && underPos > -1) {// 不为最后一个（添加课程分类）
                            String classifyId = courseTypeList.get(underPos).getClassifyId();
                            String courseIds = courseList.get(postion).getCourseId();

                            if (!UserManager.getInstance().isTrueRole("pk_1")) {
                                UserManager.getInstance().showSuccessDialog(CourserMsgActivity.this
                                        , getString(R.string.text_role));
                            } else {
                                deitLessonType(classifyId, courseIds);
                            }

                        }
                        Log.i(BLUE, "拖拽的view在BLUE中的位置:x =" + underPos);
                        break;
                }
                //是否响应拖拽事件，true响应，返回false只能接受到ACTION_DRAG_STARTED事件，后续事件不会收到
                return true;
            }
        });
    }

    // 57.修改一个排课的课程分类
    private void deitLessonType(String classifyId, String courseIds) {
        HttpManager.getInstance().doDeitLessonType("CourserMsgActivity", orgId, classifyId, courseIds,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseTypeAdapter.setSelItem(-1);// 修改成功篮框隐藏
                        getCoursesType();//获取课程种类
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserMsgActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserMsgActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.REFERSH_COURSE_TYPE:
                getCoursesType();//重新同步获取课程种类
                break;
        }
    }
}
