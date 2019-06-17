package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.AuditAdapter;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.view.LoadingFrameView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018年10月17日17:48:26
 * 审核学员
 */
public class AuditActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private ArrayList<StudentEntity> auditData = new ArrayList<>();
    private AuditAdapter auditAdapter;
    private String type;

    @Override
    public int getLayoutId() {
        return R.layout.activity_audit;
    }

    @Override
    public void initView() {
        type = getIntent().getStringExtra("type");
        if (TextUtils.equals(type, "1")) {//type 1: 添加学员 2: 添加教师
            tvTitle.setText("审核学员");
        } else if (TextUtils.equals(type, "2")) {
            tvTitle.setText("审核教师");
        }
        initRecyclerView(recyclerView, true);
        auditAdapter = new AuditAdapter(auditData, this, type);
        recyclerView.setAdapter(auditAdapter);
    }

    @Override
    public void initData() {
        auditData = (ArrayList<StudentEntity>) getIntent().getSerializableExtra("auditData");
        auditAdapter.setData(auditData);
        if (auditData.size() > 0) {
            loadView.delayShowContainer(true);
        } else {
            loadView.setNoShown(true);
        }
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
