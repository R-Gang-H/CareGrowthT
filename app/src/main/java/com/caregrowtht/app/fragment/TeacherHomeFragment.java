package com.caregrowtht.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.android.library.view.CircleImageView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BuyActivity;
import com.caregrowtht.app.activity.FormalActivity;
import com.caregrowtht.app.activity.MoreActivity;
import com.caregrowtht.app.activity.NewWorkActivity;
import com.caregrowtht.app.activity.OrgInfoActivity;
import com.caregrowtht.app.activity.OrgNotifyActivity;
import com.caregrowtht.app.adapter.CourseDownAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.Setter;

import static android.app.Activity.RESULT_OK;

/**
 * Created by haoruigang on 2018/4/23 14:22.
 * 老师添加的机构（教师主页）
 */

public class TeacherHomeFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_org_status)
    TextView tvOrgStatus;
    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.cv_name_bg)
    CardView cvNameBg;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;
    @BindView(R.id.tv_auditing)
    TextView tvAuditing;
    @BindView(R.id.rl_no_org)
    RelativeLayout rlNoOrg;
    @BindView(R.id.rl_no_unpaid)
    RelativeLayout rlNoUnpaid;
    @BindView(R.id.ll_yes_org)
    View llYesOrg;
    @BindView(R.id.tag)
    TextView tag;
    @BindView(R.id.iv_add)
    ImageView ivAdd;

    @BindViews({R.id.btn_my_course, R.id.btn_student, R.id.btn_org_course, R.id.btn_act})
    List<Button> radioButtons;
    private int position;

    private MyCourseFragment myCouFrag;

    private String orgId;
    private int pos = 0;
    private OrgEntity orgEntity;
    private int type = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_teacher_home;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            //清空数据,重新添加数据
            UserManager instance = UserManager.getInstance();
            instance.getRoleEntityList(null).clear();
            instance.getOrgEntityList(null).clear();
            type = 1;
            initData();
        });

        radioButtons.get(0).setOnClickListener(this);
        radioButtons.get(1).setOnClickListener(this);
        radioButtons.get(2).setOnClickListener(this);
        radioButtons.get(3).setOnClickListener(this);
    }

    @Override
    public void initData() {
        if (getArguments() != null) {
            orgId = getArguments().getString("orgId");//机构id
        }
        getOrgInfo();
    }


    private void getOrgInfo() {
        if (StrUtils.isEmpty(orgId)) {
            U.showToast("请输入机构代码");
            return;
        }
        //获取机构主页详情 haoruigang on 2018-4-4 16:09:48
        HttpManager.getInstance().doOrgInfo("TeacherHomeFragment", orgId, "1",
                new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        LogUtils.d("TeacherHomeFragment onSuccess", data.getData().toString());
                        orgEntity = data.getData().get(0);
                        if (orgEntity != null) {
                            // 79.获取机构权限配置
                            UserManager.getInstance().getOrgRole(getActivity(), orgEntity);

                            UserManager.getInstance().getOrgEntityList(orgEntity);

                            // 教务老师和管理者，进去教师端默认是机构课表，教学老师默认是我的课表
                            if (orgEntity.getIdentity().contains("教务") || orgEntity.getIdentity().contains("管理")) {
                                type = 2;
                                radioButtons.get(0).setText("机构课表");//选择的选项内容展示
                            }
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.COURSE_TYPE, type));

                            setChioceItem(position);

                            tvOrgName.setText(String.format("%s", TextUtils.isEmpty(orgEntity.getOrgChainName()) ?
                                    orgEntity.getOrgName() : orgEntity.getOrgName() + orgEntity.getOrgChainName()));

                            if (!TextUtils.isEmpty(orgEntity.getOrgImage())) {
                                GlideUtils.setGlideImg(getActivity(), orgEntity.getOrgImage(), R.mipmap.ic_logo_default, ivAvatar);
                                cvNameBg.setVisibility(View.GONE);
                                ivAvatar.setVisibility(View.VISIBLE);
                            } else {
                                ivAvatar.setVisibility(View.GONE);
                                cvNameBg.setVisibility(View.VISIBLE);
                                String orgShortName = orgEntity.getOrgShortName();
                                UserManager.getInstance().getOrgShortName(tvName, orgShortName);// 设置机构简称
                                tvName.setTextColor(mContext.getResources().getColor(R.color.white));
                            }

                            if (TextUtils.equals(orgEntity.getStatus(), "1")) {//审核状态 1审核通过 2待审核 3审核不通过
                                rlNoOrg.setVisibility(View.GONE);
                                tvAuditing.setText(orgEntity.getIdentity());
                                refreshLayout.setEnableRefresh(true);
                                String endAt = orgEntity.getEnd_at();
                                if (StrUtils.isEmpty(endAt) || endAt.equals("0")
                                        || Long.parseLong(endAt) <
                                        TimeUtils.getCurTimeLong() / 1000) {// 未购买或未续费,过期
                                    tvOrgStatus.setVisibility(View.GONE);
                                    llYesOrg.setVisibility(View.GONE);
                                    ivAdd.setVisibility(View.GONE);
                                    rlNoUnpaid.setVisibility(View.VISIBLE);
                                } else {
                                    Long moday = DateUtil.getStringToDate(
                                            TimeUtils.dateTiem(
                                                    DateUtil.getDate(Long.parseLong(endAt), "yyyy-MM-dd")
                                                    , -20, "yyyy-MM-dd"), "yyyy-MM-dd");// 20之前的时间戳
                                    Long day = TimeUtils.getCurTimeLong() / 1000;// 今天的时间戳
                                    tvOrgStatus.setVisibility(moday < day ? View.VISIBLE : View.GONE);
                                    tvOrgStatus.setText(String.format("您的爱成长使用过期时间：%s\t\t请您尽快续费"
                                            , TimeUtils.getDateToString(Long.parseLong(endAt)
                                                    , "yyyy/MM/dd")));
                                    llYesOrg.setVisibility(View.VISIBLE);
                                    ivAdd.setVisibility(View.VISIBLE);
                                    rlNoUnpaid.setVisibility(View.GONE);
                                }
                            } else if (TextUtils.equals(orgEntity.getStatus(), "2")) {
                                rlNoOrg.setVisibility(View.VISIBLE);
                                tvAuditing.setText("待审核");
                                tag.setText("等待机构审核通过");
                                llYesOrg.setVisibility(View.GONE);
                                ivAdd.setVisibility(View.GONE);
                                rlNoUnpaid.setVisibility(View.GONE);
                                refreshLayout.setEnableRefresh(true);
                            } else {
                                UserManager.getInstance().isNoPass(orgId);
                                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_HOME_EVENT));
                            }
                        }
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherHomeFragment onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(getActivity());
                        }  //                    U.showToast(errorMsg);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TeacherHomeFragment onError", throwable.getMessage());
                    }
                });
    }

    private void setChioceItem(int index) {
        this.position = index;
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        hideFragments(ft);
        UserManager.apply(radioButtons, TABSPEC, radioButtons.get(index));
        switch (index) {
            case 0:
                pos = 0;
                // 我的课程
                if (null == myCouFrag) {
                    myCouFrag = new MyCourseFragment();
                    ft.add(R.id.ll_content, myCouFrag);
                } else {
                    ft.show(myCouFrag);
                }
                break;
            case 1:
                // 学员
                startActivityForResult(new Intent(getActivity(), FormalActivity.class)
                        .putExtra("orgId", orgId)
                        .putExtra("position", pos), 9625);
                break;
            case 2:
//                // 课程统计
//                startActivityForResult(new Intent(getContext(), CourseRecordActivity.class).putExtra(
//                        "orgId", orgId).putExtra("position", pos), 9625);
                // 机构通知管理
                startActivityForResult(new Intent(getContext(), OrgNotifyActivity.class).putExtra(
                        "orgId", orgId).putExtra("position", pos), 9625);
                break;
            case 3:
                // 更多
                startActivityForResult(new Intent(getActivity(), MoreActivity.class).putExtra(
                        "orgId", orgId).putExtra("position", pos), 9625);
                break;
        }
        ft.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (null != myCouFrag)
            ft.hide(myCouFrag);
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    private final Setter<Button, Button> TABSPEC = (view, value, index) -> {
        if (view.getId() == value.getId()) {
            view.setSelected(true);
        } else {
            view.setSelected(false);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_my_course:
                showListPopulWindow(radioButtons.get(0));//工作课表切换
                break;
            case R.id.btn_student:
                setChioceItem(1);
                break;
            case R.id.btn_org_course:
                setChioceItem(2);
                break;
            case R.id.btn_act:
                setChioceItem(3);
                break;
        }
    }


    @OnClick({R.id.iv_avatar, R.id.cv_name_bg, R.id.tv_org_status, R.id.btn_pay_base, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_avatar:
            case R.id.cv_name_bg:
                //机构信息
                startActivity(new Intent(getActivity(), OrgInfoActivity.class));
                break;
            case R.id.tv_org_status:
            case R.id.btn_pay_base:
                startActivity(new Intent(getActivity(), BuyActivity.class)
                        .putExtra("renew", true));
                break;
            case R.id.iv_add://新建排课
                if (!UserManager.getInstance().isTrueRole("zy_1")) {
                    UserManager.getInstance().showSuccessDialog(mActivity
                            , mContext.getString(R.string.text_role));
                    break;
                } else {
                    startActivity(new Intent(getActivity(), NewWorkActivity.class));
                    getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 9625) {
            getOrgInfo();
            int position = data.getIntExtra("position", 0);
            setChioceItem(position);
        }
    }

    /**
     * 工作-课表下拉
     */
    private void showListPopulWindow(Button rvRela) {
        List<String> courseType = Arrays.asList("我的课程", "机构课程");
        String[] teacherOrgs = UserManager.getInstance().userData.getOrgIds().split(",");//分隔教师机构id
        if (teacherOrgs.length > 1) {//两个以上机构显示跨机构课程
            List<String> arryList = new ArrayList<>(courseType);
            arryList.add("跨机构课程");
            courseType = arryList;
        }
        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setBackgroundDrawable(null);
        CourseDownAdapter mAdapter = new CourseDownAdapter(getActivity(), R.layout.item_course_down, courseType, type);
        listPopupWindow.setAdapter(mAdapter);//用android内置布局，或设计自己的样式
        listPopupWindow.setAnchorView(rvRela);//以哪个控件为基准，在该处以logId为基准
        listPopupWindow.setModal(true);
        List<String> finalCourseType = courseType;
        //设置项点击监听
        listPopupWindow.setOnItemClickListener((adapterView, view, position, l) -> {
            radioButtons.get(0).setText(finalCourseType.get(position));//选择的选项内容展示
            type = position + 1;//1：我的课表 2：机构课表 3：跨机构课表

            setChioceItem(0);
            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.COURSE_TYPE, type));

            mAdapter.getSelect(position);
            mAdapter.notifyDataSetChanged();

            listPopupWindow.dismiss();//如果已经选择了，隐藏起来
        });
        listPopupWindow.show();//把ListPopWindow展示出来
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.COURSE_TYPE:
                type = (int) event.getObj();
                if (type == 1) {
                    radioButtons.get(0).setText("我的课程");//选择的选项内容展示
                } else if (type == 2) {
                    radioButtons.get(0).setText("机构课程");//选择的选项内容展示
                }
                break;
            case ToUIEvent.REFERSH_TEACHER_HOME:
                initData();
                break;
        }
    }

}
