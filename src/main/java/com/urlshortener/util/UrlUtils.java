package com.urlshortener.util;

import java.security.SecureRandom;

public class UrlUtils {
    private static final String ALLOWED_CHARACTERS = 
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    
    public static String generateShortUrl(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALLOWED_CHARACTERS.charAt(
                random.nextInt(ALLOWED_CHARACTERS.length())
            ));
        }
        return builder.toString();
    }
}
