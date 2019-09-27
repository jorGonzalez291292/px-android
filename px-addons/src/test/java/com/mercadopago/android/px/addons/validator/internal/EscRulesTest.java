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
public class EscRulesTest {

    private static final String CARD_ID = "123";
    private static final String ESC = "123";

    @Mock private ESCManagerBehaviour escManagerBehaviour;
    @Mock private EscValidationData escValidationData;
    private EscRules escRules;

    @Before
    public void setUp() {
        when(escValidationData.isEscEnable()).thenReturn(true);
        when(escValidationData.getCardId()).thenReturn(CARD_ID);
        when(escManagerBehaviour.getESC(anyString(), nullable(String.class), nullable(String.class))).thenReturn(ESC);
        escRules = new EscRules(escManagerBehaviour);
    }

    @Test
    public void testEscEnableFalse_rulesFalse() {
        when(escValidationData.isEscEnable()).thenReturn(false);
        Assert.assertFalse(escRules.apply(escValidationData));
    }

    @Test
    public void testCardIdNull_rulesFalse() {
        when(escValidationData.getCardId()).thenReturn(null);
        Assert.assertFalse(escRules.apply(escValidationData));
    }

    @Test
    public void testCardIdEmpty_rulesFalse() {
        when(escValidationData.getCardId()).thenReturn("");
        Assert.assertFalse(escRules.apply(escValidationData));
    }

    @Test
    public void testHasEscFalse_rulesFalse() {
        when(escManagerBehaviour.getESC(anyString(), nullable(String.class), nullable(String.class))).thenReturn(null);
        Assert.assertFalse(escRules.apply(escValidationData));
    }

    @Test
    public void testValidData_rulesTrue() {
        Assert.assertTrue(escRules.apply(escValidationData));
    }
}