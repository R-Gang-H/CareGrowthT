package com.caregrowtht.app.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.activity.CourserActivity;
import com.caregrowtht.app.activity.QRCodeActivity;
import com.caregrowtht.app.activity.SettingActivity;
import com.caregrowtht.app.adapter.StateAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.ItemOffsetDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

import static android.app.Activity.RESULT_OK;

/**
 * haoruigang on 2018-7-4 11:55:15.
 * ??????
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class StateFragment extends BaseFragment {

    @BindView(R.id.iv_title_left)
    AvatarImageView ivTitleLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_all)
    LinearLayout llAll;
    @BindView(R.id.tv_title_all)
    TextView tvTitleAll;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeMenuRecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    //??????
    List<MessageEntity> messageAllList = new ArrayList<>();
    //???????????????
    List<MessageEntity> messageNeedList = new ArrayList<>();
    StateAdapter stateAdapter;
    Boolean isAll = true;//true:?????? false:????????????
    private String orgId;
    private String count;//?????????????????????
    private String status = "1";//1????????? 2???????????????

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_state;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initView(View view, Bundle savedInstanceState) {
        tvTitle.setText("????????????");
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.ic_scan);

        ((BaseActivity) Objects.requireNonNull(getActivity())).initRecyclerView(recyclerView, true);
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(0, 5, 0, 5));
        stateAdapter = new StateAdapter(messageAllList, getActivity());//@TODO
        recyclerView.setAdapter(stateAdapter);

        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageIndex = 1;
            MyMessageV2(true, status);
            getUnfinishMessage();
        });
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            pageIndex++;
            MyMessageV2(false, status);
        });
        refreshLayout.setEnableLoadmoreWhenContentNotFull(false);
    }

    @Override
    public void initData() {
        String name = UserManager.getInstance().userData.getName();
        if (!TextUtils.isEmpty(name)) {
            ivTitleLeft.setTextAndColor(name.substring(0, 1),
                    getResources().getColor(R.color.b0b2b6));
        }
        GlideUtils.setGlideImg(getActivity(), UserManager.getInstance().userData.getHeadImage(),
                0, ivTitleLeft);
        refreshLayout.autoRefresh();
    }


    /**
     * haoruigang on 2018-7-4 16:35:50 1. ??????????????????
     * 1????????? 2???????????????
     */
    private void MyMessageV2(Boolean isClear, String status) {
        loadView.setProgressShown(true);
        HttpManager.getInstance().doMyMessageV2("StateFragment", status, pageIndex,
                new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        if (data.getData().size() > 0) {
                            if (TextUtils.equals(status, "1")) {
                                //1.????????????
                                if (isClear) {
                                    messageAllList.clear();
                                }
                                messageAllList.addAll(data.getData());
                                stateAdapter.setData(messageAllList);
                            } else if (TextUtils.equals(status, "2")) {
                                // 2.????????????????????????
                                if (isClear) {
                                    messageNeedList.clear();
                                }
                                messageNeedList.addAll(data.getData());
                                stateAdapter.setData(messageNeedList);
                            }
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                messageAllList.clear();
                                messageNeedList.clear();
                                loadView.setNoShown(true);
                            } else {
                                if (TextUtils.equals(status, "1")) {
                                    //1.????????????
                                    if (messageAllList.size() > 0) {
                                        loadView.delayShowContainer(true);
                                    } else {
                                        loadView.setNoShown(true);
                                    }
                                } else if (TextUtils.equals(status, "2")) {
                                    // 2.????????????????????????
                                    if (messageNeedList.size() > 0) {
                                        loadView.delayShowContainer(true);
                                    } else {
                                        loadView.setNoShown(true);
                                    }
                                }
                            }
                        }
                        if (isClear) {
                            refreshLayout.finishRefresh();
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddOrgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(getActivity());
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> MyMessageV2(isClear, status));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> MyMessageV2(isClear, status));
                    }
                });
    }

    private void getUnfinishMessage() {
        // 22.???????????????????????????
        HttpManager.getInstance().doGetUnfinishMessage("StateFragment",
                new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        count = data.getData().get(0).getCount();
                        if (Integer.valueOf(count) > 0) {
                            llAll.setVisibility(View.VISIBLE);
                        } else {
                            llAll.setVisibility(View.GONE);
                        }
                        if (TextUtils.equals(status, "1")) {
                            //1.????????????
                            tvTitleAll.setText(String.format("????????????????????????%s??????", count));
                        } else if (TextUtils.equals(status, "2")) {
                            // 2.????????????????????????
                            tvTitleAll.setText("????????????");
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddOrgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(getActivity());
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AddOrgActivity onError", throwable.getMessage());
                    }
                });
    }


    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.ll_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.rl_next_button:
                //???????????? Camera ?????? haoruigang on 2018-4-4 19:13:48
                getCamera();
                break;
            case R.id.ll_all:
                refreshLayout.refreshDrawableState();
                if (isAll) {
                    isAll = false;
                    ivRight.setVisibility(View.VISIBLE);
                    rlNextButton.setVisibility(View.GONE);
                    tvTitleAll.setText("????????????");
                    status = "2";
                    pageIndex = 1;
                    MyMessageV2(true, status);
                } else {
                    isAll = true;
                    ivRight.setVisibility(View.VISIBLE);
                    rlNextButton.setVisibility(View.VISIBLE);
                    tvTitleAll.setText(String.format("????????????????????????%s??????", count));
                    status = "1";
                    pageIndex = 1;
                    MyMessageV2(true, status);
                }
                break;
        }
    }

    /**
     * ?????????????????????Item?????????????????????????????????
     */
    private SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.margin_size_70);

        // 1. MATCH_PARENT ???????????????????????????Item?????????;
        // 2. ???????????????????????????80;
        // 3. WRAP_CONTENT???????????????????????????;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // ??????????????????????????????????????????????????????????????????
        {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                    .setBackground(R.drawable.selector_red)
                    .setText("??????")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// ????????????????????????
        }
    };

    /**
     * RecyclerView???Item???Menu???????????????
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = menuBridge -> {
        menuBridge.closeMenu();

        final int direction = menuBridge.getDirection(); // ???????????????????????????
        final int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView???Item???position???
        final int menuPosition = menuBridge.getPosition(); // ?????????RecyclerView???Item??????Position???

        if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
            delMessage(adapterPosition);//????????????
        }
    };

    /**
     * 2. ????????????
     * haoruigang on 2018-8-6 16:00:48
     */
    private void delMessage(final int adapterPosition) {
        String eventId = "";
        if (TextUtils.equals(status, "1")) {
            //1.????????????
            eventId = messageAllList.get(adapterPosition).getEventId();
        } else if (TextUtils.equals(status, "2")) {
            // 2.????????????????????????
            eventId = messageNeedList.get(adapterPosition).getEventId();
        }
        HttpManager.getInstance().doDelMessage("StateFragment", eventId,
                new HttpCallBack<BaseDataModel<MessageEntity>>() {

                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        //??????
                        if (TextUtils.equals(status, "1")) {
                            //1.????????????
                            messageAllList.remove(adapterPosition);
                        } else if (TextUtils.equals(status, "2")) {
                            // 2.????????????????????????
                            messageNeedList.remove(adapterPosition);
                        }
                        stateAdapter.messageAllList.remove(adapterPosition);
                        stateAdapter.notifyItemRemoved(adapterPosition);
                        stateAdapter.notifyItemRangeChanged(adapterPosition, messageAllList.size());
                        getUnfinishMessage();//???????????????????????????
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddOrgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(getActivity());
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AddOrgActivity onError", throwable.getMessage());
                    }
                });
    }

    //??????CAMERA?????? haoruigang on 2018-4-9 17:07:32
    private void getCamera() {
        requestPermission(
                Constant.RC_CALL_PHONE,
                new String[]{Manifest.permission.CAMERA},
                getString(R.string.rationale_camera),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        LogUtils.e(getActivity(), "TODO: CAMERA Granted", Toast.LENGTH_SHORT);
                        startActivityForResult(new Intent(getActivity(), QRCodeActivity.class), 6980);
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(getActivity(), "TODO: CAMERA Denied", Toast.LENGTH_SHORT);
                    }
                });
    }

    //    String temp;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 6980) {
            if (null != data) {
                Bundle bundle = data.getExtras();
//                if (bundle == null) {
//                    return;
//                }
                String type = bundle.getString("type");// type: 1: org=000H(??????Id) 2: org=00CC&lesson=2265(??????Id)
                String invitationCode = bundle.getString("invitationCode");
                if (type.equals("1")) {
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS
                            && !StrUtils.isEmpty(invitationCode)) {
                        orgId = invitationCode;
                        UserManager.getInstance().getOrgInfo(orgId, getActivity(), "1", null);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        U.showToast("??????????????????!");
                    }
                } else {
                    startActivity(new Intent(getActivity(), CourserActivity.class)
                            .putExtra("courseId", invitationCode));
                }
            }
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.REFERSH_TEACHER:
                initData();
                break;
            case ToUIEvent.REFERSH_ADD_ORG:
                //???????????? Camera ?????? haoruigang on 2018-4-4 19:13:48
                getCamera();
                break;
        }
    }

}
