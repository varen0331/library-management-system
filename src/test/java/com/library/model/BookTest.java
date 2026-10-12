package com.library.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void newBookStartsWithAllCopiesAvailable() {
        Book b = new Book("Clean Code", "Robert Martin", "9780132350884", 3);
        assertEquals(3, b.getTotalCopies());
        assertEquals(3, b.getAvailableCopies());
    }

    @Test
    void settersUpdateFields() {
        Book b = new Book();
        b.setTitle("Effective Java");
        b.setAvailableCopies(1);
        assertEquals("Effective Java", b.getTitle());
        assertEquals(1, b.getAvailableCopies());
    }
}