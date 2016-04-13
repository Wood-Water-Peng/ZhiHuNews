package com.example.pj.news_demo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.pj.news_demo.app.MyApp;
import com.example.pj.news_demo.constants.CacheConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.internal.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.Buffer;
import okio.Sink;
import okio.Source;

/**
 * Created by pj on 2016/4/8.
 */
public class DiskCacheUtil {
    private static final String TAG = "DiskCacheUtil";
    private static DiskLruCache sBitmapDiskLruCache;
    private static DiskLruCache sTextDiskLruCache;
    private Context mContext;

    private DiskCacheUtil() {
        mContext = MyApp.getContext();
        initBitmapDiskLruCache();
        initTextDiskLruCache();
    }

    private static DiskCacheUtil sDiskCacheUtil;

    public static DiskCacheUtil getInstance() {
        if (sDiskCacheUtil == null) {
            synchronized (MemoryCacheUtil.class) {
                if (sDiskCacheUtil == null) {
                    sDiskCacheUtil = new DiskCacheUtil();
                }
            }
        }
        return sDiskCacheUtil;
    }


    private void initTextDiskLruCache() {
        if (mContext == null) {
            throw new IllegalStateException("you must call init() first!");
        } else {
            File file = getDiskLruCacheDir(mContext, CacheConstants.DISK_CACHE_TEXT);
            int versionID = getAppVersionNum(mContext);
            sTextDiskLruCache = DiskLruCache.create(FileSystem.SYSTEM, file, versionID, 1, 10 * 1024 * 1024);
        }
    }

    private void initBitmapDiskLruCache() {
        if (mContext == null) {
            throw new IllegalStateException("you must call init() first!");
        } else {
            File file = getDiskLruCacheDir(mContext, CacheConstants.DISK_CACHE_BITMAP);
            int versionID = getAppVersionNum(mContext);
            sBitmapDiskLruCache = DiskLruCache.create(FileSystem.SYSTEM, file, versionID, 1, 10 * 1024 * 1024);
        }
    }

    /**
     * 对于缓存目录，我们优先缓存在SD卡中
     * 如果用户没有SD卡或者SD不可用，那么缓存在App的安装目录中
     */
    private File getDiskLruCacheDir(Context context, String cacheDirName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        Log.i(TAG, "path:" + cachePath);
        File file = new File(cachePath + "/" + cacheDirName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;

    }

    /**
     * @param context
     * @return App的版本号
     */
    private int getAppVersionNum(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 缓存图片
     */
    public void saveBitmap(String url, InputStream inputStream) {
        try {
            DiskLruCache.Editor editor = sBitmapDiskLruCache.edit(String2MD5Util.hashKeyForDisk(url));
            if (editor != null) {
                Sink sink = editor.newSink(0);
                Buffer buffer = new Buffer().readFrom(inputStream);
                sink.write(buffer, buffer.size());
                sink.flush();
                editor.commit();
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取出图片
     */

    public Bitmap getBitmap(String url) {
        try {
            DiskLruCache.Snapshot snapshot = sBitmapDiskLruCache.get(String2MD5Util.hashKeyForDisk(url));
            if (snapshot != null) {
                Source source = snapshot.getSource(0);
                Buffer buffer = new Buffer();
                buffer.writeAll(source);
                buffer.flush();
                Bitmap bitmap = BitmapFactory.decodeStream(buffer.inputStream());
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url
     * @param text
     */
    public void saveText(String url, String text) {
        try {
            DiskLruCache.Editor editor = sTextDiskLruCache.edit(String2MD5Util.hashKeyForDisk(url));
            if (editor != null) {
                Sink sink = editor.newSink(0);
                Buffer buffer = new Buffer();
                byte[] bytes = text.getBytes();
                buffer.write(bytes);
                Log.i(TAG, "buffer:" + buffer.toString());
                sink.write(buffer, buffer.size());
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getText(String url) {
        try {
            DiskLruCache.Snapshot snapshot = sTextDiskLruCache.get(String2MD5Util.hashKeyForDisk(url));
            if (snapshot != null) {
                Source source = snapshot.getSource(0);
                Buffer buffer = new Buffer();
                buffer.writeAll(source);
                buffer.flush();
                return new String(buffer.readByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
