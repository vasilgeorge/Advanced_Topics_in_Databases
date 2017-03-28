//package AuctionHouse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ClientLauncher {
	private static int port;
	private static String host;
	private static SocketChannel channel;
	public static String name;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 3) {
			System.err.println("Wrong number of parameters.");
			System.err.println("Usage: ClientLauncher <host> <port> <name>");
			System.exit(1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String name = args[2];
		//name = args[2] + "@" +host + ":" +port;
		
		try {
			channel = SocketChannel.open();
			channel.connect(new InetSocketAddress(host, port));		
			while (!channel.finishConnect())
				;
			
			CharBuffer buffer = CharBuffer.wrap("connect " + name + " \n");
	        while (buffer.hasRemaining()) {
	            channel.write(Charset.defaultCharset().encode(buffer));
	        }
			Client client = new Client(name, channel);
	        new Thread(client.listeningThread).start();
	        new Thread(client.commandThread).start();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}				

	}

}
