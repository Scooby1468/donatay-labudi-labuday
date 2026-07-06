package com.donation.app.domain;

import lombok.Getter;

@Getter
public class DonationException extends RuntimeException {
    private final String code;

    public DonationException(String code, String message) {
        super(message);
        this.code = code;
    }
}
