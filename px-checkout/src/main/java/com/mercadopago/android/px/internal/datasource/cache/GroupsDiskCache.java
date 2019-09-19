package com.mercadopago.android.px.internal.datasource.cache;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.core.FileManager;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupsDiskCache implements Cache<PaymentMethodSearch> {

    @NonNull private final FileManager fileManager;
    @NonNull private final JsonUtil jsonUtil;
    @NonNull private final File groupsFile;

    private static final String DEF_FILE_NAME = "px_groups";
    /* default */ final ExecutorService executorService;
    /* default */ final Handler mainHandler;

    public GroupsDiskCache(@NonNull final FileManager fileManager,
        @NonNull final JsonUtil jsonUtil,
        @NonNull final File cacheDir) {
        this.fileManager = fileManager;
        this.jsonUtil = jsonUtil;
        groupsFile = new File(createFileName(cacheDir));
        executorService = Executors.newFixedThreadPool(1);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    private String createFileName(@NonNull final File cacheDir) {
        final StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(cacheDir.getPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append(DEF_FILE_NAME);
        return fileNameBuilder.toString();
    }

    @NonNull
    @Override
    public MPCall<PaymentMethodSearch> get() {
        return new MPCall<PaymentMethodSearch>() {
            @Override
            public void enqueue(final Callback<PaymentMethodSearch> callback) {
                executorService.execute(() -> read(callback));
            }

            @Override
            public void execute(final Callback<PaymentMethodSearch> callback) {
                readExec(callback);
            }
        };
    }

    /* default */ void read(final Callback<PaymentMethodSearch> callback) {
        if (isCached()) {
            final String fileContent = fileManager.readFileContent(groupsFile);
            final PaymentMethodSearch paymentMethodSearch = jsonUtil.fromJson(fileContent, PaymentMethodSearch.class);
            if (paymentMethodSearch != null) {
                mainHandler.post(() -> callback.success(paymentMethodSearch));
            } else {
                mainHandler.post(() -> callback.failure(new ApiException()));
            }
        } else {
            mainHandler.post(() -> callback.failure(new ApiException()));
        }
    }

    /* default */ void readExec(final Callback<PaymentMethodSearch> callback) {
        if (isCached()) {
            final String fileContent = fileManager.readFileContent(groupsFile);
            final PaymentMethodSearch paymentMethodSearch = jsonUtil.fromJson(fileContent, PaymentMethodSearch.class);
            if (paymentMethodSearch != null) {
                callback.success(paymentMethodSearch);
            } else {
                callback.failure(new ApiException());
            }
        } else {
            callback.failure(new ApiException());
        }
    }

    @Override
    public void put(@NonNull final PaymentMethodSearch groups) {
        if (!isCached()) {
            executorService.execute(new CacheWriter(fileManager, groupsFile, jsonUtil.toJson(groups)));
        }
    }

    @Override
    public void evict() {
        if (isCached()) {
            new CacheEvict(fileManager, groupsFile).run();
        }
    }

    @Override
    public boolean isCached() {
        return fileManager.exists(groupsFile);
    }
}
