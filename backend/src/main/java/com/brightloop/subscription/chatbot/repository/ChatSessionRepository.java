package com.brightloop.subscription.chatbot.repository;

import com.brightloop.subscription.chatbot.entity.ChatSession;
import com.brightloop.subscription.chatbot.entity.ChatSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByIdAndCustomerId(Long id, Long customerId);

    List<ChatSession> findAllByCustomerIdOrderByStartedAtDesc(Long customerId);

    Optional<ChatSession> findFirstByCustomerIdAndStatusOrderByStartedAtDesc(
            Long customerId, ChatSessionStatus status);
}

