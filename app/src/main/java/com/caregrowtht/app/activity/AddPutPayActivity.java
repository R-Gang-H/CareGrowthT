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

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NotifyCardAdapter;
import com.caregrowtht.app.model.NotifyCardEntity;
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
public class AddPutPayActivity extends BaseActivity implements ViewOnItemClick {

    ArrayList<NotifyCardEntity> addTypeCards = new ArrayList<>();

    private int[] addImage = new int[]{R.mipmap.ic_put_btn, R.mipmap.ic_pay_btn};
    private String[] addName = new String[]{"收一笔", "支一笔"};//收与支 1：收一笔 2：支一笔

    @BindView(R.id.recycler_view)
    GridRecyclerView recyclerView;

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
        setupRecyclerView(2);
        runLayoutAnimation(recyclerView, R.anim.grid_layout_animation_from_bottom);
    }

    private void setupRecyclerView(int length) {
        final Context context = recyclerView.getContext();
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.margin_size_60);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        addTypeCards.clear();
        for (int i = 0; i < length; i++) {
            addTypeCards.add(new NotifyCardEntity(addImage[i], addName[i]));
        }
        recyclerView.setAdapter(new NotifyCardAdapter(addTypeCards, this, this));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing, spacing / 2,
                0, spacing / 2));
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
        //1：收一笔 2：支一笔
        startActivity(new Intent(this, PutOrPayActivity.class)
                .putExtra("type", String.valueOf(postion + 1)));
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
