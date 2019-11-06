package com.mercadopago.android.px.internal.features.payment_result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.mercadopago.android.px.internal.repository.InstructionsRepository;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;
import java.util.List;

public class PaymentViewModel extends ViewModel {
    private PaymentModel paymentModel;
    private InstructionsRepository instructionsRepository;

    MutableLiveData<List<Instruction>> resultLiveData = new MutableLiveData<>();

    public PaymentViewModel(final PaymentModel paymentModel,
        final InstructionsRepository instructionsRepository) {
        this.paymentModel = paymentModel;
        this.instructionsRepository = instructionsRepository;

        getInstructions();
    }



    /* default */ void getInstructions() {
        instructionsRepository.getInstructions(paymentModel.getPaymentResult()).enqueue(
            new Callback<List<Instruction>>() {
                @Override
                public void success(final List<Instruction> instructions) {

                    resultLiveData.postValue(instructions);
                }

                @Override
                public void failure(final ApiException apiException) {

                }
            });
    }
}
