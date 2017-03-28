//package AuctionHouse;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ItemController extends Thread{
	public String itemDescription;
	public boolean commandFlag;
	private ArrayList<Item> items;
	private Auctioneer auctioneer;
	private int item_id;
	private String description;
	private int startingPrice;
	int currentPrice;
	private CyclicBarrier barrier;
	
	public ItemController(Auctioneer auctioneer,CyclicBarrier barrier ) {
		// TODO Auto-generated constructor stub
		super("ItemController");
		this.barrier=barrier;
		this.auctioneer=auctioneer;
	}

	public void run(){
		JDBController dbc=new JDBController(auctioneer.dbName);
	
		try {
			items=dbc.itemsList();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		auctionSleep(15000);
		broadcast("The auction starts in 15 seconds.Waiting...");
		auctionSleep(15000);
		broadcast("The auction starts:"); 
		for (Item it : items)
		{	
			item_id=it.getId();
			description=it.getDescription();
			startingPrice=it.getStartingPrice();
			auctioneer.interestedUsers.clear();
			auctioneer.time_interval=15000;
			int counter=5;
			String itemHolder="no_holder";
			while((itemHolder.equals("no_holder")) && counter>0){
				auctionSleep(4000);				
				scenario(item_id,description,startingPrice);
				counter--;
				itemHolder="";
				try {
					itemHolder=dbc.getItemHolder(description);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if( (itemHolder.equals("no_holder"))  && (counter>0) && !(auctioneer.interestedUsers.isEmpty())){
					startingPrice=(int) (startingPrice * 0.9);
					auctioneer.time_interval=15000;
					try {
						dbc.updateItemPrice(description, startingPrice);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{					
					break; 
				}
			}
			
			String winner="";
			try {
				winner=dbc.getItemHolder(description);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String finalMessage=Constants.stop_bidding+"@"+winner+"@"+description;//the name of the winner
			broadcastInterested(finalMessage);
			try {
				barrier.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(auctioneer.SName+" Ends");
		broadcast(Constants.auction_complete);
	}
		
    public void broadcast(String message)
    {
    	CharBuffer buffer;
    	for (SocketChannel channel : auctioneer.activeChannels.keySet())
    	{
    		buffer = CharBuffer.wrap(message);
    		try {
    			while (buffer.hasRemaining())
    				channel.write(Charset.defaultCharset().encode(buffer));
    		} catch (IOException e) {

    		}
    		buffer.clear();
    	}
    }
    public void broadcastInterested(String message)
    {
    	CharBuffer buffer;
    	for (String name : auctioneer.interestedUsers)
    	{	
    		SocketChannel channel=auctioneer.revActiveChannels.get(name);
    		buffer = CharBuffer.wrap(message);
    		try {
    			while (buffer.hasRemaining())
    				channel.write(Charset.defaultCharset().encode(buffer));
    		} catch (IOException e) {

    		}
    		buffer.clear();
    	}
    }
    public void auctionSleep(int time) {
    	try {
			sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
	public void scenario(int item_id,String description,int startingPrice){
		auctioneer.itemDescription=description;	
		String firstMessage=Constants.bid_item+"@"+item_id+"@"+description+"@"+startingPrice;
		broadcast(firstMessage);
		auctioneer.commandFlag=true;
		broadcast("Are you interested?");
		auctionSleep(auctioneer.time_interval);		
		auctioneer.commandFlag=false;
		if (!(auctioneer.interestedUsers.isEmpty())){
			auctioneer.bidFlag=true;
			String secondMessage=Constants.start_bidding+"@"+item_id+"@"+description;
			broadcastInterested(secondMessage);
			
			while(auctioneer.time_interval>0){
				auctionSleep(500);
				auctioneer.time_interval=auctioneer.time_interval-500;
			}			
			auctioneer.bidFlag=false;
		}
		else {
			broadcast("Wait for the next item...");
			while(auctioneer.time_interval>0){
				auctionSleep(500);
				auctioneer.time_interval=auctioneer.time_interval-500;
			}
		}
	}	
}
