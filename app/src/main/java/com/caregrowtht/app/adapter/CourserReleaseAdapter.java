package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caregrowtht.app.R;
import com.caregrowtht.app.user.UploadModule;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * haoruigang on 2018-7-11 10:41:47
 * 发布课程 附件预览 适配器
 */
public class CourserReleaseAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_atter)
    ImageView ivAtter;
    @BindView(R.id.iv_del)
    ImageView ivDel;
    @BindView(R.id.tv_atter_name)
    TextView tvAtterName;

    private Context mContext;
    public List<UploadModule> uploadModules = new ArrayList<>();
    public List<String> pngOravis = new ArrayList<>();

    public CourserReleaseAdapter(List datas, Context context) {
        super(datas, context);
        this.mContext = context;
        this.uploadModules.addAll(datas);

    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        UploadModule uploadModule = uploadModules.get(position);
        String path = uploadModule.getPicPath();
        String picType = uploadModule.getPictureType();
        if (picType.contains("image") || picType.contains("jpg") || picType.contains("jpeg")) {//图片
            Glide.with(mContext).load(path).into(ivAtter);
        } else if (picType.contains("video") || picType.contains("mp4")) {//视频
            Glide.with(mContext).load(path).into(ivAtter);
        } else if (picType.contains("pdf")) {//PDF
            Glide.with(mContext).load(R.mipmap.icon_pdf).into(ivAtter);
        } else if (picType.contains("doc")) {//WORD
            Glide.with(mContext).load(R.mipmap.icon_word).into(ivAtter);
        } else if (picType.contains("ppt")) {//PPT
            Glide.with(mContext).load(R.mipmap.icon_ppt).into(ivAtter);
        } else if (picType.contains("xlsx")) {//EXCEL
            Glide.with(mContext).load(R.mipmap.icon_excel).into(ivAtter);
        } else {
            Glide.with(mContext).load(R.mipmap.icon_file).into(ivAtter);
        }
        tvAtterName.setText(new File(path).getName());
        ivDel.setOnClickListener(v -> {
            pngOravis.remove(position);
            uploadModules.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, uploadModules.size());
        });
    }

//    public void setData(List<UploadModule> uploadModules) {
//        this.uploadModules.clear();
//        this.uploadModules.addAll(uploadModules);
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        return uploadModules.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_atter_preview;
    }

}
