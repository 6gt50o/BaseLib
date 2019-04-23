package com.qhh.baselib.image;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;

import com.qhh.baselib.image.callback.IRequestCaptureCallback;

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

    private WeakReference<FragmentActivity> mWeakActivityRef;

    private static class SingletonHolder{
         private static final CaptureHelper INSTANCE = new CaptureHelper();
    }

    public static CaptureHelper getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public CaptureHelper init(FragmentActivity activity){
        mWeakActivityRef = new WeakReference<>(activity);
        return SingletonHolder.INSTANCE;
    }

    public CaptureHelper setIRequestCaptureCallback(IRequestCaptureCallback iRequestCaptureCallback){
        mIRequestCaptureCallback = iRequestCaptureCallback;
        return SingletonHolder.INSTANCE;
    }

    /**
     * 请求打开相机进行拍照
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

        getFragment().startActivityFroResult(intent, new Callback() {
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

    public interface Callback{
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

}
