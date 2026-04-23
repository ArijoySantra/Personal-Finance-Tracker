package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

	public static Connection getConnection() {
		Connection con = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/financetracker",
					"root",
					"Boom@1234"
			);
			System.out.println("Database connected successfully");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Database connection failed");
		}

		return con;
	}
}