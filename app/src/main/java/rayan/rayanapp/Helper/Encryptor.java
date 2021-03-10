package rayan.rayanapp.Helper;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import rayan.rayanapp.Activities.MainActivity;

public class Encryptor {


//    public static String encrypt(String plain, String TOKEN_KEY) {
//        try {
//            byte[] iv = new byte[16];
//            new SecureRandom().nextBytes(iv);
//            Cipher cipher = Cipher.getInstance("AES22/CBC/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(TOKEN_KEY.getBytes("utf-8"), "AES22"), new IvParameterSpec(iv));
//            byte[] cipherText = cipher.doFinal(plain.getBytes("utf-8"));
//            byte[] ivAndCipherText = getCombinedArray(iv, cipherText);
//            return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP);
//        } catch (Exception e) {
//            Log.e("Error takes place", "Error: " +e);
//            return null;
//        }
//    }
//
//    public static String decrypt(String encoded, String TOKEN_KEY) {
//        try {
//            byte[] ivAndCipherText = Base64.decode(encoded, Base64.NO_WRAP);
//            byte[] iv = Arrays.copyOfRange(ivAndCipherText, 0, 16);
//            byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, 16, ivAndCipherText.length);
//
//            Cipher cipher = Cipher.getInstance("AES22/CBC/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(TOKEN_KEY.getBytes("utf-8"), "AES22"), new IvParameterSpec(iv));
//            return new String(cipher.doFinal(cipherText), "utf-8");
//        } catch (Exception e) {
//            Log.e("Error takes place", "Error: " +e);
//            return null;
//        }
//    }


    public static String encrypt(String plain, String secret) {
        try {
//            byte[] iv = null;
//            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING");
//            Cipher cipher = Cipher.getInstance("AES/ECB");
//            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(secret.getBytes(), "AES"));
            byte[] cipherText = cipher.doFinal(plain.getBytes("utf-8"));
//            byte[] ivAndCipherText = getCombinedArray(iv, cipherText);
//            return Base64.encodeToString(cipherText, Base64.DEFAULT);
//            return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP);
            return Base64.encodeToString(cipherText, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("Error takes place", "Error: " +e);
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encoded, String secret) {
        try {
            byte[] ivAndCipherText = Base64.decode(encoded, Base64.NO_WRAP);
//            byte[] iv = null;
//            byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, 16, ivAndCipherText.length);

//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
//            Cipher cipher = Cipher.getInstance("AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret.getBytes(), "AES"));
            Log.e("QWQWQWQWQ", "RERERERERE" + new String(cipher.doFinal(ivAndCipherText), "utf-8"));
            return new String(cipher.doFinal(ivAndCipherText), "utf-8");
        } catch (Exception e) {
            Log.e("Error takes place", "Error: " +e);
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getCombinedArray(byte[] one, byte[] two) {
        byte[] combined = new byte[one.length + two.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < one.length ? one[i] : two[i - one.length];
        }
        return combined;
    }

}
