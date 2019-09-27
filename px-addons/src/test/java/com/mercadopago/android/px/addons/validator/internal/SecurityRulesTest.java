package com.mercadopago.android.px.addons.validator.internal;

import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscValidationData;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
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
public class SecurityRulesTest {

    private static final String CARD_ID = "123";
    private static final String ESC = "123";

    @Mock private ESCManagerBehaviour escManagerBehaviour;
    @Mock private SecurityValidationData securityValidationData;
    @Mock private EscValidationData escValidationData;
    private SecurityRules securityRulesTest;

    @Before
    public void setUp() {
        when(securityValidationData.getEscValidationData()).thenReturn(escValidationData);
        when(escValidationData.isEscEnable()).thenReturn(true);
        when(escValidationData.getCardId()).thenReturn(CARD_ID);
        when(escValidationData.isCard()).thenReturn(false);
        when(escManagerBehaviour.getESC(anyString(), nullable(String.class), nullable(String.class))).thenReturn(ESC);
        securityRulesTest = new SecurityRules(escManagerBehaviour);
    }

    @Test
    public void testEscValidationDataValid_rulesTrue() {
        Assert.assertTrue(securityRulesTest.apply(securityValidationData));
    }

    @Test
    public void testEscValidationDataNull_rulesTrue() {
        when(securityValidationData.getEscValidationData()).thenReturn(null);
        Assert.assertTrue(securityRulesTest.apply(securityValidationData));
    }

    @Test
    public void testEscValidationDataNotNullAndIsNotCardFalse_rulesFalse() {
        when(escValidationData.isCard()).thenReturn(false);
        Assert.assertTrue(securityRulesTest.apply(securityValidationData));
    }
}