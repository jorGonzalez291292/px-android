package com.mercadopago.android.px.internal.features.business_result;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.ExitAction;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.internal.PrimaryExitAction;
import com.mercadopago.android.px.model.internal.SecondaryExitAction;
import com.mercadopago.android.px.tracking.internal.events.AbortEvent;
import com.mercadopago.android.px.tracking.internal.events.PrimaryActionEvent;
import com.mercadopago.android.px.tracking.internal.events.SecondaryActionEvent;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;
import com.mercadopago.android.px.tracking.internal.views.ViewTracker;

/* default */ class BusinessPaymentResultPresenter extends BasePresenter<BusinessPaymentResultContract.View>
    implements ActionDispatcher, BusinessPaymentResultContract.Presenter {

    private final BusinessPaymentModel model;
    private final ViewTracker viewTracker;

    /* default */ BusinessPaymentResultPresenter(@NonNull final PaymentSettingRepository paymentSettings,
        @NonNull final BusinessPaymentModel model) {
        this.model = model;

        viewTracker = new ResultViewTrack(ResultViewTrack.Style.CUSTOM, new PaymentResult.Builder()
            .setPaymentData(model.getPaymentResult().getPaymentDataList())
            .setPaymentStatus(model.getPayment().getPaymentStatus())
            .setPaymentStatusDetail(model.getPayment().getPaymentStatusDetail())
            .setPaymentId(model.getPayment().getId())
            .build(), paymentSettings.getCheckoutPreference());
    }

    @Override
    public void attachView(final BusinessPaymentResultContract.View view) {
        super.attachView(view);
        mapPaymentModel();
    }

    @Override
    public void onFreshStart() {
        viewTracker.track();
    }

    @Override
    public void onAbort() {
        new AbortEvent(viewTracker).track();
        getView().processCustomExit();
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof ExitAction) {
            // Hack for tracking
            if (action instanceof PrimaryExitAction) {
                new PrimaryActionEvent(viewTracker).track();
            } else if (action instanceof SecondaryExitAction) {
                new SecondaryActionEvent(viewTracker).track();
            }
            getView().processCustomExit((ExitAction) action);
        } else {
            throw new UnsupportedOperationException("this Action class can't be executed in this screen");
        }
    }

    private void mapPaymentModel() {
        final BusinessPaymentResultViewModel viewModel = new BusinessPaymentResultMapper().map(model);
        getView().configureViews(viewModel, this);
        getView().setStatusBarColor(viewModel.headerModel.getStatusBarColor());
    }
}