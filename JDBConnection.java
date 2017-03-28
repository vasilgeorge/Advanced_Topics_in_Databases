//package AuctionHouse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author g_0zek
 *
 */
public class JDBConnection {
		
		Connection c = null;
		
		public JDBConnection(String dbName){
			try {
				Class.forName("org.postgresql.Driver");
				c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbName,"george", "lowfunded93");
				System.out.println("Connected to database auctionDB successfully!");
			} 
			catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
		}
		
		public void CloseConnection(){
			try{
				c.close();
				System.out.println("Closing connection.");
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		/*
		 * Implement SQL queries
		 */
		public ResultSet SelectFromTable(String table,String fields, String constraints) 
		{	
			 try {
	            String sql_query = "SELECT "+fields+" FROM "+table+" WHERE "+constraints;
	            PreparedStatement stmt = c.prepareStatement(sql_query);
	            ResultSet resultSet = stmt.executeQuery();
	            System.out.println("Selection is completed correctly.");
	            return resultSet;
			 }
			 catch (Exception e) {
			 	e.printStackTrace(); 
			 	return null;
			 }

		}

		public boolean InsertToTable(String table, String fields, String values)
		{
			try {
		        // Prepare a statement to update a record
				Statement stmt = c.createStatement();
				String sql = "INSERT INTO "+table+" ("+fields+") VALUES ("+values+")";
				int result = stmt.executeUpdate(sql);
		        if (result > 0){
		        	System.out.println("Insert is completed correctly.");
		        	return true;
		        }
		        else{
		        	System.out.println("Error on Insert into "+table+" fields "+fields+" values "+values);
		        	return false;
		        }
		    } 
			catch (SQLException e) {
		    	System.out.println("update error:" + e.getMessage());
		        return false;
		    }

		}
		
		public boolean UpdateTable(String table, String set, String constraints)
		{
			try {
		        String sql_query = "UPDATE "+table+" SET "+set+" WHERE "+constraints;	      
		        PreparedStatement stmt = c.prepareStatement(sql_query);	        
		        int result = stmt.executeUpdate();	        
		        if (result>0){
		        	System.out.println("Update is completed corectly.");
		        	return true;
		        }
		        else{
		        	System.out.println("Error on Update "+table+" set "+set+" where "+constraints);
		        	return false;
		        }
		    } 
			catch (SQLException e) {
		    	System.out.println("update error:" + e.getMessage());
		    	return false;
		    }

		}
		
		public boolean DeleteFromTable(String table, String constraints)
		{
			try {
	            String sql_query = "DELETE FROM "+table+" WHERE "+constraints;	        
		        PreparedStatement stmt = c.prepareStatement(sql_query);	        
		        int result = stmt.executeUpdate();	        
		        if (result>0){
		        	System.out.println("Deletion is completed corectly.");
		        	return true;
		        }
		        else{
		        	System.out.println("Error on Delete from"+table+" where "+constraints);
		        	return false;
		        }
		    }catch (SQLException e) {
		    	System.out.println("update error:" + e.getMessage());
		    	return false;
		    }

		}
}
	   
