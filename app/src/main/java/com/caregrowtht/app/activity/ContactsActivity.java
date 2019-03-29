package com.caregrowtht.app.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.wavesidebar.PinnedHeaderDecoration;
import com.caregrowtht.app.view.wavesidebar.WaveSideBarView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.nanchen.wavesidebar.FirstLetterUtil;
import com.nanchen.wavesidebar.SearchEditText;
import com.nanchen.wavesidebar.Trans2PinYinUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-7-30 16:57:37
 * 联系人 教师/学员
 * <p>
 * 选择通知对象
 */
public class ContactsActivity extends BaseActivity implements ContactsAdapter.OnContactsListenar, ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_teacher_check)
    CheckBox tvTeacherCheck;
    @BindView(R.id.tv_student_check)
    CheckBox tvStudentCheck;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.horizonMenu)
    HorizontalScrollView horizonMenu;
    @BindView(R.id.et_search)
    SearchEditText etSearch;
    @BindView(R.id.teacherMenu)
    LinearLayout teacherMenu;
    @BindView(R.id.studentMenu)
    LinearLayout studentMenu;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.ll_tob)
    LinearLayout llTob;
    @BindView(R.id.tv_thecher)
    TextView tvThecher;
    @BindView(R.id.tv_student)
    TextView tvStudent;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.side_view)
    WaveSideBarView sideView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private List<UserEntity> mContactModels;
    private ContactsAdapter mAdapter;
    private String identity = "1";//身份 1：老师 2：学员
    private List<UserEntity> addTeacherList = new ArrayList<>();
    private List<UserEntity> addStudentList = new ArrayList<>();
    private String orgId;
    private String notifyType = "1";
    private List<UserEntity> editTeacherList = new ArrayList<>();//编辑已选中的教师
    private List<UserEntity> editStudList = new ArrayList<>();//编辑已选中的学员
    private boolean isTeacher = true;//教师显示
    private boolean isStu = false;//学员显示
    private int totalThech = 1;//教师首次显示初始化数据
    private int totalStu = 0;//学员首次显示初始化数据
    private int isType = 2;
    private String status = "1"; //1:标星学员 2：活跃学员 3：非活跃待确认 4：非活跃历史 5待审核;

    @Override
    public int getLayoutId() {
        return R.layout.activity_contacts;
    }

    @Override
    public void initView() {
        tvTitle.setText("选择通知对象");
        rlNextButton.setVisibility(View.VISIBLE);
        tvStudentCheck.setVisibility(View.GONE);

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
        orgId = getIntent().getStringExtra("orgId");
        notifyType = getIntent().getStringExtra("notifyType");
        mAdapter.isTheAll = getIntent().getBooleanExtra("isTheAll", false);
        mAdapter.isStuAll = getIntent().getBooleanExtra("isStuAll", false);
        editTeacherList = (List<UserEntity>) getIntent().getSerializableExtra("teacherList");//编辑教师
        editStudList = (List<UserEntity>) getIntent().getSerializableExtra("studentList");//编辑学员
        if (TextUtils.equals(notifyType, "2") || TextUtils.equals(notifyType, "4")
                || TextUtils.equals(notifyType, "5")) {
            llTob.setVisibility(View.GONE);
        }
        if (notifyType.equals("2") || notifyType.equals("5")) {// 2：标星学员通知 5：学员通知
            isTeacher = false;//教师显示
            isStu = true;//学员显示
            totalThech = 0;//教师首次显示初始化数据
            totalStu = 1;//学员首次显示初始化数据
            identity = "2";//身份 1：老师 2：学员
            tvTeacherCheck.setVisibility(View.GONE);
            tvStudentCheck.setVisibility(View.VISIBLE);
        }
        addTeacherList.clear();
        addStudentList.clear();
        if (editTeacherList != null && editTeacherList.size() > 0) {
            addTeacherList.addAll(editTeacherList);
        }
        if (editStudList != null && editStudList.size() > 0) {
            addStudentList.addAll(editStudList);
        }
        setChecked();// 设置是否全选
        if (notifyType.equals("2")) {//2：标星学员通知
            getStudent(status);
        } else {
            getNoticeHuman();//26.选择通知对象
        }

        //=========================控件监听事件============================
        tvTeacherCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {//教师全选
            mAdapter.isTheAll = isChecked;
            //显示/删除 头像/选项
            showTeacherImage(isChecked);
            isType = 1;
            mAdapter.update(mContactModels, isType, addTeacherList, 1, notifyType);//刷新数据
        });
        tvStudentCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {//学员全选
            mAdapter.isStuAll = isChecked;
            //显示/删除 头像/选项
            showStuImage(isChecked);
            isType = 2;
            mAdapter.update(mContactModels, isType, addTeacherList, 1, notifyType);//刷新数据
        });
        etSearch.setOnEditorActionListener((s, i, keyEvent) -> {
            LogUtils.e("afterTextChanged", "afterTextChanged");
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
                if (!TextUtils.isEmpty(notifyType) && notifyType.equals("2")) {//2：标星学员通知
                    getStudent(status);
                } else {
                    getNoticeHuman();//26.选择通知对象
                }
            }
            return false;
        });
    }

    private void setChecked() {
        tvTeacherCheck.setText(mAdapter.isTheAll ? "取消" : "全选");//教师的全选
        tvStudentCheck.setText(mAdapter.isStuAll ? "取消" : "全选");//学员的全选
        tvTeacherCheck.setChecked(mAdapter.isTheAll);
        tvStudentCheck.setChecked(mAdapter.isStuAll);
    }

    private void showTeacherImage(Boolean isChecked) {
        if (isChecked) {//全选
            //初始化数据
            tvTeacherCheck.setText("取消");
            totalThech = 1;//教师首次显示初始化数据
            teacherMenu.removeAllViews();
            addTeacherList.clear();
            for (UserEntity userEntity : mContactModels) {
                showImage(userEntity);
            }
        } else {//全取消
            tvTeacherCheck.setText("全选");
            for (UserEntity userEntity : mContactModels) {
                deleteImage(userEntity);
            }
        }
    }

    private void showStuImage(Boolean isChecked) {
        if (isChecked) {//全选
            //初始化数据
            tvStudentCheck.setText("取消");
            totalStu = 1;//学员首次显示初始化数据
            studentMenu.removeAllViews();
            addStudentList.clear();
            for (UserEntity userEntity : mContactModels) {
                showImage(userEntity);
            }
        } else {//全取消
            tvStudentCheck.setText("全选");
            for (UserEntity userEntity : mContactModels) {
                deleteImage(userEntity);
            }
        }
    }

    /**
     * 26.选择通知对象
     */
    private void getNoticeHuman() {
        loadView.setProgressShown(true);
        HttpManager.getInstance().doGetNoticeHuman("ContactsActivity",
                orgId, identity, new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        setStuData(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ContactsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ContactsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("ContactsActivity onFail", throwable.getMessage());
                    }
                });
    }

    private void getStudent(final String status) {
        //10.获取机构的正式学员 haoruigang on 2018-8-7 15:50:55
        HttpManager.getInstance().doGetOrgChild("CustomActivity",
                orgId, status, pageIndex + "", "",
                new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        setStuData(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CustomActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ContactsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag("CustomActivity onError " + throwable);
                    }
                });
    }

    private void setStuData(List<UserEntity> data) {
        if (data != null && data.size() > 0) {
            mContactModels.clear();
            mContactModels.addAll(getContacts(data));
            if (isTeacher) {// 教师
                isType = 1;
                if (mContactModels.size() == addTeacherList.size()) {// 如果是全选就清空之前数据(不全选)
                    addTeacherList.clear();
                    mAdapter.isTheAll = false;
                    setChecked();
                }
                mAdapter.update(mContactModels, isType, addTeacherList, totalThech, notifyType);//刷新数据
//                    showTeacherImage(mAdapter.isTheAll);//教师/学员 切换保持已选中的教师
                if (addTeacherList != null && addTeacherList.size() > 0) {//筛选已选的联系人
                    //初始化数据
                    teacherMenu.removeAllViews();
                    for (int i = 0; i < addTeacherList.size(); i++) {
                        for (UserEntity userEntity : mContactModels) {
                            if (TextUtils.equals(addTeacherList.get(i).getUserId(), userEntity.getUserId())) {//已选
                                showImage(userEntity);
                                break;
                            }
                        }
                    }
                }
            }
            if (isStu) {// 学员
                isType = 2;
                if (mContactModels.size() == addStudentList.size()) {// 如果是全选就清空之前数据(不全选)
                    addStudentList.clear();
                    mAdapter.isStuAll = false;
                    setChecked();
                }
                mAdapter.update(mContactModels, isType, addStudentList, totalStu, notifyType);//刷新数据
//                    showStuImage(mAdapter.isStuAll);//教师/学员 切换保持已选中的学员
                if (addStudentList != null && addStudentList.size() > 0) {//筛选已选的联系人
                    //初始化数据
                    studentMenu.removeAllViews();
                    for (int i = 0; i < addStudentList.size(); i++) {
                        for (UserEntity userEntity : mContactModels) {
                            String stuId, userId;
                            if (notifyType.equals("2")) {//2：标星学员通知
                                stuId = addStudentList.get(i).getStuId();
                                userId = userEntity.getStuId();
                            } else {
                                stuId = addStudentList.get(i).getUserId();
                                userId = userEntity.getUserId();
                            }
                            if (TextUtils.equals(stuId, userId)) {//已选
                                showImage(userEntity);
                                break;
                            }
                        }
                    }
                }
            }
            loadView.delayShowContainer(true);
        } else {
            mContactModels.clear();
            mAdapter.update(mContactModels, isType, null, totalStu, notifyType);//刷新数据
            loadView.setNoShown(true);
        }
    }

    @Override
    public void showCheckImage(final UserEntity userEntity, final int position) {
        showImage(userEntity);
    }

    @Override
    public void deleteImage(final UserEntity userEntity, final int position) {
        deleteImage(userEntity);
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        final CheckBox tvSelect = view.findViewById(R.id.tv_select_stu);
        mAdapter.getSelect(postion, tvSelect);
        Boolean isSel = false;
        if (isType == 1) {
            isSel = mAdapter.getIsSeleThe().get(postion);
        }
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

    static class LetterComparator implements Comparator<UserEntity> {

        @Override
        public int compare(UserEntity contactModel, UserEntity t1) {
            if (contactModel == null || t1 == null) {
                return 0;
            }
            String lhsSortLetters = contactModel.getIndex().substring(0, 1).toUpperCase();
            String rhsSortLetters = t1.getIndex().substring(0, 1).toUpperCase();
            return lhsSortLetters.compareTo(rhsSortLetters);
        }

    }

    @OnClick({R.id.rl_back_button, R.id.tv_thecher, R.id.tv_student, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.tv_thecher:
                identity = "1";
                tvThecher.setBackgroundResource(R.drawable.notify_obj_round_left);
                tvThecher.setTextColor(ResourcesUtils.getColor(R.color.white));
                tvStudent.setBackground(null);
                tvStudent.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                tvTeacherCheck.setVisibility(View.VISIBLE);
                tvStudentCheck.setVisibility(View.GONE);
                isTeacher = tvTeacherCheck.getVisibility() == View.VISIBLE && TextUtils.equals(identity, "1");//教师
                isStu = tvStudentCheck.getVisibility() == View.VISIBLE && TextUtils.equals(identity, "2");//学员
                totalThech++;
                getNoticeHuman();//26.选择通知对象
                break;
            case R.id.tv_student:
                identity = "2";
                tvThecher.setBackground(null);
                tvThecher.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                tvStudent.setBackgroundResource(R.drawable.notify_obj_round_right);
                tvStudent.setTextColor(ResourcesUtils.getColor(R.color.white));
                tvTeacherCheck.setVisibility(View.GONE);
                tvStudentCheck.setVisibility(View.VISIBLE);
                isTeacher = tvTeacherCheck.getVisibility() == View.VISIBLE && TextUtils.equals(identity, "1");//教师
                isStu = tvStudentCheck.getVisibility() == View.VISIBLE && TextUtils.equals(identity, "2");//学员
                totalStu++;
                getNoticeHuman();//26.选择通知对象
                break;
            case R.id.btn_submit:
                switch (notifyType) {//通知的类型 1：自定义 2：放假通知 3：班级通知 4：教师通知 5：学员通知 6：全体通知
                    case "1":
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.CUSTOM_CHECK_NOTIFY, addTeacherList, addStudentList, mAdapter.isTheAll, mAdapter.isStuAll));
                        break;
                    case "2":
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.HOLIDAY_NOTIFY, addTeacherList, addStudentList, mAdapter.isTheAll, mAdapter.isStuAll));
                        break;
                    case "3":
                        break;
                    case "4":
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_NOTIFY, addTeacherList, addStudentList, mAdapter.isTheAll, mAdapter.isStuAll));
                        break;
                    case "5":
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.STUDENT_NOTIFY, addTeacherList, addStudentList, mAdapter.isTheAll, mAdapter.isStuAll));
                        break;
                    case "6":
                        break;
                }
                finish();
                break;
        }
    }


    /**
     * 从服务器获取数据并排序
     */
    public List<UserEntity> getContacts(List<UserEntity> data) {
        List<UserEntity> contacts = new ArrayList<>();
        if (data != null && data.size() > 0) {
            for (UserEntity u : data) {
                String name;
                if (!TextUtils.isEmpty(notifyType) && notifyType.equals("2")) {//2：标星学员通知
                    name = u.getStuName();
                } else {
                    name = u.getUserName();
                }
                u.setIndex(FirstLetterUtil.getFirstLetter(name));
                contacts.add(u);//1：老师 2：学员
            }
            //按字母排序
            Collections.sort(contacts, new LetterComparator());
        }
        return contacts;
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
        images.setImageResource(R.mipmap.ic_avatar_default);
        String userName, userImage;
        if (notifyType.equals("2")) {//2：标星学员通知
            userName = userEntity.getStuName();
            userImage = userEntity.getStuIcon();
        } else {
            userName = userEntity.getUserName();
            userImage = userEntity.getUserImage();
        }
        images.setTextAndColor(TextUtils.isEmpty(userName) ? "" :
                userName.substring(0, 1), getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(this, userImage, 0, images);
        tvname.setText(userName);
        // 设置id，方便后面删除
        view.setTag(userEntity);

        if (isTeacher) {//教师
            teacherMenu.addView(view, menuLinerLayoutParames);
            boolean isExist = false;//是否已存在
            for (UserEntity addTeacher : addTeacherList) {
                if (TextUtils.equals(addTeacher.getUserId(), userEntity.getUserId())) {//已选
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                addTeacherList.add(userEntity);
            }
        }
        if (isStu) {//学员
            studentMenu.addView(view, menuLinerLayoutParames);
            boolean isExist = false;//是否已存在
            for (UserEntity addStudent : addStudentList) {
                String stuId, userId;
                if (notifyType.equals("2")) {//2：标星学员通知
                    stuId = addStudent.getStuId();
                    userId = userEntity.getStuId();
                } else {
                    stuId = addStudent.getUserId();
                    userId = userEntity.getUserId();
                }
                if (TextUtils.equals(stuId, userId)) {//已选
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                addStudentList.add(userEntity);
            }
        }
        if ((addTeacherList.size() + addStudentList.size()) > 0) {
            if (ivSearch.getVisibility() == View.VISIBLE) {
                ivSearch.setVisibility(View.GONE);
            }
        }
        horizonMenu.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }

    /**
     * 删除选择的头像
     */
    private void deleteImage(UserEntity userEntity) {
        if (isTeacher) {//教师
            View view = teacherMenu.findViewWithTag(userEntity);
            teacherMenu.removeView(view);
            for (int i = 0; i < addTeacherList.size(); i++) {
                if (TextUtils.equals(addTeacherList.get(i).getUserId(), userEntity.getUserId())) {
                    addTeacherList.remove(i);
                    break;
                }
            }
        }
        if (isStu) {//学员
            View view = studentMenu.findViewWithTag(userEntity);
            studentMenu.removeView(view);
            for (int i = 0; i < addStudentList.size(); i++) {
                String stuId, userId;
                if (notifyType.equals("2")) {//2：标星学员通知
                    stuId = addStudentList.get(i).getStuId();
                    userId = userEntity.getStuId();
                } else {
                    stuId = addStudentList.get(i).getUserId();
                    userId = userEntity.getUserId();
                }
                if (TextUtils.equals(stuId, userId)) {//已选
                    addStudentList.remove(i);
                    break;
                }
            }
        }
        if ((addTeacherList.size() + addStudentList.size()) < 1) {
            if (ivSearch.getVisibility() == View.GONE) {
                ivSearch.setVisibility(View.VISIBLE);
            }
        }
        horizonMenu.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }

}
