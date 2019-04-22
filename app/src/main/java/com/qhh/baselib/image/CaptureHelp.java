package com.qhh.baselib.image;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import java.io.File;

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
public class CaptureHelp {

    private static class SingletonHolder{
         private static final CaptureHelp INSTANCE = new CaptureHelp();
    }

    public static CaptureHelp getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void requestCapture(Activity activity,String savePath){

        File captureFile = new File(savePath);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    }

}
