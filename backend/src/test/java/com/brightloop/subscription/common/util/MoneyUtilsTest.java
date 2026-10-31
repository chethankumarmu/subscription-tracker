package com.brightloop.subscription.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyUtilsTest {

    @Test
    void testFormatPaiseToRupees_StandardAmounts() {
        assertEquals("₹1,499.00", MoneyUtils.formatPaiseToRupees(149900L));
        assertEquals("₹0.00", MoneyUtils.formatPaiseToRupees(0L));
        assertEquals("₹0.00", MoneyUtils.formatPaiseToRupees(null));
        assertEquals("₹50.00", MoneyUtils.formatPaiseToRupees(5000L));
        assertEquals("₹9.99", MoneyUtils.formatPaiseToRupees(999L));
    }

    @Test
    void testFormatPaiseToRupees_IndianGrouping() {
        // Thousands
        assertEquals("₹10,000.00", MoneyUtils.formatPaiseToRupees(1000000L));
        // Lakhs (1,00,000)
        assertEquals("₹1,49,900.00", MoneyUtils.formatPaiseToRupees(14990000L));
        // Ten Lakhs (10,00,000)
        assertEquals("₹10,50,000.00", MoneyUtils.formatPaiseToRupees(105000000L));
        // Crores (1,00,00,000)
        assertEquals("₹1,00,00,000.00", MoneyUtils.formatPaiseToRupees(1000000000L));
    }
}
