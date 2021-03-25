package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ContactsAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.wavesidebar.PinnedHeaderDecoration;
import com.caregrowtht.app.view.wavesidebar.WaveSideBarView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.nanchen.wavesidebar.FirstLetterUtil;
import com.nanchen.wavesidebar.SearchEditText;
import com.nanchen.wavesidebar.Trans2PinYinUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-8-29 15:00:32
 * 选择固定学员
 */
public class FastenStudentActivity extends BaseActivity implements ContactsAdapter.OnContactsListenar, ViewOnItemClick {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.studentMenu)
    LinearLayout studentMenu;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.horizonMenu)
    HorizontalScrollView horizonMenu;
    @BindView(R.id.et_search)
    SearchEditText etSearch;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    @BindView(R.id.side_view)
    WaveSideBarView sideView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private List<UserEntity> mContactModels;
    private ContactsAdapter mAdapter;

    private String orgId;
    private int totalStu = 1;//学员首次显示初始化数据
    private List<UserEntity> addStudentList = new ArrayList<>();
    private List<UserEntity> editStudList = new ArrayList<>();//编辑已选中的学员;
    private String orgCardIds;//已选中的课时卡Ids
    private int isType = 2;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fasten_student;
    }

    @Override
    public void initView() {
        tvTitle.setText("选择固定学员");
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        rlNextButton.setVisibility(View.VISIBLE);
        tvTitleRight.setText("全选");//学员的全选
        initRecyclerView(recyclerView, true);
        final PinnedHeaderDecoration decoration = new PinnedHeaderDecoration();
        decoration.registerTypePinnedHeader(1, (parent, adapterPosition) -> true);
        recyclerView.addItemDecoration(decoration);
        mContactModels = new ArrayList<>();
        mAdapter = new ContactsAdapter(mContactModels, this, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnContactsListenar(this);
        //字母侧边导航栏
        sideView.setOnTouchLetterChangeListener(letter -> {
            for (int i = 0; i < mContactModels.size(); i++) {
                if (mContactModels.get(i).getIndex().equals(letter)) {
                    recyclerView.scrollToPosition(i);
                    LinearLayoutManager mLayoutManager =
                            (LinearLayoutManager) recyclerView.getLayoutManager();
                    mLayoutManager.scrollToPositionWithOffset(i, 0);
                    return;
                }
            }
        });
    }

    @Override
    public void initData() {
        orgId = UserManager.getInstance().getOrgId();
        orgCardIds = getIntent().getStringExtra("orgCardIds");
        editStudList = (List<UserEntity>) getIntent().getSerializableExtra("studentList");//编辑学员
        if (editStudList != null) {
            addStudentList.addAll(editStudList);
        }
        getStuData();//调取什么接口
        rlNextButton.setOnClickListener(view -> {//学员全选
            mAdapter.isStuAll = !mAdapter.isStuAll;
            mAdapter.initDate(isType);
            mAdapter.notifyDataSetChanged();
            //显示/删除 头像
            showStuImage(mAdapter.isStuAll);
        });
        etSearch.setOnEditorActionListener((s, i, keyEvent) -> {
            if (!TextUtils.isEmpty(s.getText().toString())) {
                List<UserEntity> mSearchModels = new ArrayList<>();
                for (UserEntity model : mContactModels) {
                    String str = Trans2PinYinUtil.trans2PinYin(model.getUserName());
                    if (str.contains(s.getText().toString()) || model.getUserName().contains(s.getText().toString())) {
                        mSearchModels.add(model);
                    }
                }
                setStuData(mSearchModels);
            } else {
                getStuData();//调取什么接口
            }
            return false;
        });
    }

    private void getStuData() {
        getCardChild();//40.获取机构拥有某张课时卡的学员
    }

    private void getCardChild() {
        loadView.setProgressShown(true);
        HttpManager.getInstance().doGetCardChild("FastenStudentActivity", orgId,
                orgCardIds, etSearch.getText().toString(),
                new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        setStuData(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FastenStudentActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FastenStudentActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getCardChild());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> getCardChild());
                    }
                });
    }

    private void setStuData(List<UserEntity> data) {
        if (data != null && data.size() > 0) {
            mContactModels.clear();
            mContactModels.addAll(getContacts(data));
            mAdapter.update(mContactModels, 2, addStudentList, totalStu, "");//刷新数据
//            showStuImage(mAdapter.isStuAll);//教师/学员 切换保持已选中的学员
            if (addStudentList != null && addStudentList.size() > 0) {//筛选已选的联系人
                //初始化数据
                studentMenu.removeAllViews();
                for (int i = 0; i < addStudentList.size(); i++) {
                    for (UserEntity userEntity : mContactModels) {
                        if (TextUtils.equals(addStudentList.get(i).getUserId(), userEntity.getUserId())) {//已选
                            showImage(userEntity);
                            break;
                        }
                    }
                }
            }
            loadView.delayShowContainer(true);
        } else {
            loadView.setNoShown(true);
        }
    }

    @OnClick({R.id.rl_back_button, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                Intent intent = new Intent();
//                if (addStudentList.size() == 0) {
//                    U.showToast("请选择学员");
//                    break;
//                }
                intent.putExtra("UserEntity", (Serializable) addStudentList);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void showStuImage(Boolean isChecked) {
        if (isChecked) {//全选
            //初始化数据
            tvTitleRight.setText("取消");
            totalStu = 1;//学员首次显示初始化数据
            studentMenu.removeAllViews();
            addStudentList.clear();
            for (UserEntity userEntity : mContactModels) {
                showImage(userEntity);
            }
        } else {//全取消
            tvTitleRight.setText("全选");
            for (UserEntity userEntity : mContactModels) {
                deleteImage(userEntity);
            }
        }
    }

    /**
     * 删除选择的头像
     */
    private void deleteImage(UserEntity userEntity) {
        View view = studentMenu.findViewWithTag(userEntity);
        studentMenu.removeView(view);
        for (int i = 0; i < addStudentList.size(); i++) {
            if (TextUtils.equals(addStudentList.get(i).getUserId(), userEntity.getUserId())) {
                addStudentList.remove(i);
                break;
            }
        }
        if (addStudentList.size() < 1) {
            if (ivSearch.getVisibility() == View.GONE) {
                ivSearch.setVisibility(View.VISIBLE);
            }
        }
        horizonMenu.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }

    @Override
    public void showCheckImage(UserEntity userEntity, int position) {
        showImage(userEntity);
    }

    @Override
    public void deleteImage(UserEntity userEntity, int position) {
        deleteImage(userEntity);
    }

    /**
     * 显示选择的头像
     */
    private void showImage(UserEntity userEntity) {
        // 包含TextView的LinearLayout
        // 参数设置
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.header_item, null);
        AvatarImageView images = view.findViewById(R.id.iv_avatar);
        TextView tvname = view.findViewById(R.id.tv_name);
        menuLinerLayoutParames.setMargins(6, 6, 6, 6);
        // 设置id，方便后面删除
        view.setTag(userEntity);
        images.setImageResource(R.mipmap.ic_avatar_default);
        images.setTextAndColor(TextUtils.isEmpty(userEntity.getUserName()) ? "" :
                userEntity.getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(this, userEntity.getUserImage(), 0, images);
        tvname.setText(userEntity.getUserName());

        studentMenu.addView(view, menuLinerLayoutParames);
        Boolean isExist = false;//是否已存在
        for (UserEntity addStudent : addStudentList) {
            if (TextUtils.equals(addStudent.getUserId(), userEntity.getUserId())) {//已选
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            addStudentList.add(userEntity);
        }
        if (addStudentList.size() > 0) {
            if (ivSearch.getVisibility() == View.VISIBLE) {
                ivSearch.setVisibility(View.GONE);
            }
        }
        horizonMenu.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }

    /**
     * 从服务器获取数据并排序
     */
    public static List<UserEntity> getContacts(List<UserEntity> data) {
        List<UserEntity> contacts = new ArrayList<>();
        for (UserEntity u : data) {
            u.setIndex(FirstLetterUtil.getFirstLetter(u.getUserName()));
            contacts.add(u);//1：老师 2：学员
        }
        //按字母排序
        Collections.sort(contacts, new ContactsActivity.LetterComparator());
        return contacts;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {
            finish();
            overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        final CheckBox tvSelect = view.findViewById(R.id.tv_select_stu);
        mAdapter.getSelect(postion, tvSelect);
        Boolean isSel = false;
        if (isType == 2) {
            isSel = mAdapter.getIsSelStu().get(postion);
        }
        UserEntity contact = mContactModels.get(postion);
        if (isSel) {
            showCheckImage(contact, postion);
        } else {
            deleteImage(contact, postion);
        }
    }
}
