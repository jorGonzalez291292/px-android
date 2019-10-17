package com.mercadopago.android.px.core.internal;

import android.content.Context;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.StatFs;
import android.support.annotation.VisibleForTesting;
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

public class CheckoutDownloader implements Downloader {
    @VisibleForTesting final Call.Factory client;
    private final Cache cache;
    private boolean sharedClient = true;
    private static final String PICASSO_CACHE = "px-picasso/cache";
    private static final int MIN_DISK_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int THREAD_STATS_TAG = new Random().hashCode();

    private static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(size, MIN_DISK_CACHE_SIZE);
    }

    /**
     * Creates a {@link Cache} that would have otherwise been created by calling
     * {@link #CheckoutDownloader(Context)}. This allows you to build your own {@link OkHttpClient}
     * while still getting the default disk cache.
     */
    public static Cache createDefaultCache(Context context) {
        File dir = createDefaultCacheDir(context);
        return new Cache(dir, calculateDiskCacheSize(dir));
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
    public CheckoutDownloader(final Context context) {
        this(createDefaultCacheDir(context));
    }

    /**
     * Create new downloader that uses OkHttp. This will install an image cache into the specified
     * directory.
     *
     * @param cacheDir The directory in which the cache should be stored
     */
    public CheckoutDownloader(final File cacheDir) {
        this(cacheDir, calculateDiskCacheSize(cacheDir));
    }

    /**
     * Create new downloader that uses OkHttp. This will install an image cache into your application
     * cache directory.
     *
     * @param maxSize The size limit for the cache.
     */
    public CheckoutDownloader(final Context context, final long maxSize) {
        this(createDefaultCacheDir(context), maxSize);
    }

    /**
     * Create new downloader that uses OkHttp. This will install an image cache into the specified
     * directory.
     *
     * @param cacheDir The directory in which the cache should be stored
     * @param maxSize The size limit for the cache.
     */
    public CheckoutDownloader(final File cacheDir, final long maxSize) {
        this(new OkHttpClient.Builder().cache(new Cache(cacheDir, maxSize)).build());
        sharedClient = false;
    }

    /**
     * Create a new downloader that uses the specified OkHttp instance. A response cache will not be
     * automatically configured.
     */
    public CheckoutDownloader(OkHttpClient client) {
        this.client = client;
        this.cache = client.cache();
    }

    /** Create a new downloader that uses the specified {@link Call.Factory} instance. */
    public CheckoutDownloader(Call.Factory client) {
        this.client = client;
        this.cache = null;
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
        if (responseCode >= 300) {
            response.body().close();
            TrafficStats.clearThreadStatsTag();
            throw new ResponseException(responseCode + " " + response.message(), networkPolicy,
                responseCode);
        }

        boolean fromCache = response.cacheResponse() != null;

        ResponseBody responseBody = response.body();
        return new Response(responseBody.byteStream(), fromCache, responseBody.contentLength());
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
