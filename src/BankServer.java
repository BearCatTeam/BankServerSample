import java.util.*;
import java.net.*;
import java.io.*;
public class BankServer {
	protected static boolean endFlag = false;
	protected static int endReceiver = 0;
	protected static HashMap<String, Double> Account = new HashMap<String, Double>();
	protected static ServerSocket server = null;
	public static void main(String[] args)
	{
		final int BANK_PORT=8888;
		try {
			try {
				server = new ServerSocket(BANK_PORT);
				System.out.println("Waiting for clients ..... ");
				while (true)
				{
					Socket s = server.accept();
					if (s.isConnected())
					{
						System.out.println("One client is connected");
						BankService service = new BankService(s);
						Thread t = new Thread(service);
						t.start();
					}
				}
			} finally{
				server.close();
			}
		} catch(IOException e)
		{
			if (e.getMessage().equals("socket closed"))
			{
				System.out.println("Server has been terminated");
			} else System.out.println("SERVER ERROR: \n"+e.getMessage());
		}
	}
}
