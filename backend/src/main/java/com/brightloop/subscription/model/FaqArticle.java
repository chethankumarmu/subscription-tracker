package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "faq_articles")
public class FaqArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String question;

    @Column(nullable = false, columnDefinition = "text")
    private String answer;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public FaqArticle() {}

    public FaqArticle(Long id, String question, String answer, String category, Boolean isPublished, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.isPublished = isPublished != null ? isPublished : true;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static FaqArticleBuilder builder() {
        return new FaqArticleBuilder();
    }

    public static class FaqArticleBuilder {
        private Long id;
        private String question;
        private String answer;
        private String category;
        private Boolean isPublished = true;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public FaqArticleBuilder id(Long id) { this.id = id; return this; }
        public FaqArticleBuilder question(String question) { this.question = question; return this; }
        public FaqArticleBuilder answer(String answer) { this.answer = answer; return this; }
        public FaqArticleBuilder category(String category) { this.category = category; return this; }
        public FaqArticleBuilder isPublished(Boolean isPublished) { this.isPublished = isPublished; return this; }
        public FaqArticleBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public FaqArticleBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public FaqArticle build() {
            return new FaqArticle(id, question, answer, category, isPublished, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
