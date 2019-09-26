package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.mercadopago.android.px.internal.datasource.cache.Cache;
import com.mercadopago.android.px.internal.repository.PaymentRewardRepository;
import com.mercadopago.android.px.internal.services.PaymentRewardService;
import com.mercadopago.android.px.internal.util.StatusHelper;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.PaymentReward;
import com.mercadopago.android.px.model.internal.mappers.PaymentIdMapper;
import com.mercadopago.android.px.services.BuildConfig;
import com.mercadopago.android.px.services.Callback;
import java.util.List;

public class PaymentRewardRepositoryImpl implements PaymentRewardRepository {

    /* default */ final Cache<PaymentReward> paymentRewardCache;
    private final PaymentRewardService paymentRewardService;
    private final String privateKey;
    private final String platform;
    private final String locale;
    private final String screenDensity;

    public PaymentRewardRepositoryImpl(@NonNull final Cache<PaymentReward> paymentRewardCache,
        @NonNull final PaymentRewardService paymentRewardService, @Nullable final String privateKey,
        @NonNull final String platform, @NonNull final String locale, @NonNull final String screenDensity) {
        this.paymentRewardCache = paymentRewardCache;
        this.paymentRewardService = paymentRewardService;
        this.privateKey = privateKey;
        this.platform = platform;
        this.locale = locale;
        this.screenDensity = screenDensity;
    }

    @Override
    public void getPaymentReward(@NonNull final List<IPaymentDescriptor> payments, @NonNull final PaymentResult paymentResult,
        @NonNull final PaymentRewardCallback paymentRewardCallback) {
        final Callback<PaymentReward> serviceCallback = getServiceCallback(payments, paymentResult, paymentRewardCallback);
        final boolean hasAccessToken = TextUtil.isNotEmpty(privateKey);
        final boolean hasToReturnEmptyResponse = !hasAccessToken || !StatusHelper.isSuccess(payments);
        final Campaign campaign = paymentResult.getPaymentData().getCampaign();
        final String campaignId = campaign != null ? campaign.getId() : "";

        if (hasToReturnEmptyResponse) {
            paymentRewardCallback.handleResult(payments.get(0), paymentResult, PaymentReward.EMPTY);
        } else if (paymentRewardCache.isCached()) {
            paymentRewardCache.get().enqueue(serviceCallback);
        } else {
            newCall(payments, campaignId, serviceCallback);
        }
    }

    private void newCall(@NonNull final Iterable<IPaymentDescriptor> payments, @NonNull final String campaignId,
        @NonNull final Callback<PaymentReward> serviceCallback) {
        final List<String> paymentsIds = new PaymentIdMapper().map(payments);
        final String joinedPaymentIds = TextUtil.join(paymentsIds);
        paymentRewardService.getPaymentReward(BuildConfig.API_ENVIRONMENT, locale, screenDensity, privateKey,
            joinedPaymentIds, platform, campaignId).enqueue(serviceCallback);
    }

    private Callback<PaymentReward> getServiceCallback(@NonNull final List<IPaymentDescriptor> paymentIds,@NonNull final PaymentResult paymentResult,
        @NonNull final PaymentRewardCallback paymentRewardCallback) {
        return new Callback<PaymentReward>() {
            @Override
            public void success(final PaymentReward paymentReward) {
                paymentRewardCache.put(paymentReward);
                paymentRewardCallback.handleResult(paymentIds.get(0), paymentResult, paymentReward);
            }

            @Override
            public void failure(final ApiException apiException) {
                final PaymentReward paymentReward = PaymentReward.EMPTY;
                paymentRewardCache.put(paymentReward);
                paymentRewardCallback.handleResult(paymentIds.get(0), paymentResult, paymentReward);
            }
        };
    }
}