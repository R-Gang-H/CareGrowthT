package com.caregrowtht.app.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.MomentAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.MomentCommentEntity;
import com.caregrowtht.app.model.MomentMessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ThreadPoolManager;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by haoruigang on 2018-4-18 14:36:50
 * 课程反馈列表
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MomentActivity extends BaseActivity implements MomentAdapter.OnCommentListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_growth_history)
    XRecyclerView mGrowthHistoryRecyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    MomentAdapter mAdapter;
    ArrayList<MomentMessageEntity> mArrDatas;

    private Dialog dialog;//包含输入框的 dialog

    private String orgId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_moment;
    }

    @Override
    public void initView() {
        tvTitle.setText("课程反馈");
        mArrDatas = new ArrayList<>();
        //列表
        iniXrecyclerView(mGrowthHistoryRecyclerView);
        mGrowthHistoryRecyclerView.setPullRefreshEnabled(true);
        mGrowthHistoryRecyclerView.setLoadingMoreEnabled(true);
        mAdapter = new MomentAdapter(mArrDatas, this, null, null, "2", "2");
        mGrowthHistoryRecyclerView.setAdapter(mAdapter);
        mGrowthHistoryRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                RequestMomentList(pageIndex);//34. 获取兴趣圈列表
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                RequestMomentList(pageIndex);//34. 获取兴趣圈列表
            }
        });
        mAdapter.setCommentListener(MomentActivity.this);
        mAdapter.setMomentListener((pData, position) -> {
            checkDelDialog(pData.getCircleId(), position);
        });
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this::handleWindowChange);
    }

    /**
     * 确定删除
     *
     * @param circleId
     * @param position
     */
    private void checkDelDialog(String circleId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_org, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("确定删除吗?");
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setVisibility(View.GONE);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_ok.setOnClickListener(v -> {
            delCircle(circleId, position);
            dialog.dismiss();
        });
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 删除课程反馈
     *
     * @param circleId
     * @param position
     */
    private void delCircle(String circleId, int position) {
        HttpManager.getInstance().doDelCircle("MomentActivity", circleId,
                new HttpCallBack<BaseDataModel>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        //移除
                        mArrDatas.remove(position);
                        mAdapter.listModel.remove(position);
                        mAdapter.notifyItemRemoved(position + 1);
                        mAdapter.notifyItemRangeChanged(position + 1, mArrDatas.size() - position);

                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MomentActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MomentActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MomentAdapter onError", throwable.getMessage());
                    }
                });
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
                if (mAdapter.listModel.get(mAdapter.listModel.size() - 1) != null) {
                    mAdapter.listModel.remove(mAdapter.listModel.size() - 1);
                    mAdapter.notifyDataSetChanged();
                }
            }
    }

    @Override
    public void initData() {
        MessageEntity msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        } else {
            orgId = UserManager.getInstance().getOrgId(); //getIntent().getStringExtra("orgId");
        }
        RequestMomentList(1);
    }

    private void RequestMomentList(int pageIndex) {
        //获取兴趣圈列表 2018-4-18 14:41:37
        HttpManager.getInstance().doCircleList("MomentActivity", orgId, pageIndex,
                new HttpCallBack<BaseDataModel<MomentMessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentMessageEntity> data) {
                        if (data.getData().size() > 0) {
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
                        if (mGrowthHistoryRecyclerView != null) {
                            if (pageIndex == 1) {
                                mGrowthHistoryRecyclerView.refreshComplete();
                            } else {
                                mGrowthHistoryRecyclerView.loadMoreComplete();
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (mGrowthHistoryRecyclerView != null) {
                            if (pageIndex == 1) {
                                mGrowthHistoryRecyclerView.refreshComplete();
                            } else {
                                mGrowthHistoryRecyclerView.loadMoreComplete();
                            }
                        }
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MomentActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
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
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_entry_fram, null);
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
            if (adapterPosition == mAdapter.listModel.size() - 1) {
                mAdapter.listModel.add(new MomentMessageEntity());
                mAdapter.bottomHeight = llCommentInput.getHeight();
                mAdapter.notifyDataSetChanged();
            }
            //滑动 RecyclerView，是对应 item 底部和输入框顶部对齐
            mGrowthHistoryRecyclerView.smoothScrollBy(0, itemBottomY - y);

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
                                Runnable syncRunnable = () -> RequestMomentList(finalI);
                                executorService.execute(syncRunnable);
                            }
                            executorService.shutdown();
                            Thread thread = new Thread() {
                                @Override
                                public synchronized void run() {
                                    super.run();
                                    while (true) {// 实时检测线程池是否上传完成
                                        if (executorService.isTerminated()) {
                                            mGrowthHistoryRecyclerView.scrollToPosition(position1 + 2);
//                                    MoveToPosition(layoutManager, position1 + 2);//提交完跳转指定位置
                                            break;
                                        }
                                    }
                                }
                            };
                            ThreadPoolManager.getInstance().execute(new FutureTask<>(thread, null), null);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MomentActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

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

}
