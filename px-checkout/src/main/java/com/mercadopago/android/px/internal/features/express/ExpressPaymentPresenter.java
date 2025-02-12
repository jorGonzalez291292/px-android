package com.mercadopago.android.px.internal.features.express;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.SecurityBehaviour;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import com.mercadopago.android.px.configuration.DynamicDialogConfiguration;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.core.ProductIdProvider;
import com.mercadopago.android.px.internal.features.explode.ExplodeDecoratorMapper;
import com.mercadopago.android.px.internal.features.express.slider.HubAdapter;
import com.mercadopago.android.px.internal.features.express.slider.SplitPaymentHeaderAdapter;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.ChargeRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.NoConnectivityException;
import com.mercadopago.android.px.internal.view.AmountDescriptorView;
import com.mercadopago.android.px.internal.view.ElementDescriptorView;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView;
import com.mercadopago.android.px.internal.view.SummaryView;
import com.mercadopago.android.px.internal.viewmodel.ConfirmButtonViewModel;
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel;
import com.mercadopago.android.px.internal.viewmodel.PayerCostSelection;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState;
import com.mercadopago.android.px.internal.viewmodel.mappers.ConfirmButtonViewModelMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.ElementDescriptorMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.PayButtonViewModelMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.PaymentMethodDescriptorMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.PaymentMethodDrawableItemMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.SplitHeaderMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.SummaryInfoMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.SummaryViewModelMapper;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.ExpressMetadata;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.SummaryInfo;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.tracking.internal.events.ConfirmEvent;
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker;
import com.mercadopago.android.px.tracking.internal.events.InstallmentsEventTrack;
import com.mercadopago.android.px.tracking.internal.events.SwipeOneTapEventTracker;
import com.mercadopago.android.px.tracking.internal.views.OneTapViewTracker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/* default */ class ExpressPaymentPresenter extends BasePresenter<ExpressPayment.View>
    implements PostPaymentAction.ActionController, ExpressPayment.Actions,
    AmountDescriptorView.OnClickListener {

    private static final String BUNDLE_STATE_PAYER_COST = "state_payer_cost";
    private static final String BUNDLE_STATE_SPLIT_PREF = "state_split_pref";
    private static final String BUNDLE_STATE_AVAILABLE_PM_COUNT = "state_available_pm_count";
    private static final String BUNDLE_STATE_CURRENT_PM_INDEX = "state_current_pm_index";
    @NonNull private final PaymentRepository paymentRepository;
    @NonNull private final AmountRepository amountRepository;
    @NonNull private final DiscountRepository discountRepository;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final AmountConfigurationRepository amountConfigurationRepository;
    @NonNull private final DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    @NonNull private final ChargeRepository chargeRepository;
    @NonNull private final ProductIdProvider productIdProvider;
    @NonNull private final SecurityBehaviour securityBehaviour;
    @NonNull private final ExplodeDecoratorMapper explodeDecoratorMapper;
    @NonNull private final ESCManagerBehaviour escManagerBehaviour;
    private final PaymentMethodDrawableItemMapper paymentMethodDrawableItemMapper;
    private final PayButtonViewModel payButtonViewModel;
    //TODO remove.
    /* default */ List<ExpressMetadata> expressMetadataList;
    private PayerCostSelection payerCostSelection;
    private SplitSelectionState splitSelectionState;
    private int availablePaymentMethodsCount = -1;
    private Set<String> cardsWithSplit;
    private int paymentMethodIndex;

    /* default */ ExpressPaymentPresenter(@NonNull final PaymentRepository paymentRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final AmountRepository amountRepository,
        @NonNull final GroupsRepository groupsRepository,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository,
        @NonNull final ChargeRepository chargeRepository,
        @NonNull final ESCManagerBehaviour escManagerBehaviour,
        @NonNull final ProductIdProvider productIdProvider,
        @NonNull final SecurityBehaviour securityBehaviour) {

        this.paymentRepository = paymentRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.disabledPaymentMethodRepository = disabledPaymentMethodRepository;
        this.discountRepository = discountRepository;
        this.amountRepository = amountRepository;
        this.amountConfigurationRepository = amountConfigurationRepository;
        this.chargeRepository = chargeRepository;
        this.escManagerBehaviour = escManagerBehaviour;
        this.productIdProvider = productIdProvider;
        this.securityBehaviour = securityBehaviour;

        explodeDecoratorMapper = new ExplodeDecoratorMapper();
        paymentMethodDrawableItemMapper = new PaymentMethodDrawableItemMapper();
        splitSelectionState = new SplitSelectionState();
        payButtonViewModel = new PayButtonViewModelMapper().map(
            paymentSettingRepository.getAdvancedConfiguration().getCustomStringConfiguration());

        groupsRepository.getGroups().execute(new Callback<PaymentMethodSearch>() {
            @Override
            public void success(final PaymentMethodSearch paymentMethodSearch) {
                expressMetadataList = paymentMethodSearch.getExpress();
                //Plus one to compensate for add new payment method
                payerCostSelection = createNewPayerCostSelected();
                cardsWithSplit = paymentMethodSearch.getIdsWithSplitAllowed();
            }

            @Override
            public void failure(final ApiException apiException) {
                throw new IllegalStateException("groups missing rendering one tap");
            }
        });
    }

    @Override
    public void loadViewModel() {
        availablePaymentMethodsCount = countAvailablePaymentMethods();

        final SummaryInfo summaryInfo = new SummaryInfoMapper().map(paymentSettingRepository.getCheckoutPreference());

        final ElementDescriptorView.Model elementDescriptorModel =
            new ElementDescriptorMapper().map(summaryInfo);

        final List<SummaryView.Model> summaryModels =
            new SummaryViewModelMapper(paymentSettingRepository.getCheckoutPreference().getSite().getCurrencyId(),
                discountRepository, amountRepository, elementDescriptorModel, this, summaryInfo,
                chargeRepository).map(new ArrayList<>(expressMetadataList));

        final List<PaymentMethodDescriptorView.Model> paymentModels =
            new PaymentMethodDescriptorMapper(paymentSettingRepository, amountConfigurationRepository,
                disabledPaymentMethodRepository).map(expressMetadataList);

        final List<SplitPaymentHeaderAdapter.Model> splitHeaderModels =
            new SplitHeaderMapper(paymentSettingRepository.getCheckoutPreference().getSite().getCurrencyId(),
                amountConfigurationRepository)
                .map(expressMetadataList);

        final List<ConfirmButtonViewModel> confirmButtonViewModels =
            new ConfirmButtonViewModelMapper(disabledPaymentMethodRepository).map(expressMetadataList);

        final HubAdapter.Model model =
            new HubAdapter.Model(paymentModels, summaryModels, splitHeaderModels, confirmButtonViewModels);

        getView().showToolbarElementDescriptor(elementDescriptorModel);

        getView().configureAdapters(paymentMethodDrawableItemMapper.map(expressMetadataList),
            paymentSettingRepository.getCheckoutPreference().getSite(), model);

        getView().setPayButtonText(payButtonViewModel);
    }

    @Override
    public void onViewResumed() {
        paymentRepository.attach(this);
        if (shouldReloadModel()) {
            loadViewModel();
        }
        updateElementPosition();
    }

    private boolean shouldReloadModel() {
        final int currentAvailablePaymentMethodsCount = countAvailablePaymentMethods();
        return availablePaymentMethodsCount != currentAvailablePaymentMethodsCount;
    }

    private int countAvailablePaymentMethods() {
        int currentAvailablePaymentMethodsCount = expressMetadataList.size();
        for (final ExpressMetadata expressMetadata : expressMetadataList) {
            if ((expressMetadata.isCard() &&
                disabledPaymentMethodRepository.hasPaymentMethodId(expressMetadata.getCard().getId())) ||
                disabledPaymentMethodRepository.hasPaymentMethodId(expressMetadata.getPaymentMethodId())) {
                currentAvailablePaymentMethodsCount--;
            }
        }
        return currentAvailablePaymentMethodsCount;
    }

    @Override
    public void detachView() {
        onViewPaused();
        super.detachView();
    }

    @Override
    public void recoverFromBundle(@NonNull final Bundle bundle) {
        payerCostSelection = bundle.getParcelable(BUNDLE_STATE_PAYER_COST);
        splitSelectionState = bundle.getParcelable(BUNDLE_STATE_SPLIT_PREF);
        availablePaymentMethodsCount = bundle.getInt(BUNDLE_STATE_AVAILABLE_PM_COUNT);
        paymentMethodIndex = bundle.getInt(BUNDLE_STATE_CURRENT_PM_INDEX);
    }

    @NonNull
    @Override
    public Bundle storeInBundle(@NonNull final Bundle bundle) {
        bundle.putParcelable(BUNDLE_STATE_PAYER_COST, payerCostSelection);
        bundle.putParcelable(BUNDLE_STATE_SPLIT_PREF, splitSelectionState);
        bundle.putInt(BUNDLE_STATE_AVAILABLE_PM_COUNT, availablePaymentMethodsCount);
        bundle.putInt(BUNDLE_STATE_CURRENT_PM_INDEX, paymentMethodIndex);
        return bundle;
    }

    @Override
    public void trackExpressView() {
        final OneTapViewTracker oneTapViewTracker = new OneTapViewTracker(expressMetadataList,
            paymentSettingRepository.getCheckoutPreference(),
            discountRepository.getCurrentConfiguration(),
            escManagerBehaviour.getESCCardIds(),
            cardsWithSplit);
        setCurrentViewTracker(oneTapViewTracker);
    }

    @Override
    public void startSecuredPayment() {
        final String productId = productIdProvider.getProductId();
        getView().startSecurityValidation(new SecurityValidationData.Builder().setFlowId(productId).build());
    }

    @Override
    public void confirmPayment() {
        refreshExplodingState();

        // TODO improve: This was added because onetap can detach this listener on its onDestroy
        paymentRepository.attach(this);

        final ExpressMetadata expressMetadata = expressMetadataList.get(paymentMethodIndex);

        PayerCost payerCost = null;

        final String customOptionId = expressMetadata.isCard() ? expressMetadata.getCard().getId()
            : expressMetadata.getPaymentMethodId();
        final AmountConfiguration amountConfiguration =
            amountConfigurationRepository.getConfigurationFor(customOptionId);

        if (expressMetadata.isCard() || expressMetadata.isConsumerCredits()) {
            payerCost = amountConfiguration
                .getCurrentPayerCost(splitSelectionState.userWantsToSplit(),
                    payerCostSelection.get(paymentMethodIndex));
        }

        final boolean splitPayment = splitSelectionState.userWantsToSplit() && amountConfiguration.allowSplit();
        ConfirmEvent
            .from(escManagerBehaviour.getESCCardIds(), expressMetadata, payerCost, splitPayment, paymentMethodIndex)
            .track();

        paymentRepository.startExpressPayment(expressMetadata, payerCost, splitPayment);
    }

    @Override
    public void trackSecurityFriction() {
        // TODO Review ID
        FrictionEventTracker
            .with(OneTapViewTracker.PATH_REVIEW_ONE_TAP_VIEW, FrictionEventTracker.Id.GENERIC,
                FrictionEventTracker.Style.CUSTOM_COMPONENT).track();
    }

    private void refreshExplodingState() {
        if (paymentRepository.isExplodingAnimationCompatible()) {
            getView().startLoadingButton(paymentRepository.getPaymentTimeout(), payButtonViewModel);
            getView().disableToolbarBack();
        }
    }

    @Override
    public void cancel() {
        tracker.trackAbort();
        getView().cancel();
    }

    @Override
    public void onTokenResolved() {
        cancelLoading();
        confirmPayment();
    }

    /**
     * When there is no visual interaction needed this callback is called.
     *
     * @param payment plugin payment.
     */
    @Override
    public void onPaymentFinished(@NonNull final IPaymentDescriptor payment) {
        getView().finishLoading(explodeDecoratorMapper.map(payment));
    }

    @Override
    public void onPaymentError(@NonNull final MercadoPagoError error) {
        cancelLoading();
        if (error.isInternalServerError() || error.isNoConnectivityError()) {
            FrictionEventTracker.with(OneTapViewTracker.PATH_REVIEW_ONE_TAP_VIEW,
                FrictionEventTracker.Id.GENERIC, FrictionEventTracker.Style.CUSTOM_COMPONENT,
                error)
                .track();
            getView().showErrorSnackBar(error);
        } else {
            getView().showErrorScreen(error);
        }
    }

    @Override
    public void onVisualPayment() {
        getView().showPaymentProcessor();
    }

    @Override
    public void onCvvRequired(@NonNull final Card card) {
        cancelLoading();
        getView().showSecurityCodeScreen(card);
    }

    @Override
    public void onRecoverPaymentEscInvalid(final PaymentRecovery recovery) {
        cancelLoading();
        getView().showCardFlow(recovery);
    }

    private void updateElementPosition(final int selectedPayerCost) {
        payerCostSelection.save(paymentMethodIndex, selectedPayerCost);
        updateElementPosition();
    }

    @Override
    public void onViewPaused() {
        paymentRepository.detach(this);
    }

    @Override
    public void onInstallmentsRowPressed() {
        final ExpressMetadata expressMetadata = expressMetadataList.get(paymentMethodIndex);
        final String customOptionId =
            expressMetadata.isCard() ? expressMetadata.getCard().getId() : expressMetadata.getPaymentMethodId();
        final AmountConfiguration amountConfiguration =
            amountConfigurationRepository.getConfigurationFor(customOptionId);
        final List<PayerCost> payerCostList = amountConfiguration.getAppliedPayerCost(
            splitSelectionState.userWantsToSplit());
        final int index = payerCostList.indexOf(
            amountConfiguration.getCurrentPayerCost(
                splitSelectionState.userWantsToSplit(), payerCostSelection.get(paymentMethodIndex)));

        getView().showInstallmentsList(payerCostList, index);
        new InstallmentsEventTrack(expressMetadata, amountConfiguration).track();
    }

    /**
     * When user cancel the payer cost selection this method will be called with the current payment method position
     */
    @Override
    public void onInstallmentSelectionCanceled() {
        updateElementPosition();
        getView().collapseInstallmentsSelection();
    }

    /**
     * When user selects a new payment method this method will be called with the new current paymentMethodIndex.
     *
     * @param paymentMethodIndex current payment method paymentMethodIndex.
     */
    @Override
    public void onSliderOptionSelected(final int paymentMethodIndex) {
        this.paymentMethodIndex = paymentMethodIndex;
        new SwipeOneTapEventTracker().track();
        updateElementPosition(payerCostSelection.get(paymentMethodIndex));
    }

    private void updateElementPosition() {
        getView().updateViewForPosition(paymentMethodIndex, payerCostSelection.get(paymentMethodIndex),
            splitSelectionState);
    }

    /**
     * When user selects a new payer cost for certain payment method this method will be called.
     *
     * @param payerCostSelected user selected payerCost.
     */
    @Override
    public void onPayerCostSelected(final PayerCost payerCostSelected) {
        final ExpressMetadata expressMetadata = expressMetadataList.get(paymentMethodIndex);
        final String customOptionId =
            expressMetadata.isCard() ? expressMetadata.getCard().getId() : expressMetadata.getPaymentMethodId();
        final int selected = amountConfigurationRepository.getConfigurationFor(customOptionId)
            .getAppliedPayerCost(splitSelectionState.userWantsToSplit())
            .indexOf(payerCostSelected);

        updateElementPosition(selected);
        getView().collapseInstallmentsSelection();
    }

    @Override
    public void hasFinishPaymentAnimation() {
        final IPaymentDescriptor payment = paymentRepository.getPayment();
        if (payment != null) {
            getView().showPaymentResult(payment);
        }
    }

    private void cancelLoading() {
        getView().enableToolbarBack();
        getView().cancelLoading();
    }

    @Override
    public void manageNoConnection() {
        final NoConnectivityException exception = new NoConnectivityException();
        final ApiException apiException = ApiUtil.getApiException(exception);
        final MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException, null);
        FrictionEventTracker.with(OneTapViewTracker.PATH_REVIEW_ONE_TAP_VIEW,
            FrictionEventTracker.Id.GENERIC, FrictionEventTracker.Style.CUSTOM_COMPONENT,
            mercadoPagoError)
            .track();
        getView().showErrorSnackBar(mercadoPagoError);
    }

    @Override
    public void onDiscountAmountDescriptorClicked(@NonNull final DiscountConfigurationModel discountModel) {
        getView().showDiscountDetailDialog(discountModel);
    }

    @Override
    public void onChargesAmountDescriptorClicked(@NonNull final DynamicDialogCreator dynamicDialogCreator) {
        final DynamicDialogCreator.CheckoutData checkoutData = new DynamicDialogCreator.CheckoutData(
            paymentSettingRepository.getCheckoutPreference(), Collections.singletonList(new PaymentData()));
        getView().showDynamicDialog(dynamicDialogCreator, checkoutData);
    }

    @Override
    public void onSplitChanged(final boolean isChecked) {
        if (splitSelectionState.userWantsToSplit() != isChecked) {
            payerCostSelection = createNewPayerCostSelected();
        }
        splitSelectionState.setUserWantsToSplit(isChecked);
        // cancel also update the position.
        // it is used because the installment selection can be expanded by the user.
        onInstallmentSelectionCanceled();
    }

    @Override
    public void onHeaderClicked() {
        final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
        final DynamicDialogConfiguration dynamicDialogConfiguration =
            paymentSettingRepository.getAdvancedConfiguration().getDynamicDialogConfiguration();

        final DynamicDialogCreator.CheckoutData checkoutData =
            new DynamicDialogCreator.CheckoutData(checkoutPreference, Collections.singletonList(new PaymentData()));

        if (dynamicDialogConfiguration.hasCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER)) {
            getView().showDynamicDialog(
                dynamicDialogConfiguration
                    .getCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER),
                checkoutData);
        }
    }

    @NonNull
    private PayerCostSelection createNewPayerCostSelected() {
        return new PayerCostSelection(expressMetadataList.size() + 1);
    }

    @Override
    public void onChangePaymentMethod() {
        cancelLoading();
        getView().resetPagerIndex();
    }

    @Override
    public void recoverPayment(@NonNull final PostPaymentAction postPaymentAction) {
        cancelLoading();
        getView().showCardFlow(paymentRepository.createPaymentRecovery());
    }
}