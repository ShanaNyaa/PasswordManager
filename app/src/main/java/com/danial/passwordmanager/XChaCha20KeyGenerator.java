package com.danial.passwordmanager;

import java.security.SecureRandom;

public class XChaCha20KeyGenerator {
    public static byte[] generateKey(int keyLength) {
        byte[] key = new byte[keyLength];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        return key;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}