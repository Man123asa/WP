package com.validator;

import java.io.*;
import java.sql.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.google.gson.Gson;

public class IdeaServlet extends HttpServlet 
{
	private String dbUrl="jdbc:mysql://localhost:3306/startup_db";
	private String dbUser="root";
	private String dbPass="Matt#23Leblanc88!";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession session=request.getSession(false);
		if (session==null||session.getAttribute("username")==null) 
		{
			response.setStatus(401); // Unauthorized if no session
			return;
		}
		String loggedInUser=(String) session.getAttribute("username");
		List<Idea> ideas=new ArrayList<>();
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection(dbUrl, dbUser, dbPass);
			PreparedStatement ps=con.prepareStatement("SELECT * FROM ideas WHERE username=? ORDER BY id DESC");
			ps.setString(1,loggedInUser);
			ResultSet rs=ps.executeQuery();
			while(rs.next()) 
			{
				ideas.add(new Idea(
					rs.getInt("id"),
					rs.getString("username"),
					rs.getString("statement"),
					rs.getString("category"),
					rs.getString("reasons")
				));
			}
			con.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new Gson().toJson(ideas));
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession session=request.getSession(false);
		if (session==null||session.getAttribute("username")==null) 
		{
			response.setStatus(401);
			return;
		}
		String username=(String) session.getAttribute("username");
		String statement=request.getParameter("statement");
		// scoring system
		int score=0;
		StringBuilder reasons=new StringBuilder();
		String lowerCaseStmt=(statement != null) ? statement.toLowerCase():"";
		if (lowerCaseStmt.contains("problem")||lowerCaseStmt.contains("solution")) 
		{
			score+=2;
			reasons.append("Addresses a market problem. ");
		}
		if (lowerCaseStmt.contains("franchise")||lowerCaseStmt.contains("scale")) 
		{
			score+=1;
			reasons.append("Scalable business model. ");
		}
		String category=(score>=2)?"High Potential":"Neutral Potential";
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection(dbUrl,dbUser,dbPass);
			String query="INSERT INTO ideas (username,statement,category,reasons) VALUES (?,?,?,?)";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setString(1,username);
			ps.setString(2,statement);
			ps.setString(3,category);
			ps.setString(4,reasons.toString());
			ps.executeUpdate();
			con.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
