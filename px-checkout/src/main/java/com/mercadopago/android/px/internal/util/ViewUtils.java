package com.mercadopago.android.px.internal.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mercadolibre.android.ui.font.Font;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.MPEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public final class ViewUtils {

    private ViewUtils() {
    }

    public static boolean shouldVisibleAnim(@NonNull final View viewToAnimate) {
        return hasEndedAnim(viewToAnimate) && viewToAnimate.getVisibility() != View.VISIBLE;
    }

    public static boolean shouldGoneAnim(@NonNull final View viewToAnimate) {
        return hasEndedAnim(viewToAnimate) && viewToAnimate.getVisibility() != View.GONE;
    }

    public static boolean hasEndedAnim(@NonNull final View viewToAnimate) {
        return viewToAnimate.getAnimation() == null ||
            (viewToAnimate.getAnimation() != null && viewToAnimate.getAnimation().hasEnded());
    }

    public static void loadOrCallError(final String imgUrl, final ImageView logo, final Callback callback) {
        if (!TextUtil.isEmpty(imgUrl)) {
            Picasso.with(logo.getContext())
                .load(imgUrl)
                .into(logo, callback);
        } else {
            callback.onError();
        }
    }

    public static void loadOrGone(@Nullable final CharSequence text, @NonNull final TextView textView) {
        if (TextUtil.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public static void loadOrGone(@StringRes final int resId, @NonNull final TextView textView) {
        final CharSequence value = resId == 0 ? TextUtil.EMPTY : textView.getContext().getString(resId);
        loadOrGone(value, textView);
    }

    public static void loadOrGone(@DrawableRes final int resId, final ImageView imageView) {
        if (resId == 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setImageResource(resId);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public static void setMarginBottomInView(@NonNull final View view, final int marginBottom) {
        setMarginInView(view, 0, 0, 0, marginBottom);
    }

    public static void setMarginInView(@NonNull final View button, final int leftMargin, final int topMargin,
        final int rightMargin, final int bottomMargin) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        button.setLayoutParams(params);
    }

    public static void hideKeyboard(final Activity activity) {
        try {
            final MPEditText editText = (MPEditText) activity.getCurrentFocus();
            final InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (final Exception ex) {
        }
    }

    public static void openKeyboard(final View view) {
        view.requestFocus();
        final InputMethodManager imm =
            (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showProgressLayout(final Activity activity) {
        showLayout(activity, true, false);
    }

    public static void showRegularLayout(final Activity activity) {
        showLayout(activity, false, true);
    }

    private static void showLayout(final Activity activity, final boolean showProgress, final boolean showLayout) {
        final View form = activity.findViewById(R.id.mpsdkRegularLayout);
        final View progress = activity.findViewById(R.id.mpsdkProgressLayout);

        if (progress != null) {
            progress.setVisibility(showLayout ? View.GONE : View.VISIBLE);
        }

        if (form != null) {
            form.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        }
    }

    public static void resizeViewGroupLayoutParams(final ViewGroup viewGroup, final int height, final int width) {
        final ViewGroup.LayoutParams params = viewGroup.getLayoutParams();
        final Context context = viewGroup.getContext();
        params.height = (int) context.getResources().getDimension(height);
        params.width = (int) context.getResources().getDimension(width);
        viewGroup.setLayoutParams(params);
    }

    public static void setColorInSpannable(final int color, final int indexStart, final int indexEnd,
        @NonNull final Spannable spannable) {
        if (color != 0) {
            spannable.setSpan(new ForegroundColorSpan(color), indexStart, indexEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    //TODO refactor
    public static void setFontInSpannable(final int indexStart, final int indexEnd,
        @NonNull final Spannable spannable, @Nullable final String fontStylePath, @NonNull final Context context) {

        if (TextUtil.isEmpty(fontStylePath)) {
            return;
        }

        final Typeface typeface = TypefaceUtils.load(context.getAssets(), fontStylePath);

        if (typeface == null) {
            return;
        }

        final StyleSpan styleSpan = new StyleSpan(typeface.getStyle());
        setFontInSpannable(indexStart, indexEnd, spannable, styleSpan);
    }

    //TODO refactor
    private static void setFontInSpannable(final int indexStart, final int indexEnd,
        @NonNull final Spannable spannable, @NonNull final StyleSpan styleSpan) {
        spannable.setSpan(styleSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //TODO refactor
    public static void setSemiBoldFontInSpannable(final int indexStart, final int indexEnd,
        @NonNull final Spannable spannable, @NonNull final Context context) {

        if (TextUtil.isEmpty(Font.SEMI_BOLD.getFontPath())) {
            setFontInSpannable(indexStart, indexEnd, spannable, new StyleSpan(Typeface.BOLD));
        } else {
            final Typeface semiBold = TypefaceUtils.load(context.getAssets(), Font.SEMI_BOLD.getFontPath());
            if (semiBold == null) {
                setFontInSpannable(indexStart, indexEnd, spannable, new StyleSpan(Typeface.BOLD));
                return;
            }
            setFontInSpannable(indexStart, indexEnd, spannable, new StyleSpan(semiBold.getStyle()));
        }
    }

    public static void stretchHeight(@NonNull final View view) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        );
        view.setLayoutParams(params);
    }

    public static void wrapHeight(@NonNull final View view) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(params);
    }

    @NonNull
    public static View inflate(@NonNull final ViewGroup parent, @LayoutRes final int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    @NonNull
    public static View compose(@NonNull final ViewGroup container, @NonNull final View child) {
        container.addView(child);
        return container;
    }

    @NonNull
    public static LinearLayout createLinearContainer(final Context context) {
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    public static void cancelAnimation(@NonNull final View targetView) {
        final Animation animation = targetView.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
    }

    public static void grayScaleView(@NonNull final ImageView targetView) {
        final ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        targetView.setColorFilter(filter);
    }

    public static void grayScaleViewGroup(@NonNull final ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View view = viewGroup.getChildAt(i);
            if (view instanceof ImageView) {
                grayScaleView((ImageView) view);
            } else if (view instanceof ViewGroup) {
                grayScaleViewGroup((ViewGroup) view);
            }
        }
    }

    public static void runWhenViewIsFullyMeasured(@NonNull final View view, @NonNull final Runnable runnable) {
        if (ViewCompat.isLaidOut(view)) {
            runnable.run();
        } else {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(final View v, final int left, final int top, final int right,
                    final int bottom, final int oldLeft, final int oldTop, final int oldRight, final int oldBottom) {
                    view.removeOnLayoutChangeListener(this);
                    runnable.run();
                }
            });
        }
    }
}