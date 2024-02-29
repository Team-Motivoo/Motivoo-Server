package sopt.org.motivoo.external.client.auth.apple.service;


import static sopt.org.motivoo.common.advice.CommonExceptionType.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sopt.org.motivoo.common.advice.BusinessException;

public class EncryptUtils {

    public static String encrypt(String value) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha256.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(INVALID_ENCRYPT_COMMUNICATION);
        }
    }
}
