package com.caregrowtht.app.activity;

import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NotifyCardAdapter;
import com.caregrowtht.app.model.NotifyCardEntity;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.RecoDialog;
import com.caregrowtht.app.view.xrecyclerview.ItemOffsetDecoration;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.Setter;

import static com.caregrowtht.app.uitil.AssetCopyer.copyBigDataToSD;

/**
 * 批量添加
 */
public class AddBacthActivity extends BaseActivity implements View.OnClickListener, ViewOnItemClick {

    @BindView(R.id.tv_bacth_title)
    TextView tvBacthTitle;
    @BindView(R.id.tv_add_bacth)
    TextView tvAddBacth;
    @BindView(R.id.rl_add_bacth)
    RelativeLayout rlAddBacth;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindViews({R.id.tv_num_toll, R.id.tv_money_toll, R.id.tv_time_toll})
    List<TextView> radioButtons;

    ArrayList<NotifyCardEntity> shareCards = new ArrayList<>();

    private int[] shareImage = new int[]{R.mipmap.icon_email_bacth, R.mipmap.icon_wechat_bacth, R.mipmap.icon_download_bacth};//
    private String[] shareName = new String[]{"发送邮件", "转发微信", "下载到文件"};//
    private int type;
    String fileName = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_bacth;
    }

    @Override
    public void initView() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.margin_size_14);
        initRecyclerView(recyclerView, false);
        shareCards.clear();
        for (int i = 0; i < shareImage.length; i++) {
            shareCards.add(new NotifyCardEntity(shareImage[i], shareName[i]));
        }
        recyclerView.setAdapter(new NotifyCardAdapter(shareCards, this, this));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing, 0, 0, 0));
    }

    @Override
    public void initData() {
        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {// 批量添加教师
            tvBacthTitle.setText("批量添加教师");
            tvAddBacth.setVisibility(View.VISIBLE);
            rlAddBacth.setVisibility(View.GONE);
            fileName = "初始化-批量导入教师.xlsx";// 默认
        } else {// 批量添加学员
            tvBacthTitle.setText("批量添加学员");
            tvAddBacth.setVisibility(View.GONE);
            rlAddBacth.setVisibility(View.VISIBLE);
            checkStatus(0);
            fileName = "初始化-批量导入学员-按次数收费.xlsx";
        }
        radioButtons.get(0).setOnClickListener(this);
        radioButtons.get(1).setOnClickListener(this);
        radioButtons.get(2).setOnClickListener(this);

    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        SHARE_MEDIA type;
        String path;
        switch (postion) {
            case 0:// 发送邮件
                path = Environment.getExternalStorageDirectory() + File.separator + fileName;
                if (copyBigDataToSD(AddBacthActivity.this, path, fileName)) {
                    new RecoDialog(this, null, new File(path), view1 -> {
                    }).shareMessage();
                } else {
                    U.showToast("发送失败");
                }
                break;
            case 1:// 转发微信
                path = Environment.getExternalStorageDirectory() + File.separator + fileName;
                if (copyBigDataToSD(AddBacthActivity.this, path, fileName)) {
                    type = SHARE_MEDIA.WEIXIN;
                    new RecoDialog(this, null, new File(path), view1 -> {
                    }).share(type);
                } else {
                    U.showToast("转发失败");
                }
                break;
            case 2:// 下载文件
                path = Environment.getExternalStorageDirectory() + File.separator + fileName;
                if (copyBigDataToSD(AddBacthActivity.this, path, fileName)) {
                    U.showToast("模型文件复制完成");
                    finish();
                    overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                } else {
                    U.showToast("保存失败");
                }
                break;
        }
    }

    @OnClick(R.id.tv_cancel)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_num_toll:
                checkStatus(0);
                fileName = "初始化-批量导入学员-按次数收费.xlsx";
                break;
            case R.id.tv_money_toll:
                checkStatus(1);
                fileName = "初始化-批量导入学员-按金额收课费.xlsx";
                break;
            case R.id.tv_time_toll:
                checkStatus(2);
                fileName = "初始化-批量导入学员-按时间段收费.xlsx";
                break;
        }
    }

    public void checkStatus(int index) {
        UserManager.apply(radioButtons, BTNSPEC, radioButtons.get(index));
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    final Setter<TextView, TextView> BTNSPEC = (view, value, index) -> {
        assert value != null;
        if (view.getId() == value.getId()) {
            view.setSelected(true);
        } else {
            view.setSelected(false);
        }
    };

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
