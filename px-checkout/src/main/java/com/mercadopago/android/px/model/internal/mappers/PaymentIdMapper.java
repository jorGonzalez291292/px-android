package com.mercadopago.android.px.model.internal.mappers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.IPaymentDescriptor;

public class PaymentIdMapper extends Mapper<IPaymentDescriptor, String> {
    @Override
    public String map(@NonNull final IPaymentDescriptor payment) {
        return String.valueOf(payment.getId());
    }
}