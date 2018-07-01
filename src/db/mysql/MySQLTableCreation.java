package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLTableCreation {
	
	// Initialize database
	public static void main(String[] args) {
		try {
			
			// Step 1£º connect to MySQL
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			// standard method to register jdbc driver, using reflection
			// add cj.jdbc
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
			
			if (conn==null) {
				return;
			}
			
			System.out.println("connect successfully!");
			
			// Step2: Drop tables in case they exist
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS categories";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS items";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);
			
			System.out.println("drop table successfullly!");
			
			// Step 3 Create new tables
			
			//Create table item
			sql = "CREATE TABLE items("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255),"
					+ "rating FLOAT,"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "url VARCHAR(255),"
					+ "distance FLOAT,"
					+ "PRIMARY KEY (item_id)"
					+ ");";
			stmt.executeUpdate(sql);
			
			//Create table categories
			sql = "CREATE TABLE categories("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ");";
			stmt.executeUpdate(sql);
			
			//Create table users
			sql = "CREATE TABLE users("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ");";
			stmt.executeUpdate(sql);
			
			//Create table history
			sql = "CREATE TABLE history("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY(item_id, user_id),"
					+ "FOREIGN KEY (item_id) references items(item_id),"
					+ "FOREIGN KEY (user_id) references users(user_id)"
					+ ");";
			stmt.executeUpdate(sql);
			
			// Step 4 Insert data
			sql = "INSERT INTO users VALUES("
					+ "'1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith'"
					+ ")";
		
			System.out.println("Executing query: " + sql);
			stmt.executeUpdate(sql);

			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
