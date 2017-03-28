//package AuctionHouse;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;

public class ServerLauncher {
	static final int ServerPort1 = 6666;
	static final int ServerPort2 = 6667;
	static final int ServerPort3 = 6668;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Subject syncObject = new Subject();
		CyclicBarrier barrier = new CyclicBarrier(3);
		
		Auctioneer Server1 = new Auctioneer(ServerPort1,syncObject,"Server1",barrier);
		Thread t1 = new Thread(Server1);
        t1.start();
		Auctioneer Server2 = new Auctioneer(ServerPort2,syncObject,"Server2",barrier);
		Thread t2 = new Thread(Server2);
        t2.start();
		Auctioneer Server3 = new Auctioneer(ServerPort3,syncObject,"Server3",barrier);
		Thread t3 = new Thread(Server3);
        t3.start();
	}

}
