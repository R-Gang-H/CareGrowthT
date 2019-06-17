package com.caregrowtht.app.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.uitil.BitmapUtils;
import com.caregrowtht.app.uitil.ImgLabelUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.google.zxing.WriterException;

import butterknife.BindView;

/**
 * 机构介绍
 * Created by haoruigang on 2018-4-3 16:09:22.
 */

public class OrgIntroFragment extends BaseFragment {

    @BindView(R.id.tv_orgIntro)
    TextView tvOrgIntro;
    @BindView(R.id.img_org_qrcode)
    ImageView imgOrgQrcode;
    @BindView(R.id.tv_links_web)
    TextView tvLinksWeb;
    private BaseDataModel<OrgEntity> data;
    private String intro;
    private String orgWebsite;
    private String orgQRCodeUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_org_intro;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    public void initData() {

    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.ORG_INTRO_EVENT) {
            data = (BaseDataModel<OrgEntity>) event.getObj();
            intro = data.getData().get(0).getIntro();
            orgWebsite = data.getData().get(0).getOrgWebsite();
            orgQRCodeUrl = data.getData().get(0).getOrgQRCodeUrl();
            ImgLabelUtils.getInstance().htmlThree(tvOrgIntro, intro);//加载Html富文本及图片
            Bitmap bitmap = null;
            try {
                bitmap = BitmapUtils.create2DCode(orgQRCodeUrl);//根据内容生成二维码
                imgOrgQrcode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            tvLinksWeb.setText(orgWebsite);
        }
    }
}
