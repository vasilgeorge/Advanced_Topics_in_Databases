//package AuctionHouse;

public class Constants
{
	// bidder to auctioneer commands
	public static final String connect 		    = "connect";
	public static final String i_am_interested  = "i_am_interested";
	public static final String list_description = "list_description";
	public static final String list_high_bid	= "list_high_bid";
	public static final String my_bid 		    = "my_bid";
	public static final String quit 		    = "quit";
	
	// auctioneer to bidder commands
	public static final String start_bidding    = "start_bidding";
	public static final String new_high_bid     = "new_high_bid";
	public static final String stop_bidding     = "stop_bidding";
	public static final String duplicate_name   = "duplicate_name";
	public static final String bid_item         = "bid_item";
	public static final String auction_complete = "auction_complete";
	public static final String simple_message   = "simple_message";
		
	// other constants
	public static final String no_holder        = "no_holder";
}
