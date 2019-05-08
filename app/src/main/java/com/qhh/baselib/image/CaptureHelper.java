package com.qhh.baselib.image;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.qhh.baselib.image.callback.IChoosePictureCallback;
import com.qhh.baselib.image.callback.IRequestCaptureCallback;
import com.qhh.baselib.utils.ImageUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

/**
 * @author qinhaihang
 * @version $Rev$
 * @time 19-4-22 下午10:49
 * @des
 * @packgename com.qhh.baselib.image
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public class CaptureHelper {

    private static final String REQUEST_CAPTURE = "request_capture";

    private IRequestCaptureCallback mIRequestCaptureCallback;
    private IChoosePictureCallback mIChoosePictureCallback;

    private WeakReference<FragmentActivity> mWeakActivityRef;

    
    private static CaptureHelper instance;
    
    public static CaptureHelper getInstance() {
        if (instance == null) {
            synchronized (CaptureHelper.class) {
                if (instance == null) {
                    instance = new CaptureHelper();
                }
            }
        }
        return instance;
    }

    public CaptureHelper init(FragmentActivity activity){
        mWeakActivityRef = new WeakReference<>(activity);
        return instance;
    }

    public CaptureHelper setIRequestCaptureCallback(IRequestCaptureCallback callback){
        mIRequestCaptureCallback = callback;
        return instance;
    }

    public CaptureHelper setIChoosePictureCallback(IChoosePictureCallback callback){
        mIChoosePictureCallback = callback;
        return instance;
    }

    /**
     * 打开相机进行拍照
     * @param savePath
     */
    public void requestCapture(String savePath){

        final File captureFile = new File(savePath);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri photoURI = null;

        if(captureFile != null){

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                photoURI = FileProvider.getUriForFile(mWeakActivityRef.get(),
                        "com.qhh.baselib.image.provider",
                        captureFile);
                //这段代码主要是拍照之后保存在临时路径上
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
            }else{
                //7.0以下使用这种方式创建一个Uri
                photoURI = Uri.fromFile(captureFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            }

        }

        getFragment().startActivityforResult(intent, new Callback() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                if(resultCode == RESULT_OK){
                    String path = captureFile.getAbsolutePath();
                    if(mIRequestCaptureCallback != null)
                        mIRequestCaptureCallback.success(resultCode,path);
                }else{
                    if(mIRequestCaptureCallback != null)
                        mIRequestCaptureCallback.error(resultCode);
                }

            }
        });

    }

    /**
     * 选择系统相册图片
     */
    public void chooseSysGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);

        getFragment().startActivityforResult(intent, new Callback(){
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {

                String fileSrc = null;

                if(resultCode == RESULT_OK){

                    if ("file".equals(data.getData().getScheme())) {
                        // 有些低版本机型返回的Uri模式为file
                        fileSrc = data.getData().getPath();
                    } else {
                        // Uri模型为content
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = mWeakActivityRef.get().getContentResolver().query(data.getData(), proj,
                                null, null, null);
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        fileSrc = cursor.getString(idx);
                        cursor.close();
                    }

                    if(mIChoosePictureCallback != null)
                        mIChoosePictureCallback.success(resultCode,fileSrc);

                }else{
                    if(mIChoosePictureCallback != null)
                        mIChoosePictureCallback.error(resultCode);
                }
            }
        });
    }

    /**
     * 选择系统相册图片，并且存储到指定目录
     * @param childFile 二级文件夹
     * @param fileName 最终文件名称
     */
    public void chooseSysGallery(final String childFile, final String fileName, final int quality){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);

        getFragment().startActivityforResult(intent, new Callback(){
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {

                String fileSrc = null;

                if(resultCode == RESULT_OK){

                    if ("file".equals(data.getData().getScheme())) {
                        // 有些低版本机型返回的Uri模式为file
                        fileSrc = data.getData().getPath();
                    } else {
                        // Uri模型为content
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = mWeakActivityRef.get().getContentResolver().query(data.getData(), proj,
                                null, null, null);
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        fileSrc = cursor.getString(idx);
                        cursor.close();
                    }

                    String picPath = ImageUtils.getInstance().compressPic(mWeakActivityRef.get(),
                            fileSrc, quality, childFile, fileName);

                    if(!TextUtils.isEmpty(picPath)){

                        if(mIChoosePictureCallback != null)
                            mIChoosePictureCallback.success(resultCode,picPath);

                    }else{
                        if(mIChoosePictureCallback != null)
                            mIChoosePictureCallback.error(resultCode);
                    }

                }else{
                    if(mIChoosePictureCallback != null)
                        mIChoosePictureCallback.error(resultCode);
                }
            }
        });
    }

    private RouterFragment getFragment(){

        FragmentManager manager = mWeakActivityRef.get().getSupportFragmentManager();
        RouterFragment fragment = (RouterFragment)manager.findFragmentByTag(REQUEST_CAPTURE);

        if(fragment == null){
            fragment = RouterFragment.getInstance();
            manager.beginTransaction()
                    .add(fragment,REQUEST_CAPTURE)
                    .commitAllowingStateLoss();
            manager.executePendingTransactions();
        }

        return fragment;
    }

    public void release(){

        if(mWeakActivityRef.get() != null){

            FragmentManager manager = mWeakActivityRef.get().getSupportFragmentManager();
            RouterFragment fragment = (RouterFragment)manager.findFragmentByTag(REQUEST_CAPTURE);

            if(fragment != null){
                fragment = RouterFragment.getInstance();
                manager.beginTransaction()
                        .remove(fragment)
                        .commitAllowingStateLoss();
                manager.executePendingTransactions();
            }
            instance = null;
            mWeakActivityRef.clear();

        }

    }

    public interface Callback{
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

}
