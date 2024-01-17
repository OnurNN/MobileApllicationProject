package com.example.harmony2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testPasswordMismatch() {
        String password = "onur1";
        String confirmPassword = "ozgurhoca2";
        boolean result = PasswordValidator.matching(password, confirmPassword);
        assertFalse(result);
    }
}