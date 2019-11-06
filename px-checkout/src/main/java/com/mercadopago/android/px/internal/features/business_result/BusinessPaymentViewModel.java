package com.mercadopago.android.px.internal.features.business_result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;

public class BusinessPaymentViewModel extends ViewModel {

    private BusinessPaymentModel model;
    private PaymentSettingRepository paymentSettings;
    private ResultViewTrack viewTracker;


    MutableLiveData<BusinessPaymentResultViewModel> modelMutableLiveData = new MutableLiveData<>();


    public BusinessPaymentViewModel(@NonNull final PaymentSettingRepository paymentSettings, @NonNull final BusinessPaymentModel model) {
        modelMutableLiveData.setValue(new BusinessPaymentResultMapper().map(model));
    }
}
