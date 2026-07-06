package com.donation.app.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String password;
    private String role;
    
    // Profile Fields
    private String nickname;
    private String avatarUrl;
    private String headerUrl;
    
    // MFA Fields
    private boolean mfaEnabled;
    private String mfaType; // "GOOGLE" or "SMS"
    private String mfaSecret; // Google TOTP Secret or SMS last code
    private String phoneNumber;
    
    private LocalDateTime createdAt;
}

