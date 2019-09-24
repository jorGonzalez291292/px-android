package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

public final class ScaleUtil {

    private ScaleUtil() {
    }

    public static int getPxFromDp(final int dpValue, final Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //falta landscape? pensar para android tv
    public static boolean isLowRes(final Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final boolean dpiLowRes = metrics.densityDpi < DisplayMetrics.DENSITY_HIGH;
        final boolean heightLowRes = metrics.heightPixels < 800;
        return dpiLowRes || heightLowRes;
    }

    public static String getDensityName(@NonNull final Context context) {
        final float densityScale = 1.0f / DisplayMetrics.DENSITY_DEFAULT;
        final float density = context.getResources().getDisplayMetrics().density / densityScale;

        if (density >= DisplayMetrics.DENSITY_XXXHIGH) {
            return "xxxhdpi";
        }
        if (density >= DisplayMetrics.DENSITY_XXHIGH) {
            return "xxhdpi";
        }
        if (density >= DisplayMetrics.DENSITY_XHIGH) {
            return "xhdpi";
        }
        if (density >= DisplayMetrics.DENSITY_HIGH) {
            return "hdpi";
        }
        if (density >= DisplayMetrics.DENSITY_MEDIUM) {
            return "mdpi";
        }
        return "ldpi";
    }
}