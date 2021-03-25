package com.caregrowtht.app.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.MomentAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.MomentCommentEntity;
import com.caregrowtht.app.model.MomentMessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.ThreadPoolManager;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-7-17 17:55:25
 * 学员的课程反馈
 */
public class CourserFeedbackActivity extends BaseActivity implements MomentAdapter.OnCommentListener {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.x_recycler_view)
    XRecyclerView xRecyclerView;

    @BindView(R.id.ll_btm_entry_fram)
    LinearLayout llBtmEntryFram;
    @BindView(R.id.friends_input)
    EditText friendsInput;
    @BindView(R.id.friends_send)
    TextView friendSend;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private StudentEntity stuDetails;
    MomentAdapter mAdapter;
    ArrayList<MomentMessageEntity> mArrDatas;
    private Dialog dialog;//包含输入框的 dialog
    private String imputType;
    private String circleId;

    private String stuId;
    private String orgId;
    private String type = "1";
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_courser_feedback;
    }

    @Override
    public void initView() {
        tvTitle.setText("");
        imputType = getIntent().getStringExtra("imputType");
        if (TextUtils.equals(imputType, "1")) {// imputType：1:学员详情进入的课程反馈 2:动态进入的记录详情
            stuDetails = (StudentEntity) getIntent().getExtras().getSerializable("stuDetails");
            stuId = stuDetails.getStuId();
            tvTitle.setText(String.format("%s的课程反馈", stuDetails.getStuName()));
        } else {
            circleId = getIntent().getStringExtra("circleId");
            tvTitle.setText("课程反馈详情");
            type = "2";
        }

        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        } else {
            orgId = UserManager.getInstance().getOrgId(); //getIntent().getStringExtra("orgId");
        }

        mArrDatas = new ArrayList<>();
        //列表
        iniXrecyclerView(xRecyclerView);
        mAdapter = new MomentAdapter(mArrDatas, this, null, null, type, imputType);
        mAdapter.mActivity = this;
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getStuCourseFeedback(pageIndex);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                getStuCourseFeedback(pageIndex);
            }
        });
        if (TextUtils.equals(imputType, "1")) {// imputType：1:学员详情进入的课程反馈 2:动态进入的记录详情
            xRecyclerView.setPullRefreshEnabled(true);
            xRecyclerView.setLoadingMoreEnabled(true);
        }
        llBtmEntryFram.setVisibility(View.GONE);
        mAdapter.setCommentListener(this);
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this::handleWindowChange);
    }

    /**
     * 监听键盘的显示和隐藏
     */
    private void handleWindowChange() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);//获取当前界面显示范围
        Log.i("display  ", "top = " + rect.top);
        Log.i("display  ", "bottom = " + rect.bottom);
        int displayHeight = rect.bottom - rect.top;//app内容显示高度，即屏幕高度-状态栏高度-键盘高度
        int totalHeight = getWindow().getDecorView().getHeight();
        //显示内容的高度和屏幕高度比大于 0.8 时，dismiss dialog
        if ((float) displayHeight / totalHeight > 0.8)//0.8只是一个大致的比例，可以修改
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                //如果添加了空白 item ，移除空白 item
//                if (mAdapter.listModel.get(mAdapter.listModel.size() - 1) instanceof MomentMessageEntity) {
//                    mAdapter.listModel.remove(mAdapter.listModel.size() - 1);
//                    mAdapter.notifyDataSetChanged();
//                }
            }
    }

    @Override
    public void initData() {
        if (msgEntity != null && msgEntity.getCircleId().equals("0")) {// 该课程反馈已删除
            xRecyclerView.setVisibility(View.GONE);
            loadView.setNoShown(true);
            loadView.setNoIcon(R.mipmap.ic_no_setting_org);
            loadView.setNoInfo("该课程反馈已删除");
        } else {
            if (TextUtils.equals(imputType, "1")) {// imputType：1:学员详情进入的课程反馈 2:动态进入的记录详情
                getStuCourseFeedback(pageIndex);
            } else {
                getDetailsByCirId();
            }
        }
    }

    private void getStuCourseFeedback(int argPage) {
        runOnUiThread(() -> {
            loadView.setProgressShown(true);
        });
        pageIndex = argPage;
        if (mAdapter.getItemCount() < 20) {
            pageIndex = 1;
        }
        //haoruigang on 2018-7-17 18:07:48 15.查看学员所有的课程反馈
        HttpManager.getInstance().doStuCourseFeedback("CourserFeedbackActivity",
                stuId, orgId, pageIndex + "",
                new HttpCallBack<BaseDataModel<MomentMessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentMessageEntity> data) {
                        getCourserFeed(data);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserFeedbackActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getStuCourseFeedback(1));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (loadView != null) {
                            loadView.setErrorShown(true, v -> getStuCourseFeedback(1));
                        }
                    }
                });
    }

    private void getCourserFeed(BaseDataModel<MomentMessageEntity> data) {
        if (data != null && data.getData().size() > 0) {
            if (pageIndex == 1) {
                mArrDatas.clear();
            }
            mArrDatas.addAll(data.getData());
            mAdapter.setData(mArrDatas);
            loadView.delayShowContainer(true);
        } else {
            if (pageIndex == 1) {
                loadView.setNoShown(true);
            } else {
                loadView.delayShowContainer(true);
            }
        }
        if (xRecyclerView != null) {
            if (pageIndex == 1) {
                xRecyclerView.refreshComplete();
            } else {
                xRecyclerView.loadMoreComplete();
            }
        }
    }

    private void getDetailsByCirId() {
        runOnUiThread(() -> {
            loadView.setProgressShown(true);
        });
        //haoruigang on 2018-7-17 18:07:48 15.查看学员所有的课程反馈
        HttpManager.getInstance().doStuCourseFeedback("CourserFeedbackActivity",
                circleId, new HttpCallBack<BaseDataModel<MomentMessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentMessageEntity> data) {
                        getCourserFeed(data);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserFeedbackActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getDetailsByCirId());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> getDetailsByCirId());
                    }
                });
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void onComment(View view, int adapterPosition, int position1, int position2) {
        showInputDialog(view, adapterPosition, position1, position2);
    }

    /**
     * 显示评论输入 dialog
     *
     * @param itemView
     * @param adapterPosition
     * @param position1       item
     * @param position2       评论item
     */
    @SuppressLint("ClickableViewAccessibility")
    public void showInputDialog(View itemView, final int adapterPosition, final int position1, final int position2) {
        final int itemBottomY = getCoordinateY(itemView) + itemView.getHeight();//item 底部y坐标
        dialog = new Dialog(CourserFeedbackActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = LayoutInflater.from(CourserFeedbackActivity.this).inflate(R.layout.bottom_entry_fram, null);
        dialog.setContentView(view);
        //scrollView 点击事件，点击时将 dialog dismiss，设置 onClick 监听无效
        dialog.findViewById(R.id.scrollView).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                dialog.dismiss();
            return true;
        });
        dialog.show();
        itemView.postDelayed(() -> {
            LinearLayout llCommentInput = dialog.findViewById(R.id.ll_comment_input);
            int y = getCoordinateY(llCommentInput);
            Log.i("display", "itemBottomY = " + itemBottomY + "  input text y = " + y);
            //最后一个 item 特殊处理，添加一个和输入框等高的 item，使 RecyclerView 有足够的空间滑动
//            if (adapterPosition == mAdapter.listModel.size() - 1) {
//                mAdapter.listModel.add(new MomentMessageEntity());
//                mAdapter.bottomHeight = llCommentInput.getHeight();
//                mAdapter.notifyDataSetChanged();
//            }
            //滑动 RecyclerView，是对应 item 底部和输入框顶部对齐
            xRecyclerView.smoothScrollBy(0, itemBottomY - y);

            EditText friendsInput = llCommentInput.findViewById(R.id.friends_input);
            friendsInput.setOnKeyListener((v, keyCode, event) -> {
                // 这两个条件必须同时成立，如果仅仅用了enter判断，就会执行两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 执行发送消息等操作
                    SubmitComment(friendsInput.getText().toString(), position1, position2);
                    return true;
                }
                return false;
            });
            TextView friendSend = llCommentInput.findViewById(R.id.friends_send);
            friendSend.setOnClickListener(v -> {
                // 执行发送消息等操作
                SubmitComment(friendsInput.getText().toString(), position1, position2);
            });

        }, 300);
    }

    /**
     * 获取控件左上顶点 y 坐标
     *
     * @param view
     * @return
     */
    private int getCoordinateY(View view) {
        int[] coordinate = new int[2];
        view.getLocationOnScreen(coordinate);
        return coordinate[1];
    }

    private void SubmitComment(String s, final int position1, final int position2) {
        String mReplyCommentId = null;
        if (position2 != -1) {
            mReplyCommentId = mArrDatas.get(position1).getComments().get(position2).getCommentId();
        }
        Log.e("------------", "SubmitComment");

        HttpManager.getInstance().doComment("MomentActivity",
                mArrDatas.get(position1).getCircleId(), s, mReplyCommentId,
                new HttpCallBack<BaseDataModel<MomentCommentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentCommentEntity> data) {
                        if (null != dialog && dialog.isShowing()) {
                            dialog.dismiss();
                            ExecutorService executorService = Executors.newSingleThreadExecutor();//唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行(同步)
                            for (int i = 1; i <= pageIndex; i++) {//刷新到当前item
                                int finalI = i;
                                Runnable syncRunnable = () -> {
                                    if (TextUtils.equals(imputType, "1")) {// imputType：1:学员详情进入的课程反馈 2:动态进入的记录详情
                                        getStuCourseFeedback(finalI);
                                    } else {
                                        getDetailsByCirId();
                                    }
                                };
                                executorService.execute(syncRunnable);
                            }
                            executorService.shutdown();
                            Thread thread = new Thread() {
                                @Override
                                public synchronized void run() {
                                    super.run();
                                    while (true) {// 实时检测线程池是否上传完成
                                        if (executorService.isTerminated()) {
                                            xRecyclerView.scrollToPosition(position1 + 1);
//                                    MoveToPosition(layoutManager, position1 + 2);//提交完跳转指定位置
                                            break;
                                        }
                                    }
                                }
                            };
                            ThreadPoolManager.getInstance().execute(new FutureTask<Object>(thread, null), null);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        dismissLoadingDialog();
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserFeedbackActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }
}
