package com.brightloop.subscription.support.dto;

import jakarta.validation.constraints.NotBlank;

public class AddCommentRequest {
    @NotBlank(message = "Comment body cannot be blank")
    private String body;
    private boolean internal = false;

    public AddCommentRequest() {}
    public AddCommentRequest(String body, boolean internal) {
        this.body = body;
        this.internal = internal;
    }

    public static AddCommentRequestBuilder builder() { return new AddCommentRequestBuilder(); }
    public static class AddCommentRequestBuilder {
        private String body;
        private boolean internal = false;
        public AddCommentRequestBuilder body(String body) { this.body = body; return this; }
        public AddCommentRequestBuilder internal(boolean internal) { this.internal = internal; return this; }
        public AddCommentRequest build() { return new AddCommentRequest(body, internal); }
    }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }
}
