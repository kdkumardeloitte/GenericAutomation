package com.deloitte.rpa.octa.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class AESDecryption {

	private final static Logger logger = Logger.getLogger(AESDecryption.class);

	private static final String secretKeyValue = "tranquillity";
	private static SecretKeySpec secretKey;
	private static byte[] key;
	private static final String ALGORITHM = "AES";

	private void prepareSecreteKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

	/*
    private String encrypt(String strToEncrypt, String secret) {
        try {
            prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
        	logger.error("Error while encrypting: " + e.getMessage(), e);
        }
        return null;
    }*/

	private String decrypt(String strToDecrypt, String secret) {
		try {
			prepareSecreteKey(secret);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			logger.error("Error while decrypting: "  + e.getMessage(), e);
		}
		return null;
	}

	public static String getDecryptedValue(String encryptedString) {
		return new AESDecryption().decrypt(encryptedString, secretKeyValue);
	}
}