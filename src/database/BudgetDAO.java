package database;

import model.Budget;
import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {


    public List<Budget> getMonthlyBudgets(int userId, YearMonth month) {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name, " +
                "COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "WHERE t.user_id = ? AND t.category_id = b.category_id AND t.type = 'EXPENSE' " +
                "AND YEAR(t.transaction_date) = ? AND MONTH(t.transaction_date) = ?), 0) AS spent " +
                "FROM budgets b " +
                "JOIN categories c ON b.category_id = c.id " +
                "WHERE b.user_id = ? AND b.period = 'Monthly' " +
                "AND YEAR(b.month_year) = ? AND MONTH(b.month_year) = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month.getYear());
            ps.setInt(3, month.getMonthValue());
            ps.setInt(4, userId);
            ps.setInt(5, month.getYear());
            ps.setInt(6, month.getMonthValue());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Budget b = mapBudget(rs);
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Budget> getYearlyBudgets(int userId, int year) {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name, " +
                "COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "WHERE t.user_id = ? AND t.category_id = b.category_id AND t.type = 'EXPENSE' " +
                "AND YEAR(t.transaction_date) = ?), 0) AS spent " +
                "FROM budgets b " +
                "JOIN categories c ON b.category_id = c.id " +
                "WHERE b.user_id = ? AND b.period = 'Yearly' AND YEAR(b.month_year) = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, userId);
            ps.setInt(4, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Budget b = mapBudget(rs);
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean add(Budget b) {
        String sql = "INSERT INTO budgets (user_id, category_id, amount, month_year, period) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getUserId());
            ps.setInt(2, b.getCategoryId());
            ps.setDouble(3, b.getAmount());
            ps.setDate(4, b.getMonthYear());
            ps.setString(5, b.getPeriod());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean update(Budget b) {
        String sql = "UPDATE budgets SET amount = ?, month_year = ?, period = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, b.getAmount());
            ps.setDate(2, b.getMonthYear());
            ps.setString(3, b.getPeriod());
            ps.setInt(4, b.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean delete(int budgetId) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Budget mapBudget(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setAmount(rs.getDouble("amount"));
        b.setMonthYear(rs.getDate("month_year"));
        b.setPeriod(rs.getString("period"));
        b.setCreatedAt(rs.getDate("created_at"));
        b.setCategoryName(rs.getString("category_name"));
        b.setSpent(rs.getDouble("spent"));
        return b;
    }
}