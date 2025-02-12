package com.mercadopago.android.px.mocks;

import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.utils.ResourcesUtil;

public class PaymentMethodSearchs {

    private PaymentMethodSearchs() {
    }

    public static PaymentMethodSearch getCompletePaymentMethodSearchMLA() {
        String json = ResourcesUtil.getStringResource("complete_payment_method_search_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithSavedCardsMLA() {
        String json = ResourcesUtil.getStringResource("saved_cards_payment_method_search_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithPaymentMethodOnTop() {
        String json = ResourcesUtil.getStringResource("payment_method_on_top.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithCardsMLA() {
        String json = ResourcesUtil.getStringResource("cards_but_no_account_money_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodWithoutCustomOptionsMLA() {
        String json = ResourcesUtil.getStringResource("not_cards_nor_account_money_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithOnlyCreditCardMLA() {
        String json = ResourcesUtil.getStringResource("only_credit_card_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithOnlyTicketMLA() {
        String json = ResourcesUtil.getStringResource("only_ticket_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithOnlyAccountMoneyMLA() {
        String json = ResourcesUtil.getStringResource("only_account_money_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithOnlyMercadoCredito() {
        String json = ResourcesUtil.getStringResource("mercado_credito_mock.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithOnlyCreditCardAndOneCardMLA() {
        String json = ResourcesUtil.getStringResource("only_credit_card_and_one_card_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithOnlyCreditCardAndAccountMoneyMLA() {
        String json = ResourcesUtil.getStringResource("only_credit_card_and_account_money_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithOnlyOneOffTypeAndAccountMoneyMLA() {
        String json = ResourcesUtil.getStringResource("only_one_off_type_and_account_money_MLA.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }

    public static PaymentMethodSearch getPaymentMethodSearchWithOnlyBolbradescoMLB() {
        String json = ResourcesUtil.getStringResource("only_bolbradesco_payment_method_search_MLB.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
    }
}
