package database;

import model.Scheduled;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduledDAO {

    public List<Scheduled> getAllByUser(int userId) {
        List<Scheduled> list = new ArrayList<>();
        String sql = "SELECT s.*, a.account_name, c.card_name, cat.name AS category_name " +
                "FROM scheduled_transactions s " +
                "LEFT JOIN accounts a ON s.account_id = a.id " +
                "LEFT JOIN cards c ON s.card_id = c.id " +
                "LEFT JOIN categories cat ON s.category_id = cat.id " +
                "WHERE s.user_id = ? ORDER BY s.next_date ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Scheduled s = mapResultSet(rs);
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(Scheduled s) {
        String sql = "INSERT INTO scheduled_transactions " +
                "(user_id, account_id, card_id, category_id, amount, type, description, frequency, next_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getUserId());
            if (s.getAccountId() != null) ps.setInt(2, s.getAccountId()); else ps.setNull(2, Types.INTEGER);
            if (s.getCardId() != null) ps.setInt(3, s.getCardId()); else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, s.getCategoryId());
            ps.setDouble(5, s.getAmount());
            ps.setString(6, s.getType());
            ps.setString(7, s.getDescription());
            ps.setString(8, s.getFrequency());
            ps.setDate(9, s.getNextDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Scheduled s) {
        String sql = "UPDATE scheduled_transactions SET account_id=?, card_id=?, category_id=?, " +
                "amount=?, type=?, description=?, frequency=?, next_date=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (s.getAccountId() != null) ps.setInt(1, s.getAccountId()); else ps.setNull(1, Types.INTEGER);
            if (s.getCardId() != null) ps.setInt(2, s.getCardId()); else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, s.getCategoryId());
            ps.setDouble(4, s.getAmount());
            ps.setString(5, s.getType());
            ps.setString(6, s.getDescription());
            ps.setString(7, s.getFrequency());
            ps.setDate(8, s.getNextDate());
            ps.setInt(9, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM scheduled_transactions WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Scheduled mapResultSet(ResultSet rs) throws SQLException {
        Scheduled s = new Scheduled();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        int accId = rs.getInt("account_id");
        s.setAccountId(rs.wasNull() ? null : accId);
        int cardId = rs.getInt("card_id");
        s.setCardId(rs.wasNull() ? null : cardId);
        s.setCategoryId(rs.getInt("category_id"));
        s.setAmount(rs.getDouble("amount"));
        s.setType(rs.getString("type"));
        s.setDescription(rs.getString("description"));
        s.setFrequency(rs.getString("frequency"));
        s.setNextDate(rs.getDate("next_date"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setAccountName(rs.getString("account_name"));
        s.setCardName(rs.getString("card_name"));
        s.setCategoryName(rs.getString("category_name"));
        return s;
    }

    public List<Scheduled> getDueSchedules(int userId) {
        List<Scheduled> list = new ArrayList<>();
        String sql = "SELECT s.*, a.account_name, c.card_name, cat.name AS category_name " +
                "FROM scheduled_transactions s " +
                "LEFT JOIN accounts a ON s.account_id = a.id " +
                "LEFT JOIN cards c ON s.card_id = c.id " +
                "LEFT JOIN categories cat ON s.category_id = cat.id " +
                "WHERE s.user_id = ? AND s.next_date <= CURDATE()";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Scheduled s = mapResultSet(rs);
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateNextDate(int id, Date newNextDate) {
        String sql = "UPDATE scheduled_transactions SET next_date = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, newNextDate);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}