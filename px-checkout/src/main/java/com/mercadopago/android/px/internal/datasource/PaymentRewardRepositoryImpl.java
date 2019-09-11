package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.repository.PaymentRewardRepository;
import com.mercadopago.android.px.internal.services.PaymentRewardService;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.internal.PaymentReward;
import com.mercadopago.android.px.services.Callback;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT;

public class PaymentRewardRepositoryImpl implements PaymentRewardRepository {
    private static final String DELIMITER = ",";
    private static final String PLATFORM = "MP";
    private final PaymentRewardService paymentRewardService;
    private final String privateKey;

    public PaymentRewardRepositoryImpl(final PaymentRewardService paymentRewardService, final String privateKey) {
        this.paymentRewardService = paymentRewardService;
        this.privateKey = privateKey;
    }

    @Override
    public MPCall<PaymentReward> getPaymentReward(@NonNull final Iterable<String> paymentIds) {
        final String joinedPaymentIds = TextUtils.join(DELIMITER, paymentIds);
        if (TextUtil.isNotEmpty(privateKey)) {
            return paymentRewardService.getPaymentReward(API_ENVIRONMENT, privateKey, joinedPaymentIds, PLATFORM);
        } else {
            return new MPCall<PaymentReward>() {
                @Override
                public void enqueue(final Callback<PaymentReward> callback) {
                    callback.success(new PaymentReward());
                }

                @Override
                public void execute(final Callback<PaymentReward> callback) {
                    callback.success(new PaymentReward());
                }
            };
        }
    }
}