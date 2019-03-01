package com.caregrowtht.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NotifyCardAdapter;
import com.caregrowtht.app.model.NotifyCardEntity;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.GridRecyclerView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-9-6 12:05:37
 * 添加学员底部弹框
 */
public class AddStuActivity extends BaseActivity implements ViewOnItemClick {

    ArrayList<NotifyCardEntity> addTypeCards = new ArrayList<>();

    private String[] addType = new String[]{"1", "2"};//学员添加的类型 1：邀请学员 2：添加学员
    private int[] addImage = new int[]{R.mipmap.ic_invite, R.mipmap.ic_add_stu};
    private String[] addName = new String[]{"邀请学员", "添加学员"};//学员添加的类型 1：邀请学员 2：添加学员

    @BindView(R.id.recycler_view)
    GridRecyclerView recyclerView;
    private String type;

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

        type = getIntent().getStringExtra("type");
        if (TextUtils.equals(type, "2")) {//type 1: 添加学员 2: 添加教师
            addName[0] = "邀请教师";
            addName[1] = "添加教师";
        }

        setupRecyclerView();
        runLayoutAnimation(recyclerView, R.anim.grid_layout_animation_from_bottom);
    }

    @Override
    public void initData() {

    }

    private void setupRecyclerView() {
        final Context context = recyclerView.getContext();
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.margin_size_60);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        addTypeCards.clear();
        for (int i = 0; i < addType.length; i++) {
            addTypeCards.add(new NotifyCardEntity(addType[i], addImage[i], addName[i]));
        }
        recyclerView.setAdapter(new NotifyCardAdapter(addTypeCards, this, this));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, final int ResourceId) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, ResourceId);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        // 学员添加的类型 1：邀请学员 2：添加学员
        Bundle bundle = new Bundle();
        bundle.putString("addType", addType[postion]);
        if (postion == 0) {
            startActivity(new Intent(this, InviteStuRegActivity.class)
                    .putExtras(bundle));
        } else {
            if (TextUtils.equals(type, "1")) {////type 1: 添加学员 2: 添加教师
                UserManager.getInstance().getCardStuList().clear();// 清空上次数据
                startActivity(new Intent(this, AddFormalStuActivity.class)
                        .putExtras(bundle));
            } else if (TextUtils.equals(type, "2")) {
                startActivity(new Intent(this, AddTeacherActivity.class)
                        .putExtras(bundle));
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            }
        }
        finish();
    }


    class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mSpacing;

        public ItemOffsetDecoration(int itemOffset) {
            mSpacing = itemOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mSpacing, mSpacing / 2,
                    0, mSpacing / 2);
        }
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
