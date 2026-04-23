package database;

import model.Debt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DebtDAO {

    public List<Debt> getAllByUser(int userId) {
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT * FROM debts WHERE user_id = ? ORDER BY due_date ASC, name ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Debt d = mapResultSet(rs);
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(Debt d) {
        String sql = "INSERT INTO debts (user_id, name, type, amount, remaining, due_date, interest_rate, emi_amount) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getUserId());
            ps.setString(2, d.getName());
            ps.setString(3, d.getType());
            ps.setDouble(4, d.getAmount());
            ps.setDouble(5, d.getRemaining());
            ps.setDate(6, d.getDueDate());
            ps.setDouble(7, d.getInterestRate());
            ps.setDouble(8, d.getEmiAmount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Debt d) {
        String sql = "UPDATE debts SET name=?, type=?, amount=?, remaining=?, due_date=?, interest_rate=?, emi_amount=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getType());
            ps.setDouble(3, d.getAmount());
            ps.setDouble(4, d.getRemaining());
            ps.setDate(5, d.getDueDate());
            ps.setDouble(6, d.getInterestRate());
            ps.setDouble(7, d.getEmiAmount());
            ps.setInt(8, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM debts WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Debt mapResultSet(ResultSet rs) throws SQLException {
        Debt d = new Debt();
        d.setId(rs.getInt("id"));
        d.setUserId(rs.getInt("user_id"));
        d.setName(rs.getString("name"));
        d.setType(rs.getString("type"));
        d.setAmount(rs.getDouble("amount"));
        d.setRemaining(rs.getDouble("remaining"));
        d.setDueDate(rs.getDate("due_date"));
        d.setInterestRate(rs.getDouble("interest_rate"));
        d.setEmiAmount(rs.getDouble("emi_amount"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        return d;
    }
}