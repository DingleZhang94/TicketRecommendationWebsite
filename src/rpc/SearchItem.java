package rpc;

import java.io.IOException;
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
 * Servlet implementation class SearchItem
 */
@WebServlet(name = "search", urlPatterns = { "/search" })
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		double lat = 0;
		double lon = 0;
		if (request.getParameter("lat") != null) {
			lat = Double.parseDouble(request.getParameter("lat"));
		}
		if (request.getParameter("lon") != null) {
			lon = Double.parseDouble(request.getParameter("lon"));
		}

		String keyword = request.getParameter("keyword");
		String userId = request.getParameter("user_id");
		List<Item> items = null;
		DBConnection connection = DBConnectionFactory.getConnection();
		items = connection.searchItems(lat, lon, keyword);

			
		JSONArray array = new JSONArray();
		Set<String> favoriteItems = connection.getFavoriteItemIds(userId);
		try {
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favoriteItems.contains(item.getItemId()));
				array.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			connection.close();
		}
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
