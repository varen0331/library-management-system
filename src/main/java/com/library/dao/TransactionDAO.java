package com.library.dao;

import com.library.model.Transaction;
import com.library.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private static final int LOAN_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0;

    // Returns the new transaction id, or -1 if it failed
    public int issueBook(int bookId, int memberId) {
        String checkMember = "SELECT member_id FROM members WHERE member_id = ?";
        String checkBook = "SELECT available_copies FROM books WHERE book_id = ? FOR UPDATE";
        String insert = "INSERT INTO transactions (book_id, member_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
        String reduce = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(checkMember)) {
                    ps.setInt(1, memberId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("Member not found.");
                            con.rollback();
                            return -1;
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(checkBook)) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("Book not found.");
                            con.rollback();
                            return -1;
                        }
                        if (rs.getInt("available_copies") <= 0) {
                            System.out.println("No copies available.");
                            con.rollback();
                            return -1;
                        }
                    }
                }

                LocalDate today = LocalDate.now();
                int txnId;
                try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, memberId);
                    ps.setDate(3, Date.valueOf(today));
                    ps.setDate(4, Date.valueOf(today.plusDays(LOAN_DAYS)));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        txnId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(reduce)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();
                return txnId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
            return -1;
        }
    }

    // Returns the fine amount (0 if on time), or -1 if it failed
    public double returnBook(int txnId) {
        String find = "SELECT book_id, due_date FROM transactions WHERE txn_id = ? AND return_date IS NULL FOR UPDATE";
        String close = "UPDATE transactions SET return_date = ?, fine = ? WHERE txn_id = ?";
        String restore = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int bookId;
                LocalDate due;
                try (PreparedStatement ps = con.prepareStatement(find)) {
                    ps.setInt(1, txnId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("No active issue found with that id.");
                            con.rollback();
                            return -1;
                        }
                        bookId = rs.getInt("book_id");
                        due = rs.getDate("due_date").toLocalDate();
                    }
                }

                LocalDate today = LocalDate.now();
                long daysLate = ChronoUnit.DAYS.between(due, today);
                double fine = daysLate > 0 ? daysLate * FINE_PER_DAY : 0;

                try (PreparedStatement ps = con.prepareStatement(close)) {
                    ps.setDate(1, Date.valueOf(today));
                    ps.setDouble(2, fine);
                    ps.setInt(3, txnId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(restore)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();
                return fine;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
            return -1;
        }
    }

    public List<Transaction> getIssuedBooks() {
        return query("SELECT * FROM transactions WHERE return_date IS NULL");
    }

    public List<Transaction> getOverdueBooks() {
        return query("SELECT * FROM transactions WHERE return_date IS NULL AND due_date < CURDATE()");
    }

    private List<Transaction> query(String sql) {
        List<Transaction> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTxnId(rs.getInt("txn_id"));
                t.setBookId(rs.getInt("book_id"));
                t.setMemberId(rs.getInt("member_id"));
                t.setIssueDate(rs.getDate("issue_date").toLocalDate());
                t.setDueDate(rs.getDate("due_date").toLocalDate());
                Date ret = rs.getDate("return_date");
                if (ret != null) {
                    t.setReturnDate(ret.toLocalDate());
                }
                t.setFine(rs.getDouble("fine"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return list;
    }
}