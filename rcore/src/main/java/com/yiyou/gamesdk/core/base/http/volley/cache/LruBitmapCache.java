package com.yiyou.gamesdk.core.base.http.volley.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley1.toolbox.ImageLoader;

/**
 * Created by chenshuide on 15/6/4.
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    private static final int DEFAULT_MAX_SIZE = 1024 * 1024;//1M

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }


    public LruBitmapCache() {
        this(DEFAULT_MAX_SIZE);
    }


    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url,bitmap);
    }
}
