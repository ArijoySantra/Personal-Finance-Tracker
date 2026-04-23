package database;

import model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public List<Transaction> getByUserAndMonth(int userId, YearMonth month) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, a.account_name, c.card_name, cat.name AS category_name " +
                "FROM transactions t " +
                "LEFT JOIN accounts a ON t.account_id = a.id " +
                "LEFT JOIN cards c ON t.card_id = c.id " +
                "LEFT JOIN categories cat ON t.category_id = cat.id " +
                "WHERE t.user_id = ? AND YEAR(t.transaction_date) = ? AND MONTH(t.transaction_date) = ? " +
                "ORDER BY t.transaction_date DESC, t.id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month.getYear());
            ps.setInt(3, month.getMonthValue());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction t = mapResultSet(rs);
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(Transaction t) {
        String sql = "INSERT INTO transactions (user_id, account_id, card_id, category_id, amount, type, description, transaction_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, t.getUserId());
            if (t.getAccountId() != null) ps.setInt(2, t.getAccountId()); else ps.setNull(2, Types.INTEGER);
            if (t.getCardId() != null) ps.setInt(3, t.getCardId()); else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, t.getCategoryId());
            ps.setDouble(5, t.getAmount());
            ps.setString(6, t.getType());
            ps.setString(7, t.getDescription());
            ps.setDate(8, t.getTransactionDate());

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                if (t.getAccountId() != null) {
                    updateAccountBalance(t.getAccountId(), t.getType(), t.getAmount());
                } else if (t.getCardId() != null) {
                    updateCardBalance(t.getCardId(), t.getType(), t.getAmount());
                }
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int transactionId) {
        Transaction t = getById(transactionId);
        if (t == null) return false;

        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                if (t.getAccountId() != null) {
                    reverseAccountBalance(t.getAccountId(), t.getType(), t.getAmount());
                } else if (t.getCardId() != null) {
                    reverseCardBalance(t.getCardId(), t.getType(), t.getAmount());
                }
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Transaction getById(int transactionId) {
        String sql = "SELECT t.*, a.account_name, c.card_name, cat.name AS category_name " +
                "FROM transactions t " +
                "LEFT JOIN accounts a ON t.account_id = a.id " +
                "LEFT JOIN cards c ON t.card_id = c.id " +
                "LEFT JOIN categories cat ON t.category_id = cat.id " +
                "WHERE t.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getTotalIncome(int userId, YearMonth month) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = 'INCOME' AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month.getYear());
            ps.setInt(3, month.getMonthValue());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalExpense(int userId, YearMonth month) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = 'EXPENSE' AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month.getYear());
            ps.setInt(3, month.getMonthValue());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Transaction mapResultSet(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setUserId(rs.getInt("user_id"));
        int accId = rs.getInt("account_id");
        t.setAccountId(rs.wasNull() ? null : accId);
        int cardId = rs.getInt("card_id");
        t.setCardId(rs.wasNull() ? null : cardId);
        t.setCategoryId(rs.getInt("category_id"));
        t.setAmount(rs.getDouble("amount"));
        t.setType(rs.getString("type"));
        t.setDescription(rs.getString("description"));
        t.setTransactionDate(rs.getDate("transaction_date"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        t.setAccountName(rs.getString("account_name"));
        t.setCardName(rs.getString("card_name"));
        t.setCategoryName(rs.getString("category_name"));
        return t;
    }

    private void updateAccountBalance(int accountId, String type, double amount) {
        String sign = type.equals("INCOME") ? "+" : "-";
        String sql = "UPDATE accounts SET balance = balance " + sign + " ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void reverseAccountBalance(int accountId, String type, double amount) {
        String sign = type.equals("INCOME") ? "-" : "+";
        String sql = "UPDATE accounts SET balance = balance " + sign + " ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updateCardBalance(int cardId, String type, double amount) {
        String sign = type.equals("EXPENSE") ? "+" : "-";
        String sql = "UPDATE cards SET current_balance = current_balance " + sign + " ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, cardId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void reverseCardBalance(int cardId, String type, double amount) {
        String sign = type.equals("EXPENSE") ? "-" : "+";
        String sql = "UPDATE cards SET current_balance = current_balance " + sign + " ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, cardId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public double getTotalExpenseForDate(int userId, LocalDate date) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = 'EXPENSE' AND transaction_date = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalIncomeForDate(int userId, LocalDate date) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = 'INCOME' AND transaction_date = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}