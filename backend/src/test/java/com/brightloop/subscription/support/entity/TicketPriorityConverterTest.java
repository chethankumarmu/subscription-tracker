package com.brightloop.subscription.support.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TicketPriorityConverterTest {

    private final TicketPriorityConverter converter = new TicketPriorityConverter();

    @Test
    @DisplayName("Should convert TicketPriority.MEDIUM to database value NORMAL")
    void shouldConvertMediumToNormal() {
        String dbValue = converter.convertToDatabaseColumn(TicketPriority.MEDIUM);
        assertEquals("NORMAL", dbValue);
    }

    @Test
    @DisplayName("Should convert TicketPriority.LOW to database value LOW")
    void shouldConvertLowToLow() {
        String dbValue = converter.convertToDatabaseColumn(TicketPriority.LOW);
        assertEquals("LOW", dbValue);
    }

    @Test
    @DisplayName("Should convert TicketPriority.HIGH to database value HIGH")
    void shouldConvertHighToHigh() {
        String dbValue = converter.convertToDatabaseColumn(TicketPriority.HIGH);
        assertEquals("HIGH", dbValue);
    }

    @Test
    @DisplayName("Should convert database value NORMAL to TicketPriority.MEDIUM")
    void shouldConvertNormalToMedium() {
        TicketPriority entityValue = converter.convertToEntityAttribute("NORMAL");
        assertEquals(TicketPriority.MEDIUM, entityValue);
    }

    @Test
    @DisplayName("Should convert database value LOW to TicketPriority.LOW")
    void shouldConvertLowToEntity() {
        TicketPriority entityValue = converter.convertToEntityAttribute("LOW");
        assertEquals(TicketPriority.LOW, entityValue);
    }

    @Test
    @DisplayName("Should convert database value HIGH to TicketPriority.HIGH")
    void shouldConvertHighToEntity() {
        TicketPriority entityValue = converter.convertToEntityAttribute("HIGH");
        assertEquals(TicketPriority.HIGH, entityValue);
    }

    @Test
    @DisplayName("Null or unknown database value should default to TicketPriority.MEDIUM")
    void shouldDefaultNullOrUnknownToMedium() {
        assertEquals(TicketPriority.MEDIUM, converter.convertToEntityAttribute(null));
        assertEquals(TicketPriority.MEDIUM, converter.convertToEntityAttribute("UNKNOWN_VAL"));
    }
}
