package com.qhh.baselib.image.callback;

/**
 * @author qinhaihang_vendor
 * @version $Rev$
 * @time 2019/4/23 15:37
 * @des
 * @packgename com.qhh.baselib.image.callback
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public interface IRequestCaptureCallback {
    void error(int resultCode);
    void success(int resultCode,String path);
}
