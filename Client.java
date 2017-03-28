//package AuctionHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Client extends Thread{
	private static SocketChannel channel;
	private static String name;
	private ArrayList<String> boughtItems;
	public Runnable listeningThread;
	public Runnable commandThread;
	

	public Client(String name,SocketChannel channel) {
		// TODO Auto-generated constructor stub
        this.name = name;
        this.channel = channel;            
        this.boughtItems=new ArrayList<String>();
        
        listeningThread = new Runnable() {
        	public void run() {
        		try {
					Client.this.runListeningThread();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        };
        
        commandThread = new Runnable() {
        	public void run() {
        		try {
					Client.this.runCommandThread();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        };
	}

	protected void runCommandThread() throws IOException{
		// TODO Auto-generated method stub
		while(true){
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        System.out.print(name+":~$");
	        String command="";
	        try {
				command = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        String[] parts = command.split(" ");
	        String part1 = parts[0];
	        
	        if(command.equals("bid")){
	        	System.out.println(name+":~$"+"Enter an amount.");
	        }		       
	        else if (part1.equals("bid")){
	        	int amount=0;
	        	try{
	        		amount = Integer.parseInt(parts[1]);
	        	} catch(NumberFormatException e){
	        		System.out.println(name+":~$"+"Enter a number!");		        		
	        	}
	        	if (amount!=0)
	        		send(channel,"bid "+amount);
	        }
	        else if (command.equals("list_high_bid")){
	        	send(channel,Constants.list_high_bid);
	        }
	        else if (command.equals("list_description")){        	
	        	send(channel,Constants.list_description);
	        }
	        else if (command.equals("i_am_interested")){        	
	        	send(channel,Constants.i_am_interested);
	        }
	        else if (command.equals("quit")){
	        	send(channel,Constants.quit);
	        	//channel.close();
	        	System.out.println(name+":~$"+"You bought: "+boughtItems);
	        	System.exit(1);
	        }
	        else {
	        	System.out.println(name+":~$"+"Wrong command.");
	        }
		}
	   
   }
		

	protected void runListeningThread() throws IOException {
		// TODO Auto-generated method stub
		while (true) {
            ByteBuffer bufferA = ByteBuffer.allocate(256);
            String message = "";
            while ((channel.read(bufferA)) > 0) {
				bufferA.flip();
                message += Charset.defaultCharset().decode(bufferA);
            }
            if (message.length() > 0) { 
                String[] parts = message.split("@");
    	        String identifier = parts[0];
            	handleMessage(identifier,message);   
            }
            //System.out.println();
		}
	}
	
	protected void send(SocketChannel channel, String message) throws IOException {	
		byte [] mes = new String(message).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(mes);
		channel.write(buffer);
        buffer.clear();
	}
	
	private void handleMessage(String identifier,String message) {		
		if (identifier.equals(Constants.duplicate_name)){
	    	handleDuplicateName();
		}	
		else if (identifier.equals(Constants.auction_complete)){
	      	handleAuctionComplete();
		}
		else if (identifier.equals(Constants.start_bidding)){	      	
	        handleStartBidding(message);
		}
	    else if (identifier.equals(Constants.bid_item)){
	        handleBidItem(message);
		}
	    else if (identifier.equals(Constants.new_high_bid)){
	        handleNewHighBid(message);
		}
	    else if (identifier.equals(Constants.stop_bidding)){
	        handleStopBidding(message);
		}
	    else{
	    	handleInfo(message);	   
	    }
	}		
 
	private void handleStopBidding(String message) {
		System.out.println("You shall now stop bidding for the item.");
        String[] parts = message.split("@");
		String winner = parts[1];
		String description = parts[2];
		if (name.equals(winner)) {
			System.out.print(name+":~$");
			System.out.println("Congratulations! The item: " + description + " is yours!"); // TODO check whether we should check the props.get("itemID")
			boughtItems.add(description);
		}
		System.out.print(name+":~$");
	}
	
	private void handleInfo(String message) {
		//System.out.println("Server said: "+message);
		System.out.println(message);
		System.out.print(name+":~$");		
	}

	private void handleNewHighBid(String message) {
        String[] parts = message.split("@");
		String username = parts[1];
		String amount = parts[2];
		System.out.println("New highest bid for current item belongs to " +username);
        System.out.println(name+":~$"+"New price: " + amount);
        System.out.print(name+":~$");
	}

	private void handleBidItem(String message) {
        String[] parts = message.split("@");
		String item_id = parts[1];
		String description = parts[2];
		String starting_price = parts[3];
		System.out.println("New item out for bidding!");
		System.out.println(name+":~$"+"Item ID: " + item_id);
		System.out.println(name+":~$"+"Item Description: " + description);
		System.out.println(name+":~$"+"Starting Price: " + starting_price);
		System.out.print(name+":~$");
	}

	private void handleStartBidding(String message) {
        String[] parts = message.split("@");
		String item_id = parts[1];
		String description = parts[2];;
		System.out.println("You may now start bidding for item with description = " + description + "!");
		System.out.print(name+":~$");
	}

	private void handleAuctionComplete() {
		System.out.println("Auction is now completed.");
		System.out.println(name+":~$"+"Thank you for your participation");
        try {
            channel.close();
        } catch (IOException e) {    
        }
        System.out.println(name+":~$"+"You bought: "+boughtItems);
        System.exit(0);
	}

	private void handleDuplicateName() {
        System.err.println("Username " + name + " already exists.");
        System.exit(1);		
	}	

}
