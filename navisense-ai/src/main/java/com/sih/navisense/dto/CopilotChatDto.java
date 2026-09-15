package com.sih.navisense.dto;

public class CopilotChatDto {
    private String question;
    private String answer;
    private String category;
    private String confidence;

    public CopilotChatDto() {}

    public CopilotChatDto(String question, String answer, String category, String confidence) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.confidence = confidence;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
}
