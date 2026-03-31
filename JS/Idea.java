package com.validator;

public class Idea 
{
	public int id;
	public String username;
	public String statement;
	public String category;
	public String reasons;

	public Idea(int id,String username,String statement,String category,String reasons) 
	{
		this.id=id;
		this.username=username;
		this.statement=statement;
		this.category=category;
		this.reasons=reasons;
	}
}
