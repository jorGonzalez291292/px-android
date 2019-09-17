package com.mercadopago.android.px.internal.features.business_result;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.model.ExitAction;

import static com.mercadopago.android.px.internal.features.Constants.RESULT_CUSTOM_EXIT;

public class BusinessPaymentResultActivity extends PXActivity<BusinessPaymentResultPresenter>
    implements BusinessPaymentResultContract.View {

    private static final String EXTRA_BUSINESS_PAYMENT_MODEL = "extra_business_payment_model";

    public static Intent getIntent(@NonNull final Context context, @NonNull final BusinessPaymentModel model) {
        final Intent intent = new Intent(context, BusinessPaymentResultActivity.class);
        intent.putExtra(EXTRA_BUSINESS_PAYMENT_MODEL, model);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.px_activity_business_result);

        presenter = createPresenter();
        presenter.attachView(this);
        if (savedInstanceState == null) {
            presenter.onFreshStart();
        }
    }

    @NonNull
    private BusinessPaymentResultPresenter createPresenter() {
        final BusinessPaymentModel model = getIntent().getParcelableExtra(EXTRA_BUSINESS_PAYMENT_MODEL);
        return new BusinessPaymentResultPresenter(
            Session.getInstance().getConfigurationModule().getPaymentSettings(), model);
    }

    @Override
    public void configureViews(@NonNull final BusinessPaymentResultViewModel model,
        @NonNull final ActionDispatcher callback) {
        final PaymentResultHeader header = findViewById(R.id.header);
        header.setModel(model.headerModel);
        final PaymentResultBody body = findViewById(R.id.body);
        body.setModel(model.bodyModel);
        //TODO migrate
        BusinessResultLegacyRenderer.render(findViewById(R.id.container), callback, model);
    }

    @Override
    public void onBackPressed() {
        presenter.onAbort();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
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
}