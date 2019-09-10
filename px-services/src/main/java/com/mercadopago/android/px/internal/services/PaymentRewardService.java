package com.mercadopago.android.px.internal.services;

import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.internal.PaymentReward;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PaymentRewardService {

    @GET("/{version}/px_mobile/payments/congrats")
    MPCall<PaymentReward> getPaymentReward(
        @Path(value = "version", encoded = true) String version,
        @Query("access_token") String accessToken,
        @Query("payment_ids") String paymentIds,
        @Query("platform") String platform);
}