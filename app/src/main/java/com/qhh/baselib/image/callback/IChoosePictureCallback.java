package com.qhh.baselib.image.callback;

/**
 * @author qinhaihang_vendor
 * @version $Rev$
 * @time 2019/4/23 16:23
 * @des
 * @packgename com.qhh.baselib.image.callback
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public interface IChoosePictureCallback {
    void error(int resultCode);
    void success(int resultCode,String path);
}
