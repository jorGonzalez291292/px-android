package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.PaymentDataHelper;
import com.mercadopago.android.px.internal.util.ResourceUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import java.util.Locale;
import javax.annotation.Nonnull;

public class PaymentMethodComponent extends CompactComponent<PaymentMethodComponent.PaymentMethodProps, Void> {

    public PaymentMethodComponent(@NonNull final PaymentMethodProps props) {
        super(props);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View paymentMethodView = ViewUtils.inflate(parent, R.layout.px_payment_result_method);

        return paymentMethodView;
    }

    public static class PaymentMethodProps implements Parcelable {

        public static final Creator<PaymentMethodProps> CREATOR = new Creator<PaymentMethodProps>() {
            @Override
            public PaymentMethodProps createFromParcel(final Parcel in) {
                return new PaymentMethodProps(in);
            }

            @Override
            public PaymentMethodProps[] newArray(final int size) {
                return new PaymentMethodProps[size];
            }
        };
        /* default */ final TotalAmount.Props totalAmountProps;
        @Nullable
        /* default */ final String lastFourDigits;
        @Nullable
        /* default */ final String disclaimer;
        /* default */ final PaymentMethod paymentMethod;

        private PaymentMethodProps(final PaymentMethod paymentMethod,
            @Nullable final String lastFourDigits,
            @Nullable final String disclaimer,
            final TotalAmount.Props totalAmountProps) {
            this.paymentMethod = paymentMethod;
            this.lastFourDigits = lastFourDigits;
            this.disclaimer = disclaimer;
            this.totalAmountProps = totalAmountProps;
        }

        protected PaymentMethodProps(final Parcel in) {
            paymentMethod = in.readParcelable(PaymentMethod.class.getClassLoader());
            totalAmountProps = in.readParcelable(TotalAmount.Props.class.getClassLoader());
            lastFourDigits = in.readString();
            disclaimer = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeParcelable(paymentMethod, flags);
            dest.writeParcelable(totalAmountProps, flags);
            dest.writeString(lastFourDigits);
            dest.writeString(disclaimer);
        }
    }
}
