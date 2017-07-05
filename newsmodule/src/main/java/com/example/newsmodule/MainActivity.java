package com.example.newsmodule;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

public class MainActivity extends Activity {
    private ImageView mimgv;
    private ImageUtils mImageUtils;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mimgv= (ImageView) findViewById(R.id.main_imgv);
        url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1499050319&di=3807036bd546ac52108cf463bfce1366&imgtyp" +
                "e=jpg&er=1&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201410%2F19%2F20141019231649_fxjZC.thumb.700_0.jpeg";
//        url = "https://mykomatsu.komatsu.com.cn/Storage/Plat/APP/AppSlidAd/201705021356103554870.jpg";
        init();
        
    }

    private void init() {
        mImageUtils = new ImageUtils(url);
        mImageUtils.getImage(new ImageUtils.MyListener() {
            @Override
            public void myListener(boolean isget, Bitmap bt) {
                if (isget) {
//                    if(){ //图片发生变化--直接修改url调用changeImage可以修改原本的图片文件

//                    mImageUtils.scaleImage(MainActivity.this, mimgv, BitmapFactory.decodeResource(MainActivity.this.getResources(),R.mipmap.player_bg));
                    mImageUtils.scaleImage(MainActivity.this,mimgv,bt);
                    Log.e("sjsh", "获取成功");
                } else {//获取不成功的时候写自己的逻辑
                    mimgv.setImageResource(R.mipmap.katong);
                    mImageUtils.new Task().execute(url);
                    Log.e("sjsh", "没有找到文件开始下载");
                }
            }
        });
    }

}
