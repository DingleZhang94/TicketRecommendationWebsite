package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;



public class RpcHelper {
	
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException{
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			out.println(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			out.close();
		}
	}

	public static void writeJsonArray(HttpServletResponse response, JSONArray array)throws IOException {
		System.out.println("inside write Json Array");
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			out.println(array);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			out.close();
		}
	}
	
	public static JSONObject readJsonObject(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		try(BufferedReader reader = request.getReader()) {
			String line = null;
			while((line = reader.readLine())!=null) {
				sb.append(line);
			}
			return new JSONObject(sb.toString());
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return new JSONObject();
	}
	
	public static Cookie getCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(604800);
//		cookie.setHttpOnly(true);
//		cookie.setSecure(true);
		System.out.println("cookie name: " + name + "; cookie value: " + value);
		return cookie;
	}
	
	public static boolean verifyCookie(DBConnection conn, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String userId = null;
		String token = null;
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals("username")) {
				userId = cookie.getValue();
			}else if (cookie.getName().equals("token")) {
				token = cookie.getValue();
			}
		}
		return conn.verifyToken(userId, token);
	}
	
	public static String[] getUserAndTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) return null;
		String[] res = new String[2];
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals("username")) {
				res[0] = cookie.getValue();
			}else if (cookie.getName().equals("token")) {
				res[1] = cookie.getValue();
			}
		}
		return res;
	}
	
	public static void printRequestPara(HttpServletRequest request) {
		Enumeration<String>  names =  request.getParameterNames();
		while(names.hasMoreElements()) {
			System.out.println(names.nextElement());
		}
		
	}
}
