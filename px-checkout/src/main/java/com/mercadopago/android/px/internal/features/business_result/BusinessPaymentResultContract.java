package com.mercadopago.android.px.internal.features.business_result;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.view.BusinessActions;
import com.mercadopago.android.px.model.ExitAction;

/* default */ interface BusinessPaymentResultContract {

    /* default */ interface View extends MvpView {
        void configureViews(@NonNull final BusinessPaymentResultViewModel model,
            @NonNull final BusinessActions callback);

        void processCustomExit();

        void processCustomExit(@NonNull final ExitAction action);

        void setStatusBarColor(@ColorRes int color);

        void downloadAppAction(@NonNull final String deepLink);
        void crossSellingAction(@NonNull final String deepLink);
        void discountItemAction(final int index, @Nullable final String deepLink, @Nullable final String trackId);
        void loyaltyAction(@NonNull final String deepLink);
    }

    /* default */ interface Presenter {
        void onFreshStart();

        void onAbort();
    }
}