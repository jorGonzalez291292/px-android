package com.mercadopago.android.px.addons.validator.internal;

import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscValidationData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityCodeRulesTest {

    private static final String CARD_ID = "123";
    private static final String ESC = "123";

    @Mock private ESCManagerBehaviour escManagerBehaviour;
    @Mock private EscValidationData escValidationData;
    private SecurityCodeRules securityCodeRules;

    @Before
    public void setUp() {
        when(escValidationData.isEscEnable()).thenReturn(true);
        when(escValidationData.getCardId()).thenReturn(CARD_ID);
        when(escValidationData.isCard()).thenReturn(false);
        when(escManagerBehaviour.getESC(anyString(), nullable(String.class), nullable(String.class))).thenReturn(ESC);
        securityCodeRules = new SecurityCodeRules(escManagerBehaviour);
    }

    @Test
    public void testEscValidationDataValid_rulesTrue() {
        Assert.assertTrue(securityCodeRules.apply(escValidationData));
    }

    @Test
    public void testEscValidationDataIsNotCardTrueAndEscRulesFalse_rulesTrue() {
        when(escValidationData.isEscEnable()).thenReturn(false);
        Assert.assertTrue(securityCodeRules.apply(escValidationData));
    }

    @Test
    public void testEscValidationDataIsNotCardFalseAndEscRulesTrue_rulesTrue() {
        when(escValidationData.isCard()).thenReturn(true);
        Assert.assertTrue(securityCodeRules.apply(escValidationData));
    }

    @Test
    public void testEscValidationDataIsNotCardFalseAndEscRulesFalse_rulesFalse() {
        when(escValidationData.isEscEnable()).thenReturn(false);
        when(escValidationData.isCard()).thenReturn(true);
        Assert.assertFalse(securityCodeRules.apply(escValidationData));
    }
}