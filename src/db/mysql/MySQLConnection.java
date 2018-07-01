package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {
	
	private Connection conn;
	private PreparedStatement saveItemStmt = null;
	private PreparedStatement getSaveItemStmt() {
		try{
			if (saveItemStmt == null) {
			if (conn == null) {
				System.out.println("DB Connection failed!");
				return null;
			}
			saveItemStmt = conn.prepareStatement("INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saveItemStmt;
	}
	public MySQLConnection() {
		super();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		
		if (conn == null) {
			System.err.println("DB connection failed!");
			return;
		}
		
		try {
			String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			for (String itemId : itemIds) {
				stmt.setString(2, itemId);
				stmt.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {

		if (conn == null) {
			System.err.println("DB connection failed!");
			return;
		}
		
		try {
			String sql = "DELETE IGNORE FROM history WHERE user_id = ? AND item_id = ? ;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			for (String itemId : itemIds) {
				stmt.setString(2, itemId);
				stmt.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String keyword) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, keyword);
		
		for (Item item : items) {
			saveItem(item);
		}
		
		return items;
	}

	@Override
	public void saveItem(Item item) {
		
		// avoid null connection before each write
		if (conn == null) {
			System.out.println("DB Connection failed!");
			return;
		}
		try {
			PreparedStatement stmt = getSaveItemStmt();
			stmt.setString(1, item.getItemId());
			stmt.setString(2, item.getName());
			stmt.setDouble(3, item.getRating());
			stmt.setString(4, item.getAddress());
			stmt.setString(5, item.getImageUrl());
			stmt.setString(6, item.getUrl());
			stmt.setDouble(7, item.getDistance());
			stmt.execute();
			
			String sql = "INSERT IGNORE INTO categories VALUES (?, ?)";
			stmt = conn.prepareStatement(sql);
			for (String category : item.getCategories()) {
				stmt.setString(1, item.getItemId());
				stmt.setString(2, category);
				stmt.execute();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
