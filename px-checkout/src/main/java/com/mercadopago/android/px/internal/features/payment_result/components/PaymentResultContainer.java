package com.mercadopago.android.px.internal.features.payment_result.components;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_result.model.Badge;
import com.mercadopago.android.px.internal.features.payment_result.props.PaymentResultBodyProps;
import com.mercadopago.android.px.internal.features.payment_result.props.PaymentResultProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.internal.viewmodel.GenericLocalized;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;

public class PaymentResultContainer extends Component<PaymentResultProps, Void> {

    static {
        RendererFactory.register(PaymentResultContainer.class, PaymentResultRenderer.class);
    }

    public static final int DEFAULT_ICON_IMAGE = R.drawable.px_icon_default;
    public static final int ITEM_ICON_IMAGE = R.drawable.px_icon_product;
    public static final int CARD_ICON_IMAGE = R.drawable.px_icon_card;
    public static final int BOLETO_ICON_IMAGE = R.drawable.px_icon_boleto;

    //armar componente Badge que va como hijo
    public static final int DEFAULT_BADGE_IMAGE = 0;
    public static final int CHECK_BADGE_IMAGE = R.drawable.px_badge_check;
    public static final int PENDING_BADGE_GREEN_IMAGE = R.drawable.px_badge_pending;
    public static final int PENDING_BADGE_ORANGE_IMAGE = R.drawable.px_badge_pending_orange;
    public static final int ERROR_BADGE_IMAGE = R.drawable.px_badge_error;
    public static final int WARNING_BADGE_IMAGE = R.drawable.px_badge_warning;

    public PaymentResultContainer(@NonNull final ActionDispatcher dispatcher,
        @NonNull final PaymentResultProps props) {
        super(props, dispatcher);
    }

    public PaymentResultHeader.Model getHeaderModel() {

        final PaymentResultViewModel paymentResultViewModel =
            PaymentResultViewModelFactory.createPaymentResultViewModel(props.paymentResult);

        return new PaymentResultHeader.Model.Builder()
            .setDynamicHeight(!hasBodyComponent())
            .setBackground(paymentResultViewModel.getBackgroundResId())
            .setStatusBarColor(paymentResultViewModel.getStatusBarResId())
            .setIconImage(getIconImage(props))
            .setIconUrl(getIconUrl(props))
            .setBadgeImage(getBadgeImage(props, paymentResultViewModel))
            .setTitle(new GenericLocalized(props.hasInstructions() ? props.getInstructionsTitle() : null,
                paymentResultViewModel.getTitleResId()))
            .setLabel(new GenericLocalized(null, paymentResultViewModel.getLabelResId()))
            .setSuccess(paymentResultViewModel.isApprovedSuccess())
            .build();
    }

    public boolean hasBodyComponent() {
        if (props.paymentResult != null) {
            final PaymentResultViewModel paymentResultViewModel =
                PaymentResultViewModelFactory.createPaymentResultViewModel(props.paymentResult);
            return paymentResultViewModel.isApprovedSuccess() || paymentResultViewModel.hasBodyError();
        }
        return false;
    }

    @Nullable
    public Body getBodyComponent() {
        if (props.paymentResult != null) {
            final PaymentResultBodyProps bodyProps =
                new PaymentResultBodyProps.Builder(props.getPaymentResultScreenPreference())
                    .setPaymentResult(props.paymentResult)
                    .setInstruction(props.instruction)
                    .setCurrencyId(props.currencyId)
                    .setProcessingMode(props.processingMode)
                    .build();
            return new Body(bodyProps, getDispatcher());
        }
        return null;
    }

    /* default */ FooterPaymentResult getFooterContainer() {
        return new FooterPaymentResult(props.paymentResult, getDispatcher());
    }

    @Nullable
    private String getIconUrl(@NonNull final PaymentResultProps props) {
        final PaymentResultScreenConfiguration paymentResultScreenConfiguration =
            props.getPaymentResultScreenPreference();
        final String paymentStatus = props.paymentResult.getPaymentStatus();
        final String paymentStatusDetail = props.paymentResult.getPaymentStatusDetail();
        return paymentResultScreenConfiguration.getPreferenceUrlIcon(paymentStatus, paymentStatusDetail);
    }

    private int getIconImage(@NonNull final PaymentResultProps props) {
        final PaymentResultScreenConfiguration paymentResultScreenConfiguration =
            props.getPaymentResultScreenPreference();
        final String paymentStatus = props.paymentResult.getPaymentStatus();
        final String paymentStatusDetail = props.paymentResult.getPaymentStatusDetail();

        if (paymentResultScreenConfiguration.hasCustomizedImageIcon(paymentStatus, paymentStatusDetail)) {
            return paymentResultScreenConfiguration.getPreferenceIcon(paymentStatus, paymentStatusDetail);
        } else if (isItemIconImage(props.paymentResult)) {
            return ITEM_ICON_IMAGE;
        } else if (isCardIconImage(props.paymentResult)) {
            return CARD_ICON_IMAGE;
        } else if (isBoletoIconImage(props.paymentResult)) {
            return BOLETO_ICON_IMAGE;
        } else {
            return DEFAULT_ICON_IMAGE;
        }
    }

    private boolean isItemIconImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();

        return Payment.StatusCodes.STATUS_APPROVED.equalsIgnoreCase(status) ||
            Payment.StatusCodes.STATUS_PENDING.equalsIgnoreCase(status) &&
                Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT.equalsIgnoreCase(statusDetail);
    }

    private boolean isCardIconImage(@NonNull final PaymentResult paymentResult) {
        if (isPaymentMethodIconImage(paymentResult)) {
            final String paymentTypeId = paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId();

            return PaymentTypes.PREPAID_CARD.equalsIgnoreCase(paymentTypeId) ||
                PaymentTypes.DEBIT_CARD.equalsIgnoreCase(paymentTypeId) ||
                PaymentTypes.CREDIT_CARD.equalsIgnoreCase(paymentTypeId);
        }
        return false;
    }

    private boolean isBoletoIconImage(@NonNull final PaymentResult paymentResult) {
        if (isPaymentMethodIconImage(paymentResult)) {
            final String paymentMethodId = paymentResult.getPaymentData().getPaymentMethod().getId();
            return PaymentMethods.BRASIL.BOLBRADESCO.equalsIgnoreCase(paymentMethodId);
        }
        return false;
    }

    private boolean isPaymentMethodIconImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();

        return ((Payment.StatusCodes.STATUS_PENDING).equalsIgnoreCase(status) &&
            !Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT.equalsIgnoreCase(statusDetail) ||
            Payment.StatusCodes.STATUS_IN_PROCESS.equalsIgnoreCase(status) ||
            Payment.StatusCodes.STATUS_REJECTED.equalsIgnoreCase(status));
    }

    private int getBadgeImage(@NonNull final PaymentResultProps props, @NonNull final PaymentResultViewModel viewModel) {
        if (props.hasCustomizedBadge()) {
            final String badge = props.getPreferenceBadge();
            switch (badge) {
            case Badge.CHECK_BADGE_IMAGE:
                return CHECK_BADGE_IMAGE;
            case Badge.PENDING_BADGE_IMAGE:
                return PENDING_BADGE_GREEN_IMAGE;
            default:
                return DEFAULT_BADGE_IMAGE;
            }
        } else if (props.paymentResult == null) {
            return DEFAULT_BADGE_IMAGE;
        } else {
            return viewModel.getBadgeResId();
        }
    }
}