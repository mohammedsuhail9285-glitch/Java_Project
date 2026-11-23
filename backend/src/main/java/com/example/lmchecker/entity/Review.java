package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "violation_id", nullable = false)
    private Long violationId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(nullable = false, length = 64)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Review() {}

    public Review(Long id, Long violationId, Long reviewerId, String action, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.violationId = violationId;
        this.reviewerId = reviewerId;
        this.action = action;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getViolationId() { return violationId; }
    public void setViolationId(Long violationId) { this.violationId = violationId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long violationId;
        private Long reviewerId;
        private String action;
        private String comment;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder violationId(Long violationId) { this.violationId = violationId; return this; }
        public Builder reviewerId(Long reviewerId) { this.reviewerId = reviewerId; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder comment(String comment) { this.comment = comment; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Review build() {
            return new Review(id, violationId, reviewerId, action, comment, createdAt);
        }
    }
}
