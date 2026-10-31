package com.brightloop.subscription;

import com.brightloop.subscription.chatbot.service.ChatbotServiceTest;
import com.brightloop.subscription.chatbot.service.IntentDetectorServiceTest;
import com.brightloop.subscription.support.entity.TicketPriorityConverterTest;
import com.brightloop.subscription.support.service.SupportTicketServiceTest;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Running Person 2 Chatbot & Support Test Suite");
        System.out.println("=================================================");

        Class<?>[] testClasses = new Class<?>[] {
                TicketPriorityConverterTest.class,
                IntentDetectorServiceTest.class,
                ChatbotServiceTest.class,
                SupportTicketServiceTest.class
        };

        AtomicInteger totalPassed = new AtomicInteger(0);
        AtomicInteger totalFailed = new AtomicInteger(0);

        for (Class<?> testClass : testClasses) {
            System.out.println("\nTesting: " + testClass.getSimpleName());

            for (Method m : testClass.getDeclaredMethods()) {
                m.setAccessible(true);
                if (m.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
                    try {
                        Object instance = testClass.getDeclaredConstructor().newInstance();
                        if (testClass.isAnnotationPresent(org.junit.jupiter.api.extension.ExtendWith.class)) {
                            org.mockito.MockitoAnnotations.openMocks(instance);
                        }
                        Method setUpMethod = findSetUpMethod(testClass);
                        if (setUpMethod != null) {
                            setUpMethod.setAccessible(true);
                            setUpMethod.invoke(instance);
                        }
                        m.invoke(instance);
                        System.out.println("  [PASS] " + m.getName());
                        totalPassed.incrementAndGet();
                    } catch (Exception e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        System.err.println("  [FAIL] " + m.getName() + " -> " + cause.getMessage());
                        totalFailed.incrementAndGet();
                    }
                } else if (m.isAnnotationPresent(org.junit.jupiter.params.ParameterizedTest.class)) {
                    org.junit.jupiter.params.provider.ValueSource vs =
                            m.getAnnotation(org.junit.jupiter.params.provider.ValueSource.class);
                    if (vs != null && vs.strings().length > 0) {
                        for (String str : vs.strings()) {
                            try {
                                Object instance = testClass.getDeclaredConstructor().newInstance();
                                if (testClass.isAnnotationPresent(org.junit.jupiter.api.extension.ExtendWith.class)) {
                                    org.mockito.MockitoAnnotations.openMocks(instance);
                                }
                                Method setUpMethod = findSetUpMethod(testClass);
                                if (setUpMethod != null) {
                                    setUpMethod.setAccessible(true);
                                    setUpMethod.invoke(instance);
                                }
                                m.invoke(instance, str);
                                System.out.println("  [PASS] " + m.getName() + "(\"" + str + "\")");
                                totalPassed.incrementAndGet();
                            } catch (Exception e) {
                                Throwable cause = e.getCause() != null ? e.getCause() : e;
                                System.err.println("  [FAIL] " + m.getName() + "(\"" + str + "\") -> " + cause.getMessage());
                                totalFailed.incrementAndGet();
                            }
                        }
                    }
                }
            }
        }

        System.out.println("\n=================================================");
        System.out.println("Test Run Complete: " + totalPassed.get() + " PASSED, " + totalFailed.get() + " FAILED");
        System.out.println("=================================================");

        if (totalFailed.get() > 0) {
            System.exit(1);
        }
    }

    private static Method findSetUpMethod(Class<?> testClass) {
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(org.junit.jupiter.api.BeforeEach.class)) {
                return m;
            }
        }
        return null;
    }
}
