package com.caregrowtht.app.uitil;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Date: 2019/4/20
 * Author: haoruigang
 * Description: com.caregrowtht.app.uitil
 */
public class AssetCopyer {

    public static boolean copyBigDataToSD(Activity activity, String path, String fileName) {
        try {
            InputStream myInput;
            if (new File(path).exists()) {// 存在
                FilePickerUtils.getInstance().deleteSingleFile(path);
            }
            OutputStream myOutput = new FileOutputStream(path);
            myInput = activity.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
