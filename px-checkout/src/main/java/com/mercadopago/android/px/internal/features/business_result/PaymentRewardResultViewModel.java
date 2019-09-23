package com.mercadopago.android.px.internal.features.business_result;

import android.support.annotation.Nullable;
import com.mercadolibre.android.mlbusinesscomponents.components.common.downloadapp.MLBusinessDownloadAppData;
import com.mercadolibre.android.mlbusinesscomponents.components.crossselling.MLBusinessCrossSellingBoxData;
import com.mercadolibre.android.mlbusinesscomponents.components.discount.MLBusinessDiscountBoxData;
import com.mercadolibre.android.mlbusinesscomponents.components.loyalty.MLBusinessLoyaltyRingData;
import java.util.List;

/* default */ public class PaymentRewardResultViewModel {

    /* default */ private final MLBusinessLoyaltyRingData loyaltyRingData;
    /* default */ private final MLBusinessDiscountBoxData discountBoxData;
    /* default */ private final MLBusinessDownloadAppData downloadAppData;
    /* default */ private final List<MLBusinessCrossSellingBoxData> crossSellingBoxData;

    /* default */ PaymentRewardResultViewModel(
        @Nullable final MLBusinessLoyaltyRingData loyaltyRingData,
        @Nullable final MLBusinessDiscountBoxData discountBoxData,
        @Nullable final MLBusinessDownloadAppData downloadAppData,
        @Nullable final List<MLBusinessCrossSellingBoxData> crossSellingBoxData) {
        this.loyaltyRingData = loyaltyRingData;
        this.discountBoxData = discountBoxData;
        this.downloadAppData = downloadAppData;
        this.crossSellingBoxData = crossSellingBoxData;
    }

    @Nullable
    public MLBusinessLoyaltyRingData getLoyaltyRingData() {
        return loyaltyRingData;
    }

    @Nullable
    public MLBusinessDiscountBoxData getDiscountBoxData() {
        return discountBoxData;
    }

    @Nullable
    public MLBusinessDownloadAppData getDownloadAppData() {
        return downloadAppData;
    }

    public List<MLBusinessCrossSellingBoxData> getCrossSellingBoxData() {
        return crossSellingBoxData;
    }
}
