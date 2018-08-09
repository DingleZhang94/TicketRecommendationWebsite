package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet(name = "login", urlPatterns = { "/login" })
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		JSONObject res = new JSONObject();
		try {
			if (RpcHelper.verifyCookie(conn, request)) {
				String[] cookies = RpcHelper.getUserAndTokenFromCookie(request);
				System.out.println("userid: " + cookies[0]);
				res.put("userId", cookies[0]);
				res.put("fullname", conn.getFullname(cookies[0]));
				res.put("result", "success");
				RpcHelper.writeJsonObject(response, res);
			}else {
				RpcHelper.writeJsonObject(response, res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			conn.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("handle login POST");
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			JSONObject obj = RpcHelper.readJsonObject(request);
			String userId = obj.getString("username");
			String password = obj.getString("password");
			System.out.printf("username: %s ; password: %s \n", userId, password);
			if (conn.verifyLogin(userId, password)) {
				String token = conn.generateToken(userId);
				Cookie c_user = RpcHelper.getCookie("username", userId);
				Cookie c_token = RpcHelper.getCookie("token", token);
				response.addCookie(c_user);
				response.addCookie(c_token);
				JSONObject res = new JSONObject();
				res.put("userId", userId);
				res.put("fullname", conn.getFullname(userId));
				res.put("result", "success");
				RpcHelper.writeJsonObject(response, res);
			} else {
				RpcHelper.writeJsonObject(response, new JSONObject().put("result:", "wrong id or password!"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
