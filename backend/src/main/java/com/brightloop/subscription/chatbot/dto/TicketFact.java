package com.brightloop.subscription.chatbot.dto;

import com.brightloop.subscription.support.entity.TicketPriority;
import com.brightloop.subscription.support.entity.TicketStatus;

public class TicketFact {
    private Long id;
    private String number;
    private TicketStatus status;
    private TicketPriority priority;
    private String subject;

    public TicketFact() {}

    public TicketFact(Long id, String number, TicketStatus status, TicketPriority priority, String subject) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.priority = priority;
        this.subject = subject;
    }

    public static TicketFactBuilder builder() {
        return new TicketFactBuilder();
    }

    public static class TicketFactBuilder {
        private Long id;
        private String number;
        private TicketStatus status;
        private TicketPriority priority;
        private String subject;

        public TicketFactBuilder id(Long id) { this.id = id; return this; }
        public TicketFactBuilder number(String number) { this.number = number; return this; }
        public TicketFactBuilder status(TicketStatus status) { this.status = status; return this; }
        public TicketFactBuilder priority(TicketPriority priority) { this.priority = priority; return this; }
        public TicketFactBuilder subject(String subject) { this.subject = subject; return this; }

        public TicketFact build() {
            return new TicketFact(id, number, status, priority, subject);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}
