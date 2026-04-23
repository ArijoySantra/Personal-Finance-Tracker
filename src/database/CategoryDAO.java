package database;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories(int userId) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE user_id IS NULL OR user_id = ? ORDER BY type, name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                int uid = rs.getInt("user_id");
                c.setUserId(rs.wasNull() ? null : uid);
                c.setName(rs.getString("name"));
                c.setType(rs.getString("type"));
                c.setIcon(rs.getString("icon"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (user_id, name, type, icon) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (category.getUserId() != null) {
                ps.setInt(1, category.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, category.getName());
            ps.setString(3, category.getType());
            ps.setString(4, category.getIcon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Category findByNameAndType(String name, String type, int userId) {
        String sql = "SELECT * FROM categories WHERE name = ? AND type = ? AND (user_id = ? OR user_id IS NULL) ORDER BY user_id IS NULL LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setInt(3, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getOrCreateDebtPaymentCategory(int userId) {
        // Try to find an existing category (user-specific or global)
        Category existing = findByNameAndType("Debt Payment", "EXPENSE", userId);
        if (existing != null) {
            return existing.getId();
        }

        String insertSql = "INSERT INTO categories (user_id, name, type, icon) VALUES (NULL, 'Debt Payment', 'EXPENSE', NULL)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Category mapResultSet(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        int uid = rs.getInt("user_id");
        c.setUserId(rs.wasNull() ? null : uid);
        c.setName(rs.getString("name"));
        c.setType(rs.getString("type"));
        c.setIcon(rs.getString("icon"));
        return c;
    }
}