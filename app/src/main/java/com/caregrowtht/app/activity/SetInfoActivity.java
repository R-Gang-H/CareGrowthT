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
 * ??????????????????
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


    String mImageName = "";//user/avatar/?????????+??????id.jpg
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
        //???????????????colorPrimaryDark??????????????????????????????
        GradientUtils.setColor(SetInfoActivity.this, R.drawable.mine_title_bg, true);
        tvTitle.setText(R.string.set_personal_profile);
        mOssClient = new AliYunOss(this);
        mType = getIntent().getStringExtra("type");
    }

    @Override
    public void initData() {
        if (mType != null) {
            //???????????????????????????
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
                        nickName = String.format("%s??????", s.toString().substring(0, 1));
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
                mSubmitBtn.setText("?????????");
            }
            if (mType.equals("edit")) {
                //???????????????????????????
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
                mSubmitBtn.setText("??????");
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
            U.showToast("???????????????");
            return;
        }
        nickname = etNickName.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            U.showToast("???????????????");
            return;
        }
        //Url
        if (mImageName != null && !mImageName.equals("")) {
            headImage = Constant.OSS_URL + mImageName;
        }
        //haoruigang on 2018-4-20 14:51:17 ??????????????????
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
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
        final String items[] = {"??????", "??????"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????????");
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
        configCompress(takePhoto); // ????????????
        configTakePhotoOption(takePhoto);
        if (isTakePhoto) { // ??????
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        } else { // ????????????
            takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
        }
    }

    /**
     * ?????? ????????????
     *
     * @return
     */
    private CropOptions getCropOptions() {
        int height = 100;
        int width = 100;

        CropOptions.Builder builder = new CropOptions.Builder();

        //????????????????????????
        builder.setAspectX(width).setAspectY(height);
        //????????????Takephoto?????????????????????
        builder.setWithOwnCrop(false);
        return builder.create();
    }

    /**
     * ?????????????????????
     *
     * @param takePhoto
     */
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //????????????Takephoto???????????????
        if (false) {
            builder.setWithOwnGallery(true);
        }
        //???????????????????????????
        if (true) {
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());
    }

    /**
     * ??????TakePhoto??????
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
        //?????????????????????Android6.0???7.0??????????????????
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        // ????????????????????????
        String mHeaderAbsolutePath = result.getImages().get(0).getOriginalPath();
        Logger.d(mHeaderAbsolutePath);

        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(mHeaderAbsolutePath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
            //return the compressed file path

            new Handler().post(() -> Glide.with(SetInfoActivity.this).load(outfile).into(ivAvatar));

            mImageName = Constant.photo + TimeUtils.getCurTimeLong("yyyyMMddHHmmss") + UserManager.getInstance().userData.getUid() + ".jpg";
            //?????????
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
     * ?????? ????????????
     *
     * @param takePhoto
     */
    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 102400;
        int width = 800;
        int height = 800;
        //???????????? ???????????????
        boolean showProgressBar = true;
        //???????????????????????????
        boolean enableRawFile = true;
        CompressConfig config;
        if (false) {
            //???????????????????????????
            config = new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            //?????????????????????????????????
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
