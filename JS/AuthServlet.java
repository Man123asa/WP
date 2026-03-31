package com.validator;

import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class AuthServlet extends HttpServlet 
{
	private String dbUrl="jdbc:mysql://localhost:3306/startup_db";
	private String dbUser="root";
	private String dbPass="Matt#23Leblanc88!";
	protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException 
	{
		String action=request.getParameter("action");
		String user=request.getParameter("username");
		String pass=request.getParameter("password");
		String email=request.getParameter("email");
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection(dbUrl,dbUser,dbPass);
			if("signup".equals(action)) 
			{
				PreparedStatement ps=con.prepareStatement("INSERT INTO users(username,email,password) VALUES(?,?,?)");
				ps.setString(1,user);
				ps.setString(2,email);
				ps.setString(3,pass);
				ps.executeUpdate();
				HttpSession session=request.getSession();
				session.setAttribute("username",user);
				response.getWriter().write("success");
			} 
			else if("login".equals(action)) 
			{
				PreparedStatement ps=con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
				ps.setString(1,user);
				ps.setString(2,pass);
				ResultSet rs=ps.executeQuery();

				if(rs.next()) 
				{
					HttpSession session=request.getSession();
					session.setAttribute("username",user);
					session.setAttribute("email",rs.getString("email"));
					response.getWriter().write("success");
				} 
				else 
				{
					response.setStatus(401);
					response.getWriter().write("fail");
				}
			}
			con.close();
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException 
	{
		String action=request.getParameter("action");
		
		if("logout".equals(action))
		{
			HttpSession session=request.getSession(false);
			if(session!=null) 
			{
				session.invalidate();
			}
			response.getWriter().write("logged_out");
		}
		else
		{
			// Check if user is logged in (Session Validation)
			HttpSession session=request.getSession(false);
			if(session!=null && session.getAttribute("username")!=null)
			{
				response.getWriter().write("active");
			}
			else
			{
				response.setStatus(401);
			}
		}
	}
}
