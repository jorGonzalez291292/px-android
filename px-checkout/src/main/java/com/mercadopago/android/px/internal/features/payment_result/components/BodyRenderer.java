package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;

public class BodyRenderer extends Renderer<Body> {
    @Override
    public View render(@NonNull final Body component, @NonNull final Context context, final ViewGroup parent) {
        final PaymentResultBody body = parent.findViewById(R.id.body);
        if (component.hasInstructions()) {
            body.setVisibility(View.GONE);
            RendererFactory.create(context, component.getInstructionsComponent()).render(parent);
        } else if (component.hasBodyError()) {
            body.setVisibility(View.GONE);
            RendererFactory.create(context, component.getBodyErrorComponent()).render(parent);
        }
        return parent;
    }
}