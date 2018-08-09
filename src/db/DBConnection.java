package db;

import java.util.List;
import java.util.Set;

import entity.Item;

/**
 * @author dingl
 *
 */
/**
 * @author dingl
 *
 */
public interface DBConnection extends AutoCloseable{
	/**
	 * Close the connection.
	 */
	public void close();

	/**
	 * Insert the favorite items for a user.
	 * 
	 * @param userId
	 * @param itemIds
	 */
	public void setFavoriteItems(String userId, List<String> itemIds);

	/**
	 * Delete the favorite items for a user.
	 * 
	 * @param userId
	 * @param itemIds
	 */
	public void unsetFavoriteItems(String userId, List<String> itemIds);

	/**
	 * Get the favorite item id for a user.
	 * 
	 * @param userId
	 * @return itemIds
	 */
	public Set<String> getFavoriteItemIds(String userId);

	/**
	 * Get the favorite items for a user.
	 * 
	 * @param userId
	 * @return items
	 */
	public Set<Item> getFavoriteItems(String userId);

	/**
	 * Gets categories based on item id
	 * 
	 * @param itemId
	 * @return set of categories
	 */
	public Set<String> getCategories(String itemId);

	/**
	 * Search items near a geolocation and a term (optional).
	 * 
	 * @param userId
	 * @param lat
	 * @param lon
	 * @param term
	 *            (Nullable)
	 * @return list of items
	 */
	public List<Item> searchItems(double lat, double lon, String term);

	/**
	 * Save item into db.
	 * 
	 * @param item
	 */
	public void saveItem(Item item);

	/**
	 * Get full name of a user. (This is not needed for main course, just for demo
	 * and extension).
	 * 
	 * @param userId
	 * @return full name of the user
	 */
	public String getFullname(String userId);

	/**
	 * Return whether the credential is correct. (This is not needed for main
	 * course, just for demo and extension)
	 * 
	 * @param userId
	 * @param password
	 * @return boolean
	 */
	public boolean verifyLogin(String userId, String password);
	
	
	
	/**
	 * @param userid
	 * @return true if update, false if failed.
	 */
	public String generateToken(String userId);
	
	
	
	/**
	 * Delete the token given userId.
	 * @param userId
	 */
	public void deleteToken(String userId);
	
	
	
	/**
	 * @param userId
	 * @param cookie
	 * @return true if validate.
	 */
	public boolean verifyToken(String userId, String token); 
	
	/**
	 * 
	 * 
	 * @param userId (Nullable)
	 * @param password (Nullable)
	 * @param firstName (Nullable)
	 * @param lastName (Nullable)
	 * @return int: 0 - success
	 * 				1 - invalid input 
	 * 				2 - duplicate users
	 * 				3 - server error
	 * 
	 * 
	 * 
	 */
	public int registerUser(String userId, String password, String firstName, String lastName);
}


