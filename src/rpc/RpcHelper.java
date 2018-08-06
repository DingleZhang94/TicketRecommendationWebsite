package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;



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
	
	public static void printRequestPara(HttpServletRequest request) {
		Enumeration<String>  names =  request.getParameterNames();
		while(names.hasMoreElements()) {
			System.out.println(names.nextElement());
		}
		
	}
}
