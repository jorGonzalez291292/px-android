package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;

public class BodyErrorRenderer extends Renderer<BodyError> {

    @Override
    public View render(@NonNull final BodyError component, @NonNull final Context context, final ViewGroup parent) {
        final View bodyErrorView = inflate(R.layout.px_payment_result_body_error, parent);
        final ViewGroup bodyViewGroup = bodyErrorView.findViewById(R.id.bodyErrorContainer);
        final MPTextView titleTextView = bodyViewGroup.findViewById(R.id.help_title);
        final MPTextView descriptionTextView = bodyViewGroup.findViewById(R.id.help_description);
        final MPTextView titleDescriptionTextView = bodyViewGroup.findViewById(R.id.paymentResultBodyErrorTitleDescription);
        final View bodyErrorDescriptionDivider = bodyViewGroup.findViewById(R.id.bodyErrorDescriptionDivider);

        setText(titleTextView, component.getTitle(context));
        setText(titleDescriptionTextView, component.getTitleDescription(context));
        setText(descriptionTextView, component.getDescription(context));

        if (component.getTitle(context).isEmpty()) {
            final LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            final int marginTop = (int) context.getResources().getDimension(R.dimen.px_l_margin);
            params.setMargins(0, marginTop, 0, 0);
            descriptionTextView.setLayoutParams(params);
        }

        if (!component.getTitleDescription(context).isEmpty()) {
            bodyErrorDescriptionDivider.setVisibility(View.VISIBLE);
        }

        ViewUtils.stretchHeight(bodyViewGroup);

        return bodyErrorView;
    }
}