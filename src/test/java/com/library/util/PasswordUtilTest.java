package com.library.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void correctPasswordVerifies() {
        String stored = PasswordUtil.hash("Admin@123");
        assertTrue(PasswordUtil.verify("Admin@123", stored));
    }

    @Test
    void wrongPasswordFails() {
        String stored = PasswordUtil.hash("Admin@123");
        assertFalse(PasswordUtil.verify("wrong", stored));
    }

    @Test
    void sameKeyHashesDifferentBecauseOfSalt() {
        String first = PasswordUtil.hash("Admin@123");
        String second = PasswordUtil.hash("Admin@123");
        assertNotEquals(first, second);
    }

    @Test
    void hashDoesNotContainPlainPassword() {
        String stored = PasswordUtil.hash("Admin@123");
        assertFalse(stored.contains("Admin@123"));
    }
}