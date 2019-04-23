package com.qhh.baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author qinhaihang_vendor
 * @version $Rev$
 * @time 2019/4/23 16:36
 * @des
 * @packgename com.qhh.baselib.utils
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public class ImageUtils {

    private static class SingletonHolder{
        private static final ImageUtils INSTANCE = new ImageUtils();
    }

    public static ImageUtils getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public String compressPic(Context context,String fileSrc,int quality, String childFile ,String file_Name){
        // 获取图片的宽和高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap imageBitmap;

        // 压缩图片
        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                (double) options.outWidth / 1024f,
                (double) options.outHeight / 1024f)));
        options.inJustDecodeBounds = false;
        imageBitmap = BitmapFactory.decodeFile(fileSrc, options);

        // 若imageBitmap为空则图片信息不能正常获取
        if (null == imageBitmap) {
            Log.e("ImageUtils", "图片信息无法正常获取！");
            return null;
        }

        // 部分手机会对图片做旋转，这里检测旋转角度
        int degree = readPictureDegree(fileSrc);
        if (degree != 0) {
            // 把图片旋转为正的方向
            imageBitmap = rotateImage(degree, imageBitmap);
        }

        //可根据流量及网络状况对图片进行压缩
        String saveImagePath;
        if (quality > 0 && quality <= 100) {
            saveImagePath = saveBitmapToFile(context, imageBitmap, childFile, file_Name, quality);
        }else{
            Log.e("ImageUtils", "图片压缩系数应该在 1 ~ 100 之间");
            context = null;
            saveImagePath = "";
        }

        return saveImagePath;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree 旋转角度
     */
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  旋转角度
     * @param bitmap 原图
     * @return bitmap 旋转后的图片
     */
    public Bitmap rotateImage(int angle, Bitmap bitmap) {
        // 图片旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 得到旋转后的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 保存Bitmap至本地
     *
     * @param
     */
    public String saveBitmapToFile(Context context, Bitmap bmp, String childFile ,String file_Name,int quality) {
        String sFile_path = getImagePath(context,childFile,file_Name);

        File file = new File(sFile_path);
        FileOutputStream fOut;
        boolean compress;
        try {
            fOut = new FileOutputStream(file);
            compress = bmp.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            if(!compress){
                sFile_path = "";
            }
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            sFile_path = "";
        } catch (IOException e) {
            e.printStackTrace();
            sFile_path = "";
        }finally {
            context = null;
            return sFile_path;
        }
    }

    /**
     * 获得保存的图片的路径  fileName 为 null 或 ""  返回的默认为 裁剪的图片路径
     *
     * @return
     */
    public String getImagePath(Context context, String childFile ,String fileName) {
        String path;

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = context.getFilesDir().getAbsolutePath();
        }else if(TextUtils.isEmpty(childFile)){
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + childFile + File.separator;
        }

        if (!path.endsWith("/")) {
            path += "/";
        }

        File folder = new File(path);
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
        path += (fileName + ".jpg");
        return path;
    }

}
