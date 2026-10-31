package com.brightloop.subscription.support.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TicketPriorityConverter implements AttributeConverter<TicketPriority, String> {

    @Override
    public String convertToDatabaseColumn(TicketPriority attribute) {
        if (attribute == null) {
            return "NORMAL";
        }
        return switch (attribute) {
            case LOW -> "LOW";
            case MEDIUM -> "NORMAL";
            case HIGH -> "HIGH";
        };
    }

    @Override
    public TicketPriority convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return TicketPriority.MEDIUM;
        }
        return switch (dbData.toUpperCase()) {
            case "LOW" -> TicketPriority.LOW;
            case "NORMAL" -> TicketPriority.MEDIUM;
            case "HIGH" -> TicketPriority.HIGH;
            default -> TicketPriority.MEDIUM;
        };
    }
}

