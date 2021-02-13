package org.acme.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@ApplicationScoped
public class Cryptographer {

    public String encrypt(String strToEncrypt) {
        String encryptedStr = null;
        try {
            System.out.printf("handling to encrypt given data: {%s}", strToEncrypt);
            encryptedStr = Base64.getEncoder().encodeToString(
                    getCipher(Cipher.ENCRYPT_MODE).doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.err.printf("an error occurred while encrypting: %s ", strToEncrypt);
        }
        return encryptedStr;
    }

    public String decrypt(String strToDecrypt) throws Exception {
        String decryptedStr;
        try {
            System.out.printf("handling to decrypt given data: {%s}", strToDecrypt);
            decryptedStr = new String(getCipher(Cipher.DECRYPT_MODE).doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.err.printf("an error occurred while decrypting: %s ", strToDecrypt);
            throw e;
        }
        return decryptedStr;
    }

    private Cipher getCipher(int cipherMode) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey tmp = factory.generateSecret(new PBEKeySpec("pass123".toCharArray(), "salt".getBytes(), 10, 128));
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(
                Arrays.copyOf("54A9F8BE356AC4F07A3A128BEA99F7F6".getBytes(), 16));
        cipher.init(cipherMode, secretKey, ivParameterSpec);
        return cipher;
    }
}
