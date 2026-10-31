package com.brightloop.subscription.chatbot.service;

import com.brightloop.subscription.chatbot.entity.ChatIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntentDetectorServiceTest {

    private IntentDetectorService intentDetector;

    @BeforeEach
    void setUp() {
        intentDetector = new IntentDetectorService();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "I want to speak with a human",
            "Connect me with an agent please",
            "Can I talk to a live person?",
            "Representative needed",
            "Escalate this to support team",
            "Helpdesk assistance needed"
    })
    @DisplayName("Should detect SPEAK_TO_HUMAN intent")
    void shouldDetectSpeakToHuman(String input) {
        assertEquals(ChatIntent.SPEAK_TO_HUMAN, intentDetector.detectIntent(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Can I pause my subscription?",
            "I want to hold my plan",
            "Freeze subscription temporarily",
            "Suspend subscription for next month",
            "Pause plan"
    })
    @DisplayName("Should detect SUBSCRIPTION_PAUSE intent")
    void shouldDetectSubscriptionPause(String input) {
        assertEquals(ChatIntent.SUBSCRIPTION_PAUSE, intentDetector.detectIntent(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "When is my next billing date?",
            "When will I be charged?",
            "What is my renewal date?",
            "Next payment date please",
            "When do you bill me next?"
    })
    @DisplayName("Should detect NEXT_BILLING_DATE intent")
    void shouldDetectNextBillingDate(String input) {
        assertEquals(ChatIntent.NEXT_BILLING_DATE, intentDetector.detectIntent(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "What is the status of my invoice?",
            "Show my latest bill",
            "Do I have any unpaid receipts?",
            "Latest invoice status",
            "Payment status of current bill"
    })
    @DisplayName("Should detect INVOICE_STATUS intent")
    void shouldDetectInvoiceStatus(String input) {
        assertEquals(ChatIntent.INVOICE_STATUS, intentDetector.detectIntent(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Tell me about my subscription",
            "What is my current plan?",
            "Show my active plan details",
            "Subscription details please"
    })
    @DisplayName("Should detect SUBSCRIPTION_DETAILS intent")
    void shouldDetectSubscriptionDetails(String input) {
        assertEquals(ChatIntent.SUBSCRIPTION_DETAILS, intentDetector.detectIntent(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "What is your refund policy?",
            "FAQ on billing",
            "How do I cancel my plan terms?",
            "What is Brightloop?"
    })
    @DisplayName("Should detect FAQ_QUERY intent")
    void shouldDetectFaqQuery(String input) {
        assertEquals(ChatIntent.FAQ_QUERY, intentDetector.detectIntent(input));
    }

    @Test
    @DisplayName("Should return UNKNOWN for null or gibberish messages")
    void shouldDetectUnknown() {
        assertEquals(ChatIntent.UNKNOWN, intentDetector.detectIntent(null));
        assertEquals(ChatIntent.UNKNOWN, intentDetector.detectIntent(""));
        assertEquals(ChatIntent.UNKNOWN, intentDetector.detectIntent("   "));
        assertEquals(ChatIntent.UNKNOWN, intentDetector.detectIntent("asdfghjkl random noise 12345"));
    }
}
