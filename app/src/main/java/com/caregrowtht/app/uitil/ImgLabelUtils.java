package com.caregrowtht.app.uitil;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.caregrowtht.app.MyApplication;

import java.net.URL;

/**
 * Created by haoruigang on 2018/4/4 9:48.
 * ImageGetter接口的使用 工具类
 *
 * @author Susie
 */

public class ImgLabelUtils {

    private static final String TAG = "ImgLabelUtils";

    /**
     * 网络图片name
     */
    private String picName = "networkPic.jpg";
    /**
     * 网络图片Getter
     */
    private NetworkImageGetter mImageGetter;

    private static class SingletonHolder {
        static ImgLabelUtils INSTANCE = new ImgLabelUtils();
    }

    public static ImgLabelUtils getInstance() {
        return ImgLabelUtils.SingletonHolder.INSTANCE;
    }

    /**
     * String htmlOne = "本地图片测试:" + "<img src='/mnt/sdcard/imgLabel.jpg'>";
     *
     * @param mTvOne
     * @param htmlOne
     */
    public void htmlOne(TextView mTvOne, String htmlOne) {
        mTvOne.setText(Html.fromHtml(htmlOne, new LocalImageGetter(), null));
    }

    /**
     * String htmlTwo = "项目图片测试:" + "<img src=/" "+R.drawable.growth_history_head_bg+" / ">";
     *
     * @param mTvTwo
     * @param htmlTwo
     */
    public void htmlTwo(TextView mTvTwo, String htmlTwo) {
        mTvTwo.setText(Html.fromHtml(htmlTwo, new ProImageGetter(), null));
    }

    /**
     * 网络图片路径
     * private String htmlThree = "网络图片测试:" + "<img src='jpg/1373780364_7576.jpg'>";
     *
     * @param htmlThree
     * @param mTvThree
     */
    public void htmlThree(TextView mTvThree, String htmlThree) {
        mImageGetter = new NetworkImageGetter();
        mTvThree.setText(Html.fromHtml(htmlThree, mImageGetter, null));
    }

    /**
     * 本地图片
     *
     * @author Susie
     */
    private final class LocalImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {
            // 获取本地图片
            Drawable drawable = Drawable.createFromPath(source);
            // 必须设为图片的边际,不然TextView显示不出图片
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            // 将其返回
            return drawable;
        }
    }

    /**
     * 项目资源图片
     *
     * @author Susie
     */
    private final class ProImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {
            // 获取到资源id
            int id = Integer.parseInt(source);
            Drawable drawable = MyApplication.getAppContext().getResources().getDrawable(id);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;
        }
    }


    /**
     * 网络图片
     *
     * @author Susie
     */
    public final class NetworkImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            Log.i("RG", "source---?>>>" + source);
            Drawable drawable = null;
            URL url;
            try {
                url = new URL(source);
                Log.i("RG", "url---?>>>" + url);
                drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
            } catch (Exception e) {
                Log.e("RG", "url---?>>>" + e);
                e.printStackTrace();
                return null;
            }
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                    drawable.getIntrinsicHeight());
            drawable.setBounds(0, 0, 1280, 720);
            Log.i("RG", "url---?>>>" + url);
            return drawable;
        }
    }

    //解决NetworkOnMainThreadException异常
    public void struct() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork() // or
                // .detectAll()
                // for
                // all
                // detectable
                // problems
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                .penaltyLog() // 打印logcat
                .penaltyDeath().build());
    }

    /**
     * 设置水印图片到中间
     *
     * @param src
     * @param watermark
     * @return
     */
    public static Bitmap createWaterMaskCenter(Bitmap src, Bitmap watermark) {
        return createWaterMaskBitmap(src, watermark,
                (src.getWidth() - watermark.getWidth()) / 2,
                (src.getHeight() - watermark.getHeight()) / 2);
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,
                                                int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
//        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        // 存储
        canvas.restore();
        return newb;
    }


}
