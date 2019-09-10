package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.PaymentDataHelper;
import com.mercadopago.android.px.internal.util.ResourceUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import java.util.Locale;

public class PaymentResultMethod extends ConstraintLayout {

    public PaymentResultMethod(final Context context) {
        this(context, null);
    }

    public PaymentResultMethod(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentResultMethod(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.px_payment_result_method, this);
    }

    public void setModel(@NonNull final Model model) {
        final ImageView icon = findViewById(R.id.icon);
        final MPTextView description = findViewById(R.id.description);
        final MPTextView statement = findViewById(R.id.statement);
        final PaymentResultAmount amount = findViewById(R.id.amount);

        icon.setImageDrawable(ContextCompat.getDrawable(getContext(),
            ResourceUtil.getIconResource(getContext(), model.paymentMethodId)));

        ViewUtils.loadOrGone(getDescription(model), description);
        ViewUtils.loadOrGone(getStatement(model), statement);
        amount.setModel(model.amountModel);
    }

    @Nullable
    private String getStatement(@NonNull final Model model) {
        if (PaymentTypes.isCardPaymentType(model.paymentTypeId) && TextUtil.isNotEmpty(model.statement)) {
            return getResources().getString(R.string.px_text_state_account_activity_congrats, model.statement);
        }
        return null;
    }

    @NonNull
    private String getDescription(@NonNull final Model model) {
        if (PaymentTypes.isCardPaymentType(model.paymentTypeId)) {
            return String.format(Locale.getDefault(), "%s %s %s",
                model.paymentMethodName,
                getResources().getString(R.string.px_ending_in),
                model.lastFourDigits);
        } else {
            return model.paymentMethodName;
        }
    }

    public static final class Model {

        public static Model with(@NonNull final PaymentData paymentData, @NonNull final String currencyId) {
            return with(paymentData, currencyId, null);
        }

        public static Model with(@NonNull final PaymentData paymentData, @NonNull final String currencyId,
            @Nullable final String statement) {

            final PaymentResultAmount.Model amountModel = new PaymentResultAmount.Model.Builder(
                PaymentDataHelper.getPrettyAmountToPay(paymentData), currencyId)
                .setPayerCost(paymentData.getPayerCost())
                .build();

            final PaymentMethod paymentMethod = paymentData.getPaymentMethod();
            return new Builder(paymentMethod.getId(), paymentMethod.getName(), paymentMethod.getPaymentTypeId())
                .setLastFourDigits(paymentData.getToken() != null ? paymentData.getToken().getLastFourDigits() : null)
                .setStatement(statement)
                .setAmountModel(amountModel)
                .build();
        }

        @NonNull /* default */ final String paymentMethodId;
        @NonNull /* default */ final String paymentMethodName;
        @NonNull /* default */ final String paymentTypeId;
        @NonNull /* default */ final PaymentResultAmount.Model amountModel;
        @Nullable /* default */ final String lastFourDigits;
        @Nullable /* default */ final String statement;

        /* default */ Model(@NonNull final Builder builder) {
            paymentMethodId = builder.paymentMethodId;
            paymentMethodName = builder.paymentMethodName;
            paymentTypeId = builder.paymentTypeId;
            amountModel = builder.amountModel;
            lastFourDigits = builder.lastFourDigits;
            statement = builder.statement;
        }

        public static class Builder {
            @NonNull /* default */ String paymentMethodId;
            @NonNull /* default */ String paymentMethodName;
            @NonNull /* default */ String paymentTypeId;
            /* default */ PaymentResultAmount.Model amountModel;
            @Nullable /* default */ String lastFourDigits;
            @Nullable /* default */ String statement;

            public Builder(@NonNull final String paymentMethodId, @NonNull final String paymentMethodName,
                @NonNull final String paymentTypeId) {
                this.paymentMethodId = paymentMethodId;
                this.paymentMethodName = paymentMethodName;
                this.paymentTypeId = paymentTypeId;
            }

            public Builder setAmountModel(@NonNull final PaymentResultAmount.Model amountModel) {
                this.amountModel = amountModel;
                return this;
            }

            public Builder setLastFourDigits(@Nullable final String lastFourDigits) {
                this.lastFourDigits = lastFourDigits;
                return this;
            }

            public Builder setStatement(@Nullable final String statement) {
                this.statement = statement;
                return this;
            }

            public Model build() {
                return new Model(this);
            }
        }
    }
}