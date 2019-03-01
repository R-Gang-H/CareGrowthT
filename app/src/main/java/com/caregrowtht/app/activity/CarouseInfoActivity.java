package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CarouselEntity;
import com.caregrowtht.app.uitil.ImgLabelUtils;
import com.caregrowtht.app.view.RecoDialog;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-8 10:55:04 轮播图详情
 */
public class CarouseInfoActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_courseName)
    TextView tvCourseName;
    @BindView(R.id.tv_course_detail)
    TextView tvCourseDetail;
    private CarouselEntity carouselEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_carouse_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("轮播图详情");
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.ic_share);

        //解决NetworkOnMainThreadException异常
        ImgLabelUtils.getInstance().struct();
    }

    @Override
    public void initData() {
        carouselEntity = (CarouselEntity) getIntent().getSerializableExtra("carouselEntity");
        setData(carouselEntity);
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                UMImage image = new UMImage(this, carouselEntity.getImage());
                //轮播图分享
                String shareUrl = "http://wechat.acz.1bu2bu.com/index.php/Home/Org/carousel?id=" + carouselEntity.getBannerId();
                Log.e("----------", "sharUrl===" + shareUrl);
                UMWeb web = new UMWeb(shareUrl);
                web.setTitle(carouselEntity.getText());//标题
                web.setThumb(image);  //缩略图
//                web.setDescription(carouselEntity.getContent());//描述
                web.setDescription("轮播图");//描述
                new RecoDialog(this, web, view1 -> {
                }).show();
                break;
        }
    }

    /**
     * 课程详情 数据处理
     * haoruigang on 2018-4-8 11:57:37
     *
     * @param data
     */
    public void setData(CarouselEntity data) {
        Glide.with(CarouseInfoActivity.this).load(data.getImage()).into(ivLogo);
        tvCourseName.setText(data.getText());
        if (TextUtils.equals(data.getType(), "1")) {//1：第三方链接 2：富文本

        } else {
            ImgLabelUtils.getInstance().htmlThree(tvCourseDetail, data.getContent());//加载Html富文本及图片
        }
    }
}
