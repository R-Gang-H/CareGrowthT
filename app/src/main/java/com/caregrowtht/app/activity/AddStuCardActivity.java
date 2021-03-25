package com.caregrowtht.app.activity;

/**
 * haoruigang on 2018-08-07 20:30:35
 * 为学员添加新卡
 */

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NotifyCardAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.NotifyCardEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.GridRecyclerView;
import com.caregrowtht.app.view.xrecyclerview.ItemOffsetDecoration;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-08-07 20:36:04
 * 为学员添加新卡
 */
public class AddStuCardActivity extends BaseActivity implements ViewOnItemClick {

    ArrayList<NotifyCardEntity> addTypeCards = new ArrayList<>();

    private int[] addImage = new int[]{R.mipmap.ic_renew, R.mipmap.ic_newcard, R.mipmap.ic_family_share};
    private String[] addName = new String[]{"充值缴费", "购买新卡", "与家人共用"};//学员添加新卡的类型 1：充值缴费 2：购买新卡 3：与家人共用

    @BindView(R.id.recycler_view)
    GridRecyclerView recyclerView;

    private StudentEntity stuDetails;
    private boolean isNoCard, isFarmi;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_stu_card;
    }

    @Override
    public void initView() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
    }

    @Override
    public void initData() {
        stuDetails = (StudentEntity) getIntent().getSerializableExtra("stuDetails");
        isNoCard = getIntent().getBooleanExtra("isNoCard", false);// 全是不可用卡
        getCardByPhone();
    }

    private void setupRecyclerView(int length) {
        final Context context = recyclerView.getContext();
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.margin_size_30);
        int spanCount = 3;
        if (isNoCard && isFarmi) {
            spanCount = 1;
        } else if (isNoCard || isFarmi) {
            spanCount = 2;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));
        addTypeCards.clear();
        for (int i = 0; i < length; i++) {
            if (isNoCard && i == 0) {// 全是不可用卡，不显示充值缴费
                continue;
            }
            addTypeCards.add(new NotifyCardEntity(addImage[i], addName[i]));
        }
        recyclerView.setAdapter(new NotifyCardAdapter(addTypeCards, this, this));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing / 2, spacing,
                spacing / 2, spacing / 2));
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, final int ResourceId) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, ResourceId);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    private void getCardByPhone() {
        String stuId = null;
        if (stuDetails != null) {
            stuId = stuDetails.getStuId();
        }
        String orgId = UserManager.getInstance().getOrgId();
        HttpManager.getInstance().doGetCardByPhone("AddStuCardActivity",
                orgId, stuId, "",
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        int length;
                        isFarmi = !(data.getData().size() > 0);
                        if (data.getData().size() > 0) {// 是否有与家人共用的学员（>0 有）
                            length = addImage.length;// 默认全部显示
                        } else {
                            length = addImage.length - 1;// 不显示与家人共用
                        }
                        setupRecyclerView(length);
                        runLayoutAnimation(recyclerView, R.anim.grid_layout_animation_from_bottom);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddStuCardActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddStuCardActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AddStuCardActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        //学员添加新卡的类型
        if (postion < 2) {//1：充值缴费 2：购买新卡
            if (!UserManager.getInstance().isTrueRole("xy_4")) {
                UserManager.getInstance().showSuccessDialog(this
                        , getString(R.string.text_role));
            } else {
                startActivity(new Intent(this, NewCardBuyActivity.class)
                        .putExtra("stuDetails", stuDetails)
                        .putExtra("addType", String.valueOf(postion + 1)));
            }
        } else if (postion == 2) {//3：与家人共用
            startActivity(new Intent(this, FamilyShareActivity.class)
                    .putExtra("stuDetails", stuDetails)
                    .putExtra("toShare", "2"));// toShare： 1:从添加学员进入 2:从学员详情进入 3:从审核学员进入
            overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
        }
        finish();
    }

    @OnClick(R.id.ib_colse)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_colse:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {
            finish();
            overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

}
