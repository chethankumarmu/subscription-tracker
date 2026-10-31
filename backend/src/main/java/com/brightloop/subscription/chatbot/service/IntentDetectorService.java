package com.brightloop.subscription.chatbot.service;

import com.brightloop.subscription.chatbot.entity.ChatIntent;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class IntentDetectorService {

    private static final Pattern HUMAN_PATTERN = Pattern.compile(
            "\\b(human|agent|representative|person|support team|helpdesk|escalat(e|ion)|talk to someone|live chat)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern PAUSE_PATTERN = Pattern.compile(
            "\\b(pause|hold|freeze|suspend|temporar(y|ily) stop)\\b.*\\b(sub|subscription|plan|account)?\\b|" +
            "\\b(can i pause|how to pause|pause my)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern BILLING_DATE_PATTERN = Pattern.compile(
            "\\b(next billing|billing date|when.*(charged|renew|bill|charge)|renewal date|due date of bill|next payment date|next charge)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern INVOICE_PATTERN = Pattern.compile(
            "\\b(invoice|bill|receipt|payment status|latest bill|past invoice|unpaid|how much do i owe|invoice status)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern SUBSCRIPTION_PATTERN = Pattern.compile(
            "\\b(my subscription|subscription details?|current plan|what plan|active plan|plan features?|subscription status)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern FAQ_PATTERN = Pattern.compile(
            "\\b(faq|how does|how do i|policy|refund|terms|cancel policy|what is)\\b",
            Pattern.CASE_INSENSITIVE);

    public ChatIntent detectIntent(String message) {
        if (message == null || message.trim().isEmpty()) {
            return ChatIntent.UNKNOWN;
        }

        String trimmed = message.trim();

        if (HUMAN_PATTERN.matcher(trimmed).find()) {
            return ChatIntent.SPEAK_TO_HUMAN;
        }

        if (PAUSE_PATTERN.matcher(trimmed).find()) {
            return ChatIntent.SUBSCRIPTION_PAUSE;
        }

        if (BILLING_DATE_PATTERN.matcher(trimmed).find()) {
            return ChatIntent.NEXT_BILLING_DATE;
        }

        if (INVOICE_PATTERN.matcher(trimmed).find()) {
            return ChatIntent.INVOICE_STATUS;
        }

        if (SUBSCRIPTION_PATTERN.matcher(trimmed).find()) {
            return ChatIntent.SUBSCRIPTION_DETAILS;
        }

        if (FAQ_PATTERN.matcher(trimmed).find()) {
            return ChatIntent.FAQ_QUERY;
        }

        return ChatIntent.UNKNOWN;
    }
}
