package com.qhh.baselib.utils;

import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author qinhaihang_vendor
 * @version $Rev$
 * @time 2019/6/17 11:13
 * @des
 * @packgename com.qhh.baselib.utils
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean copyAssetsFileToPath(Context context, String fileName, String outPath) {
        File dirFile = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dirFile.mkdirs();
        }
        File outFile = new File(outPath);
        if (outFile.exists() && outFile.isFile()) {
            outFile.delete();
        }
        try {
            InputStream in = context.getAssets().open(fileName);
            if (in == null) {
                Log.e(TAG, fileName + " the src file is not existed");
                return false;
            }
            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int length = in.read(buffer);
            while (length > 0) {
                out.write(buffer, 0, length);
                length = in.read(buffer);
            }

            out.flush();
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
            outFile.delete();
            e.printStackTrace();
        }
        return false;
    }

    public static void save2SDbin(String basePath,byte[] data,String name){

        File baseFile = new File(basePath);
        if(!baseFile.exists() || !baseFile.isDirectory()){
            baseFile.mkdirs();
        }

        File binFile = new File(baseFile, name + ".YUV");

        FileOutputStream fos = null;
        DataOutputStream dos = null;

        try {

            fos = new FileOutputStream(binFile, true);
            dos = new DataOutputStream(fos);

            long startTime = System.currentTimeMillis();

            dos.write(data);
            dos.flush();

            long time = System.currentTimeMillis() - startTime;
            Log.i("qhh_time","save time = " + time);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(dos != null){
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
