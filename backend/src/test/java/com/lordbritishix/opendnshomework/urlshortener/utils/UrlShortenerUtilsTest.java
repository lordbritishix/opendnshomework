package com.lordbritishix.opendnshomework.urlshortener.utils;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class UrlShortenerUtilsTest {
    @Test
    public void createShortStringCreatesWithSameCharIfSameSeed() {
        assertEquals(
                UrlShortenerUtils.createShortString(123L), UrlShortenerUtils.createShortString(123L));
        assertEquals(
                UrlShortenerUtils.createShortString(400L), UrlShortenerUtils.createShortString(400L));
    }

    @Test
    public void createShortStringIsShort() {
        long seed = 125123551L;
        assertTrue(UrlShortenerUtils.createShortString(RandomUtils.nextInt()).length() < String.valueOf(seed).length());
    }

    @Test
    public void createShortStringCreatesWithDifferentCharIfDifferentSeed() {
        assertNotEquals(
                UrlShortenerUtils.createShortString(RandomUtils.nextInt()), UrlShortenerUtils.createShortString(RandomUtils.nextInt()));
    }

    @Test
    public void isValidReturnsFalseIfNotBase64Encoded() {
        String notBase64EncodedUrl = "http://google.com";
        assertFalse(UrlShortenerUtils.isValidUrl(notBase64EncodedUrl));
    }

    @Test
    public void isValidReturnsTrueIfBase64EncodedAndValid() {
        // http://google.com/search
        String validBase64EncodedUrl = "aHR0cDovL2dvb2dsZS5jb20vc2VhcmNo";
        assertTrue(UrlShortenerUtils.isValidUrl(validBase64EncodedUrl));
    }

    @Test
    public void isValidReturnsFalseIfBase64EncodedButNotValid() {
        // google.com/search
        String notValidBase64EncodedUrl = "Z29vZ2xlLmNvbS9zZWFyY2g=";
        assertFalse(UrlShortenerUtils.isValidUrl(notValidBase64EncodedUrl));
    }
}
