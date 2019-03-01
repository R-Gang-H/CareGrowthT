package com.caregrowtht.app.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.user.MultipleItem;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoruigang on 2018-7-27 18:28:17
 * 通知对象 适配器
 */

public class NotifyObjAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {

    private Activity activity;

    private List<OrgNotifyEntity> orgNotifyList = new ArrayList<>();
    ArrayList<OrgNotifyEntity> teacherEntitie = new ArrayList<>();//教师
    ArrayList<OrgNotifyEntity> studendEntitie = new ArrayList<>();//学生
    private String type;//1:全部 2：未回执

    public NotifyObjAdapter(Activity mActivity, List<MultipleItem> data, List listdate) {
        super(data);
        this.activity = mActivity;
        orgNotifyList.addAll(listdate);
        addItemType(MultipleItem.TYPE_RECEIPT_NO, R.layout.item_notify_obj);
        addItemType(MultipleItem.TYPE_RECEIPT_ALL, R.layout.item_notify_obj);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {
        if (orgNotifyList.size() == 0) {
            return;
        }
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        TextView tvTitle;
        switch (helper.getItemViewType()) {
            case MultipleItem.TYPE_RECEIPT_NO:
                int teacher_receipt_true = 0;
                int teacher_receipt_false = 0;
                teacherEntitie.clear();
                for (OrgNotifyEntity thecherEntity : orgNotifyList) {
                    if (thecherEntity.getIdentity().equals("1")) {//身份 1：老师 2：学员
                        teacherEntitie.add(thecherEntity);
                        if (thecherEntity.getIsReceipt().equals("2")) {//是否已经回执 1：未回执 2：已回执
                            teacher_receipt_true++;
                        } else {
                            teacher_receipt_false++;
                        }
                    }
                }
                if (teacherEntitie.size() == 0) {
                    helper.getView(R.id.rl_exist).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.rl_exist).setVisibility(View.VISIBLE);
                    tvTitle = helper.getView(R.id.tv_title);
                    if (TextUtils.equals(type, "1")) {
                        tvTitle.setText(String.format("老师 %s 人\t已回执 %s 人\t未回执 %s 人",
                                teacherEntitie.size(), teacher_receipt_true, teacher_receipt_false));
                    } else {
                        tvTitle.setText(String.format("老师 %s 人\t已回执 %s 人",
                                teacherEntitie.size(), teacher_receipt_true));
                    }
                    RecyclerView recyclerView = helper.getView(R.id.recycler_view);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(new NotifySignAdapter(teacherEntitie, activity));

                }
                break;
            case MultipleItem.TYPE_RECEIPT_ALL:
                int receipt_true = 0;
                int receipt_false = 0;
                studendEntitie.clear();
                for (OrgNotifyEntity studentEntity : orgNotifyList) {
                    if (studentEntity.getIdentity().equals("2")) {//身份 1：老师 2：学员
                        studendEntitie.add(studentEntity);
                        if (studentEntity.getIsReceipt().equals("2")) {//是否已经回执 1：未回执 2：已回执
                            receipt_true++;
                        } else {
                            receipt_false++;
                        }
                    }
                }
                if (studendEntitie.size() == 0) {
                    helper.getView(R.id.rl_exist).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.rl_exist).setVisibility(View.VISIBLE);
                    helper.getView(R.id.rl_exist).setVisibility(View.VISIBLE);
                    tvTitle = helper.getView(R.id.tv_title);
                    if (TextUtils.equals(type, "1")) {
                        tvTitle.setText(String.format("学员 %s 人\t已回执 %s 人\t未回执 %s 人", studendEntitie.size(), receipt_true, receipt_false));
                    } else {
                        tvTitle.setText(String.format("学员 %s 人\t已回执 %s 人", studendEntitie.size(), receipt_true));
                    }
                    RecyclerView recyclerView = helper.getView(R.id.recycler_view);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(new NotifySignAdapter(studendEntitie, activity));
                }
                break;
        }
    }

    public void setData(ArrayList<OrgNotifyEntity> mArrDatas, String type, Boolean isClear) {
        if (isClear) {
            this.orgNotifyList.clear();
        }
        this.type = type;
        this.orgNotifyList.addAll(mArrDatas);
        notifyDataSetChanged();
    }

}
