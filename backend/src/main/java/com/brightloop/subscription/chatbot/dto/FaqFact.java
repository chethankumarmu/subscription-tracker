package com.brightloop.subscription.chatbot.dto;

import java.util.List;

public class FaqFact {
    private String question;
    private String answer;
    private String category;
    private List<String> keywords;

    public FaqFact() {}

    public FaqFact(String question, String answer, String category, List<String> keywords) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.keywords = keywords;
    }

    public static FaqFactBuilder builder() {
        return new FaqFactBuilder();
    }

    public static class FaqFactBuilder {
        private String question;
        private String answer;
        private String category;
        private List<String> keywords;

        public FaqFactBuilder question(String question) { this.question = question; return this; }
        public FaqFactBuilder answer(String answer) { this.answer = answer; return this; }
        public FaqFactBuilder category(String category) { this.category = category; return this; }
        public FaqFactBuilder keywords(List<String> keywords) { this.keywords = keywords; return this; }

        public FaqFact build() {
            return new FaqFact(question, answer, category, keywords);
        }
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}
