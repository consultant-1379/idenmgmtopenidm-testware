package com.ericsson.nms.security.taf.test.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Decoder {

    public static final String __OBFUSCATE = "OBF:";
    public static final String __CRYPT = "CRYPT:";
    private static final String CRYPT_ALGORITHM = "AES";
    private static final byte[] NON_SECRET_KEY = { -50, 50, -16, -26, -99, -61, 94, 45, 26, 75, -39, -44, -48, -75, 40, 4 };
    private static final String CIPHER_STRING = "AES/CBC/PKCS5Padding";
    private String passw;
    private String keystoreType;
    private String cryptoAlias;
    private KeyStore keyStore;
    private InputStream in;
    private char[] clearPassword;
    private Key key;

    public void setKeystoreType(String kt) {
        this.keystoreType = kt;
    }

    public void setCryptoAlias(String ca) {
        this.cryptoAlias = ca;
    }

    public void setKeystorePassword(String pass) {
        this.passw = pass;
    }

    public char[] unfold(String s) throws GeneralSecurityException {
        char[] passwordCopy = null;
        if (null != s) {
            if (s.startsWith(__CRYPT)) {
                passwordCopy = decrypt(s).toCharArray();
            } else if (s.startsWith(__OBFUSCATE)) {
                passwordCopy = deobfuscate(s).toCharArray();
            } else {
                passwordCopy = s.toCharArray();
            }
        }
        return passwordCopy;
    }

    public String deobfuscate(String s) {
        if (s.startsWith(__OBFUSCATE))
            s = s.substring(__OBFUSCATE.length());

        byte[] b = new byte[s.length() / 2];
        int l = 0;
        for (int i = 0; i < s.length(); i += 4) {
            String x = s.substring(i, i + 4);
            int i0 = Integer.parseInt(x, 36);
            int i1 = (i0 / 256);
            int i2 = (i0 % 256);
            b[l++] = (byte) ((i1 + i2 - 254) / 2);
        }

        return new String(b, 0, l);
    }

    public String decrypt(String s) throws GeneralSecurityException {
        if (s.startsWith(__CRYPT))
            s = s.substring(__CRYPT.length());

        Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(NON_SECRET_KEY,
                CRYPT_ALGORITHM));
        return new String(cipher.doFinal(hexStringToByteArray(s)));
    }

    private byte[] hexStringToByteArray(String hex) {
        byte rc[] = new byte[hex.length() / 2];
        for (int i = 0; i < rc.length; i++) {
            String h = hex.substring(i * 2, i * 2 + 2);
            int x = Integer.parseInt(h, 16);
            rc[i] = (byte) x;
        }
        return rc;
    }

    public String decode(String vector, String password,
            String keystoreJecksFile) {
        String data = "";
        try {
            in = new FileInputStream(keystoreJecksFile);
            keyStore = KeyStore.getInstance(keystoreType);
            clearPassword = unfold(passw);
            keyStore.load(in, clearPassword);
            key = keyStore.getKey(cryptoAlias, clearPassword);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        if (key == null) {
            String msg = "Encryption key not found";
            throw new NullPointerException(msg);
        }

        try {
            byte[] iv = Base64.decodeBase64(vector);
            byte[] encData = Base64.decodeBase64(password);
            Cipher symmetric = Cipher.getInstance(CIPHER_STRING);
            symmetric.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] encryptedData = symmetric.doFinal(encData);
            data = new String(encryptedData);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return data;

    }

}