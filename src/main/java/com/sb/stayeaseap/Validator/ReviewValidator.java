package com.sb.stayeaseap.Validator;

import org.springframework.stereotype.Component;

@Component
public class ReviewValidator {

    public void validate(Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Review comment cannot be empty.");
        }
    }
}