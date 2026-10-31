package com.brightloop.subscription.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqFact {
    private String question;
    private String answer;
    private String category;
    private List<String> keywords;
}

