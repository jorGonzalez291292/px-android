package com.mercadopago.android.px.core.internal;

import android.content.Context;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.StatFs;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.NetworkPolicy;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

final class CheckoutDownloader implements Downloader {
    private final Call.Factory client;
    private final Cache cache;
    private boolean sharedClient = true;
    private static final String PICASSO_CACHE = "px-picasso/cache";
    private static final int MAX_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MIN_DISK_CACHE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final int THREAD_STATS_TAG = new Random().hashCode();

    private static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                available = (statFs.getBlockCountLong()) * statFs.getBlockSizeLong();
            } else {
                available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            }
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }

    private static File createDefaultCacheDir(Context context) {
        File cache = new File(context.getCacheDir(), PICASSO_CACHE);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    /**
     * Create new downloader that uses OkHttp. This will install an image cache into your application
     * cache directory.
     */
    CheckoutDownloader(final Context context) {
        this(createDefaultCacheDir(context));
    }

    /**
     * Create new downloader that uses OkHttp. This will install an image cache into the specified
     * directory.
     *
     * @param cacheDir The directory in which the cache should be stored
     */
    CheckoutDownloader(final File cacheDir) {
        this(cacheDir, calculateDiskCacheSize(cacheDir));
    }

    /**
     * Create new downloader that uses OkHttp. This will install an image cache into the specified
     * directory.
     *
     * @param cacheDir The directory in which the cache should be stored
     * @param maxSize The size limit for the cache.
     */
    CheckoutDownloader(final File cacheDir, final long maxSize) {
        this(new OkHttpClient.Builder().cache(new Cache(cacheDir, maxSize)).build());
        sharedClient = false;
    }

    /**
     * Create a new downloader that uses the specified OkHttp instance. A response cache will not be
     * automatically configured.
     */
    CheckoutDownloader(OkHttpClient client) {
        this.client = client;
        this.cache = client.cache();
    }

    @Override
    public Response load(final Uri uri, final int networkPolicy) throws IOException {
        CacheControl cacheControl = null;
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                cacheControl = CacheControl.FORCE_CACHE;
            } else {
                CacheControl.Builder builder = new CacheControl.Builder();
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache();
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore();
                }
                cacheControl = builder.build();
            }
        }

        Request.Builder builder = new Request.Builder().url(uri.toString());
        if (cacheControl != null) {
            builder.cacheControl(cacheControl);
        }
        TrafficStats.setThreadStatsTag(THREAD_STATS_TAG);
        okhttp3.Response response = client.newCall(builder.build()).execute();
        int responseCode = response.code();
        ResponseBody body = response.body();
        if (responseCode >= 300) {
            if(body != null) body.close();
            TrafficStats.clearThreadStatsTag();
            throw new ResponseException(responseCode + " " + response.message(), networkPolicy,
                responseCode);
        }

        boolean fromCache = response.cacheResponse() != null;

        return body != null ? new Response(body.byteStream(), fromCache, body.contentLength()) : null;
    }

    @Override public void shutdown() {
        if (!sharedClient && cache != null) {
            try {
                cache.close();
            } catch (IOException ignored) {
            }
        }
    }
}
