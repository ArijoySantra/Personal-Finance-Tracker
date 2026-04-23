package database;

import model.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {

    public List<Card> getCardsByUser(int userId) {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE user_id = ? ORDER BY card_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Card c = new Card();
                c.setId(rs.getInt("id"));
                c.setUserId(rs.getInt("user_id"));
                c.setCardName(rs.getString("card_name"));
                c.setCardType(rs.getString("card_type"));
                c.setCreditLimit(rs.getDouble("credit_limit"));
                c.setCurrentBalance(rs.getDouble("current_balance"));
                c.setDueDate(rs.getDate("due_date"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                int assocAccId = rs.getInt("associated_account_id");
                c.setAssociatedAccountId(rs.wasNull() ? null : assocAccId);
                c.setInterestRate(rs.getDouble("interest_rate"));
                c.setPaidOffMonthly(rs.getBoolean("paid_off_monthly"));
                c.setStartingDay(rs.getInt("starting_day"));
                c.setPaymentDay(rs.getInt("payment_day"));
                c.setAutomaticPayment(rs.getBoolean("automatic_payment"));
                c.setNotes(rs.getString("notes"));

                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addCard(Card card) {
        String sql = "INSERT INTO cards (user_id, card_name, card_type, credit_limit, current_balance, due_date, " +
                "associated_account_id, interest_rate, paid_off_monthly, starting_day, payment_day, automatic_payment, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, card.getUserId());
            ps.setString(2, card.getCardName());
            ps.setString(3, card.getCardType());
            ps.setDouble(4, card.getCreditLimit());
            ps.setDouble(5, card.getCurrentBalance());
            ps.setDate(6, card.getDueDate());

            if (card.getAssociatedAccountId() != null) {
                ps.setInt(7, card.getAssociatedAccountId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setDouble(8, card.getInterestRate());
            ps.setBoolean(9, card.isPaidOffMonthly());
            ps.setInt(10, card.getStartingDay());
            ps.setInt(11, card.getPaymentDay());
            ps.setBoolean(12, card.isAutomaticPayment());
            ps.setString(13, card.getNotes());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCard(Card card) {
        String sql = "UPDATE cards SET card_name=?, card_type=?, credit_limit=?, current_balance=?, due_date=?, " +
                "associated_account_id=?, interest_rate=?, paid_off_monthly=?, starting_day=?, payment_day=?, " +
                "automatic_payment=?, notes=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, card.getCardName());
            ps.setString(2, card.getCardType());
            ps.setDouble(3, card.getCreditLimit());
            ps.setDouble(4, card.getCurrentBalance());
            ps.setDate(5, card.getDueDate());

            if (card.getAssociatedAccountId() != null) {
                ps.setInt(6, card.getAssociatedAccountId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setDouble(7, card.getInterestRate());
            ps.setBoolean(8, card.isPaidOffMonthly());
            ps.setInt(9, card.getStartingDay());
            ps.setInt(10, card.getPaymentDay());
            ps.setBoolean(11, card.isAutomaticPayment());
            ps.setString(12, card.getNotes());
            ps.setInt(13, card.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCard(int cardId) {
        String sql = "DELETE FROM cards WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cardId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalOutstanding(int userId) {
        String sql = "SELECT SUM(current_balance) FROM cards WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}