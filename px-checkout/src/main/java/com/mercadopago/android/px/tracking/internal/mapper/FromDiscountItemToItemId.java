package com.mercadopago.android.px.tracking.internal.mapper;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.internal.PaymentReward;

public class FromDiscountItemToItemId extends Mapper<PaymentReward.Discount.Item, String> {
    @Override
    public String map(@NonNull final PaymentReward.Discount.Item discountItem) {
        return discountItem.getCampaignId();
    }
}