package com.example.pj.news_demo.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by pj on 2016/4/7
 * 对UniversalImageLoader再做一层封装
 */
public class ImageLoaderTools {
    private static final String TAG = "ImageLoaderTools";
    private static ImageLoaderTools sImageLoaderTools;
    private static ImageLoader sImageLoader;
    private static final int DISK_CACHE_SIZE_BYTES = 50 * 1024 * 1024;
    private static final int MEMORY_CACHE_SIZE_BYTES = 2 * 1024 * 1024;

    private ImageLoaderTools(Context context) {
        ImageLoader imageLoader = initImageLoader(context);
        sImageLoader = imageLoader;
    }

    //确保该方法只会被调用一次
    private static ImageLoader initImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存�??
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
//                .showStubImage(R.drawable.friends_sends_pictures_no)//设置下载期间显示的图�?
//                .showImageForEmptyUri(R.drawable.friends_sends_pictures_no)//设置图片URI为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.friends_sends_pictures_no)//设置图片加载/解码过程中错误时候显示的图片
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)//
                .discCacheSize(DISK_CACHE_SIZE_BYTES)
                .memoryCacheSize(MEMORY_CACHE_SIZE_BYTES)
                .build();

        ImageLoader tmpIL = ImageLoader.getInstance();
        tmpIL.init(config);//全局初始化此配置
        return tmpIL;

    }

    public static ImageLoaderTools getInstance(Context context) {
        if (sImageLoaderTools == null) {
            sImageLoaderTools = new ImageLoaderTools(context);
        }
        return sImageLoaderTools;
    }

    public void displayImage(String res, ImageView imageView) {
        //这个res分很多种情况
        if (res.startsWith("http://")) {
            sImageLoader.displayImage(res, imageView);
        } else if (res.startsWith("assets://")) {
            sImageLoader.displayImage(res, imageView);
        } else if (res.startsWith("file://")) {
            sImageLoader.displayImage(res, imageView);
        } else if (res.startsWith("content://")) {
            sImageLoader.displayImage(res, imageView);
        } else if (res.startsWith("drawable://")) {
            sImageLoader.displayImage(res, imageView);
        } else {
            Uri uri = Uri.parse(res);
            imageView.setImageURI(uri);

        }
    }
}
