package com.mercadopago.android.px.internal.features.payment_result;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.model.exceptions.ApiException;

public interface PaymentResultContract {

    interface View extends MvpView {

        void configureViews(@NonNull final PaymentResultViewModel model, @NonNull final ActionDispatcher callback);

        void showApiExceptionError(@NonNull final ApiException exception, @NonNull final String requestOrigin);

        void showInstructionsError();

        void openLink(@NonNull final String url);

        void finishWithResult(final int resultCode);

        void changePaymentMethod();

        void recoverPayment();

        void copyToClipboard(@NonNull final String content);

        void setStatusBarColor(@ColorRes final int color);
    }

    interface Presenter {

        void onFreshStart();

        void onAbort();
    }
}