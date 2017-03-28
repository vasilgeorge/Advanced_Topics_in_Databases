//package AuctionHouse;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.io.*;


public class Auctioneer extends Observer implements Runnable{
	private ServerSocketChannel serverSocket;
    private int port;
    private int peerPort;
	private Auctioneer auctioneer;
	private SocketChannel peerChannel;
	private Selector selector;
	    
    public HashMap<SocketChannel,String> activeChannels;
    public HashMap<String,SocketChannel> revActiveChannels;
    public ArrayList<String> interestedUsers = new ArrayList<String>();
    public String itemDescription;
    public boolean commandFlag;
    public boolean bidFlag;
    public Subject syncObject;
    public String SName;
    public int time_interval;//time L for all the actions
    CyclicBarrier barrier;
    public String dbName;
	
	   public Auctioneer(int port,Subject syncObject,String SName,CyclicBarrier barrier) throws IOException
	   {
		   this.port = port;
		   this.syncObject=syncObject;
		   this.SName=SName;
		   this.barrier=barrier;
		   this.syncObject.attach(this);
		   
	   }

	   public void run()
	   {
			System.out.println("Auctioneer starting ...");
			//initialize variables.
			switch (SName){
				case "Server1": dbName="auctionDB1";break;
				case "Server2": dbName="auctionDB2";break;
				case "Server3": dbName="auctionDB3";break;
			}
			JDBController dbController = new JDBController(dbName);
			activeChannels=new HashMap();
			revActiveChannels=new HashMap();
			time_interval=15000;
			commandFlag=false;
			bidFlag=false;
			itemDescription="no item yet";
			ItemController icon=new ItemController(this,barrier);
			icon.start();
			
	        // Get selector
	        Selector selector;
			try {
				selector = Selector.open();
		        System.out.println("Selector open: " + selector.isOpen());
		        // Get server socket channel and register with selector
		        ServerSocketChannel serverSocket = ServerSocketChannel.open();
		        InetSocketAddress hostAddress = new InetSocketAddress("192.168.1.66", port);
		        serverSocket.bind(hostAddress);
		        serverSocket.configureBlocking(false);
		        int ops = serverSocket.validOps();
		        SelectionKey selectKy = serverSocket.register(selector, ops, null);
		        for (;;) {

		            //System.out.println("Waiting for select...");
		            int noOfKeys = selector.select();
		            //System.out.println("Number of selected keys: " + noOfKeys);
		            Set selectedKeys = selector.selectedKeys();		           
		            Iterator iter = selectedKeys.iterator();
		            		            
		            while (iter.hasNext()) {
		                SelectionKey ky = (SelectionKey) iter.next();
		                if (ky.isAcceptable()) {
		                    // Accept the new client connection
		                    SocketChannel client;
							try {
								client = serverSocket.accept();
			                    client.configureBlocking(false);
			                    // Add the new connection to the selector
			                    client.register(selector, SelectionKey.OP_READ);
			                    System.out.println("Accepted new connection from client: " + client);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                }
		                else if (ky.isReadable()) {
		                    // Read the data from client
		                    SocketChannel client = (SocketChannel) ky.channel();
		                    ByteBuffer buffer = ByteBuffer.allocate(256);
		                    client.read(buffer);
		                    String output = new String(buffer.array()).trim();
		                    System.out.println("Message read from client: " + output);
		        	        
		                    String[] parts = output.split(" ");
		        	        String part1 = parts[0];
		                    //handleMessages(output);
		                    if (output.equals(Constants.list_high_bid)) {
		                    	int itemId = 1; 			//ItemsArrayList.getCurrentItemId;
		                    	String highestBid="";
		        				try {
		        					highestBid = Integer.toString(dbController.getCurrentBid(itemDescription));
		        				} catch (SQLException e) {
		        					// TODO Auto-generated catch block
		        					e.printStackTrace();
		        				}
		        				send(client,"Current highest bid:" + highestBid);
		                    }
		        	        else if (output.equals(Constants.list_description)){
		        	        	send(client,"Description of current item:" + itemDescription);
		        	        }
		                    else if ((output.equals(Constants.quit))||(output.equals(""))) {
		                    	String bidder=activeChannels.get(client);
		        				try {
		        					String bid=activeChannels.get(client);
			                    	activeChannels.remove(client);
			                    	revActiveChannels.remove(bidder);
		        					boolean flag = false;
		        					flag = dbController.deleteBidder(bid);
		        					if (!flag)
		        						System.out.println("error in deleting bidder!");
		        				} 
		        				catch (SQLException e) {
		        					// TODO Auto-generated catch block
		        					e.printStackTrace();
		        				}
		                        client.close();
		                        System.out.println("Client messages are complete; close.");
		                    }
		                    else if (part1.equals("bid")){
		                    	String name=activeChannels.get(client);
		                    	if ((bidFlag)&&(interestedUsers.contains(name))){
			        	        	int amount=-1;
			        	        	boolean flag = false;			        	        	
			        	        	try{
			        	        		amount = Integer.parseInt(parts[1]);
			        	        		int currentBid = dbController.getCurrentBid(itemDescription);
			        	        		/* Update only on strictly higher bid! */
			        	        		if (amount > currentBid){
			        	        			send(client,"Your bid has been accepted.\n");	
			        	        			flag = dbController.updateItemBid(itemDescription, name, amount);
			        	        		}
			        	        		else
			        	        			send(client,"Your bid has been rejected.\n");	
			        	        	} 
			        	        	catch(NumberFormatException e){
			        	        		send(client,"You haven't entered a number: ");		        		
			        	        	} 
			        	        	catch (SQLException e) {
			        					// TODO Auto-generated catch block
			        					e.printStackTrace();
			        				}
			        	        	if (flag){
			        	        		send(client,"You bidded: " + amount);
			        	        		syncObject.setState(amount,name,itemDescription);
			        	        	}
		                    	}
		                    	else{
		                    		send(client,"You cannot bid at this time.");
		                    	}			        	        	
		                    }
		                    else if (part1.equals("connect")){
		                    	String name = parts[1];
		                    	try {		                			
		                			boolean flag = false;
		                			flag = dbController.isDuplicate(name);
		                			if ((!flag)&&(!name.equals("no_holder"))){
		                				flag = dbController.insertBidder(name, 1);
		                				send(client,"Success on registering!");
		                			}
		                			else{
		                				send(client,Constants.duplicate_name);
		                				client.close();
		                			}
		                		} catch (IOException e1) {
		                			// TODO Auto-generated catch block
		                			e1.printStackTrace();
		                		}
		                        catch (SQLException e) {
		                			// TODO Auto-generated catch block
		                			e.printStackTrace();
		                		}
			                    activeChannels.put(client,name);
			                    revActiveChannels.put(name, client);
		                    }
		                    if (commandFlag){
		                    	if (output.equals(Constants.i_am_interested)){
		                    		String name=activeChannels.get(client);
		                    		if (!interestedUsers.contains(name)){
		                    		interestedUsers.add(name);
		                    		}
		                    	}
		                    }
		                } // end if (ky...)
		                iter.remove();
		            } // end while loop
		        } // end for loop

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	    
	   }

	   protected void send(SocketChannel channel, String message) throws IOException {	
			byte [] mes = new String(message).getBytes();
	        ByteBuffer buffer = ByteBuffer.wrap(mes);
			channel.write(buffer);
	        buffer.clear();
	   }	
	   
	    public void broadcast(String message)
	    {
	    	CharBuffer buffer;
	    	for (SocketChannel channel : activeChannels.keySet())
	    	{
	    		buffer = CharBuffer.wrap(message);
	    		try {
	    			while (buffer.hasRemaining())
	    				channel.write(Charset.defaultCharset().encode(buffer));
	    		} catch (IOException e) {
	    			//e.printStackTrace();
	    		}
	    		buffer.clear();
	    	}
	    }
	    
	    public void broadcastInterested(String message)
	    {
	    	CharBuffer buffer;
	    	for (String name : interestedUsers)
	    	{	
	    		SocketChannel channel=revActiveChannels.get(name);
	    		buffer = CharBuffer.wrap(message);
	    		try {
	    			while (buffer.hasRemaining())
	    				channel.write(Charset.defaultCharset().encode(buffer));
	    		} catch (IOException e) {

	    		}
	    		buffer.clear();
	    	}
	    }
	 
	   private void handleMessages(String output) {
		// TODO Auto-generated method stub
		
	   }
	@Override
	public void update() {
		// TODO Auto-generated method stub
		System.out.println(SName+":New high bid");		
		String message=Constants.new_high_bid+"@"+syncObject.getUsername()+"@"+syncObject.getAmount();
		time_interval=15000;
		broadcastInterested(message);	
	}
	   	
}

