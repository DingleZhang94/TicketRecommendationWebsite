package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import external.TicketMasterAPI;
import sun.misc.GC.LatencyRequest;

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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		double lat=0;
		double lon=0;
		if (request.getParameter("lat") != null) {
			lat = Double.parseDouble(request.getParameter("lat"));
		}
		if (request.getParameter("lon") != null) {
			lon = Double.parseDouble(request.getParameter("lon"));
		}
		
		String keyword = request.getParameter("term");
		
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, keyword);
		JSONArray array = new JSONArray();
		try {
			for(Item item : items) {
				array.put(item.toJSONObject());
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
