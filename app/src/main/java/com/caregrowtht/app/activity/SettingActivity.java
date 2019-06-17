package com.caregrowtht.app.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.CleanCacheUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.GradientUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.DrawableCenterTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-7-5 15:27:19
 * 个人设置
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SettingActivity extends BaseActivity {

    @BindView(R.id.lv_setting)
    ListView mListView;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    ArrayList<String> mArrSettingOptions;
    @BindView(R.id.btn_edit)
    ImageView btnEdit;
    @BindView(R.id.iv_head_icon)
    AvatarImageView ivHeadIcon;
    @BindView(R.id.tv_user_nickname)
    TextView tvUserNickname;

    private SettingAdapter pAdapter;

    private int[] iconSetting = {R.mipmap.ic_make_org, R.mipmap.ic_make_pwd, R.mipmap.ic_feedback,
            R.mipmap.ic_clear_cache, R.mipmap.ic_about_my, R.mipmap.ic_logout};

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        //动态改变“colorPrimaryDark”来实现沉浸式状态栏
        GradientUtils.setColor(SettingActivity.this, R.drawable.mine_title_bg, true);

        mArrSettingOptions = new ArrayList<>();
        mArrSettingOptions.add("我的机构");
        mArrSettingOptions.add("修改密码");
        mArrSettingOptions.add("意见反馈");
        mArrSettingOptions.add("清除缓存");
        mArrSettingOptions.add("关于爱成长");
        mArrSettingOptions.add("退出登录");

        pAdapter = new SettingAdapter();
        mListView.setAdapter(pAdapter);

        pAdapter.setSettingListener(index -> {
            switch (index) {
                case 0://我的机构
                    startActivity(new Intent(SettingActivity.this, MyFollowActivity.class));
                    break;
                case 1://修改密码
                    startActivity(new Intent(this, RegActivity.class).putExtra("type", "2"));
                    break;
                case 2://意见反馈
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, AppFeedbackActivity.class));
                    break;
                case 3://清除缓存
                    SettingActivity.this.CleanCache();
                    break;
                case 4://关于爱成长
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, AboutUsActivity.class));
                    break;
                case 5://登出
                    showLogoutDialog();
                    break;
            }
        });
    }

    @Override
    public void initData() {
        tvTitle.setText(UserManager.getInstance().userData.getNickname());
        String imageUrl = UserManager.getInstance().userData.getHeadImage();
        ivHeadIcon.setTextAndColor(TextUtils.isEmpty(UserManager.getInstance().userData.getName()) ? "" :
                        UserManager.getInstance().userData.getName().substring(0, 1),
                getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(SettingActivity.this, imageUrl, 0, ivHeadIcon);
        String nickname = UserManager.getInstance().userData.getNickname();
        Log.e("------------", "nickname=" + nickname);
        if (nickname != null && !nickname.equals("")) {
            tvUserNickname.setText(nickname);
        } else {
            tvUserNickname.setText("暂无");
        }
    }

    // 退出弹框
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否退出登录？")
                .setPositiveButton("确定", (d, which) -> {
                    HttpManager.getInstance().dologout(this);
                })
                .setNegativeButton("取消", (d, which) -> d.dismiss())
                .create().show();
    }


    // 清除缓存
    private void CleanCache() {
        Dialog dialog = new AlertDialog.Builder(SettingActivity.this)
                .setTitle("提示")
                .setMessage("是否清除缓存？")
                .setPositiveButton("确定", (dialog1, which) -> {
                    CleanCacheUtil.clearAllCache(SettingActivity.this);
                    pAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss())
                .create();
        dialog.show();
    }

    @OnClick({R.id.iv_left, R.id.btn_edit, R.id.iv_head_icon})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left://返回按钮
                finish();
                break;
            case R.id.btn_edit:
            case R.id.iv_head_icon://设置个人信息
                Intent pIntent = new Intent(SettingActivity.this, SetInfoActivity.class);
                pIntent.putExtra("type", "edit");
                startActivity(pIntent);
                break;
        }
    }


    public class SettingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mArrSettingOptions.size();
        }

        @Override
        public Object getItem(int i) {
            return mArrSettingOptions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            SettingHolder holder;

            if (view == null) {
                holder = new SettingHolder();
                view = View.inflate(SettingActivity.this, R.layout.item_setting_cell, null);
                view.setTag(holder);
            } else {
                holder = (SettingHolder) view.getTag();
            }

            holder.rlSetting = view.findViewById(R.id.rl_setting);
            holder.mSettingBtn = view.findViewById(R.id.tv_setting);
            holder.mArrowView = view.findViewById(R.id.tv_setting_arrow);
            holder.mSeperateLine = view.findViewById(R.id.lv_seperate_line);

            if (i == 1 || i == 5) {
                holder.mSeperateLine.setVisibility(View.VISIBLE);
            } else {
                holder.mSeperateLine.setVisibility(View.GONE);
            }
            holder.mSettingBtn.setVisibility(View.VISIBLE);
            holder.mArrowView.setVisibility(View.VISIBLE);
            holder.mSettingBtn.setText(mArrSettingOptions.get(i));//设置文字

            Drawable drawable = view.getResources().getDrawable(iconSetting[i]);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mSettingBtn.setCompoundDrawables(drawable, null, null, null);

            holder.rlSetting.setOnClickListener(view1 -> {
                if (mSettingListener != null) {
                    mSettingListener.SettingBtnClick(i);
                }
            });
            return view;
        }

        class SettingHolder {
            RelativeLayout rlSetting;
            DrawableCenterTextView mSettingBtn;
            ImageView mArrowView;
            RelativeLayout mSeperateLine;
        }

        public void setSettingListener(SettingListener mSettingListener) {
            this.mSettingListener = mSettingListener;
        }

        SettingListener mSettingListener;
    }

    interface SettingListener {
        void SettingBtnClick(int index);
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.REFERSH_TEACHER:
                initData();
                break;
        }
    }

}
