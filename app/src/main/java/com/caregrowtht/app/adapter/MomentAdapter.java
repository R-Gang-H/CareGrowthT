package com.caregrowtht.app.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.activity.FileDisplayActivity;
import com.caregrowtht.app.activity.ReportPupoWindow;
import com.caregrowtht.app.model.BaseBeanModel;
import com.caregrowtht.app.model.MomentMessageEntity;
import com.caregrowtht.app.model.NineGridTestModel;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.CommentPopup;
import com.caregrowtht.app.view.ninegrid.NineGridTestLayout;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Created by haoruigang on 2018-4-18 15:57:46
 * 兴趣圈
 */

public class MomentAdapter extends RecyclerView.Adapter {

    private final String type;
    private final String imputType;
    @BindView(R.id.constraint)
    CardView constraint;
    @BindView(R.id.tv_stu_info)
    TextView tvStuInfo;
    @BindView(R.id.iv_Avatar)
    AvatarImageView ivAvatar;
    @BindView(R.id.iv_child_avatar)
    AvatarImageView ivChildAvatar;
    @BindView(R.id.tv_relative)
    TextView tvRelative;
    @BindView(R.id.tv_child_name)
    TextView tvChildName;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.layout_nine_grid)
    NineGridTestLayout layoutNineGrid;
    @BindView(R.id.ll_comment)
    LinearLayout llComment;
    @BindView(R.id.ll_like)
    LinearLayout llLike;
    @BindView(R.id.reply_line)
    TextView replyLine;
    @BindView(R.id.rv_reply)
    RecyclerView rvReply;
    @BindView(R.id.iv_author_avatar)
    AvatarImageView ivAuthorAvatar;
    @BindView(R.id.tv_author_name)
    TextView tvAuthorName;
    @BindView(R.id.tv_author_date)
    TextView tvAuthorDate;
    @BindView(R.id.tv_del_moment)
    TextView tvDelMoment;
    @BindView(R.id.iv_milestone)
    ImageView ivMilestone;
    @BindView(R.id.commentary)
    TextView commentary;

    @BindView(R.id.item_moment)
    LinearLayout mMomentItem;
    @BindView(R.id.iv_line)
    ImageView ivLine;

    public BaseActivity mActivity;
    public ArrayList<MomentMessageEntity> listModel = new ArrayList<>();

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();

    private ViewOnItemClick onItemClick;
    private ViewOnItemLongClick longClick;

    private OnCommentListener mCommentListener;
    private MomentListener momentListener;

    public int bottomHeight;
    private CommentPopup mCommentPopup;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;

    public interface OnCommentListener {
        void onComment(View view, int adapterPosition, int position1, int position2);
    }

    public void setCommentListener(OnCommentListener l) {
        this.mCommentListener = l;
    }

    public interface MomentListener {
        void delMoment(MomentMessageEntity pData, int position);
    }

    public void setMomentListener(MomentListener l) {
        this.momentListener = l;
    }

    public MomentAdapter(List datas, BaseActivity context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick, String type, String imputType) {
        this.mActivity = context;
        this.listModel.addAll(datas);
        this.onItemClick = onItemClick1;
        this.longClick = onItemLongClick;
        this.type = type;//type:1.兴趣圈 2.课程反馈 3.学员所以的课程反馈
        this.imputType = imputType;//{对type为1 的细分} imputType：1:学员详情进入的课程反馈 2:动态进入的记录详情
        // 初始化数据
        initDate();
        //初始化PopupWindow
        mCommentPopup = new CommentPopup(mActivity);
    }

    public void convert(RecyclerView.ViewHolder holder, int position, Context context) {
        //设置textView显示内容为list里的对应项
        MomentMessageEntity pData = listModel.get(position);
        Log.e("------------------", "position=" + position);
        if (pData == null) {
            return;
        }
        if (type.equals("1")) {//兴趣圈
            mMomentItem.setVisibility(View.VISIBLE);
            String headImage = "";
            if (TextUtils.equals(imputType, "1")) {//学员详情进入的课程反馈
                headImage = pData.getParentHeadImage();
            } else if (TextUtils.equals(imputType, "2")) {//从动态进入添加默认值
                headImage = pData.getAuthorAvatar();
            }
            if (pData.getAuthorNickname() != null) {
                ivChildAvatar.setTextAndColor(TextUtils.isEmpty(pData.getAuthorNickname()) ? ""
                                : pData.getAuthorNickname().substring(0, 1),
                        mActivity.getResources().getColor(R.color.b0b2b6));
            }
            GlideUtils.setGlideImg(mActivity, headImage, 0, ivChildAvatar);
            tvChildName.setText(pData.getAuthorNickname());
        } else if (type.equals("2")) {//课程反馈
            String childName = pData.getChildName();
            String[] childNames = childName.split("#");//集体学员
            String childNe = childName.replace("#", ",");
            if (childNames.length == 1) {//单个
                if (imputType.equals("2")) {// 2:动态进入的记录详情
                    if (StrUtils.isNotEmpty(pData.getLessonId())) {
                        if (position == 0 || !listModel.get(position - 1).getLessonId().equals(pData.getLessonId())) {
                            tvStuInfo.setVisibility(View.VISIBLE);
                        } else {
                            tvStuInfo.setVisibility(View.GONE);
                        }
                    } else {
                        tvStuInfo.setVisibility(View.VISIBLE);
                    }
                    tvChildName.setVisibility(View.VISIBLE);
                    tvChildName.setTextColor(ResourcesUtils.getColor(R.color.color_9));
                    tvChildName.setText(String.format("(%s\t学员)", childName));
                } else {// 1:学员详情进入的课程反馈
                    tvChildName.setVisibility(View.GONE);
                    if (position == 0 || !listModel.get(position - 1).getChildId().equals(pData.getChildId())) {
                        constraint.setVisibility(View.VISIBLE);
                    } else {
                        constraint.setVisibility(View.GONE);
                    }
                    ivAvatar.setTextAndColor(TextUtils.isEmpty(childName) ? ""
                                    : childName.substring(0, 1),
                            mActivity.getResources().getColor(R.color.b0b2b6));
                    GlideUtils.setGlideImg(mActivity, pData.getChildImage(), 0, ivAvatar);
                    tvRelative.setText(childName);
                }
            } else if (childNames.length > 1) {//集体
                if (imputType.equals("2")) {// 2:动态进入的记录详情
                    tvStuInfo.setVisibility(View.VISIBLE);
                } else {// 1:学员详情进入的课程反馈
                    if (position == 0) {
                        constraint.setVisibility(View.VISIBLE);
                    }
                }
                tvChildName.setVisibility(View.VISIBLE);
                tvChildName.setTextColor(ResourcesUtils.getColor(R.color.color_9));
                tvChildName.setText(String.format("(%s,%s名学员相关)", childNe, childNames.length));
            }
            String tDate;
            if (StrUtils.isNotEmpty(pData.getStartAt())) {// 2:动态进入的记录详情
                tDate = pData.getStartAt();
            } else {
                tDate = pData.getTime();
            }
            tvStuInfo.setText(String.format("%s\t\t%s",
                    DateUtil.getDate(Long.valueOf(tDate),
                            "yyyy/MM/dd"), pData.getCourseName()));

            ivChildAvatar.setVisibility(View.GONE);
            ivAuthorAvatar.setVisibility(View.VISIBLE);
            tvAuthorName.setVisibility(View.VISIBLE);

            if (pData.getAuthorNickname() != null) {
                ivAuthorAvatar.setTextAndColor(TextUtils.isEmpty(pData.getAuthorNickname()) ? ""
                        : pData.getAuthorNickname().substring(0, 1), mActivity.getResources().getColor(R.color.b0b2b6));
            }
            String authorAvatar;
            if (imputType.equals("2")) {// 2:动态进入的记录详情
                authorAvatar = pData.getAuthorAvatar();
            } else {// 1:学员详情进入的课程反馈
                authorAvatar = pData.getParentHeadImage();
            }
            GlideUtils.setGlideImg(mActivity, authorAvatar, 0, ivAuthorAvatar);
            tvAuthorName.setText(String.format("%s\t%s", pData.getAuthorRelative(), pData.getAuthorNickname()));
        }

        ivLine.setVisibility((listModel.size() - 1) == position ? View.GONE : View.VISIBLE);//最后一个横线隐藏
        tvContent.setText(TextUtils.isEmpty(pData.getContent()) ? "" : pData.getContent().replace("\\n", "\n"));
        final LinearLayout llAtter = holder.itemView.findViewById(R.id.ll_atter);
        llAtter.setVisibility(TextUtils.isEmpty(pData.getAccessory()) ? View.GONE : View.VISIBLE);
        if (llAtter.getVisibility() == View.VISIBLE) {
            //必须先清空上次add的View
            ViewParent parent = llAtter.getParent();
            if (parent != null) {
                llAtter.removeAllViews();
            }
            String accessoryPath = pData.getAccessory();
            String[] accessory = accessoryPath.split("#");
            for (String filePath : accessory) {
                @SuppressLint("InflateParams") final View view = LayoutInflater.from(context).inflate(R.layout.item_atter_text, null);
                final TextView tvAtter = view.findViewById(R.id.tv_atter);
                tvAtter.setGravity(Gravity.START);
                tvAtter.setText(filePath.substring(filePath.lastIndexOf("/") + 1));
                tvAtter.setOnClickListener(v -> {
                    //动态权限申请
                    mActivity.requestPermission(Constant.REQUEST_CODE_WRITE,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            mActivity.getString(R.string.rationale_file),
                            new PermissionCallBackM() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void onPermissionGrantedM(int requestCode, String... perms) {
                                    String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                                    FileDisplayActivity.actionStart(mActivity, filePath, fileName);
                                }

                                @Override
                                public void onPermissionDeniedM(int requestCode, String... perms) {
                                    LogUtils.e(mActivity, "TODO: WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT);
                                }
                            });
                });
                llAtter.addView(tvAtter);
            }
        }

        String[] mUrls = layoutNineGrid.ShowImageOrVideo(pData.getPngOravis());
        NineGridTestModel model = new NineGridTestModel();
        if (!TextUtils.isEmpty(pData.getPngOravis())) {
            Collections.addAll(model.urlList, mUrls);
            model.isShowAll = false;
            layoutNineGrid.setIsShowAll(false);
        }
        //设置图片 //九宫格点击事件，跳转封装在控件内了
        layoutNineGrid.setUrlList(model.urlList);

        tvAuthorDate.setText(DateUtil.convertTime(Long.valueOf(pData.getTime())));
        if (pData.getAuthorId() != null) {
            tvDelMoment.setVisibility(pData.getAuthorId().equals(
                    UserManager.getInstance().userData.getUid())
                    || pData.getAuthorId().equals("1")// authorId = 1 我发布的可以删除 0 不可以删除
                    ? View.VISIBLE : View.GONE);
        }
        if (!TextUtils.isEmpty(pData.getLike()) || pData.getComments().size() > 0) {
            llComment.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(pData.getLike())) {
                llLike.setVisibility(View.GONE);
            } else {
                llLike.setVisibility(View.VISIBLE);
                commentary.setText(pData.getLike());
            }
            if (pData.getComments().size() > 0) {
                replyLine.setVisibility(View.VISIBLE);
                rvReply.setVisibility(View.VISIBLE);
                LinearLayoutManager manager = new LinearLayoutManager(mActivity);
                manager.setOrientation(RecyclerView.VERTICAL);
                rvReply.setLayoutManager(manager);
                CommonsAdapter commentAdapter = new CommonsAdapter(pData.getComments(), mActivity, (view, position1) -> {
                    // 评论点
                    if (mCommentListener != null) {
                        mCommentListener.onComment(view, holder.getAdapterPosition(), position, position1);
                    }
                }, null);
                rvReply.setAdapter(commentAdapter);
            } else {
                replyLine.setVisibility(View.GONE);
                rvReply.setVisibility(View.GONE);
            }
        } else {
            llComment.setVisibility(View.GONE);
        }
        //是否点赞
        if (UserManager.getInstance().CheckIsCircles(pData.getCircleId())) {
            getIsSelected().put(position, true);// 同时修改map的值保存状态
        } else {
            getIsSelected().put(position, false);// 同时修改map的值保存状态
        }

        mCommentPopup.setOnCommentPopupClickListener(new CommentPopup.OnCommentPopupClickListener() {

            @Override
            public void onLikeClick(View v, TextView likeText) {
                //U.showToast(getPosition() + "like");
                //是否点赞
                v.setTag(getIsSelected().get(getPosition()) ? 0 : 1);
                switch ((int) v.getTag()) {
                    case 0:
                        v.setTag(1);
                        likeText.setText("取消");

                        getSelect(likeText, getPosition());
                        AddStar(listModel.get(getPosition()), 1);//取消
                        break;
                    case 1:
                        v.setTag(0);
                        likeText.setText("赞\t");

                        getSelect(likeText, getPosition());
                        AddStar(listModel.get(getPosition()), 2);//点赞
                        break;
                }
            }

            @Override
            public void onCommentClick(View v) {
//                                U.showToast(getPosition() + "comment");
                if (mCommentListener != null) {
                    // 评论点 这里将整个 itemView 传递过去，position 没用到，留着没什么影响 :)
                    mCommentListener.onComment(v, holder.getAdapterPosition(), getPosition(), -1);
                }
            }
        });
        ivMilestone.setOnClickListener(v -> {
            setPosition(position);
//                        U.showToast(position + "ivMilestone");
            if (mCommentPopup.getmOnPopupClickListener() != null) {
                mCommentPopup.getmOnPopupClickListener().onLikeClick(getIsSelected().get(getPosition()) ? "取消" : "赞\t");
            }
            mCommentPopup.showPopupWindow(v);
        });
        mMomentItem.setOnLongClickListener(v -> {
            mActivity.startActivity(new Intent(mActivity, ReportPupoWindow.class).putExtra("mCircleId", pData.getCircleId()));
            mActivity.overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            return false;
        });
        tvDelMoment.setOnClickListener(v -> {
            if (momentListener != null) {
                momentListener.delMoment(pData, position);
            }
        });
    }

    public void setData(ArrayList<MomentMessageEntity> argList) {
        listModel.clear();
        listModel.addAll(argList);
        // 初始化数据
        initDate();
        notifyDataSetChanged();
    }

    // 初始化isSelected的数据
    public void initDate() {
        if (listModel.size() > 0) {
            for (int i = 0; i < listModel.size(); i++) {
                getIsSelected().put(i, false);
            }
        }
    }

    public void getSelect(TextView like, int position) {
        like.setSelected(!getIsSelected().get(position));
        getIsSelected().put(position, like.isSelected());// 同时修改map的值保存状态
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }


    public int getLayoutResId() {
        return R.layout.item_moment_message;
    }

    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_BOTTOM = 2;

    @Override
    public int getItemViewType(int position) {
        if (listModel.get(position) != null)
            return TYPE_NORMAL;
        else
            return TYPE_BOTTOM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        int res = getLayoutResId();
        switch (viewType) {
            case TYPE_NORMAL:
                view = LayoutInflater.from(mActivity).inflate(res, parent, false);
                return new XrecyclerViewHolder(view, onItemClick, longClick);
            case TYPE_BOTTOM:
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_bottom, parent, false);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = bottomHeight;
                return new BottomHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ButterKnife.bind(this, holder.itemView);
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                convert(holder, position, mActivity);
                break;
        }
    }

    private class BottomHolder extends RecyclerView.ViewHolder {

        BottomHolder(final View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    /**
     * 取消/点赞
     *
     * @param pData
     * @param type
     */
    private void AddStar(MomentMessageEntity pData, int type) {
        if (UserManager.getInstance().getmMyStarCircles() != null) {
            //点赞
            HttpManager.getInstance().doCircleOperate("star", pData.getCircleId(), "1", new HttpCallBack<BaseBeanModel>() {

                @Override
                public void onSuccess(BaseBeanModel data) {
                    String likeName;
                    if (type == 1) {//取消
//                        U.showToast("取消点赞");
                        if (pData.getLike().contains(",")) {
                            likeName = "," + UserManager.getInstance().userData.getNickname();
                        } else {
                            likeName = UserManager.getInstance().userData.getNickname();
                        }
                        pData.setLike(StrUtils.deleteSubString(pData.getLike(), likeName));
                        listModel.set(getPosition(), pData);//用该规范替换列表中指定位置的元素
                        for (int i = 0; i < UserManager.getInstance().getmMyStarCircles().size(); i++) {
                            if (UserManager.getInstance().getmMyStarCircles().get(i).getCircleId().equals(pData.getCircleId())) {
                                UserManager.getInstance().getmMyStarCircles().remove(i);
                            }
                        }
                    } else {//点赞
//                        U.showToast("点赞成功");
                        if (!TextUtils.isEmpty(pData.getLike())) {
                            likeName = pData.getLike() + "," + UserManager.getInstance().userData.getNickname();
                        } else {
                            likeName = UserManager.getInstance().userData.getNickname();
                        }
                        pData.setLike(likeName);
                        listModel.set(getPosition(), pData);//用该规范替换列表中指定位置的元素
                        UserManager.getInstance().getmMyStarCircles().add(pData);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onFail(int statusCode, String errorMsg) {
                    if (statusCode == 1002 || statusCode == 1011) {//异地登录
                        U.showToast("该账户在异地登录!");
                        HttpManager.getInstance().dologout(mActivity);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    LogUtils.e("MomentAdapter", throwable.getMessage());
                }
            });
        }
    }

}
