package com.mercadopago.android.px.internal.features.business_result;

import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.util.Logger;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.BusinessActions;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.ExitAction;

import static com.mercadopago.android.px.internal.features.Constants.RESULT_CUSTOM_EXIT;
import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.getSafeIntent;

public class BusinessPaymentResultActivity extends PXActivity<BusinessPaymentResultPresenter>
    implements BusinessPaymentResultContract.View, PaymentResultBody.OnClickBusinessActions, ActionDispatcher {

    private static final String TAG = BusinessPaymentResultActivity.class.getSimpleName();
    private static final String EXTRA_BUSINESS_PAYMENT_MODEL = "extra_business_payment_model";

    private BusinessPaymentViewModel paymentResultViewModel;

    public static Intent getIntent(@NonNull final Context context,
        @NonNull final BusinessPaymentModel model) {
        final Intent intent = new Intent(context, BusinessPaymentResultActivity.class);
        intent.putExtra(EXTRA_BUSINESS_PAYMENT_MODEL, model);
        return intent;
    }

    @Override
    public void onCreated(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.px_activity_payment_result);

        presenter = createPresenter();
        presenter.attachView(this);
        if (savedInstanceState == null) {
            presenter.onFreshStart();
        }
        paymentResultViewModel = new BusinessPaymentViewModel(Session.getInstance().getConfigurationModule().getPaymentSettings(),
            getIntent().getParcelableExtra(EXTRA_BUSINESS_PAYMENT_MODEL));

        paymentResultViewModel.modelMutableLiveData.observe(this, businessPaymentResultViewModel -> {
            configureViews(businessPaymentResultViewModel);
        });
    }


    @NonNull
    private BusinessPaymentResultPresenter createPresenter() {
        final BusinessPaymentModel model = getIntent().getParcelableExtra(EXTRA_BUSINESS_PAYMENT_MODEL);
        return new BusinessPaymentResultPresenter(
            Session.getInstance().getConfigurationModule().getPaymentSettings(), model);
    }


    public void configureViews(@NonNull final BusinessPaymentResultViewModel model) {
        findViewById(R.id.loading).setVisibility(View.GONE);
        final PaymentResultHeader header = findViewById(R.id.header);
        header.setModel(model.headerModel);
        header.setOnClickListener(v -> {
           paymentResultViewModel.modelMutableLiveData.setValue(model);
        });
        final PaymentResultBody body = findViewById(R.id.body);
        body.init(model.bodyModel, this);
        //TODO migrate
        BusinessResultLegacyRenderer.render(findViewById(R.id.container), this, model);
        Toast.makeText(this, "pija", Toast.LENGTH_SHORT).show();
    }


    public void configureViews(@NonNull final BusinessPaymentResultViewModel model,
        @NonNull final BusinessActions callback) {
        findViewById(R.id.loading).setVisibility(View.GONE);
        final PaymentResultHeader header = findViewById(R.id.header);
        header.setModel(model.headerModel);
        final PaymentResultBody body = findViewById(R.id.body);
        body.init(model.bodyModel, callback);
        //TODO migrate
        BusinessResultLegacyRenderer.render(findViewById(R.id.container), callback, model);
    }

    @Override
    public void onBackPressed() {
        presenter.onAbort();
    }

    @Override
    public void processCustomExit() {
        processCustomExit(new ExitAction("exit", RESULT_OK));
    }

    @Override
    public void processCustomExit(@NonNull final ExitAction action) {
        final Intent intent = action.toIntent();
        setResult(RESULT_CUSTOM_EXIT, intent);
        finish();
    }

    @Override
    public void setStatusBarColor(@ColorRes final int color) {
        ViewUtils.setStatusBarColor(ContextCompat.getColor(this, color), getWindow());
    }

    @Override
    public void processBusinessAction(@NonNull final String deepLink) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)));
        } catch (ActivityNotFoundException e) {
            Logger.debug(TAG, e);
        }
    }

    @Override
    public void processCrossSellingBusinessAction(@NonNull final String deepLink) {
        try {
            startActivity(getSafeIntent(this, Uri.parse(deepLink)));
        } catch (ActivityNotFoundException e) {
            Logger.debug(TAG, e);
        }
    }

    @Override
    public void onClickShowAllDiscounts(@NonNull final String deepLink) {

    }

    @Override
    public void OnClickDownloadAppButton(@NonNull final String deepLink) {

    }

    @Override
    public void OnClickCrossSellingButton(@NonNull final String deepLink) {

    }

    @Override
    public void onClickDiscountItem(final int index, @Nullable final String deepLink, @Nullable final String trackId) {

    }

    @Override
    public void onClickLoyaltyButton(@NonNull final String deepLink) {

    }

    @Override
    public void dispatch(final Action action) {

    }
}