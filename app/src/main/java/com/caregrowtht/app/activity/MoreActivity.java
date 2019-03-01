package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.MoreAdapter;
import com.caregrowtht.app.adapter.NotifyCardAdapter;
import com.caregrowtht.app.model.NotifyCardEntity;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;
import java.util.Arrays;

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
    @BindView(R.id.rv_more_item)
    RecyclerView rvMoreItem;

    ArrayList<NotifyCardEntity> moreCards = new ArrayList<>();
    //更多管理的类型 1：机构信息 2：教师管理 3：课程管理 4：课时卡管理 5：教室管理 6：课时卡VS排课 7：通知管理
    private String[] moreType = new String[]{"1", "2", "3", "4", "5", "6"};
    private int[] moreImage = new int[]{R.mipmap.ic_org_info, R.mipmap.ic_teacher_msg,
            R.mipmap.ic_course_msg, R.mipmap.ic_course_time_msg, R.mipmap.ic_class_msg,
            R.mipmap.ic_course_vs};
    private String[] moreName = new String[]{"机构信息", "教师管理", "课程管理", "课时卡管理",
            "教室管理", "课时卡VS排课"};
    //更多管理的类型 1：机构信息 2：教师管理 3：课程管理 4：课时卡管理 5：教室管理 6：课时卡VS排课 7：通知管理

    private String[] moreFunct = new String[]{
            "课表具备更多视图和功能", "给学员批量请假", "集中处理当日出勤", "设置教学大纲库",
            "设置机构主页", "更全面的数据统计分析", "批量导入学员", "批量导入教师",
            "新增和修改权限", "教师离职", "激活非活跃学员"};

    private int position;

    @Override
    public int getLayoutId() {
        return R.layout.activity_more;
    }

    @Override
    public void initView() {
        tvTitle.setText("更多");

        setupRecyclerView();
        initRecyclerView(rvMoreItem, true);
        rvMoreItem.setAdapter(new MoreAdapter(Arrays.asList(moreFunct), this));
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
        for (int i = 0; i < moreType.length; i++) {
            moreCards.add(new NotifyCardEntity(moreType[i], moreImage[i], moreName[i]));
        }
        rvMore.setAdapter(new NotifyCardAdapter(moreCards, this, this));
        rvMore.addItemDecoration(new ItemOffsetDecoration(spacing));
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        //更多管理的类型 1：机构信息 2：教师管理 3：课程管理 4：课时卡管理 5：教室管理 6：课时卡VS排课 7：通知管理
        switch (moreType[postion]) {
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
//            case "7":
//                // 机构通知管理
//                startActivity(new Intent(this, OrgNotifyActivity.class));
//                break;
            default:
                //活动
                startActivity(new Intent(this, ActionActivity.class).putExtra(
                        "orgId", UserManager.getInstance().getOrgId()));
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
