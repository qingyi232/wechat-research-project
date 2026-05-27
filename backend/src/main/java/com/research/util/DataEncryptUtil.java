package com.research.util;

import cn.hutool.crypto.symmetric.AES;
import cn.hutool.core.codec.Base64;

import java.nio.charset.StandardCharsets;

public class DataEncryptUtil {

    private static final String AES_KEY = "ResearchPrj2026!";
    private static final AES aes = new AES(AES_KEY.getBytes(StandardCharsets.UTF_8));

    public static String encrypt(String data) {
        if (data == null || data.isEmpty()) return data;
        return aes.encryptBase64(data);
    }

    public static String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) return encryptedData;
        return aes.decryptStr(encryptedData);
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int at = email.indexOf("@");
        if (at <= 2) return email;
        return email.substring(0, 2) + "***" + email.substring(at);
    }
}
