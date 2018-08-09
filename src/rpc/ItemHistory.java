package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;


/**
 * Servlet implementation class ItemHistory
 */
@WebServlet(name = "history", urlPatterns = { "/history" })
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		DBConnection conn = DBConnectionFactory.getConnection();
		
		if (RpcHelper.verifyCookie(conn, request)) {
			try {
				Set<Item> items = conn.getFavoriteItems(userId);
				for (Item item : items) {
					JSONObject obj= item.toJSONObject();
					
					// add "favorite" attribute to help front end design
					obj.put("favorite", true);
					array.put(obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				conn.close();
			}			
		}
		
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @return JSONOBject {
	 * 						result: "Success".
	 * 						}
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");

			String item_id = input.getString("favorite"); 
			List<String> itemIds = new ArrayList<>();
			itemIds.add(item_id);

			conn.setFavoriteItems(userId, itemIds);
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "Success"));
			System.out.println("do post" + item_id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			String item_id = input.getString("favorite");
			List<String> itemIds = new ArrayList<>();
			
			itemIds.add(item_id);
			
			conn.unsetFavoriteItems(userId, itemIds);
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "Success"));
			System.out.println("Do delete");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
