package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.FCTListAdapter;
import com.caregrowtht.app.user.UserManager;

import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.Setter;

/**
 * 功能列表
 */
public class FCTListActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindViews({R.id.btn_app, R.id.btn_pc, R.id.btn_xcx})
    List<Button> radioButtons;

    String[] fctArray = {"机构教师APP端", "机构教师PC端", "学员端小程序"};
    FCTListAdapter adapter;

    private int position;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fctlist;
    }

    @Override
    public void initView() {
        tvTitle.setText("功能列表");
        initRecyclerView(recyclerView, true);
        adapter = new FCTListAdapter(Arrays.asList(fctArray), this);
        recyclerView.setAdapter(adapter);
        radioButtons.get(0).setOnClickListener(this);
        radioButtons.get(1).setOnClickListener(this);
        radioButtons.get(2).setOnClickListener(this);
        setChioceItem(position);
    }

    public void setChioceItem(int index) {
        this.position = index;
        UserManager.apply(radioButtons, TABSPEC, radioButtons.get(index));
        switch (index) {
            case 0:
                // 机构教师APP端
                recyclerView.scrollToPosition(0);
                break;
            case 1:
                // 机构教师PC端
                recyclerView.scrollToPosition(1);
                break;
            case 2:
                // 学员端小程序
                recyclerView.scrollToPosition(2);
                break;
        }
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    final Setter<TextView, TextView> TABSPEC = (view, value, index) -> {
        if (view.getId() == value.getId()) {
            view.setSelected(true);
        } else {
            view.setSelected(false);
        }
    };

    @Override
    public void initData() {

    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_app:
                setChioceItem(0);
                break;
            case R.id.btn_pc:
                setChioceItem(1);
                break;
            case R.id.btn_xcx:
                setChioceItem(2);
                break;
        }
    }
}
