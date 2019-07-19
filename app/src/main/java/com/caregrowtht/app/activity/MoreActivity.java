package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NotifyCardAdapter;
import com.caregrowtht.app.model.NotifyCardEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-19 10:42:04
 * 更多
 */
public class MoreActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_more)
    RecyclerView rvMore;

    ArrayList<NotifyCardEntity> moreCards = new ArrayList<>();
    //更多管理的类型 1：机构信息 2：教师管理 3：课程管理 4：课时卡管理 5：教室管理 6：课时卡VS排课 7：通知管理
    private int[] moreImage = new int[]{R.mipmap.ic_org_info, R.mipmap.ic_teacher_msg,
            R.mipmap.ic_course_msg, R.mipmap.ic_course_time_msg, R.mipmap.ic_class_msg,
            R.mipmap.ic_course_vs, R.mipmap.ic_my_aicz, R.mipmap.ic_app_pc};
    private String[] moreName = new String[]{"机构信息", "教师管理", "课程管理", "课时卡管理",
            "教室管理", "课时卡VS排课", "我的爱成长", "PC端"};
    //更多管理的类型 1：机构信息 2：教师管理 3：课程管理 4：课时卡管理 5：教室管理 6：课时卡VS排课 7：通知管理

    private int position;

    @Override
    public int getLayoutId() {
        return R.layout.activity_more;
    }

    @Override
    public void initView() {
        tvTitle.setText("更多");

        setupRecyclerView();
    }

    @Override
    public void initData() {
        position = getIntent().getIntExtra("position", 0);
    }

    @OnClick({R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                setResult(RESULT_OK, getIntent().putExtras(bundle));
                finish();
                break;
        }
    }

    private void setupRecyclerView() {
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.margin_size_20);
        initRecyclerGrid(rvMore, 3);
        moreCards.clear();
        for (int i = 0; i < moreImage.length; i++) {
            moreCards.add(new NotifyCardEntity(moreImage[i], moreName[i]));
        }
        rvMore.setAdapter(new NotifyCardAdapter(moreCards, this, this));
        rvMore.addItemDecoration(new ItemOffsetDecoration(spacing));
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        //更多管理的类型 1：机构信息 2：教师管理 3：课程管理 4：课时卡管理 5：教室管理 6：课时卡VS排课 7：通知管理
        switch (String.valueOf(postion + 1)) {
            case "1":
                //机构信息
                startActivity(new Intent(this, OrgInfoActivity.class));
                break;
            case "2":
                //教师管理
                startActivity(new Intent(this, TeacherMsgActivity.class));
                break;
            case "3":
                //课程管理
                startActivity(new Intent(this, CourserMsgActivity.class));
                break;
            case "4":
                //课时卡管理
                startActivity(new Intent(this, CourserCardMsgActivity.class));
                break;
            case "5":
                //教室管理
                startActivity(new Intent(this, ClassMsgActivity.class));
                break;
            case "6":
                // 课时卡VS排课
                startActivity(new Intent(this, CourseVsActivity.class));
                break;
            case "7":
                // 我的爱成长（续费）
                startActivity(new Intent(this, BuyActivity.class)
                        .putExtra("renew", true));
                break;
            case "8":
                // PC端
                startActivity(new Intent(this, AppPCActivity.class));
                break;
        }
    }

    class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mSpacing;

        ItemOffsetDecoration(int itemOffset) {
            mSpacing = itemOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mSpacing, mSpacing, 0, 0);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {

            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            setResult(RESULT_OK, getIntent().putExtras(bundle));
            finish();
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

}
