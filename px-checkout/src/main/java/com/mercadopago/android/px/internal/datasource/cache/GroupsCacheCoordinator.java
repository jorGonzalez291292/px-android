package com.mercadopago.android.px.internal.datasource.cache;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;

public class GroupsCacheCoordinator implements GroupsCache {

    @NonNull private final GroupsDiskCache groupsDiskCache;
    @NonNull private final GroupsMemCache groupsMemCache;

    public GroupsCacheCoordinator(@NonNull final GroupsDiskCache groupsDiskCache,
        @NonNull final GroupsMemCache groupsMemCache) {
        this.groupsDiskCache = groupsDiskCache;
        this.groupsMemCache = groupsMemCache;
    }

    @NonNull
    @Override
    public MPCall<PaymentMethodSearch> get() {
        if (groupsMemCache.isCached()) {
            return groupsMemCache.get();
        } else {
            return new MPCall<PaymentMethodSearch>() {
                @Override
                public void enqueue(final Callback<PaymentMethodSearch> callback) {
                    groupsDiskCache.get().enqueue(getCallbackDisk(callback));
                }

                @Override
                public void execute(final Callback<PaymentMethodSearch> callback) {
                    groupsDiskCache.get().execute(getCallbackDisk(callback));
                }
            };
        }
    }

    /* default */ Callback<PaymentMethodSearch> getCallbackDisk(final Callback<PaymentMethodSearch> callback) {
        return new Callback<PaymentMethodSearch>() {
            @Override
            public void success(final PaymentMethodSearch paymentMethodSearch) {
                groupsMemCache.put(paymentMethodSearch);
                callback.success(paymentMethodSearch);
            }

            @Override
            public void failure(final ApiException apiException) {
                callback.failure(apiException);
            }
        };
    }

    @Override
    public void put(@NonNull final PaymentMethodSearch groups) {
        groupsMemCache.put(groups);
        groupsDiskCache.put(groups);
    }

    @Override
    public void evict() {
        groupsDiskCache.evict();
        groupsMemCache.evict();
    }

    @Override
    public boolean isCached() {
        return groupsMemCache.isCached() || groupsDiskCache.isCached();
    }
}
