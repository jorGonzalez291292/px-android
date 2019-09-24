package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.components.BodyError;
import com.mercadopago.android.px.internal.features.payment_result.props.BodyErrorProps;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.mocks.PaymentResults;
import com.mercadopago.android.px.model.PaymentResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BodyErrorTest {

    private static final String ERROR_TITLE = "error_title";
    private static final String CALL_FOR_AUTH_ERROR_TITLE = "error_title";
    private static final String CONTINGENCY_DESCRIPTION = "contingency_description";
    private static final String REVIEW_MANUAL_DESCRIPTION = "review_manual_description";
    private static final String CALL_FOR_AUTH_DESCRIPTION = "call_for_auth_description";
    private static final String REJECTED_INSUFFICIENT_DATA = "insufficient_data_description";
    private static final String REJECTED_INSUFFICIENT_AMOUNT = "insufficient_amount_description";
    private static final String DUPLICATED_DESCRIPTION = "duplicated_description";
    private static final String MAX_ATTEMPTS_DESCRIPTION = "max_attempt_description";
    private static final String EMPTY_DESCRIPTION = TextUtil.EMPTY;

    @Mock private ActionDispatcher dispatcher;
    @Mock private Context context;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
        when(context.getString(R.string.px_error_description_contingency)).thenReturn(CONTINGENCY_DESCRIPTION);
        when(context.getString(R.string.px_error_description_review_manual)).thenReturn(REVIEW_MANUAL_DESCRIPTION);
        when(context.getString(R.string.px_error_description_insufficient_data)).thenReturn(REJECTED_INSUFFICIENT_DATA);
        when(context.getString(R.string.px_what_can_do)).thenReturn(ERROR_TITLE);
        when(context.getString(R.string.px_text_how_can_authorize)).thenReturn(CALL_FOR_AUTH_ERROR_TITLE);
        when(context.getString(R.string.px_error_description_call)).thenReturn(CALL_FOR_AUTH_DESCRIPTION);
        when(context.getString(R.string.px_error_description_duplicated_payment)).thenReturn(DUPLICATED_DESCRIPTION);
        when(context.getString(R.string.px_error_description_max_attempts)).thenReturn(MAX_ATTEMPTS_DESCRIPTION);
        when(context.getString(R.string.px_error_description_rejected_by_insufficient_amount)).thenReturn(REJECTED_INSUFFICIENT_AMOUNT);


    }

    @Test
    public void testBodyErrorTitleForCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(CALL_FOR_AUTH_ERROR_TITLE, bodyError.getTitle(context));
    }

    @Test
    public void testBodyErrorTitleForInsufficientAmount() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(ERROR_TITLE, bodyError.getTitle(context));
    }

    @Test
    public void testBodyErrorTitleForRejectedOther() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(TextUtil.EMPTY, bodyError.getTitle(context));
    }

    @Test
    public void testBodyErrorTitleForInsufficientData() {
        final PaymentResult paymentResult = PaymentResults.getBoletoRejectedPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(ERROR_TITLE, bodyError.getTitle(context));
    }

    @Test
    public void testBodyErrorTitleForDuplicatedPayment() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedDuplicatedPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals("", bodyError.getTitle(context));
    }

    @Test
    public void testBodyErrorTitleForPendingContingency() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(TextUtil.EMPTY, bodyError.getTitle(context));
    }

    @Test
    public void testBodyErrorTitleOnEmptyCase() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals("", bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorDescriptionForPendingContingency() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(CONTINGENCY_DESCRIPTION, bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorDescriptionForPendingReviewManual() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessReviewManualPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(REVIEW_MANUAL_DESCRIPTION, bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorDescriptionForRejectedCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(CALL_FOR_AUTH_DESCRIPTION, bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorDescriptionForRejectedInsufficientAmount() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(REJECTED_INSUFFICIENT_AMOUNT, bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorDescriptionForRejectedOtherReason() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(EMPTY_DESCRIPTION, bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorDescriptionForRejectedInsufficientData() {
        final PaymentResult paymentResult = PaymentResults.getBoletoRejectedPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(REJECTED_INSUFFICIENT_DATA, bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorDescriptionForRejectedDuplicatedPayment() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedDuplicatedPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(DUPLICATED_DESCRIPTION, bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorDescriptionOnEmptyCase() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(TextUtil.EMPTY, bodyError.getDescription(context));
    }

    @Test
    public void testBodyErrorHasActionForCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);
        assertEquals(bodyError.getDescription(context), CALL_FOR_AUTH_DESCRIPTION);
    }

    @Test
    public void testBodyErrorDoestHaveActionForOtherRejected() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);
        assertNotEquals(bodyError.getDescription(context), CALL_FOR_AUTH_DESCRIPTION);
    }


    @Test
    public void testBodyErrorDescriptionForRejectedMaxAttempts() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedMaxAttemptsPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher);

        assertEquals(MAX_ATTEMPTS_DESCRIPTION, bodyError.getDescription(context));
    }


    private BodyErrorProps getBodyErrorProps(final PaymentResult paymentResult) {
        return new BodyErrorProps.Builder()
            .setStatus(paymentResult.getPaymentStatus())
            .setStatusDetail(paymentResult.getPaymentStatusDetail())
            .setPaymentMethodName(paymentResult.getPaymentData().getPaymentMethod().getName())
            .build();
    }
}
