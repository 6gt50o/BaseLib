package com.qhh.baselib.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import java.util.Random;

/**
 * @author qinhaihang_vendor
 * @version $Rev$
 * @time 2019/4/23 14:03
 * @des
 * @packgename com.qhh.baselib.image
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public class RouterFragment extends Fragment {

    private static final int MAX_TRY_COUNT = 10; //requesCode 刷新次数

    private SparseArray<CaptureHelper.Callback> mCallBacks = new SparseArray<>();
    private Random mCodeGenerator = new Random();

    public RouterFragment() {
    }

    public static RouterFragment getInstance(){
        return new RouterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private int createRequestCode() {
        int requestCode;
        int tryCount = 0;
        do {
            requestCode = mCodeGenerator.nextInt(0x0000FFFF);
            tryCount++;
        } while (mCallBacks.indexOfKey(requestCode) >= 0 && tryCount < MAX_TRY_COUNT);

        return requestCode;
    }

    public void startActivityforResult(Intent intent, CaptureHelper.Callback callback){
        int requestCode = createRequestCode();
        mCallBacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CaptureHelper.Callback callback = mCallBacks.get(requestCode);
        mCallBacks.remove(requestCode);
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
        }
    }

}
