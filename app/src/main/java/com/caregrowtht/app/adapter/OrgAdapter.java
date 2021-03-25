package com.caregrowtht.app.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.view.CircleImageView;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 机构列表
 * Created by haoruigang on 2018-4-18 18:33:29 修复有数据不显示
 */

public class OrgAdapter extends XrecyclerAdapter {

    BaseActivity mContext;

    @BindView(R.id.cv_name_bg)
    CardView cvNameBg;
    @BindView(R.id.iv_logo)
    CircleImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_org_name)
    TextView mOrgName;
    @BindView(R.id.tv_tag)
    TextView mTagView;
    @BindView(R.id.tv_org_status)
    TextView tvOrgStatus;
    @BindView(R.id.tv_address)
    TextView mAddressView;
    @BindView(R.id.btn_call)
    ImageView mCallBtn;

    private ArrayList<OrgEntity> listModel = new ArrayList<>();

    public OrgAdapter(List datas, BaseActivity context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick) {
        super(datas, context, onItemClick1, onItemLongClick);
        mContext = context;
        listModel.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        OrgEntity item = listModel.get(position);
        mOrgName.setText(String.format("%s", TextUtils.isEmpty(item.getOrgChainName()) ?
                item.getOrgName() : item.getOrgName() + item.getOrgChainName()));
        mTagView.setText(item.getTypeStr());
        tvOrgStatus.setVisibility(TextUtils.equals(item.getStatus(), "2") ? View.VISIBLE : View.INVISIBLE);//1 是待审核  2是审核通过
        String OrgImage = item.getOrgImage();
        if (!TextUtils.isEmpty(OrgImage) && OrgImage != null) {
            GlideUtils.setGlideImg(mContext, OrgImage, R.mipmap.ic_logo_default, ivAvatar);
            cvNameBg.setVisibility(View.GONE);
            ivAvatar.setVisibility(View.VISIBLE);
        } else {
            ivAvatar.setVisibility(View.GONE);
            cvNameBg.setVisibility(View.VISIBLE);
            String orgShortName = item.getOrgShortName();
            UserManager.getInstance().getOrgShortName(tvName, orgShortName);// 设置机构简称
            tvName.setTextColor(mContext.getResources().getColor(R.color.white));
        }

//            item.getCname() + "\t" +
        mAddressView.setText(item.getSname() + "-" + item.getAname() + "\t" + item.getAddress());
        mCallBtn.setVisibility(TextUtils.isEmpty(item.getOrgPhone()) ? View.GONE : View.VISIBLE);
        mCallBtn.setOnClickListener(view -> {
            //获取CALL_PHONE权限
            mContext.requestPermission(
                    Constant.RC_CALL_PHONE,
                    new String[]{Manifest.permission.CALL_PHONE},
                    mContext.getString(R.string.rationale_call_phone),
                    new PermissionCallBackM() {
                        @Override
                        public void onPermissionGrantedM(int requestCode, String... perms) {
                            LogUtils.e(mContext, "TODO: CALL_PHONE Granted", Toast.LENGTH_SHORT);
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getOrgPhone()));
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void onPermissionDeniedM(int requestCode, String... perms) {
                            LogUtils.e(mContext, "TODO: CALL_PHONE Denied", Toast.LENGTH_SHORT);
                        }
                    });
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_org;
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public void setData(ArrayList<OrgEntity> argList, Boolean isClear) {
        if (isClear) {
            listModel.clear();
        }
        listModel.addAll(argList);
        notifyDataSetChanged();
    }
}