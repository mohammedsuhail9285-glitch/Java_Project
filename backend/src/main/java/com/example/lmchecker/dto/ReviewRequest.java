package com.example.lmchecker.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewRequest {
    @NotBlank(message = "Action is required (CONFIRM, DISMISS, RESOLVE, REQUEST_INFO)")
    private String action;

    @NotBlank(message = "Reviewer rationale / comment is required")
    private String comment;

    private String remediationAction;

    public ReviewRequest() {}

    public ReviewRequest(String action, String comment, String remediationAction) {
        this.action = action;
        this.comment = comment;
        this.remediationAction = remediationAction;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getRemediationAction() { return remediationAction; }
    public void setRemediationAction(String remediationAction) { this.remediationAction = remediationAction; }
}
