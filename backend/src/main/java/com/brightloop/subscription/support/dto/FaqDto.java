package com.brightloop.subscription.support.dto;

public class FaqDto {
    private Long id;
    private String category;
    private String question;
    private String answer;
    private Boolean isPublished;

    public FaqDto() {}

    public FaqDto(Long id, String category, String question, String answer, Boolean isPublished) {
        this.id = id;
        this.category = category;
        this.question = question;
        this.answer = answer;
        this.isPublished = isPublished;
    }

    public Long getId() { return id; }
    public String getCategory() { return category; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public Boolean getIsPublished() { return isPublished; }
}

