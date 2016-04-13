package com.example.pj.news_demo.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by pj on 2016/4/8.
 */
public class MemoryCacheUtil {
    private static final String TAG = "MemoryCacheUtil";
    private static LruCache<String, Bitmap> sMemoryCache;
    private static final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);

    private MemoryCacheUtil() {
    }

    private static MemoryCacheUtil sMemoryCacheUtil;

    public static MemoryCacheUtil getInstance() {
        if (sMemoryCacheUtil == null) {
            synchronized (MemoryCacheUtil.class) {
                if (sMemoryCacheUtil == null) {
                    sMemoryCacheUtil = new MemoryCacheUtil();
                    initMemoryCache();
                }
            }
        }
        return sMemoryCacheUtil;
    }

    private static void initMemoryCache() {
        if (sMemoryCache == null) {
            sMemoryCache = new LruCache<String, Bitmap>(MAX_MEMORY / 8) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight() / 1024;
                }
            };
        }
    }

    public void clearCache() {
        if (sMemoryCache != null) {
            if (sMemoryCache.size() > 0) {
                Log.d("CacheUtils",
                        "sMemoryCache.size() " + sMemoryCache.size());
                sMemoryCache.evictAll();
                Log.d("CacheUtils", "sMemoryCache.size()" + sMemoryCache.size());
            }
            sMemoryCache = null;
        }
    }

    public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (sMemoryCache.get(key) == null) {
            if (key != null && bitmap != null)
                sMemoryCache.put(key, bitmap);
        } else
            Log.w(TAG, "the res is already exits");
    }

    public synchronized Bitmap getBitmapFromMemCache(String key) {
        Bitmap bm = sMemoryCache.get(key);
        if (key != null) {
            return bm;
        }
        return null;
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    public synchronized void removeImageCache(String key) {
        if (key != null) {
            if (sMemoryCache != null) {
                Bitmap bm = sMemoryCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }

}
