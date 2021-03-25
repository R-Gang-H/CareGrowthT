package com.caregrowtht.app.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.U;
import com.bumptech.glide.Glide;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.GradientUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.uitil.alicloud.oss.AliYunOss;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.orhanobut.logger.Logger;
import com.zxy.tiny.Tiny;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Create by haoruigang on 2018-4-20 14:46:26
 * 设置个人信息
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SetInfoActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_avatar)
    AvatarImageView ivAvatar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_nickName)
    EditText etNickName;
    @BindView(R.id.btn_submit)
    Button mSubmitBtn;


    String mImageName = "";//user/avatar/时间戳+用户id.jpg
    AliYunOss mOssClient;

    private InvokeParam invokeParam;
    private TakePhoto takePhoto;
    private String mType;
    private String nickname, name;
    private String headImage;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_info;
    }

    @Override
    public void initView() {
        //动态改变“colorPrimaryDark”来实现沉浸式状态栏
        GradientUtils.setColor(SetInfoActivity.this, R.drawable.mine_title_bg, true);
        tvTitle.setText(R.string.set_personal_profile);
        mOssClient = new AliYunOss(this);
        mType = getIntent().getStringExtra("type");
    }

    @Override
    public void initData() {
        if (mType != null) {
            //设置默认的预设昵称
//            String secretPhone = UserManager.getInstance().userData.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            etName.setText("");
            etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("beforeTextChanged", s.toString());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d("onTextChanged", s.toString());
                    String nickName = "";
                    if (!TextUtils.isEmpty(s.toString())) {
                        nickName = String.format("%s老师", s.toString().substring(0, 1));
                    }
                    etNickName.setText(nickName);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("afterTextChanged", s.toString());
                }
            });
            etNickName.setText("");
            if (mType.equals("register")) {
                mSubmitBtn.setText("下一步");
            }
            if (mType.equals("edit")) {
                //编辑状态：预设信息
                ivAvatar.setTextAndColor(TextUtils.isEmpty(UserManager.getInstance().userData.getName()) ? "" :
                                UserManager.getInstance().userData.getName().substring(0, 1),
                        getResources().getColor(R.color.b0b2b6));
                if (!StrUtils.isEmpty(UserManager.getInstance().userData.getHeadImage())) {
                    GlideUtils.setGlideImg(this,
                            UserManager.getInstance().userData.getHeadImage(), 0, ivAvatar);
                }
                if (!StrUtils.isEmpty(UserManager.getInstance().userData.getNickname())) {
                    etNickName.setText(UserManager.getInstance().userData.getNickname());
                }
                if (!StrUtils.isEmpty(UserManager.getInstance().userData.getName())) {
                    etName.setText(UserManager.getInstance().userData.getName());
                }
                mSubmitBtn.setText("保存");
            }
        }
    }

    @OnClick({R.id.iv_left, R.id.iv_avatar, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                if (mType.equals("register")) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                finish();
                break;
            case R.id.iv_avatar:
                showPhotoDialog();
                break;
            case R.id.btn_submit:
                setPersonalProfile();
                break;
        }
    }

    private void setPersonalProfile() {
        name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            U.showToast("请输入姓名");
            return;
        }
        nickname = etNickName.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            U.showToast("请输入昵称");
            return;
        }
        //Url
        if (mImageName != null && !mImageName.equals("")) {
            headImage = Constant.OSS_URL + mImageName;
        }
        //haoruigang on 2018-4-20 14:51:17 设置个人信息
        String finalHeadImage = headImage;
        HttpManager.getInstance().doSetProfile("SetInfoActivity",
                name, nickname, headImage,
                new HttpCallBack<BaseDataModel<UserEntity>>(SetInfoActivity.this) {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        UserEntity pNewUserData = UserManager.getInstance().userData;
                        if (!StrUtils.isEmpty(finalHeadImage)) {
                            pNewUserData.setHeadImage(finalHeadImage);
                        }
                        pNewUserData.setNickname(nickname);
                        pNewUserData.setName(name);
                        UserManager.getInstance().save(pNewUserData);
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SetInfoActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void showPhotoDialog() {
        final String items[] = {"拍照", "相册"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择头像");
        builder.setItems(items, (dialog, which) -> {
            dialog.dismiss();
            switch (which) {
                case 0:
                    if (!selfPermissionGranted(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(SetInfoActivity.this,
                                new String[]{Manifest.permission.CAMERA}, 120);
                    } else {
                        takeOrPickPhoto(true);
                    }
                    break;
                case 1:
                    takeOrPickPhoto(false);
                    break;
                default:
                    break;
            }
        });
        builder.create().show();
    }

    private void takeOrPickPhoto(boolean isTakePhoto) {
        File file = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        TakePhoto takePhoto = getTakePhoto();
        configCompress(takePhoto); // 压缩处理
        configTakePhotoOption(takePhoto);
        if (isTakePhoto) { // 拍照
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        } else { // 选取图片
            takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
        }
    }

    /**
     * 配置 裁剪选项
     *
     * @return
     */
    private CropOptions getCropOptions() {
        int height = 100;
        int width = 100;

        CropOptions.Builder builder = new CropOptions.Builder();

        //按照宽高比例裁剪
        builder.setAspectX(width).setAspectY(height);
        //是否使用Takephoto自带的裁剪工具
        builder.setWithOwnCrop(false);
        return builder.create();
    }

    /**
     * 拍照相关的配置
     *
     * @param takePhoto
     */
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //是否使用Takephoto自带的相册
        if (false) {
            builder.setWithOwnGallery(true);
        }
        //纠正拍照的旋转角度
        if (true) {
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        // 拍照或者选图成功
        String mHeaderAbsolutePath = result.getImages().get(0).getOriginalPath();
        Logger.d(mHeaderAbsolutePath);

        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(mHeaderAbsolutePath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
            //return the compressed file path

            new Handler().post(() -> Glide.with(SetInfoActivity.this).load(outfile).into(ivAvatar));

            mImageName = Constant.photo + TimeUtils.getCurTimeLong("yyyyMMddHHmmss") + UserManager.getInstance().userData.getUid() + ".jpg";
            //文件名
            mOssClient.upload(mImageName, outfile, null);
        });
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    /**
     * 配置 压缩选项
     *
     * @param takePhoto
     */
    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 102400;
        int width = 800;
        int height = 800;
        //是否显示 压缩进度条
        boolean showProgressBar = true;
        //压缩后是否保存原图
        boolean enableRawFile = true;
        CompressConfig config;
        if (false) {
            //使用自带的压缩工具
            config = new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            //使用开源的鲁班压缩工具
            LubanOptions option = new LubanOptions.Builder()
                    .setMaxHeight(height)
                    .setMaxWidth(width)
                    .setMaxSize(maxSize)
                    .create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onBackPressed() {
        if (mType.equals("register")) {
            return;
        } else {
            super.onBackPressed();
        }
    }
}
