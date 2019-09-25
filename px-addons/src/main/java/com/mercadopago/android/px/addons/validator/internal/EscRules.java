package com.mercadopago.android.px.addons.validator.internal;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscValidationData;
import java.util.ArrayList;
import java.util.List;

/* default */ final class EscRules extends RuleSet<EscValidationData> {

    private final ESCManagerBehaviour escManagerBehaviour;

    /* default */ EscRules(@NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.escManagerBehaviour = escManagerBehaviour;
    }

    @Override
    public List<Rule<EscValidationData>> getRules() {
        final List<Rule<EscValidationData>> rules = new ArrayList<>();
        rules.add(this::isCard);
        rules.add(this::hasNotEsc);
        return rules;
    }

    /**
     * @return true if payment method is not a card. Then we can ask for biometrics
     */
    private boolean isCard(final EscValidationData data) {
        return data.isCard();
    }

    /**
     * @return true if is esc enable, the card has an id and that id has saved esc. Then we can ask for biometrics
     */
    private boolean hasNotEsc(final EscValidationData data) {
        // si el flujo funciona con esc, la tarjeta tiene id y el id tiene esc guardado, podemos aplicar biometrics.
        return !data.isEscEnable() || data.getCardId() == null ||
            TextUtils.isEmpty(escManagerBehaviour.getESC(data.getCardId(), null, null));
    }
}