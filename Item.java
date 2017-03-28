//package AuctionHouse;

import java.util.ArrayList;
import java.util.HashMap;


public class Item
{
	private int id;
	private int startingPrice;
	private String description;
	private ArrayList<String> interestedUsers;
	private String currentBidder;
	private int currentBid;

	
	public Item(int startingPrice, String description, int id)
	{
		this.id              = id;
		this.startingPrice   = startingPrice;
		this.description     = description;
		this.interestedUsers = new ArrayList<String>();
		this.currentBidder   = Constants.no_holder;
		this.currentBid      = startingPrice; 
	}
	
	public void addUser(String username)
	{
		this.interestedUsers.add(username);
	}
	
	public int getId() 
	{
		return id;
	}

	public int getStartingPrice() {
		return startingPrice;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<String> getInterestedUsers()
	{
		return interestedUsers;
	}

	public String getCurrentBidder()
	{
		return currentBidder;
	}
	
	public int getCurrentBid()
	{
		return currentBid;
	}
	
	public void setCurrentBidder(String currentBidder)
	{
		this.currentBidder = currentBidder;
	}
	
	public void setCurrentBid(int currentBid)
	{
		this.currentBid = currentBid;
	}
}
