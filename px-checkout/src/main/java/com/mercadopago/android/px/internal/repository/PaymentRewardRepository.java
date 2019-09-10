package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.internal.PaymentReward;
import java.util.List;

public interface PaymentRewardRepository {
    void getPaymentReward(@NonNull List<IPaymentDescriptor> paymentIds, @NonNull final PaymentRewardCallback callback);

    interface PaymentRewardCallback {
        void handleResult(@NonNull final IPaymentDescriptor payment, @NonNull final PaymentReward paymentReward);
    }
}