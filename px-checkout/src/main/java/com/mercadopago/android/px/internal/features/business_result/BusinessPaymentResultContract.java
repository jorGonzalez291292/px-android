package com.mercadopago.android.px.internal.features.business_result;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.model.ExitAction;

/* default */ interface BusinessPaymentResultContract {

    /* default */ interface View extends MvpView {
        void configureViews(@NonNull final BusinessPaymentResultViewModel model,
            @NonNull final ActionDispatcher callback);

        void processCustomExit();

        void processCustomExit(@NonNull final ExitAction action);
    }

    /* default */ interface Presenter {
        void onFreshStart();

        void onAbort();
    }
}