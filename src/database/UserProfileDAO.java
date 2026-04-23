package database;

import model.UserProfile;
import java.sql.*;

public class UserProfileDAO {

    public UserProfile getByUserId(int userId) {
        String sql = "SELECT * FROM user_profiles WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(UserProfile profile) {
        // Check if exists
        UserProfile existing = getByUserId(profile.getUserId());
        if (existing != null) {
            return update(profile);
        } else {
            return insert(profile);
        }
    }

    private boolean insert(UserProfile profile) {
        String sql = "INSERT INTO user_profiles (user_id, monthly_income, occupation, financial_goal, country) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, profile.getUserId());
            ps.setDouble(2, profile.getMonthlyIncome());
            ps.setString(3, profile.getOccupation());
            ps.setString(4, profile.getFinancialGoal());
            ps.setString(5, profile.getCountry());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean update(UserProfile profile) {
        String sql = "UPDATE user_profiles SET monthly_income=?, occupation=?, financial_goal=?, country=? WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, profile.getMonthlyIncome());
            ps.setString(2, profile.getOccupation());
            ps.setString(3, profile.getFinancialGoal());
            ps.setString(4, profile.getCountry());
            ps.setInt(5, profile.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private UserProfile mapResultSet(ResultSet rs) throws SQLException {
        UserProfile up = new UserProfile();
        up.setId(rs.getInt("id"));
        up.setUserId(rs.getInt("user_id"));
        up.setMonthlyIncome(rs.getDouble("monthly_income"));
        up.setOccupation(rs.getString("occupation"));
        up.setFinancialGoal(rs.getString("financial_goal"));
        up.setCountry(rs.getString("country"));
        return up;
    }
}