package com.mercadopago.android.px.model.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.math.BigDecimal;
import java.util.List;

public final class PaymentReward implements Parcelable {

    public static final PaymentReward EMPTY = new PaymentReward();

    @SerializedName("mpuntos")
    @Nullable private final Score score;
    @SerializedName("discounts")
    @Nullable private final Discount discount;

    private PaymentReward() {
        score = null;
        discount = null;
    }

    /* default */ PaymentReward(final Parcel in) {
        score = in.readParcelable(Score.class.getClassLoader());
        discount = in.readParcelable(Discount.class.getClassLoader());
    }

    public static final Creator<PaymentReward> CREATOR = new Creator<PaymentReward>() {
        @Override
        public PaymentReward createFromParcel(final Parcel in) {
            return new PaymentReward(in);
        }

        @Override
        public PaymentReward[] newArray(final int size) {
            return new PaymentReward[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(score, flags);
        dest.writeParcelable(discount, flags);
    }

    /* default */ static final class Score implements Parcelable {

        public static final Creator<Score> CREATOR = new Creator<Score>() {
            @Override
            public Score createFromParcel(final Parcel in) {
                return new Score(in);
            }

            @Override
            public Score[] newArray(final int size) {
                return new Score[size];
            }
        };

        private final Progress progress;
        private final String title;
        private final Action action;

        /* default */ Score(final Parcel in) {
            progress = in.readParcelable(Progress.class.getClassLoader());
            title = in.readString();
            action = in.readParcelable(Action.class.getClassLoader());
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeParcelable(progress, flags);
            dest.writeString(title);
            dest.writeParcelable(action, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /* default */ static final class Progress implements Parcelable {

            public static final Creator<Progress> CREATOR = new Creator<Progress>() {
                @Override
                public Progress createFromParcel(final Parcel in) {
                    return new Progress(in);
                }

                @Override
                public Progress[] newArray(final int size) {
                    return new Progress[size];
                }
            };

            private final BigDecimal percentage;
            @SerializedName("level_color")
            private final String color;
            @SerializedName("level_number")
            private final int level;

            /* default */ Progress(final Parcel in) {
                percentage = ParcelableUtil.getBigDecimal(in);
                color = in.readString();
                level = in.readInt();
            }

            @Override
            public void writeToParcel(final Parcel dest, final int flags) {
                ParcelableUtil.write(dest, percentage);
                dest.writeString(color);
                dest.writeInt(level);
            }

            @Override
            public int describeContents() {
                return 0;
            }

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

        /* default */ static final class Action implements Parcelable {

            public static final Creator<Action> CREATOR = new Creator<Action>() {
                @Override
                public Action createFromParcel(final Parcel in) {
                    return new Action(in);
                }

                @Override
                public Action[] newArray(final int size) {
                    return new Action[size];
                }
            };

            private final String label;
            private final String mlTarget;
            private final String mpTarget;

            /* default */ Action(final Parcel in) {
                label = in.readString();
                mlTarget = in.readString();
                mpTarget = in.readString();
            }

            @Override
            public void writeToParcel(final Parcel dest, final int flags) {
                dest.writeString(mlTarget);
                dest.writeString(label);
                dest.writeString(mpTarget);
            }

            @Override
            public int describeContents() {
                return 0;
            }

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

    /* default */ static final class Discount implements Parcelable {

        public static final Creator<Discount> CREATOR = new Creator<Discount>() {
            @Override
            public Discount createFromParcel(final Parcel in) {
                return new Discount(in);
            }

            @Override
            public Discount[] newArray(final int size) {
                return new Discount[size];
            }
        };

        private final String title;
        private final Action action;
        private final List<Item> items;

        /* default */ Discount(final Parcel in) {
            title = in.readString();
            action = in.readParcelable(Action.class.getClassLoader());
            items = in.createTypedArrayList(Item.CREATOR);
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeString(title);
            dest.writeParcelable(action, flags);
            dest.writeTypedList(items);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /* default */ static final class Action implements Parcelable {

            public static final Creator<Action> CREATOR = new Creator<Action>() {
                @Override
                public Action createFromParcel(final Parcel in) {
                    return new Action(in);
                }

                @Override
                public Action[] newArray(final int size) {
                    return new Action[size];
                }
            };

            private final String label;
            private final String target;

            /* default */ Action(final Parcel in) {
                label = in.readString();
                target = in.readString();
            }

            @Override
            public void writeToParcel(final Parcel dest, final int flags) {
                dest.writeString(label);
                dest.writeString(target);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public String getLabel() {
                return label;
            }

            public String getTarget() {
                return target;
            }
        }

        /* default */ static final class Item implements Parcelable {

            public static final Creator<Item> CREATOR = new Creator<Item>() {
                @Override
                public Item createFromParcel(final Parcel in) {
                    return new Item(in);
                }

                @Override
                public Item[] newArray(final int size) {
                    return new Item[size];
                }
            };

            private final String title;
            private final String subtitle;
            private final String boldText;
            private final String icon;
            private final String target;

            /* default */ Item(final Parcel in) {
                title = in.readString();
                subtitle = in.readString();
                boldText = in.readString();
                icon = in.readString();
                target = in.readString();
            }

            @Override
            public void writeToParcel(final Parcel dest, final int flags) {
                dest.writeString(title);
                dest.writeString(subtitle);
                dest.writeString(boldText);
                dest.writeString(icon);
                dest.writeString(target);
            }

            @Override
            public int describeContents() {
                return 0;
            }

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