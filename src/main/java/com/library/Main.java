package com.library;

import com.library.dao.BookDAO;
import com.library.dao.MemberDAO;
import com.library.dao.TransactionDAO;
import com.library.dao.UserDAO;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final BookDAO bookDAO = new BookDAO();
    private static final MemberDAO memberDAO = new MemberDAO();
    private static final TransactionDAO txnDAO = new TransactionDAO();
    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {

        System.out.println("=== Library Management System ===");

        String role = null;
        for (int attempt = 1; attempt <= 3 && role == null; attempt++) {
            String username = readText("Username: ");
            String password = readText("Password: ");
            role = userDAO.login(username, password);
            if (role == null) {
                System.out.println("Invalid credentials. Attempts left: " + (3 - attempt));
            }
        }

        if (role == null) {
            System.out.println("Too many failed attempts. Exiting.");
            return;
        }

        System.out.println("Logged in as " + role);
        if (role.equals("ADMIN")) {
            adminMenu();
        } else {
            studentMenu();
        }
        System.out.println("Goodbye!");
    }

    // ---------- Menus ----------

    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Add book");
            System.out.println("2. View all books");
            System.out.println("3. Search books");
            System.out.println("4. Update book");
            System.out.println("5. Delete book");
            System.out.println("6. Add member");
            System.out.println("7. View all members");
            System.out.println("8. Issue book");
            System.out.println("9. Return book");
            System.out.println("10. View issued books");
            System.out.println("11. View overdue books");
            System.out.println("0. Exit");

            int choice = readInt("Choose: ");
            switch (choice) {
                case 1 -> addBook();
                case 2 -> printAll(bookDAO.getAllBooks(), "No books found.");
                case 3 -> searchBooks();
                case 4 -> updateBook();
                case 5 -> deleteBook();
                case 6 -> addMember();
                case 7 -> printAll(memberDAO.getAllMembers(), "No members found.");
                case 8 -> issueBook();
                case 9 -> returnBook();
                case 10 -> printAll(txnDAO.getIssuedBooks(), "No books are currently issued.");
                case 11 -> printAll(txnDAO.getOverdueBooks(), "No overdue books.");
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void studentMenu() {
        while (true) {
            System.out.println("\n--- STUDENT MENU ---");
            System.out.println("1. View all books");
            System.out.println("2. Search books");
            System.out.println("0. Exit");

            int choice = readInt("Choose: ");
            switch (choice) {
                case 1 -> printAll(bookDAO.getAllBooks(), "No books found.");
                case 2 -> searchBooks();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ---------- Actions ----------

    private static void addBook() {
        String title = readText("Title: ");
        String author = readText("Author: ");
        String isbn = readText("ISBN: ");
        int copies = readInt("Number of copies: ");
        if (copies <= 0) {
            System.out.println("Copies must be at least 1.");
            return;
        }
        if (bookDAO.addBook(new Book(title, author, isbn, copies))) {
            System.out.println("Book added.");
        }
    }

    private static void searchBooks() {
        String keyword = readText("Search by title or author: ");
        printAll(bookDAO.searchByTitle(keyword), "No matching books.");
    }

    private static void updateBook() {
        int id = readInt("Book ID to update: ");
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        System.out.println("Current: " + book);
        String title = readOptional("New title (Enter to keep): ");
        String author = readOptional("New author (Enter to keep): ");
        if (!title.isEmpty()) book.setTitle(title);
        if (!author.isEmpty()) book.setAuthor(author);
        if (bookDAO.updateBook(book)) {
            System.out.println("Book updated.");
        }
    }

    private static void deleteBook() {
        int id = readInt("Book ID to delete: ");
        if (bookDAO.deleteBook(id)) {
            System.out.println("Book deleted.");
        } else {
            System.out.println("Could not delete. Check the ID, or the book may have issue history.");
        }
    }

    private static void addMember() {
        String name = readText("Name: ");
        String email = readText("Email: ");
        String phone = readText("Phone: ");
        if (memberDAO.addMember(new Member(name, email, phone))) {
            System.out.println("Member added.");
        }
    }

    private static void issueBook() {
        int bookId = readInt("Book ID: ");
        int memberId = readInt("Member ID: ");
        int txnId = txnDAO.issueBook(bookId, memberId);
        if (txnId > 0) {
            System.out.println("Book issued. Transaction ID: " + txnId + " (due in 14 days)");
        }
    }

    private static void returnBook() {
        int txnId = readInt("Transaction ID: ");
        double fine = txnDAO.returnBook(txnId);
        if (fine >= 0) {
            System.out.println("Book returned. Fine: Rs. " + fine);
        }
    }

    // ---------- Helpers ----------

    private static <T> void printAll(List<T> items, String emptyMessage) {
        if (items.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (T item : items) {
            System.out.println(item);
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static String readOptional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}