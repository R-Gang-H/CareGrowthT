package com.caregrowtht.app.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.ActionInfoActivity;
import com.caregrowtht.app.activity.AdvertInfoActivity;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.activity.CarouseInfoActivity;
import com.caregrowtht.app.adapter.MomentAdapter;
import com.caregrowtht.app.model.CarouselEntity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MomentCommentEntity;
import com.caregrowtht.app.model.MomentMessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.GradientUtils;
import com.caregrowtht.app.uitil.ThreadPoolManager;
import com.caregrowtht.app.view.BannerView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.github.mmin18.widget.RealtimeBlurView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import butterknife.BindView;

/**
 * Create by haoruigang on 2018-4-18 14:36:50
 * 兴趣圈
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MomentFragment extends BaseFragment implements ViewOnItemClick, MomentAdapter.OnCommentListener {

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    @BindView(R.id.blurView)
    RealtimeBlurView blurView;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.rv_growth_history)
    XRecyclerView mGrowthHistoryRecyclerView;

    MomentAdapter mAdapter;
    ArrayList<MomentMessageEntity> mArrDatas;

    Boolean isShowBer = true;
    BannerView mBannerView;
    private String titleName;
    private List<View> viewList = new ArrayList<>();
    private View view;
    private List<CarouselEntity> carouselData;
    private Dialog dialog;//包含输入框的 dialog

    private static int THRESHOLD_OFFSET = 10;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_moment;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        GradientUtils.setPaddingSmart(getActivity(), blurView);
        tvTitle.setText("兴趣圈");
        toolbar.setBottom(180);
        animationHide();
        mArrDatas = new ArrayList<>();
//        mChildId = UserManager.getInstance().mCurChildId;
        //列表
        iniXrecyclerView(mGrowthHistoryRecyclerView);
        mAdapter = new MomentAdapter(mArrDatas, mActivity, this, null, "1", "1");
        mAdapter.mActivity = mActivity;
        mGrowthHistoryRecyclerView.setAdapter(mAdapter);
        this.view = view;
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
        mGrowthHistoryRecyclerView.setLoadingMoreEnabled(true);
        mAdapter.setCommentListener(MomentFragment.this);
        mGrowthHistoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean controlVisible = true;
            int scrollDistance = 0;

            //onScrolled 滑动停止的时候调用
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (controlVisible && scrollDistance > THRESHOLD_OFFSET) {//手指上滑即Scroll向下滚动的时候，dy为正
                    animationShow();
                    //重置使下面的if语句起作用
                    controlVisible = false;
                    scrollDistance = 0;
                } else if (!controlVisible && scrollDistance < -THRESHOLD_OFFSET) {//手指下滑即Scroll向上滚动的时候，dy为负
                    animationHide();
                    controlVisible = true;
                    scrollDistance = 0;
                }

                //当scrollDistance累计到隐藏（显示)ToolBar之后，如果Scroll向下（向上）滚动，则停止对scrollDistance的累加
                //直到Scroll开始往反方向滚动，再次启动scrollDistance的累加
                if ((controlVisible && dy > 0) || (!controlVisible && dy < 0)) {
                    scrollDistance += dy;
                }
            }
        });
        mActivity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() -> handleWindowChange());
    }

    private void animationShow() {
        toolbar.animate()
                .setInterpolator(new AccelerateInterpolator(1))
                .setDuration(180)
                .translationY(0);
    }

    private void animationHide() {
        toolbar.animate()
                .translationY(-toolbar.getBottom())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
    }

    /**
     * 监听键盘的显示和隐藏
     */
    private void handleWindowChange() {
        Rect rect = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);//获取当前界面显示范围
        Log.i("display  ", "top = " + rect.top);
        Log.i("display  ", "bottom = " + rect.bottom);
        int displayHeight = rect.bottom - rect.top;//app内容显示高度，即屏幕高度-状态栏高度-键盘高度
        int totalHeight = mActivity.getWindow().getDecorView().getHeight();
        //显示内容的高度和屏幕高度比大于 0.8 时，dismiss dialog
        if ((float) displayHeight / totalHeight > 0.8)//0.8只是一个大致的比例，可以修改
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                //如果添加了空白 item ，移除空白 item
                if (mAdapter.listModel.get(mAdapter.listModel.size() - 1) instanceof MomentMessageEntity) {
                    mAdapter.listModel.remove(mAdapter.listModel.size() - 1);
                    mAdapter.notifyDataSetChanged();
                }
            }
    }

    @Override
    public void initData() {
        GetBannerData();//33. 获取兴趣圈轮播图
        titleName = tvTitle.getText().toString();
        mGrowthHistoryRecyclerView.refresh();
    }

    public void GetBannerData() {
        HttpManager.getInstance().doBanners("MomentFragment", new HttpCallBack<BaseDataModel<CarouselEntity>>(getActivity()) {
            @Override
            public void onSuccess(BaseDataModel<CarouselEntity> data) {
                carouselData = data.getData();
                setData(carouselData);
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1002 || statusCode == 1011) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(getActivity());
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void RequestMomentList(int pageIndex) {
        //获取兴趣圈列表 2018-4-18 14:41:37
        HttpManager.getInstance().doCircleList("MomentFragment", pageIndex,
                new HttpCallBack<BaseDataModel<MomentMessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentMessageEntity> data) {
                        if (data.getData().size() > 0) {
                            if (pageIndex == 1) {
                                mArrDatas.clear();
                                mAdapter.setData(data.getData(), true);
                            } else {
                                mAdapter.setData(data.getData(), false);
                            }
                            mArrDatas.addAll(data.getData());
                        }
                        if (mGrowthHistoryRecyclerView != null) {
                            mGrowthHistoryRecyclerView.refreshComplete();
                            mGrowthHistoryRecyclerView.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (mGrowthHistoryRecyclerView != null) {
                            mGrowthHistoryRecyclerView.refreshComplete();
                            mGrowthHistoryRecyclerView.loadMoreComplete();
                        }
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(getActivity());
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData(List<CarouselEntity> data) {
        viewList.clear();
        if (data.size() > 0) {
            if (isShowBer) {
                View inflateBanner = LayoutInflater.from(getActivity()).inflate(R.layout.item_banner, (ViewGroup) view, false);
                mBannerView = inflateBanner.findViewById(R.id.it_banner);//兴趣圈轮播广告
                mGrowthHistoryRecyclerView.addHeaderView(inflateBanner);
                isShowBer = false;
            }
        }
        for (int i = 0; i < data.size(); i++) {
            ImageView image = new ImageView(getActivity());
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            //设置显示格式
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            GlideUtils.setGlideImg(getActivity(), data.get(i).getImage(), R.mipmap.ic_org_action, image);
            int finalI = i;
            image.setOnClickListener(view -> {
//                U.showToast(data.get(finalI).getBannerId() + "");
                if (TextUtils.equals(data.get(finalI).getType(), "2")) {//1：第三方链接 2：富文本
                    startActivity(new Intent(getActivity(), CarouseInfoActivity.class).putExtra("carouselEntity", data.get(finalI)));
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(data.get(finalI).getLink()));
//                    intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    startActivity(intent);
                }
            });
            image.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mBannerView.startLoop(false);
                        mBannerView.startLoop(true);
                        break;
                }
                return false;
            });
            viewList.add(image);
        }
        mBannerView.startLoop(false);
        mBannerView.startLoop(true);
        mBannerView.setViewList(viewList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        if (carouselData.size() > 0) {
            position = position - 2;
        } else {
            position = position - 1;
        }
        // 子项的点击事件监听
        MomentMessageEntity pData = mArrDatas.get(position);
        switch (pData.getType()) {
            case "1":
            case "2":
            case "3":
            case "4":
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("MomentEntity", pData);
//                bundle.putString("titleName", titleName);
                break;
            case "5"://广告
                startActivity(new Intent(mActivity, AdvertInfoActivity.class).putExtra("pData", pData));
                break;
            case "6"://活动
                startActivity(new Intent(getActivity(), ActionInfoActivity.class).putExtra("actId", pData.getActivityId()));
                break;
        }
    }

    public void setActivity(BaseActivity mActivity) {
        this.mActivity = mActivity;
        if (mAdapter != null) {
            mAdapter.mActivity = mActivity;
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
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_entry_fram, null);
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

        HttpManager.getInstance().doComment("MomentFragment",
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
                                    RequestMomentList(finalI);
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
                                            mGrowthHistoryRecyclerView.scrollToPosition(position1 + 2);
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
                            HttpManager.getInstance().dologout(getActivity());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

}
