package com.library.dao;

import com.library.model.Member;
import com.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public boolean addMember(Member m) {
        String sql = "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getEmail());
            ps.setString(3, m.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
            return false;
        }
    }

    public List<Member> getAllMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching members: " + e.getMessage());
        }
        return list;
    }

    public Member getMemberById(int id) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding member: " + e.getMessage());
        }
        return null;
    }

    public boolean updateMember(Member m) {
        String sql = "UPDATE members SET name=?, email=?, phone=? WHERE member_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getEmail());
            ps.setString(3, m.getPhone());
            ps.setInt(4, m.getMemberId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating member: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMember(int id) {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setMemberId(rs.getInt("member_id"));
        m.setName(rs.getString("name"));
        m.setEmail(rs.getString("email"));
        m.setPhone(rs.getString("phone"));
        return m;
    }
}