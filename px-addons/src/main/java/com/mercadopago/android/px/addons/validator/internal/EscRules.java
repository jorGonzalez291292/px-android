package com.mercadopago.android.px.addons.validator.internal;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscValidationData;
import java.util.ArrayList;
import java.util.Collection;

/* default */ final class EscRules implements Rule<EscValidationData> {

    private final ESCManagerBehaviour escManagerBehaviour;

    /* default */ EscRules(@NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.escManagerBehaviour = escManagerBehaviour;
    }

    /**
     * @return true if with this escValidationData we can ask for biometrics
     */
    @Override
    public boolean apply(@NonNull final EscValidationData escValidationData) {
        boolean askForBiometrics = false;
        for (final Rule<EscValidationData> rule : getRules()) {
        // at least un rule has to be true
            askForBiometrics |= rule.apply(escValidationData);
        }
        return askForBiometrics;
    }

    private Collection<Rule<EscValidationData>> getRules() {
        final Collection<Rule<EscValidationData>> rules = new ArrayList<>();
        rules.add(this::isNotCard);
        rules.add(this::hasEsc);
        return rules;
    }

    /**
     * @return true if payment method is not a card. Then we can ask for biometrics
     */
    private boolean isNotCard(final EscValidationData data) {
        return !data.isCard();
    }

    /**
     * @return true if is esc enable, the card has an id and that id has saved esc. Then we can ask for biometrics
     */
    private boolean hasEsc(final EscValidationData data) {
        // si el flujo funciona con esc, la tarjeta tiene id y el id tiene esc guardado, podemos aplicar biometrics.
        return data.isEscEnable() && data.getCardId() != null &&
            !TextUtils.isEmpty(escManagerBehaviour.getESC(data.getCardId(), null, null));
    }
}