package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.mercadopago.android.px.internal.repository.PaymentRewardRepository;
import com.mercadopago.android.px.internal.services.PaymentRewardService;
import com.mercadopago.android.px.model.internal.PaymentReward;
import java.util.List;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT;

public class PaymentRewardRepositoryImpl implements PaymentRewardRepository {
    private static final String DELIMITER = ",";
    private static final String PLATFORM = "MP";
    private final PaymentRewardService paymentRewardService;

    public PaymentRewardRepositoryImpl(final PaymentRewardService paymentRewardService) {
        this.paymentRewardService = paymentRewardService;
    }

    public PaymentReward getPaymentReward(@NonNull final String accessToken, @NonNull final List<String> paymentIds) {
        final String joinedPaymentIds = TextUtils.join(DELIMITER, paymentIds);
        paymentRewardService.getPaymentReward(API_ENVIRONMENT, accessToken, joinedPaymentIds, PLATFORM);
    }
}