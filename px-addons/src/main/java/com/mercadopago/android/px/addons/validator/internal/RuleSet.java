package com.mercadopago.android.px.addons.validator.internal;

import android.support.annotation.NonNull;
import java.util.List;

public abstract class RuleSet<T> implements Rule<T> {
    @Override
    public boolean apply(@NonNull final T data) {
        for (final Rule<T> rule : getRules()) {
            if (!rule.apply(data)) {
                return false;
            }
        }
        return true;
    }

    abstract List<Rule<T>> getRules();
}