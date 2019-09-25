package com.mercadopago.android.px.model.internal;

import com.mercadopago.android.px.model.CardMetadata;

public interface ExpressPaymentMethod {

    default String getPaymentMethodId() {
        return "";
    }

    default String getPaymentTypeId() {
        return "";
    }

    default CardMetadata getCard() {
        return null;
    }

    default boolean isCard() {
        return false;
    }

    default String getCustomOptionId() {
        return null;
    }
}