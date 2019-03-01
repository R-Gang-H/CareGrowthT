package com.caregrowtht.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NotifyCardAdapter;
import com.caregrowtht.app.model.NotifyCardEntity;
import com.caregrowtht.app.view.xrecyclerview.GridRecyclerView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-7-30 11:42:55
 * 新建通知
 */
public class NewNotifyActivity extends BaseActivity implements ViewOnItemClick {

    ArrayList<NotifyCardEntity> notifyCards = new ArrayList<>();

    private String[] notifyType = new String[]{"1", "2", "3", "4", "5", "6"};//通知的类型 1：自定义 2：放假通知 3：班级通知 4：教师通知 5：学员通知 6：全体通知
    private int[] notifyImage = new int[]{R.mipmap.ic_custom, R.mipmap.ic_holiday_notify, R.mipmap.ic_class_notify, R.mipmap.ic_teacher_notify, R.mipmap.ic_stu_notify, R.mipmap.ic_all_notify};
    private String[] notifyName = new String[]{"自定义", "标星学员通知", "班级通知", "教师通知", "学员通知", "全体通知"};//通知的类型 1：自定义 2：放假通知 3：班级通知 4：教师通知 5：学员通知 6：全体通知

    @BindView(R.id.recycler_view)
    GridRecyclerView recyclerView;
    @BindView(R.id.ib_colse)
    ImageButton ibColse;

    private String OrgId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_notify;
    }

    @Override
    public void initView() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;

        setupRecyclerView();
        runLayoutAnimation(recyclerView, R.anim.grid_layout_animation_from_bottom);
    }

    private void setupRecyclerView() {
        final Context context = recyclerView.getContext();
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.margin_size_20);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        notifyCards.clear();
        for (int i = 0; i < notifyType.length; i++) {
            notifyCards.add(new NotifyCardEntity(notifyType[i], notifyImage[i], notifyName[i]));
        }
        recyclerView.setAdapter(new NotifyCardAdapter(notifyCards, this, this));
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
        //通知的类型 // 1：自定义 // 2：放假通知 3：班级通知 4：教师通知 5：学员通知 6：全体通知
        startActivity(new Intent(this, CustomActivity.class)
                .putExtra("orgId", OrgId)
                .putExtra("notifyType", notifyType[postion]));
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
            outRect.set(mSpacing, mSpacing, mSpacing, mSpacing);
        }
    }

    @Override
    public void initData() {
        OrgId = getIntent().getStringExtra("OrgId");
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
