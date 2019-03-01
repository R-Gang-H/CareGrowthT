package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.android.library.view.CircleImageView;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.JsonBean;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GetJsonDataUtils;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.uitil.alicloud.oss.AliYunOss;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.google.gson.Gson;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.orhanobut.logger.Logger;
import com.zxy.tiny.Tiny;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-13 11:59:24
 * 创建机构
 */
public class CreateOrgActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.et_for_short)
    EditText etForShort;
    @BindView(R.id.et_full_name)
    EditText etFullName;
    @BindView(R.id.et_chain)
    EditText etChain;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.et_detail_address)
    EditText etDetailAddress;
    @BindView(R.id.et_telephone)
    EditText etTelephone;
    @BindView(R.id.et_org_mobile)
    EditText etOrgMobile;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_create)
    Button btnCreate;

    private InvokeParam invokeParam;
    private TakePhoto takePhoto;
    String mImageName = "";//user/avatar/时间戳+用户id.jpg
    AliYunOss mOssClient;

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    //Url
    String headImage = "";
    private String pname, cname, dname;
    private String orgId = "";//该字段有值表示编辑机构信息，空表示创建一个新的机构
    private OrgEntity orgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_org;
    }

    @Override
    public void initView() {
        orgEntity = (OrgEntity) getIntent().getSerializableExtra("orgEntity");// ""是创建 ,不为空是编辑
        if (orgEntity != null) {// 不为空是编辑机构
            tvTitle.setText("编辑机构信息");
            orgId = orgEntity.getOrgId();
            headImage = orgEntity.getOrgImage();
            GlideUtils.setGlideImg(this, orgEntity.getOrgImage(), R.mipmap.ic_logo_default, ivAvatar);
            etForShort.setText(orgEntity.getOrgShortName());
            etFullName.setText(orgEntity.getOrgName());
            etChain.setText(orgEntity.getOrgChainName());
            pname = orgEntity.getSname();
            cname = orgEntity.getCname();
            dname = orgEntity.getAname();
            tvAddress.setText(String.format("%s\t%s\t%s", pname, cname, dname));
            etDetailAddress.setText(orgEntity.getOrgAddress());
            etTelephone.setText(orgEntity.getTelephone());
            etOrgMobile.setText(orgEntity.getOrgPhone());
            btnCreate.setText("确定");
        } else {// ""是创建
            tvTitle.setText("创建新机构");
        }
        mOssClient = new AliYunOss(this);
    }

    @Override
    public void initData() {
        new Thread(() -> {
            // 子线程中解析省市区数据
            initJsonData();
        }).start();
    }

    private void initJsonData() {//解析数据
        String JsonData = new GetJsonDataUtils().getJson(this, "province.json");//获取assets目录下的json文件数据
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @OnClick({R.id.rl_back_button, R.id.rl_avatar, R.id.tv_address, R.id.btn_cancel, R.id.btn_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.rl_avatar:
                takeOrPickPhoto(false);
                break;
            case R.id.tv_address:
                hideKeyboard();
                showPickerView();
                break;
            case R.id.btn_create:
                editOrg();
                break;
        }
    }

    private void editOrg() {
        //47.创建新机构 / 编辑机构信息
        String orgName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(orgName)) {
            U.showToast("请输入机构全称");
            return;
        }
        String orgShortName = etForShort.getText().toString().trim();
        if (TextUtils.isEmpty(orgShortName)) {
            U.showToast("请输入机构简称");
            return;
        }
        if (TextUtils.isEmpty(pname) || TextUtils.isEmpty(cname) || TextUtils.isEmpty(dname)) {
            U.showToast("请选择机构地址");
            return;
        }
        String orgChainName = etChain.getText().toString().trim();
        String address = etDetailAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            U.showToast("请输入详细地址");
            return;
        }
        String telephone = etTelephone.getText().toString().trim();
        String orgPhone = etOrgMobile.getText().toString().trim();
        if (mImageName != null && !mImageName.equals("")) {
            headImage = Constant.OSS_URL + mImageName;
        }
        HttpManager.getInstance().doEditOrg("CreateOrgActivity",
                orgId, orgName, orgShortName, orgChainName, headImage, pname,
                cname, dname, address, telephone, orgPhone,
                new HttpCallBack<BaseDataModel<OrgEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        if (orgEntity == null || orgId == null || orgId.equals("")) {//不为空是创建新机构
                            String[] OrgIds = UserManager.getInstance().userData.getOrgIds().split(",");
                            String OrgId = "";
                            if (OrgIds.length > 0 && !TextUtils.isEmpty(OrgIds[0])) {// 已经有机构
                                OrgId = UserManager.getInstance().userData.getOrgIds() + ",";
                            }
                            OrgId += data.getData().get(0).getOrgId();
                            UserManager.getInstance().userData.setOrgIds(OrgId);
                            UserManager.getInstance().userData.setPassOrgIds(OrgId);
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_HOME_EVENT));
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                            startActivity(new Intent(CreateOrgActivity.this, MainActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CreateOrgActivity.this);
                        } else if (statusCode == 1025) {
                            U.showToast("不能重复绑定!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void showPickerView() {// 城市弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            pname = options1Items.get(options1).getPickerViewText();
            cname = options2Items.get(options1).get(options2);
            dname = options3Items.get(options1).get(options2).get(options3);
            tvAddress.setText(String.format("%s%s%s", pname, cname, dname));
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void takeOrPickPhoto(boolean isTakePhoto) {
        File file = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        TakePhoto takePhoto = getTakePhoto();
//         configCompress(takePhoto); // 压缩处理
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
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void takeSuccess(TResult result) {
        // 拍照或者选图成功
        String mHeaderAbsolutePath = result.getImages().get(0).getOriginalPath();
        Logger.d(mHeaderAbsolutePath);

        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(mHeaderAbsolutePath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
            //return the compressed file path

            new Handler().post(() -> Glide.with(CreateOrgActivity.this).load(outfile).into(ivAvatar));

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
}
