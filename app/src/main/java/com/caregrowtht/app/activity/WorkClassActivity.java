package com.caregrowtht.app.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourserTypeAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-17 16:08:01
 * 选择排课/班级
 */
public class WorkClassActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_all_select)
    CheckBox tvAllSelect;
    @BindView(R.id.rl_select)
    RelativeLayout rlSelect;
    @BindView(R.id.et_cancel_count)
    EditText etCancelCount;
    @BindView(R.id.tv_couont)
    TextView tvCouont;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private List<CourseEntity> courseDatas = new ArrayList<>();
    private CourserTypeAdapter mAdapter;
    private String cardType;

    //编辑上页数据
    List<CourseEntity> mCourses;
    List<CourseEntity> countList;
    private String addType;// 4：新建课时卡 5:编辑课时卡管理

    @Override
    public int getLayoutId() {
        return R.layout.activity_work_class;
    }

    @Override
    public void initView() {
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        tvTitle.setText("选择排课/班级");

        cardType = getIntent().getStringExtra("cardType");
        mCourses = (List<CourseEntity>) getIntent().getSerializableExtra("mCourses");
        countList = (List<CourseEntity>) getIntent().getSerializableExtra("mCount");
        addType = getIntent().getStringExtra("addType");

        if (TextUtils.equals(cardType, "1")) {// 次数
            tvCouont.setText("次");
        }
        initRecyclerView(recyclerView, true);
        mAdapter = new CourserTypeAdapter(courseDatas, this, this, mCourses, countList, addType);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        getCourses("");
        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            //@TODO 用SqlLite 保存数据
            getCourses(textView.getText().toString().trim());
            LogUtils.tag("sousuo");
            return false;
        });
        tvAllSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvAllSelect.setSelected(isChecked);
            rlSelect.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            mAdapter.isCheckAll = isChecked;
            mAdapter.isAll = isChecked;
            mAdapter.isCount = "";
            etCancelCount.setText("");
            mAdapter.initDate(false);
            mAdapter.notifyDataSetChanged();
        });
        etCancelCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.isCount = s.toString().trim();
                mAdapter.initDate(false);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getCourses(String searchText) {
        //37.获取机构的所有排课或班级 haoruigang on 2018-8-24 12:43:20
        HttpManager.getInstance().doGetCourses("WorkClassActivity",
                UserManager.getInstance().getOrgId(),
                searchText, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseDatas.clear();
                        courseDatas.addAll(data.getData());
                        if (TextUtils.isEmpty(searchText)) {
                            courseDatas.addAll(removeDuplicateOrder(courseDatas));//筛选有几种课程分类
                        }
                        Collections.sort(courseDatas, (o1, o2) -> o1.getClassifyId().compareToIgnoreCase(o2.getClassifyId()));
                        mAdapter.setData(courseDatas, searchText, cardType);// searchText 通过搜索显示
                        tvAllSelect.setVisibility(TextUtils.isEmpty(searchText) ? View.VISIBLE : View.GONE);//隐藏全选
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("WorkClassActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(WorkClassActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("WorkClassActivity onError", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.et_search, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                boolean isValues = true, isValues1 = false;
                List<CourseEntity> courseEntityList = new ArrayList<>();
                List<CourseEntity> countList = new ArrayList<>();
                HashMap<Integer, CourseEntity> mCourseModels = mAdapter.getIsCourseEntity();
                HashMap<Integer, CourseEntity> mCount = mAdapter.getCount();
                for (int j = 0; j < mCourseModels.size(); j++) {
                    CourseEntity courseEntity = mCourseModels.get(j);
                    CourseEntity count = mCount.get(j);
                    if (courseEntity != null && count != null) {
                        if (!TextUtils.isEmpty(courseEntity.getCourseName()) &&
                                !TextUtils.isEmpty(count.getCourseCount())) {
                            courseEntityList.add(courseEntity);
                            countList.add(count);
                            isValues = false;//有选择班级排课
                        } else if (!TextUtils.isEmpty(courseEntity.getCourseName()) &&
                                TextUtils.isEmpty(count.getCourseCount())) {
                            isValues1 = true;//有消课选项未填写
                        }
                    }
                }
                if (TextUtils.equals(cardType, "3")) {//年卡
                    isValues = false;// 有选择班级排课
                    isValues1 = false;// 不用填写消课选项
                }
                if (isValues) {
                    U.showToast("请选择排课/班级!");
                    return;
                }
                if (isValues1) {
                    U.showToast("有消课选项未填写!");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("CourseEntity", (Serializable) courseEntityList);// 课程信息
                intent.putExtra("Count", (Serializable) countList);// 消课次数
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setOnItemClickListener(View view, int position) {
        final CheckBox tvSelectCourse = view.findViewById(R.id.tv_select_course);
        final RelativeLayout rlSelect = view.findViewById(R.id.rl_select);
        final EditText etCancelCount = view.findViewById(R.id.et_cancel_count);
        etCancelCount.setText("");
        mAdapter.isCount = "";
        if (tvSelectCourse.getText().equals("全选")) {
            mAdapter.index = position;
            mAdapter.isClick = true;
            mAdapter.isAll = !mAdapter.getIsSelected().get(position);

            if (mAdapter.isAll && !TextUtils.equals(cardType, "3")) {//年卡不用突出显示
                //点击让这个Item突出显示
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
                        (this, view, getString(R.string.transition_name));
                UserManager.getInstance().setCourVsview(view);
                startActivity(new Intent(this, WorkClassEditActivity.class)
                                .putExtra("cardType", cardType)// 卡类型
                                .putExtra("Selected", mAdapter.isAll)// 是否选中
                        , options.toBundle());
            } else {
                mAdapter.getData(courseDatas.get(position).getClassifyId(), position);
            }
        } else {
            mAdapter.getSelect(position, tvSelectCourse, rlSelect, etCancelCount);
        }
    }

    /**
     * 根据classifyId去重
     *
     * @param orderList
     * @return
     * @author haoruigang
     */
    private static List<CourseEntity> removeDuplicateOrder(List<CourseEntity> orderList) {
        Set<CourseEntity> set = new TreeSet<>((a, b) -> {
            // 字符串则按照asicc码升序排列
            return a.getClassifyId().compareTo(b.getClassifyId());
        });
        set.addAll(orderList);
        return new ArrayList<>(set);
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
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_WORK_COUNT) {
            mAdapter.isAll = (Boolean) event.getObj();
            mAdapter.isCount = (String) event.getObj1();
            mAdapter.getData(courseDatas.get(mAdapter.index).getClassifyId(), mAdapter.index);
        }
    }

}
