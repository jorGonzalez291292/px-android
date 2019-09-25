package com.mercadopago.android.px.addons.validator.internal;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import java.util.ArrayList;
import java.util.Collection;

public final class SecurityRules implements Rule<SecurityValidationData> {

    private final ESCManagerBehaviour escManagerBehaviour;

    public SecurityRules(@NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.escManagerBehaviour = escManagerBehaviour;
    }

    /**
     * @return true if with this securityValidationData we can ask for biometrics
     */
    @Override
    public boolean apply(@NonNull final SecurityValidationData securityValidationData) {
        boolean askForBiometrics = true;
        for (final Rule<SecurityValidationData> rule : getRules()) {
            // All rules have to be true
            askForBiometrics &= rule.apply(securityValidationData);
        }
        return askForBiometrics;
    }

    private Collection<Rule<SecurityValidationData>> getRules() {
        final Collection<Rule<SecurityValidationData>> rules = new ArrayList<>();
        rules.add(this::validateEscData);
        return rules;
    }

    /**
     * @return true if there isn't data to validate or if with this data we can ask for biometrics
     */
    private boolean validateEscData(@NonNull final SecurityValidationData data) {
        return data.getEscValidationData() == null ||
            new EscRules(escManagerBehaviour).apply(data.getEscValidationData());
    }
}
