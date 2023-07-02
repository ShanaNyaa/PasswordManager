package com.danial.passwordmanager;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;

public class XChaCha20EncryptionHelper {

    private final LazySodiumJava lazySodium;

    public XChaCha20EncryptionHelper() {
        SodiumJava sodium = new SodiumJava();
        lazySodium = new LazySodiumJava(sodium);
    }

    public byte[] encrypt(byte[] message, byte[] key, byte[] nonce) {
        byte[] ciphertext = new byte[message.length];
        System.arraycopy(message, 0, ciphertext, 0, message.length);
        lazySodium.cryptoStreamXChaCha20(ciphertext, ciphertext.length, nonce, key);
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext, byte[] key, byte[] nonce) {
        byte[] decrypted = new byte[ciphertext.length];
        System.arraycopy(ciphertext, 0, decrypted, 0, ciphertext.length);
        lazySodium.cryptoStreamXChaCha20(decrypted, decrypted.length, nonce, key);
        return decrypted;
    }
}