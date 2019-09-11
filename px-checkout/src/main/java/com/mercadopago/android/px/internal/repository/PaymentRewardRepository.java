package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.internal.PaymentReward;
import java.util.List;

public interface PaymentRewardRepository {
    MPCall<PaymentReward> getPaymentReward(@NonNull List<String> paymentIds);
}