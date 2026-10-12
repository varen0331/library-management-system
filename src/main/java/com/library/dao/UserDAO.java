package com.library.dao;

import com.library.util.DBConnection;
import com.library.util.PasswordUtil;

import java.sql.*;

public class UserDAO {

    // role must be "ADMIN" or "STUDENT"
    public boolean register(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Returns the role if login succeeds, otherwise null
    public String login(String username, String password) {
        String sql = "SELECT password_hash, role FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(password, rs.getString("password_hash"))) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return null;
    }
}