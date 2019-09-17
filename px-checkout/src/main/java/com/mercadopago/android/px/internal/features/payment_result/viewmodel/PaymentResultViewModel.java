package com.mercadopago.android.px.internal.features.payment_result.viewmodel;

import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;

public class PaymentResultViewModel {

    public final PaymentResultHeader.Model headerModel;
    public final PaymentResultBody.Model bodyModel;

    public PaymentResultViewModel(final PaymentResultHeader.Model headerModel,
        final PaymentResultBody.Model bodyModel) {
        this.headerModel = headerModel;
        this.bodyModel = bodyModel;
    }
}