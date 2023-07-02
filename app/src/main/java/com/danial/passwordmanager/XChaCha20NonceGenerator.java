package com.danial.passwordmanager;

import java.security.SecureRandom;

public class XChaCha20NonceGenerator {
    private static final int NONCE_SIZE_BYTES = 24;

    public static byte[] generateNonce() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] nonce = new byte[NONCE_SIZE_BYTES];
        secureRandom.nextBytes(nonce);
        return nonce;
    }
}
