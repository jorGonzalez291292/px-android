package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.PayerCost;
import java.math.BigDecimal;
import java.util.Locale;

public class PaymentResultAmount extends LinearLayout {

    public PaymentResultAmount(final Context context) {
        this(context, null);
    }

    public PaymentResultAmount(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentResultAmount(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.px_payment_result_amount, this);
    }

    public void setModel(@NonNull final Model model) {
        final MPTextView title = findViewById(R.id.title);
        final MPTextView detail = findViewById(R.id.detail);
        ViewUtils.loadOrGone(getAmountTitle(model), title);
        ViewUtils.loadOrGone(getAmountDetail(model), detail);
    }

    @NonNull
    private String getAmountTitle(@NonNull final Model model) {
        if (hasPayerCostWithMultipleInstallments(model.payerCost)) {
            final String installmentsAmount = CurrenciesUtil
                    .getLocalizedAmountWithoutZeroDecimals(model.currencyId, model.payerCost.getInstallmentAmount());
            return String.format(Locale.getDefault(),
                    "%dx %s",
                    model.payerCost.getInstallments(),
                    installmentsAmount);
        } else {
            return CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(model.currencyId, model.amount);
        }
    }

    @Nullable
    private String getAmountDetail(@NonNull final Model model) {
        if (hasPayerCostWithMultipleInstallments(model.payerCost)) {
            return String.format(Locale.getDefault(), "(%s)", CurrenciesUtil
                    .getLocalizedAmountWithoutZeroDecimals(model.currencyId, model.payerCost.getTotalAmount()));
        }
        return null;
    }

    private boolean hasPayerCostWithMultipleInstallments(@Nullable final PayerCost payerCost) {
        return payerCost != null && payerCost.hasMultipleInstallments();
    }

    public static final class Model {
        @NonNull /* default */ final BigDecimal amount;
        @NonNull /* default */ final String currencyId;
        @Nullable /* default */ final PayerCost payerCost;

        /* default */ Model(@NonNull final Builder builder) {
            amount = builder.amount;
            currencyId = builder.currencyId;
            payerCost = builder.payerCost;
        }

        public static class Builder {
            @NonNull /* default */ BigDecimal amount;
            @NonNull /* default */ String currencyId;
            @Nullable /* default */ PayerCost payerCost;

            public Builder(@NonNull final BigDecimal amount, @NonNull final String currencyId) {
                this.amount = amount;
                this.currencyId = currencyId;
            }

            public Builder setPayerCost(@Nullable final PayerCost payerCost) {
                this.payerCost = payerCost;
                return this;
            }

            public Model build() {
                return new Model(this);
            }
        }
    }
}