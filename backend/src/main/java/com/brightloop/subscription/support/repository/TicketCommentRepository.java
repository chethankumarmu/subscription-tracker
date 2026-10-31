package com.brightloop.subscription.support.repository;

import com.brightloop.subscription.support.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    List<TicketComment> findAllByTicketIdOrderByCreatedAtAsc(Long ticketId);

    List<TicketComment> findAllByTicketIdAndInternalFalseOrderByCreatedAtAsc(Long ticketId);
}

