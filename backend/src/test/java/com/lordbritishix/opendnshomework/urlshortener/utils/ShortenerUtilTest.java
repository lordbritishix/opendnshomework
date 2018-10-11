package com.lordbritishix.opendnshomework.urlshortener.utils;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ShortenerUtilTest {
    @Test
    public void createShortStringCreatesWithSameCharIfSameSeed() {
        assertEquals(
                ShortenerUtil.createShortString(123L), ShortenerUtil.createShortString(123L));
        assertEquals(
                ShortenerUtil.createShortString(400L), ShortenerUtil.createShortString(400L));
    }

    @Test
    public void createShortStringIsShort() {
        long seed = 125123551L;
        assertTrue(ShortenerUtil.createShortString(RandomUtils.nextInt()).length() < String.valueOf(seed).length());
    }

    @Test
    public void createShortStringCreatesWithDifferentCharIfDifferentSeed() {
        assertNotEquals(
                ShortenerUtil.createShortString(RandomUtils.nextInt()), ShortenerUtil.createShortString(RandomUtils.nextInt()));
    }

    @Test
    public void toBase10ProducesSameIdForSameString() {
        assertEquals(
                ShortenerUtil.toBase10("abc"), ShortenerUtil.toBase10("abc"));
    }

    @Test
    public void toBase10ProducesDifferentIdForDifferentString() {
        assertNotEquals(
                ShortenerUtil.toBase10("abc"), ShortenerUtil.toBase10("abcd"));
    }
}
