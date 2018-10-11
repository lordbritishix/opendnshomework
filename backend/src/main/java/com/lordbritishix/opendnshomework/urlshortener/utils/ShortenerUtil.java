package com.lordbritishix.opendnshomework.urlshortener.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShortenerUtil {
    // base-64 character space
    private static final String CHARACTER_SPACE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789()";

    /**
     * Based on https://stackoverflow.com/questions/742013/how-to-code-a-url-shortener
     * tldr:
     * 1. Convert seed to base 64
     * 2. Map base 64 values to character space
     * 3. Use that as the short string
     *
     * seed value must be monotonically increasing to guarantee uniqueness of data
     */
    public static String createShortString(long seed) {
        Long[] converted = convertToBase(seed, CHARACTER_SPACE.length());

        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(converted).forEach(p -> stringBuilder.append(CHARACTER_SPACE.charAt(p.intValue())));
        return stringBuilder.toString();
    }

    public static int toBase10(String base64) {
        int val = 0;
        int pow = base64.length() - 1;
        for (char c : base64.toCharArray()) {
            int idx = CHARACTER_SPACE.indexOf(c);
            val += idx * (Math.pow(64, pow));
        }

        return val;
    }

    private static Long[] convertToBase(long num, int base) {
        List<Long> result = new ArrayList<>();
        while (num > 0) {
            result.add(num % base);
            num = num / base;
        }

        Collections.reverse(result);
        return result.toArray(new Long[0]);
    }
}