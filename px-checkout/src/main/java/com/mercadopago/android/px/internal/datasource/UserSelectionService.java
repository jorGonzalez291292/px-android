package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;

public class UserSelectionService implements UserSelectionRepository {

    private static final String PREF_PRIMARY_SELECTED_PM = "PREF_PRIMARY_SELECTED_PAYMENT_METHOD";
    private static final String PREF_SECONDARY_SELECTED_PM = "PREF_SECONDARY_SELECTED_PAYMENT_METHOD";
    private static final String PREF_SELECTED_PAYER_COST = "PREF_SELECTED_INSTALLMENT";
    private static final String PREF_SELECTED_ISSUER = "PREF_SELECTED_ISSUER";
    private static final String PREF_PAYMENT_TYPE = "PREF_SELECTED_PAYMENT_TYPE";

    @NonNull private final SharedPreferences sharedPreferences;
    @NonNull private final JsonUtil jsonUtil;

    //TODO persist local storage.
    @Nullable private Card card;

    public UserSelectionService(@NonNull final SharedPreferences sharedPreferences,
        @NonNull final JsonUtil jsonUtil) {
        this.sharedPreferences = sharedPreferences;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void removePaymentMethodSelection() {
        sharedPreferences.edit().remove(PREF_PRIMARY_SELECTED_PM).apply();
        sharedPreferences.edit().remove(PREF_SECONDARY_SELECTED_PM).apply();
        removePayerCostSelection();
        removeIssuerSelection();
    }

    private void removeIssuerSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_ISSUER).apply();
    }

    private void removePayerCostSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_PAYER_COST).apply();
    }

    private void removeCardSelection() {
        card = null;
        removePaymentMethodSelection();
        removeIssuerSelection();
        removePayerCostSelection();
    }

    @Override
    public boolean hasPayerCostSelected() {
        return getPayerCost() != null;
    }

    @Override
    public boolean hasCardSelected() {
        return getCard() != null;
    }

    /**
     * it's important to select and then add the installments there is a side effect after changing the payment method
     * that deletes the old payer cost cache
     *
     * @param primary new payment method selected.
     * @param secondary payment method selected.
     */
    @Override
    public void select(@Nullable final PaymentMethod primary, @Nullable final PaymentMethod secondary) {
        if (primary == null) {
            removePaymentMethodSelection();
        } else {
            sharedPreferences.edit().putString(PREF_PRIMARY_SELECTED_PM, jsonUtil.toJson(primary)).apply();

            if (secondary != null) {
                sharedPreferences.edit().putString(PREF_SECONDARY_SELECTED_PM, jsonUtil.toJson(secondary)).apply();
            }

            removePayerCostSelection();
        }
    }

    @Override
    public void select(@NonNull final PayerCost payerCost) {
        sharedPreferences.edit().putString(PREF_SELECTED_PAYER_COST, jsonUtil.toJson(payerCost)).apply();
    }

    @Override
    public void select(@NonNull final Issuer issuer) {
        sharedPreferences.edit().putString(PREF_SELECTED_ISSUER, jsonUtil.toJson(issuer)).apply();
    }

    @Override
    public void select(@Nullable final Card card, @Nullable final PaymentMethod secondaryPaymentMethod) {
        if (card == null) {
            removeCardSelection();
        } else {
            this.card = card;
            select(card.getPaymentMethod(), secondaryPaymentMethod);
            select(card.getIssuer());
        }
    }

    @Override
    public void select(final String paymentType) {
        sharedPreferences.edit().putString(PREF_PAYMENT_TYPE, paymentType).apply();
    }

    @Override
    @Nullable
    public PaymentMethod getPaymentMethod() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_PRIMARY_SELECTED_PM, TextUtil.EMPTY),
            PaymentMethod.class);
    }

    @Nullable
    @Override
    public PaymentMethod getSecondaryPaymentMethod() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_SECONDARY_SELECTED_PM, TextUtil.EMPTY),
            PaymentMethod.class);
    }

    @Override
    @Nullable
    public PayerCost getPayerCost() {
        return jsonUtil.fromJson(
            sharedPreferences.getString(PREF_SELECTED_PAYER_COST, TextUtil.EMPTY), PayerCost.class);
    }

    @Nullable
    @Override
    public Issuer getIssuer() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_SELECTED_ISSUER, TextUtil.EMPTY), Issuer.class);
    }

    @Nullable
    @Override
    public Card getCard() {
        return card;
    }

    @NonNull
    @Override
    public String getPaymentType() {
        return sharedPreferences.getString(PREF_PAYMENT_TYPE, TextUtil.EMPTY);
    }

    @Override
    public void reset() {
        sharedPreferences.edit().remove(PREF_PAYMENT_TYPE).apply();
        removePayerCostSelection();
        removePaymentMethodSelection();
        removeIssuerSelection();
        removeCardSelection();
    }
}
