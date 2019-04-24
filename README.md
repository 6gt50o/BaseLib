#### 一个自己日常接类的基础库
现有的功能是提供打开系统相机进行拍摄，访问系统相册获取相册图片 。 第一版本，以后会不断的根据自己平常在项目中使用到的以及平常的思考到的工具库总结在本项目中。

#### 集成
1. 在项目的根 build.gradle 中添加
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2. 在项目 module 中的 build.gradle添加
```gradle
implementation 'com.github.qinhaihang:BaseLib:v1.0.0'
```
> v1.0.0中可以更换到 release 中的最新版本

#### 主要API介绍

1. init 方法
init 方法是必须调用，传入相应的 Activity 实例，因为操作系统相机以及图册会涉及到 onActivtyResult 回调。本工具通过一个中间 RouteFragment 进行转接，
避免每次都要写 onActivityResult .

2. 调用相机进行拍摄，原图存入指定路径
```java
    public void takePhoto(View view) {
        //指定文件名称以及路径
        String path = new File(Environment.getExternalStorageDirectory(),
                "test1.jpg").getAbsolutePath();

        CaptureHelper.getInstance()
                .init(this)
                .setIRequestCaptureCallback(new IRequestCaptureCallback() {
                    @Override
                    public void error(int resultCode) {
                        //错误回调
                    }

                    @Override
                    public void success(int resultCode, String path) {

                    }
                })
                .requestCapture(path);
    }
```

3. 获取系统相册中选中图片的路径
```java
 CaptureHelper.getInstance()
                .init(this)
                .setIChoosePictureCallback(new IChoosePictureCallback() {
                    @Override
                    public void error(int resultCode) {
                        Log.e("qhh","chooseSysGallery resultCode = "+resultCode);
                    }

                    @Override
                    public void success(int resultCode, String path) {
                        Log.i("qhh","chooseSysGallery resultCode = "+resultCode);
                        Log.i("qhh","path = "+path);
                        //回调中 path 是最终的图片的目录
                    }
                })
                .chooseSysGallery();
```
4. 获取系统相册中的图片，并且压缩存储到指定的目录
```java
       CaptureHelper.getInstance()
                .init(this)
                .setIChoosePictureCallback(new IChoosePictureCallback() {
                    @Override
                    public void error(int resultCode) {
                        Log.e("qhh","chooseSysGallery resultCode = "+resultCode);
                    }

                    @Override
                    public void success(int resultCode, String path) {
                        Log.i("qhh","chooseSysGallery resultCode = "+resultCode);
                        Log.i("qhh","path = "+path);
                    }
                })
                .chooseSysGallery("qhh","test",100); //第一个参数是二级目录，可以传空，第二个是图片文件的名称，第三个是压缩系数
```









