package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.util.FragmentUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultType;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.ExternalFragment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import java.util.ArrayList;
import java.util.List;

public final class PaymentResultBody extends LinearLayout {

    public PaymentResultBody(final Context context) {
        this(context, null);
    }

    public PaymentResultBody(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentResultBody(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.px_payment_result_body, this);
        setOrientation(VERTICAL);
    }

    public void setModel(@NonNull final Model model) {
        renderReceipt(model.receiptId);
        renderHelp(model.help);
        renderFragment(R.id.px_fragment_container_top, model.topFragment);
        renderMethods(model);
        renderFragment(R.id.px_fragment_container_bottom, model.bottomFragment);
    }

    private void renderReceipt(@Nullable final String receiptId) {
        final PaymentResultReceipt receipt = findViewById(R.id.receipt);
        if (TextUtil.isNotEmpty(receiptId)) {
            receipt.setVisibility(VISIBLE);
            receipt.setReceiptId(receiptId);
        } else {
            receipt.setVisibility(GONE);
        }
    }

    private void renderHelp(@Nullable final String help) {
        final View helpContainer = findViewById(R.id.help);
        if (TextUtil.isNotEmpty(help)) {
            helpContainer.setVisibility(VISIBLE);
            final TextView helpTitle = helpContainer.findViewById(R.id.help_title);
            final TextView helpDescription = helpContainer.findViewById(R.id.help_description);
            helpTitle.setText(R.string.px_what_can_do);
            helpDescription.setText(help);
        } else {
            helpContainer.setVisibility(GONE);
        }
    }

    private void renderMethods(@NonNull final Model model) {
        final PaymentResultMethod primaryMethod = findViewById(R.id.primaryMethod);
        final PaymentResultMethod secondaryMethod = findViewById(R.id.secondaryMethod);

        primaryMethod.setVisibility(GONE);
        secondaryMethod.setVisibility(GONE);

        if (model.methodModels != null && !model.methodModels.isEmpty()) {
            if (model.methodModels.size() > 1) {
                secondaryMethod.setVisibility(VISIBLE);
                secondaryMethod.setModel(model.methodModels.get(1));
            }
            primaryMethod.setVisibility(VISIBLE);
            primaryMethod.setModel(model.methodModels.get(0));
        }
    }

    private void renderFragment(@IdRes final int id, @Nullable final ExternalFragment fragment) {
        final ViewGroup container = findViewById(id);
        if (fragment != null) {
            container.setVisibility(VISIBLE);
            FragmentUtil.replaceFragment(container, fragment);
        } else {
            container.setVisibility(GONE);
        }
    }

    public static final class Model {

        public static Model from(@NonNull final PaymentResult paymentResult, @NonNull final
            PaymentResultScreenConfiguration configuration, @NonNull final String currencyId) {
            final List<PaymentResultMethod.Model> methodModels = new ArrayList<>();
            for (final PaymentData paymentData : paymentResult.getPaymentDataList()) {
                methodModels.add(PaymentResultMethod.Model.with(paymentData, currencyId));
            }

            return new Builder()
                .setMethodModels(methodModels)
                .setReceiptId(String.valueOf(paymentResult.getPaymentId()))
                .setTopFragment(configuration.getTopFragment())
                .setBottomFragment(configuration.getBottomFragment())
                .build();
        }

        /* default */ final List<PaymentResultMethod.Model> methodModels;
        @Nullable /* default */ final String receiptId;
        @Nullable /* default */ final String help;
        @Nullable /* default */ final String statement;
        @Nullable /* default */ final ExternalFragment topFragment;
        @Nullable /* default */ final ExternalFragment bottomFragment;

        public Model(@NonNull final Builder builder) {
            methodModels = builder.methodModels;
            receiptId = builder.receiptId;
            help = builder.help;
            statement = builder.statement;
            topFragment = builder.topFragment;
            bottomFragment = builder.bottomFragment;
        }

        public Builder toBuilder() {
            return new Builder()
                .setMethodModels(methodModels)
                .setReceiptId(receiptId)
                .setHelp(help)
                .setStatement(statement)
                .setTopFragment(topFragment)
                .setBottomFragment(bottomFragment);
        }

        public static class Builder {
            /* default */ List<PaymentResultMethod.Model> methodModels;
            @Nullable /* default */ String receiptId;
            @Nullable /* default */ String help;
            @Nullable /* default */ String statement;
            @Nullable /* default */ ExternalFragment topFragment;
            @Nullable /* default */ ExternalFragment bottomFragment;

            public Builder setMethodModels(@NonNull final List<PaymentResultMethod.Model> methodModels) {
                this.methodModels = methodModels;
                return this;
            }

            public Builder setReceiptId(@Nullable final String receiptId) {
                this.receiptId = receiptId;
                return this;
            }

            public Builder setHelp(@Nullable final String help) {
                this.help = help;
                return this;
            }

            public Builder setTopFragment(@Nullable final ExternalFragment topFragment) {
                this.topFragment = topFragment;
                return this;
            }

            public Builder setBottomFragment(@Nullable final ExternalFragment bottomFragment) {
                this.bottomFragment = bottomFragment;
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