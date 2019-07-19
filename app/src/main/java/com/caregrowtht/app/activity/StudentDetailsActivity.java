package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.android.library.view.CircleImageView;
import com.android.library.view.ExpandListView;
import com.android.library.view.MyGridView;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NewCardsAdapter;
import com.caregrowtht.app.adapter.StudentCardAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.RelativeEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * 消课记录/学员详情
 */
public class StudentDetailsActivity extends BaseActivity implements ViewOnItemClick {
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_title_left)
    CircleImageView ivTitleLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_head_icon)
    AvatarImageView ivHeadIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_star_text)
    TextView tvStarText;
    @BindView(R.id.tv_the_active)
    TextView tvTheActive;
    @BindView(R.id.tv_course_task)
    TextView tvCourseTask;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_small_name)
    TextView tvSmallName;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.iv_star)
    ImageView ivStar;
    @BindView(R.id.tv_course)
    TextView tvCourse;
    @BindView(R.id.lv_relative)
    ExpandListView lvRelative;
    @BindView(R.id.rv_card)
    RecyclerView rvCard;
    @BindView(R.id.iv_add)
    ImageView ivAdd;

    private String stuId;
    private String orgId;

    NewCardsAdapter mCardsAdapter;
    List<RelativeEntity> sumList = new ArrayList<>();
    List<CourseEntity> mListCard = new ArrayList<>();
    private StudentEntity stuDetails = new StudentEntity();
    ArrayList<StudentEntity> familData = new ArrayList<>();// 家庭共用学员
    private StudentCardAdapter couStuAdapter;
    StudentEntity formalEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_student_details;
    }


    @Override
    public void initView() {
        rlNextButton.setVisibility(View.VISIBLE);
        tvTitle.setText("学员详情");
        tvTitleRight.setText("课程反馈");
        tvTitleRight.setBackgroundResource(R.mipmap.ic_cours_fb);

        initRecyclerView(rvCard, true);
        mCardsAdapter = new NewCardsAdapter(mListCard, this, this, "3", "3");//展示页面 1:选择购买新卡(addType 1：充值缴费 2：购买新卡) 2:新建课时卡种类 3:学员课时卡（addType 3：编辑课时卡）
        rvCard.setAdapter(mCardsAdapter);
    }


    @Override
    public void initData() {
        formalEntity = (StudentEntity) getIntent().getSerializableExtra("StudentEntity");
        stuId = formalEntity.getStuId();
        orgId = UserManager.getInstance().getOrgId();
        getStudentDetails();
    }

    private void setStuStatus() {
        tvTheActive.setVisibility(View.GONE);
        tvCourseTask.setVisibility(View.GONE);
        if (TextUtils.isEmpty(formalEntity.getStatus())) {
            formalEntity.setStatus("1");// 默认活跃学员
        }
        switch (formalEntity.getStatus()) {//状态 0待审核 1活跃 2卡用完了 3卡过期了 4未办卡 5非活跃 6未审核通过
            case "0":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
                tvTheActive.setVisibility(View.VISIBLE);
                tvCourseTask.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void getStudentDetails() {
        HttpManager.getInstance().doStudentDetails("StudentDetailsActivity",
                orgId, stuId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (data != null && data.getData().size() > 0) {
                            stuDetails = data.getData().get(0);
                            setData(stuDetails);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentDetailsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StudentDetailsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                    }
                });
    }

    private void setData(StudentEntity data) {
        formalEntity.setStatus(data.getActiveStatus());
        setStuStatus();

        tvName.setText(data.getStuName());
        String nickName = data.getStuNickname();
        tvSmallName.setVisibility(TextUtils.isEmpty(nickName) ? View.GONE : View.VISIBLE);
        tvSmallName.setText(nickName);
        String birthday = data.getBirthday();
        int Age = Integer.parseInt(DateUtil.getSysTimeType("yyyy")) - Integer.parseInt(DateUtil.getDate(Long.parseLong(birthday), "yyyy"));
        tvAge.setVisibility(TextUtils.isEmpty(birthday) || birthday.equals("0") ? View.GONE : View.VISIBLE);
        tvAge.setText(String.format("%s岁", Age));
        if (!TextUtils.isEmpty(data.getStuName())) {
            ivHeadIcon.setTextAndColor(data.getStuName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
        }
        GlideUtils.setGlideImg(this, data.getStuIcon(), 0, ivHeadIcon);
        ivGender.setImageResource(data.getSex().equals("1") ? R.mipmap.ic_sex_man : R.mipmap.ic_sex_women);
        if (TextUtils.equals("1", data.getIsStar())) {
            ivStar.setImageResource(R.mipmap.ic_cllo_true);
            tvStarText.setText("已标星");
        } else {
            ivStar.setImageResource(R.mipmap.ic_cllo_false);
            tvStarText.setText("未标星");
        }
        tvCourse.setText(data.getRegularCourse());

        sumList = data.getRelativeList();
        CommonAdapter<RelativeEntity> sumListAdapter = new CommonAdapter<RelativeEntity>(StudentDetailsActivity.this, R.layout.item_relative, sumList) {
            @Override
            protected void convert(ViewHolder viewHolder, RelativeEntity item, int position) {
                int relationId = Integer.parseInt(item.getRelationId()) - 1;
                String relationName = Constant.relationArray[(relationId < 0) ? 0 : relationId];
                viewHolder.setText(R.id.tv_name, String.format("%s(%s)", item.getNickname(), relationName));

                final AvatarImageView ivHeadIcon = viewHolder.getView(R.id.iv_avatar);
                ivHeadIcon.setTextAndColor(relationName, mContext.getResources().getColor(R.color.b0b2b6));
                GlideUtils.setGlideImg(StudentDetailsActivity.this, item.getHeadImage(), 0, ivHeadIcon);

                String secretPhone = item.getMobile().replaceAll(
                        "(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                if (!UserManager.getInstance().isTrueRole("xy_2")) {
                    viewHolder.setText(R.id.tv_phone, secretPhone);
                } else {// 查看学员的联系方式 权限
                    if (TextUtils.isEmpty(formalEntity.getWechatIn())) {// 未激活学员
                        viewHolder.setText(R.id.tv_phone, item.getMobile());
                    } else {
                        if (!TextUtils.isEmpty(item.getIs_first()) && item.getIs_first().equals("1")) {// 是否是第一家长is_first 1:是 2:不是
                            viewHolder.setText(R.id.tv_phone, item.getMobile());
                        } else {
                            viewHolder.setText(R.id.tv_phone, secretPhone);
                        }
                    }
                }
            }
        };
        lvRelative.setAdapter(sumListAdapter);

        mListCard.clear();
        mListCard.addAll(data.getCourseCards());
        mCardsAdapter.update(mListCard);
    }

    @OnClick({R.id.rl_back_button, R.id.tv_title_right, R.id.rl_star, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_title_right:
                startActivity(new Intent(this, CourserFeedbackActivity.class)
                        .putExtra("stuDetails", stuDetails)
                        .putExtra("imputType", "1"));// imputType：1:学员详情进入的课程反馈 2:动态进入的记录详情
                break;
            case R.id.rl_star:
                signStar(stuDetails);
                break;
            case R.id.iv_add:
                if (mListCard.size() > 0) {// 有课时卡
                    startActivity(new Intent(this, AddStuCardActivity.class)
                            .putExtra("stuDetails", stuDetails));
                    overridePendingTransition(R.anim.window_out, R.anim.window_back);//底部弹出动画
                } else {// 进入 2：购买新卡
                    startActivity(new Intent(this, NewCardBuyActivity.class)
                            .putExtra("stuDetails", stuDetails)
                            .putExtra("addType", "2"));
                }
                break;
        }
    }

    private void signStar(StudentEntity entity) {
        //20.标星/取消标星学员
        HttpManager.getInstance().doSignStar("StudentDetailsActivity", entity.getStuId(),
                UserManager.getInstance().getOrgId(),
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (!TextUtils.isEmpty(entity.getIsStar()) && entity.getIsStar().equals("1")) {
                            entity.setIsStar("2");
                            ivStar.setImageResource(R.mipmap.ic_cllo_false);
                            tvStarText.setText("未标星");
                        } else {
                            entity.setIsStar("1");
                            ivStar.setImageResource(R.mipmap.ic_cllo_true);
                            tvStarText.setText("已标星");
                        }
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ACTIVE_STU));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentDetailsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StudentDetailsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StudentDetailsActivity onError", throwable.getMessage());
                    }
                });
    }


    @Override
    public void setOnItemClickListener(View view, int postion) {
        if (TextUtils.equals(mListCard.get(postion).getStatus(), "2")) {//状态 1正常 2解除绑定
            selectCard(postion);
        } else {
            //家庭共用(先判断是否有共用学员)
            theSameParent(orgId, stuId, postion);
        }
    }

    //查看课时卡
    private void selectCard(int postion) {
        Intent intent = new Intent(StudentDetailsActivity.this, CourseListActivity.class);
        intent.putExtra("sid", stuId);
        intent.putExtra("cid", mListCard.get(postion).getCardId());
        startActivity(intent);
    }

    /**
     * 学员卡片操作弹框
     */
    private void showCardDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_card_oper, null);
        final TextView tvFamiliar = view.findViewById(R.id.tv_familiar);
        tvFamiliar.setVisibility(familData.size() > 0 ? View.VISIBLE : View.GONE);
        tvFamiliar.setOnClickListener(v -> {
            //家庭共用
            String cardId = mListCard.get(position).getCardId();
            showFamilDialog(familData, cardId);
            dialog.dismiss();
        });
        final TextView tvCourseCard = view.findViewById(R.id.tv_course_card);
        tvCourseCard.setOnClickListener(v -> {
            dialog.dismiss();
            selectCard(position);
        });
        final TextView tvEditCard = view.findViewById(R.id.tv_edit_card);
        tvEditCard.setOnClickListener(v -> {
            dialog.dismiss();
            if (!UserManager.getInstance().isTrueRole("xy_4")) {
                UserManager.getInstance().showSuccessDialog(this
                        , getString(R.string.text_role));
            } else {
                //编辑课时卡
                startActivity(new Intent(this, TimeCardBuyActivity.class)
                        .putExtra("addType", "3")// 学员添加新卡的类型 1：充值缴费 2：购买新卡 3：编辑课时卡
                        .putExtra("CourseCardsEntity", mListCard.get(position))
                        .putExtra("StudentEntity", stuDetails));
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            }
        });
        final TextView tvUnbind = view.findViewById(R.id.tv_unbind);
        tvUnbind.setOnClickListener(v -> {
            if (!UserManager.getInstance().isTrueRole("xy_4")) {
                UserManager.getInstance().showSuccessDialog(this
                        , getString(R.string.text_role));
            } else {
                String[] shareNames = mListCard.get(position).getShareName().split(",");
                if (StrUtils.isNotEmpty(shareNames) && StrUtils.isNotEmpty(shareNames[0]) && shareNames.length > 1) {
                    // 共用学员2个及以上
                    showUnBindDialog(mListCard.get(position));
                } else {// 跳转解除绑定课时卡
                    startActivity(new Intent(this, UnBindCardActivity.class)
                            .putExtra("CourseCardsEntity", mListCard.get(position))
                            .putExtra("StudentEntity", stuDetails));
                }
            }
            dialog.dismiss();
        });
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
    }

    /**
     * 42.返回与学员拥有相同家长的孩子(课时卡共用)
     *
     * @param orgId
     * @param stuId
     * @param position
     */
    private void theSameParent(String orgId, String stuId, int position) {
        String cardId = mListCard.get(position).getCardId();
        HttpManager.getInstance().doTheSameParent("StudentDetailsActivity", orgId, stuId, cardId,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        familData = data.getData();
                        showCardDialog(position);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentDetailsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StudentDetailsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StudentDetailsActivity onError", throwable.getMessage());
                    }
                });
    }

    /**
     * 提示
     *
     * @param courseCardsEntity
     */
    private void showUnBindDialog(CourseEntity courseCardsEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_org, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("提示");
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(R.string.text_dialog_prompt_unbind);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_ok.setOnClickListener(v -> {
            dropChildCard(courseCardsEntity);
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
     * 家庭共用
     *
     * @param studentList
     */
    private void showFamilDialog(ArrayList<StudentEntity> studentList, String cardId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_famil, null);
        MyGridView gvTch = view.findViewById(R.id.gv_tch);
        couStuAdapter = new StudentCardAdapter(this, R.layout.item_stu_situat, studentList, new CourseEntity());//家庭共用
        gvTch.setAdapter(couStuAdapter);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        if (studentList.size() == 0) {
            tv_ok.setVisibility(View.GONE);
        }
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_ok.setOnClickListener(v -> {
            StringBuilder studentIds = new StringBuilder();
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
            courseCardShating(studentIds.toString(), cardId);
            dialog.dismiss();
        });
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    private void courseCardShating(String stuId, String cardId) {
        HttpManager.getInstance().doCourseCardShating("StudentDetailsActivity", stuId, cardId, orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        U.showToast("共用成功");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentDetailsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StudentDetailsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StudentDetailsActivity onError", throwable.getMessage());
                    }
                });
    }

    /**
     * 36.解除绑定课时卡
     *
     * @param courseCardsEntity
     */
    private void dropChildCard(CourseEntity courseCardsEntity) {
        HttpManager.getInstance().doDropChildCard("StudentDetailsActivity", courseCardsEntity.getCardId(),
                stuDetails.getStuId(), "", "", "",
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        U.showToast("解绑成功");
                        getStudentDetails();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentDetailsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StudentDetailsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StudentDetailsActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_STUDENT) {
            initData();
        }
    }
}
