package com.example.newsmodule;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yangfan on 2017/6/15.
 */

public class ImageUtils {
    Bitmap bitmap;
    Bitmap bitmap_final;
    String url;
    boolean isGet;


    public ImageUtils(String url) {
        this.url = url;
        new Task().execute(url);
    }

    public  void changeImage(String newUrl){
        new Task().execute(newUrl);
    }

    /**
     * 异步线程下载图片
     */
    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap = GetImageInputStream((String) params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SavaImage(bitmap, Environment.getExternalStorageDirectory().getPath() + "/Test");//保存的文件夹
        }
    }

    /**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @param path   本地路径
     * @return void
     */
    public void SavaImage(Bitmap bitmap, String path) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(path + "/" + "bizhiii" + ".png");//保存时的文件名
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取到本地下载的图片
     *
     * @return
     */
    public void getImage(MyListener myListener) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(Environment.getExternalStorageDirectory()
                    .getPath() + "/Test" + "/" + "bizhiii" + ".png");
            isGet = true;
            bitmap_final = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            isGet = false;
        }
        if (null != myListener) {
            myListener.myListener(isGet,bitmap_final);
        }
    }

    /**
     * 给ImageView设置图片并完成适配
     * @param activity
     * @param view
     * @param drawableResId
     */
    public void scaleImage(final Activity activity, final View view, Bitmap drawableResId) {
        Log.e("kaishi", "开始调用");
        // 获取屏幕的高宽
        Point outSize = new Point();
        activity.getWindow().getWindowManager().getDefaultDisplay().getSize(outSize);
        // 解析将要被处理的图片
//        Bitmap resourceBitmap = BitmapFactory.decodeResource(activity.getResources(), drawableResId);
        Bitmap resourceBitmap = drawableResId;
        if (resourceBitmap == null) {
            return;
        }
        // 开始对图片进行拉伸或者缩放
        // 使用图片的缩放比例计算将要放大的图片的高度
        int bitmapScaledHeight = Math.round(resourceBitmap.getHeight() * outSize.x * 1.0f / resourceBitmap.getWidth());
        // 以屏幕的宽度为基准，如果图片的宽度比屏幕宽，则等比缩小，如果窄，则放大
        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resourceBitmap, outSize.x, bitmapScaledHeight, false);
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //这里防止图像的重复创建，避免申请不必要的内存空间
                if (scaledBitmap.isRecycled())
                    //必须返回true
                    return true;
                // 当UI绘制完毕，我们对图片进行处理
                int viewHeight = view.getMeasuredHeight();
                // 计算将要裁剪的图片的顶部以及底部的偏移量
                int offset = (scaledBitmap.getHeight() - viewHeight) / 2;
                // 对图片以中心进行裁剪，裁剪出的图片就是非常适合做引导页的图片了
                Bitmap finallyBitmap = Bitmap.createBitmap(scaledBitmap, 0, offset, scaledBitmap.getWidth(),
                        scaledBitmap.getHeight() - offset * 2);
                if (!finallyBitmap.equals(scaledBitmap)) {//如果返回的不是原图，则对原图进行回收
                    scaledBitmap.recycle();
                    System.gc();
                }
//                // 设置图片显示
                view.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), finallyBitmap));
                return true;
            }
        });
//        mimgv.setBackgroundResource(R.mipmap.ic_launcher);//这里需要手动设置一张随意图片才能触发上面的监听完成图片设置
        view.setBackgroundColor(Color.parseColor("#ffffff"));//这里需要手动设置背景颜色才能触发上面的监听完成图片设置
    }

    public interface MyListener {
        public void myListener(boolean isget, Bitmap bt);
    }

}
