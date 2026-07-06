package com.donation.app.infrastructure.mfa;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@Component
public class GoogleAuthService {

    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public String getOtpAuthURL(String email, String secret) {
        return "otpauth://totp/DonationApp:" + email + "?secret=" + secret + "&issuer=DonationApp";
    }

    public boolean authorize(String secret, int code) {
        if (secret == null || secret.isBlank()) return false;
        long timeIndex = System.currentTimeMillis() / 1000 / 30;
        Base32 base32 = new Base32();
        byte[] key = base32.decode(secret);

        for (int i = -1; i <= 1; i++) {
            if (verifyCode(key, timeIndex + i) == code) {
                return true;
            }
        }
        return false;
    }

    private static int verifyCode(byte[] key, long t) {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        try {
            SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);
            byte[] hash = mac.doFinal(data);
            int offset = hash[19] & 0xf;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xff);
            }
            truncatedHash &= 0x7fffffff;
            truncatedHash %= 1000000;
            return (int) truncatedHash;
        } catch (Exception e) {
            return -1;
        }
    }
}
