package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Register
 */
@WebServlet(name = "register", description = "User registration", urlPatterns = { "/register" })
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			String userId = request.getParameter("username");
			String password = request.getParameter("password");
			String cfmPsw = request.getParameter("confirm-psw");
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("lastname");
			System.out.println(cfmPsw);
			RpcHelper.printRequestPara(request);
			int res = 2;
			if(cfmPsw != null && password!=null && cfmPsw.equals(password)) {
				res = conn.registerUser(userId, password, firstname, lastname);
			}
			String noti = "";
			if(res == 0 ) {
				noti = "success!";
			}else {
				noti = "Unsucess!";
			}
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result:", noti));
			System.out.println("Register: " + userId + " code: " + res);
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			conn.close();
		}
	}

}
