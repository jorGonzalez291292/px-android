package com.mercadopago.android.px.internal.features.paymentresult;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.features.paymentresult.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.model.exceptions.ApiException;

public interface PaymentResultContract {

    interface View extends MvpView {

        void setModel(@NonNull final PaymentResultViewModel model);

        void showApiExceptionError(ApiException exception, String requestOrigin);

        void showInstructionsError();

        void openLink(String url);

        void finishWithResult(final int resultCode);

        void changePaymentMethod();

        void recoverPayment();

        void copyToClipboard(@NonNull final String content);
    }

    interface Presenter {

        void freshStart();

        void onAbort();
    }
}