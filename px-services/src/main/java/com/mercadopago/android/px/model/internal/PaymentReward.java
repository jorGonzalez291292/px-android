package com.mercadopago.android.px.model.internal;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public final class PaymentReward {

    @SerializedName("mpuntos")
    @Nullable private Score score;
    @SerializedName("discounts")
    @Nullable private Discount discount;

    /* default */ static final class Score {

        private Progress progress;
        private String title;
        private Action action;

        /* default */ static final class Progress {

            private BigDecimal percentage;
            @SerializedName("level_color")
            private String color;
            @SerializedName("level_number")
            private int level;

            public BigDecimal getPercentage() {
                return percentage;
            }

            public String getColor() {
                return color;
            }

            public int getLevel() {
                return level;
            }
        }

        /* default */ static final class Action {

            private String label;
            private String mlTarget;
            private String mpTarget;

            public String getLabel() {
                return label;
            }

            public String getMlTarget() {
                return mlTarget;
            }

            public String getMpTarget() {
                return mpTarget;
            }
        }

        public Progress getProgress() {
            return progress;
        }

        public String getTitle() {
            return title;
        }

        public Action getAction() {
            return action;
        }
    }

    /* default */ static final class Discount {

        private String title;
        private Action action;
        private List<Item> items;

        /* default */ static final class Action {

            private String label;
            private String target;

            public String getLabel() {
                return label;
            }

            public String getTarget() {
                return target;
            }
        }

        /* default */ static final class Item {

            private String title;
            private String subtitle;
            private String boldText;
            private String icon;
            private String target;

            public String getTitle() {
                return title;
            }

            public String getSubtitle() {
                return subtitle;
            }

            public String getBoldText() {
                return boldText;
            }

            public String getIcon() {
                return icon;
            }

            public String getTarget() {
                return target;
            }
        }

        public String getTitle() {
            return title;
        }

        public Action getAction() {
            return action;
        }

        public List<Item> getItems() {
            return items;
        }
    }
}