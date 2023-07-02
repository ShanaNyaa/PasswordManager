package com.danial.passwordmanager;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESEncryptionHelper {
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE_BYTES = 16; // 128 bits

    public static byte[] encrypt(byte[] message, byte[] encryptionKey) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        byte[] iv = generateInitializationVector();
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        byte[] encryptedMessage = cipher.doFinal(message);
        byte[] ciphertext = new byte[iv.length + encryptedMessage.length];
        System.arraycopy(iv, 0, ciphertext, 0, iv.length);
        System.arraycopy(encryptedMessage, 0, ciphertext, iv.length, encryptedMessage.length);
        return ciphertext;
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] encryptionKey) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        byte[] iv = new byte[KEY_SIZE_BYTES];
        byte[] encryptedMessage = new byte[ciphertext.length - KEY_SIZE_BYTES];
        System.arraycopy(ciphertext, 0, iv, 0, KEY_SIZE_BYTES);
        System.arraycopy(ciphertext, KEY_SIZE_BYTES, encryptedMessage, 0, encryptedMessage.length);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        return cipher.doFinal(encryptedMessage);
    }

    private static byte[] generateKeyFromPassword(String encryptionKey) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key = new byte[KEY_SIZE_BYTES];
        byte[] passwordBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        byte[] hash = digest.digest(passwordBytes);
        System.arraycopy(hash, 0, key, 0, KEY_SIZE_BYTES);
        return key;
    }

    private static byte[] generateInitializationVector() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[KEY_SIZE_BYTES];
        random.nextBytes(iv);
        return iv;
    }

    public static byte[] generateEncryptionKey() throws NoSuchAlgorithmException {
        // Generate a random encryption key
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE_BYTES);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }
}
