package com.mercadopago.android.px.internal.features.payment_result.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsTertiaryInfoProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsTertiaryInfo extends Component<InstructionsTertiaryInfoProps, Void> {

    public InstructionsTertiaryInfo(@NonNull final InstructionsTertiaryInfoProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
