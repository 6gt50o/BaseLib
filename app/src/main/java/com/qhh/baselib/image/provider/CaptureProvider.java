package com.qhh.baselib.image.provider;

import android.support.v4.content.FileProvider;

/**
 * @author qinhaihang_vendor
 * @version $Rev$
 * @time 2019/4/23 14:48
 * @des 用于兼容 7.0 以上系统
 * @packgename com.qhh.baselib.image.provider
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public class CaptureProvider extends FileProvider {
    @Override
    public boolean onCreate() {
        return super.onCreate();
    }
}
