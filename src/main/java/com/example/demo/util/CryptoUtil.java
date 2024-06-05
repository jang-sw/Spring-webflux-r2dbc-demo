package com.example.demo.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {

	
	private final String alg = "AES/CBC/PKCS5Padding";
    private final String key = "01234567890123456789012345678901";
    private final String iv = key.substring(0, 16);

    public String AESEncrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String AESDecrypt(String cipherText) throws Exception {
    	
		Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
	    	
    }
    public String encodeSHA512(String planeText) {
    	try {
    		planeText = planeText + "salt1Salt!salt";
    		String encodingText = "";
    		MessageDigest md = MessageDigest.getInstance("SHA-512");
    		md.update(planeText.getBytes(StandardCharsets.UTF_8));
    		encodingText = DatatypeConverter.printBase64Binary(md.digest());
    		return encodingText;
    	}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
