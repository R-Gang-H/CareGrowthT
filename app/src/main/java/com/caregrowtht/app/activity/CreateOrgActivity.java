package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.android.library.view.CircleImageView;
import com.android.library.view.WheelPopup;
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
 * ????????????
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
    @BindView(R.id.tv_stu_num)
    TextView tvStuNum;
    @BindView(R.id.et_promo_code)
    EditText etPromoCode;
    @BindView(R.id.et_marke)
    EditText etMarke;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_create)
    Button btnCreate;

    private InvokeParam invokeParam;
    private TakePhoto takePhoto;
    String mImageName = "";//user/avatar/?????????+??????id.jpg
    AliYunOss mOssClient;

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    //Url
    String headImage = "";
    private String pname, cname, dname;
    private String orgId = "";//???????????????????????????????????????????????????????????????????????????
    private OrgEntity orgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_org;
    }

    @Override
    public void initView() {
        orgEntity = (OrgEntity) getIntent().getSerializableExtra("orgEntity");// ""????????? ,??????????????????
        if (orgEntity != null) {// ????????????????????????
            tvTitle.setText("??????????????????");
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
            tvStuNum.setText(orgEntity.getScale());
            etPromoCode.setText(orgEntity.getCoupon());
            etMarke.setText(orgEntity.getRecommend_mobile());
            btnCreate.setText("??????");
        } else {// ""?????????
            tvTitle.setText("???????????????");
        }
        mOssClient = new AliYunOss(this);
    }

    @Override
    public void initData() {
        new Thread(() -> {
            // ?????????????????????????????????
            initJsonData();
        }).start();
    }

    private void initJsonData() {//????????????
        String JsonData = new GetJsonDataUtils().getJson(this, "province.json");//??????assets????????????json????????????
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//???Gson ????????????
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//????????????
            ArrayList<String> CityList = new ArrayList<>();//????????????????????????????????????
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//??????????????????????????????????????????
            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//??????????????????????????????
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//????????????
                ArrayList<String> City_AreaList = new ArrayList<>();//??????????????????????????????
                //??????????????????????????????????????????????????????????????????null ?????????????????????????????????????????????
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//??????????????????????????????
            }
            /**
             * ??????????????????
             */
            options2Items.add(CityList);
            /**
             * ??????????????????
             */
            options3Items.add(Province_AreaList);
        }
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson ??????
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

    @OnClick({R.id.rl_back_button, R.id.rl_avatar, R.id.tv_address, R.id.tv_stu_num, R.id.btn_cancel, R.id.btn_create})
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
            case R.id.tv_stu_num:
                selectStuNum();
                break;
            case R.id.btn_create:
                editOrg();
                break;
        }
    }

    private void selectStuNum() {
        WheelPopup pop = new WheelPopup(this, Constant.setStuNum);
        pop.showAtLocation(View.inflate(this, R.layout.item_color_course, null), Gravity
                .BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            tvStuNum.setText(argValue);
            return null;
        });
    }

    private void editOrg() {
        //47.??????????????? / ??????????????????
        String orgName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(orgName)) {
            U.showToast("?????????????????????");
            return;
        }
        String orgShortName = etForShort.getText().toString().trim();
        if (TextUtils.isEmpty(orgShortName)) {
            U.showToast("?????????????????????");
            return;
        }
        if (TextUtils.isEmpty(pname) || TextUtils.isEmpty(cname) || TextUtils.isEmpty(dname)) {
            U.showToast("?????????????????????");
            return;
        }
        String orgChainName = etChain.getText().toString().trim();
        String address = etDetailAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            U.showToast("?????????????????????");
            return;
        }
        String telephone = etTelephone.getText().toString().trim();
        String orgPhone = etOrgMobile.getText().toString().trim();
        if (orgPhone.length() > 0 && orgPhone.length() != 11) {
            U.showToast("???????????????????????????");
            return;
        }
        if (mImageName != null && !mImageName.equals("")) {
            headImage = Constant.OSS_URL + mImageName;
        }
        String scale = tvStuNum.getText().toString();
        String coupon = etPromoCode.getText().toString();
        String recommendMobile = etMarke.getText().toString();
        HttpManager.getInstance().doEditOrg("CreateOrgActivity",
                orgId, orgName, orgShortName, orgChainName, headImage, pname,
                cname, dname, address, telephone, orgPhone, scale, coupon, recommendMobile,
                new HttpCallBack<BaseDataModel<OrgEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        if (orgEntity == null || orgId == null || orgId.equals("")) {//???????????????????????????

                            UserManager.getInstance().getOrgEntityList(data.getData().get(0));

                            UserManager.getInstance().orgAddTeacher(data.getData().get(0).getOrgId());
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_HOME_EVENT));
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                            startActivity(new Intent(CreateOrgActivity.this, BuyActivity.class)
                                    .putExtra("orgId", data.getData().get(0).getOrgId()));
                        }
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(CreateOrgActivity.this);
                        } else if (statusCode == 1025) {
                            U.showToast("??????????????????!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void showPickerView() {// ?????????????????????
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            //?????????????????????????????????????????????
            pname = options1Items.get(options1).getPickerViewText();
            cname = options2Items.get(options1).get(options2);
            dname = options3Items.get(options1).get(options2).get(options3);
            tvAddress.setText(String.format("%s%s%s", pname, cname, dname));
        })
                .setTitleText("????????????")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //???????????????????????????
                .setContentTextSize(20)
                .build();
        /*pvOptions.setPicker(options1Items);//???????????????
        pvOptions.setPicker(options1Items, options2Items);//???????????????*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//???????????????
        pvOptions.show();
    }

    private void takeOrPickPhoto(boolean isTakePhoto) {
        File file = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        TakePhoto takePhoto = getTakePhoto();
//         configCompress(takePhoto); // ????????????
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
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void takeSuccess(TResult result) {
        // ????????????????????????
        String mHeaderAbsolutePath = result.getImages().get(0).getOriginalPath();
        Logger.d(mHeaderAbsolutePath);

        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(mHeaderAbsolutePath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
            //return the compressed file path

            new Handler().post(() -> Glide.with(CreateOrgActivity.this).load(outfile).into(ivAvatar));

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
}
