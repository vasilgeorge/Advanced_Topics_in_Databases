//package AuctionHouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author g_0zek
 *
 */
public class JDBController {
	String dbName;
	
	public JDBController(String dbName){
		this.dbName=dbName; 
	}
	
	public boolean isDuplicate(String name) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		boolean result=false;
		ResultSet rset = connObject.SelectFromTable("Bidders", "bidder_name", "bidder_name='"+name+"'");
		if (rset.next())
			result=true;
		connObject.CloseConnection();
		return(result);
	}
	
	public boolean insertBidder(String name, int auctioneer) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		boolean result=false;
		result = connObject.InsertToTable("Bidders", "bidder_name,registered_to", "'"+name+"','"+auctioneer+"'");
		connObject.CloseConnection();
		return(result);
	}
	
	public ArrayList<String> itemsOfBidder(String name) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		ArrayList<String> resultList = new ArrayList<String>();
		ResultSet rset=null;
		rset = connObject.SelectFromTable("Items", "item_description", "item_owner='"+name+"'");
		while(rset.next()){
			resultList.add(rset.getString(1));
		}
		connObject.CloseConnection();
		return(resultList);
	}
	
	public ArrayList<Item> itemsList() throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		ArrayList<Item> resultList = new ArrayList<Item>();
		ResultSet rset=null;
		int item_id;
		String description;
		int startingPrice;
		rset = connObject.SelectFromTable("Items", "*", "item_owner!=' ' ");
		while(rset.next()){
			item_id=rset.getInt(1);
			description=rset.getString(2);
			startingPrice=rset.getInt(3);
			Item item = new Item(startingPrice,description,item_id);
			resultList.add(item);
		}
		connObject.CloseConnection();
		return(resultList);
	}
	
	public boolean deleteBidder(String name) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		boolean result=false;
		result = connObject.DeleteFromTable("Bidders","bidder_name='"+name+"'");
		connObject.CloseConnection();
		return(result);
	}
	
	public String getItemDescription(int id) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		ResultSet rset=null;
		String result="";
		rset = connObject.SelectFromTable("Items", "item_description", "item_id='"+id+"'");
		if (rset.next()){
			result = rset.getString(1);
		}
		connObject.CloseConnection();
		return(result);
	}	
	
	public int getCurrentBid(String desc) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		ResultSet rset=null;
		int result=-1;
		rset = connObject.SelectFromTable("Items", "final_price", "item_description='"+desc+"'");
		if (rset.next()){
			result = rset.getInt(1);
		}
		connObject.CloseConnection();
		return(result);
	}	
	
	public String getItemHolder(String desc) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		ResultSet rset=null;
		String result="no_holder";
		rset = connObject.SelectFromTable("Items", "item_owner", "item_description='"+desc+"'");
		if (rset.next()){
			result = rset.getString(1);
		}
		connObject.CloseConnection();
		return(result);
	}
	/*
	 * The following method assumes newBid is bigger than currentBid 
	 */
	public boolean updateItemBid(String itemDescription, String bidder, int newBid) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		boolean result=false;
		result = connObject.UpdateTable("Items", "final_price ='"+newBid+"',item_owner ='"+bidder+"'", "item_description='"+itemDescription+"'");
		connObject.CloseConnection();
		return(result);
	}
	
	public boolean updateItemPrice(String itemDescription,int new_price) throws SQLException{
		JDBConnection connObject = new JDBConnection(dbName);
		boolean result=false;
		int final_price=new_price-1;
		result = connObject.UpdateTable("Items", "starting_price ='"+new_price+"',final_price ='"+final_price+"'", "item_description='"+itemDescription+"'");
		connObject.CloseConnection();
		return(result);
	}	

}
