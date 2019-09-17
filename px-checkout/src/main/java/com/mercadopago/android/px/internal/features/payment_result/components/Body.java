package com.mercadopago.android.px.internal.features.payment_result.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_result.props.BodyErrorProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsProps;
import com.mercadopago.android.px.internal.features.payment_result.props.PaymentResultBodyProps;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import com.mercadopago.android.px.internal.util.PaymentDataHelper;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;

public class Body extends Component<PaymentResultBodyProps, Void> {

    public Body(@NonNull final PaymentResultBodyProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public boolean hasInstructions() {
        return props.instruction != null;
    }

    public Instructions getInstructionsComponent() {
        final InstructionsProps instructionsProps = new InstructionsProps.Builder()
            .setInstruction(props.instruction)
            .setProcessingMode(props.processingMode)
            .build();
        return new Instructions(instructionsProps, getDispatcher());
    }

    public boolean hasBodyError() {
        final PaymentResultViewModel paymentResultViewModel =
            PaymentResultViewModelFactory.createPaymentResultViewModel(props.paymentResult);
        return paymentResultViewModel.hasBodyError();
    }

    public BodyError getBodyErrorComponent() {
        final BodyErrorProps bodyErrorProps = new BodyErrorProps.Builder()
            .setStatus(props.paymentResult.getPaymentStatus())
            .setStatusDetail(props.paymentResult.getPaymentStatusDetail())
            .setPaymentMethodName(props.paymentResult.getPaymentData().getPaymentMethod().getName())
            .setPaymentAmount(CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(props.currencyId,
                PaymentDataHelper.getPrettyAmountToPay(props.paymentResult.getPaymentData())))
            .build();
        return new BodyError(bodyErrorProps, getDispatcher());
    }
}