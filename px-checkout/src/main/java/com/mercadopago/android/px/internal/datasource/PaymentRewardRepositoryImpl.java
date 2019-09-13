package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.mercadopago.android.px.internal.repository.PaymentRewardRepository;
import com.mercadopago.android.px.internal.services.PaymentRewardService;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.PaymentReward;
import java.util.List;

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
    public void getPaymentReward(@NonNull final List<IPaymentDescriptor> paymentIds, @NonNull final Callback callback) {
        final String joinedPaymentIds = TextUtils.join(DELIMITER, paymentIds);
        if (TextUtil.isNotEmpty(privateKey)) {
            paymentRewardService.getPaymentReward(API_ENVIRONMENT, privateKey, joinedPaymentIds, PLATFORM).enqueue(
                new com.mercadopago.android.px.services.Callback<PaymentReward>() {
                    @Override
                    public void success(final PaymentReward paymentReward) {
                        callback.handlePayment(paymentIds.get(0), paymentReward);
                    }

                    @Override
                    public void failure(final ApiException apiException) {
                        callback.handlePayment(paymentIds.get(0), PaymentReward.EMPTY);
                    }
                });
        } else {
            callback.handlePayment(paymentIds.get(0), PaymentReward.EMPTY);
        }
    }
}