package com.caregrowtht.app.uitil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.caregrowtht.app.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    //----------------------------------------屏幕截图并分享----------------------------------------

    /**
     * 把ViewGroup转化为一个bitmap
     */
    public static Bitmap convertViewToBitmap(View view, int IMAGE_WIDTH, int IMAGE_HEIGHT) {
        //由于直接new出来的view是不会走测量、布局、绘制的方法的，所以需要我们手动去调这些方法，不然生成的图片就是黑色的。
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(IMAGE_WIDTH, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(IMAGE_HEIGHT, View.MeasureSpec.EXACTLY);

        view.measure(widthMeasureSpec, heightMeasureSpec);
        view.layout(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        Bitmap bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    /**
     * 进行截取屏幕
     *
     * @param pActivity
     * @return bitmap
     */

    public static Bitmap takeScreenShot(Activity pActivity) {
        Bitmap bitmap = null;
        View view = pActivity.getWindow().getDecorView();
        // 设置是否可以进行绘图缓存
        view.setDrawingCacheEnabled(true);
        // 如果绘图缓存无法，强制构建绘图缓存
        view.buildDrawingCache();
        // 返回这个缓存视图
        bitmap = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        // 测量屏幕宽和高
        view.getWindowVisibleDisplayFrame(frame);
        int stautsHeight = frame.top;
        Log.d("jiangqq", "状态栏的高度为:" + stautsHeight);
        int width = pActivity.getWindowManager().getDefaultDisplay().getWidth();
        int height = pActivity.getWindowManager().getDefaultDisplay().getHeight();
        // 根据坐标点和需要的宽和高创建bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height - stautsHeight);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return bitmap;
    }


    /**
     * 保存图片到sdcard中
     *
     * @param pBitmap
     */
    private static boolean savePic(Bitmap pBitmap, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            if (null != fos) {
                pBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static void delFile(String filePath) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File root = new File(filePath);
            final File[] files = root.listFiles();
            files[0].delete();
        }
    }

    /**
     * 截图
     *
     * @param pActivity
     * @return 截图并且保存sdcard成功返回true，否则返回false
     */

    public static boolean shotActivity(Activity pActivity, String strName) {
        return savePic(takeScreenShot(pActivity), strName);
    }

    public static boolean shotBitmap(View view, int IMAGE_WIDTH, int IMAGE_HEIGHT, String fileName) {
        return savePic(convertViewToBitmap(view, IMAGE_WIDTH, IMAGE_HEIGHT), fileName);
    }


}
