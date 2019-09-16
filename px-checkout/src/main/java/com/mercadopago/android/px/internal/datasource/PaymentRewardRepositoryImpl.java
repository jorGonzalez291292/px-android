package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.mercadopago.android.px.internal.datasource.cache.Cache;
import com.mercadopago.android.px.internal.repository.PaymentRewardRepository;
import com.mercadopago.android.px.internal.services.PaymentRewardService;
import com.mercadopago.android.px.internal.util.StatusHelper;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.PaymentReward;
import com.mercadopago.android.px.model.internal.mappers.PaymentIdMapper;
import com.mercadopago.android.px.services.Callback;
import java.util.List;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT;

public class PaymentRewardRepositoryImpl implements PaymentRewardRepository {
    private static final String DELIMITER = ",";
    private static final String PLATFORM = "MP";

    private final Cache<PaymentReward> paymentRewardCache;
    private final PaymentRewardService paymentRewardService;
    private final String privateKey;

    public PaymentRewardRepositoryImpl(
        final Cache<PaymentReward> paymentRewardCache,
        final PaymentRewardService paymentRewardService, final String privateKey) {
        this.paymentRewardCache = paymentRewardCache;
        this.paymentRewardService = paymentRewardService;
        this.privateKey = privateKey;
    }

    @Override
    public void getPaymentReward(@NonNull final List<IPaymentDescriptor> payments,
        @NonNull final PaymentRewardCallback paymentRewardCallback) {
        final Callback<PaymentReward> serviceCallback = getServiceCallback(payments, paymentRewardCallback);
        final boolean hasAccessToken = TextUtil.isNotEmpty(privateKey);
        final boolean hasToReturnEmptyResponse = !hasAccessToken || !StatusHelper.isSuccess(payments);

        if (hasToReturnEmptyResponse) {
            paymentRewardCallback.handleResult(payments.get(0), PaymentReward.EMPTY);
        } else if (paymentRewardCache.isCached()) {
            paymentRewardCache.get().enqueue(serviceCallback);
        } else {
            newCall(payments, serviceCallback);
        }
    }

    private void newCall(@NonNull final Iterable<IPaymentDescriptor> payments,
        @NonNull final Callback<PaymentReward> serviceCallback) {
        final List<String> paymentsIds = new PaymentIdMapper().map(payments);
        final String joinedPaymentIds = TextUtils.join(DELIMITER, paymentsIds);
        paymentRewardService.getPaymentReward(API_ENVIRONMENT, privateKey, joinedPaymentIds, PLATFORM)
            .enqueue(serviceCallback);
    }

    private Callback<PaymentReward> getServiceCallback(@NonNull final List<IPaymentDescriptor> paymentIds,
        @NonNull final PaymentRewardCallback paymentRewardCallback) {
        return new Callback<PaymentReward>() {
            @Override
            public void success(final PaymentReward paymentReward) {
                paymentRewardCallback.handleResult(paymentIds.get(0), paymentReward);
            }

            @Override
            public void failure(final ApiException apiException) {
                paymentRewardCallback.handleResult(paymentIds.get(0), PaymentReward.EMPTY);
            }
        };
    }
}