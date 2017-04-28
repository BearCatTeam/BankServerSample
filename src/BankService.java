import java.util.*;
import java.net.*;
import java.io.*;
public class BankService implements Runnable { 
	protected static int instance = 0; 
	private Socket s;
	private Scanner in;
	private PrintWriter out;
	public BankService(Socket s)
	{
		this.s = s;
		instance = instance+1;
	}
	public void run()
	{
		try {
			try {
				in = new Scanner(s.getInputStream());
				out = new PrintWriter(s.getOutputStream());
				doService();
			} finally {
				instance--;
				s.close();
			}
		} catch(IOException e)
		{
			System.out.println("SERVICE'S RUN ERROR: \n"+e.getMessage());
		} finally {
			in.close();
			out.close();
		}
	}
	public void doService()
	{
		try {
			while(true)
			{
				while(!in.hasNext())
				{
					System.out.println("no data");
				}
				String command = in.next();
				System.out.println("Received command is: "+command);
				if (command.equals("STOP") || BankServer.endFlag == true)
				{
					BankServer.endFlag = true;
					BankServer.endReceiver++;
					if (BankServer.endReceiver == instance)
					{
						BankServer.server.close();
						return;
					}
				} else if (command.equals("CLOSE")) 
				{
					if(instance==1)
					{
						BankServer.server.close();
					}
					return;
				} else executeCommand(command);
			}
		} catch (IOException e)
		{
			System.out.println("SERVICE'S doService() ERROR: \n"+e.getMessage());
		}
	}
	public void executeCommand(String command)
	{
		System.out.println("Executing command: "+command);
		System.out.println("---------------------------------");
		if (command.equals("SHOW"))
		{
			String ac = in.next();
			if (checkExisting(ac))
			{
				System.out.println("Received account number: "+ac);
				System.out.println("Its remaining Balance: "+BankServer.Account.get(ac).toString());
				System.out.println("---------------------------------");
				out.println("success");
				out.flush();
				out.println(BankServer.Account.get(ac).toString());
				out.flush();
			} else
			{
				out.println("fail");
				out.flush();
			}
			return;
		} else if (command.equals("CREATE"))
		{
			String ac = in.next();
			Double ib = Double.parseDouble(in.next());
			if (!checkExisting(ac))
			{
				BankServer.Account.put(ac, ib);
				System.out.println("Create account "+ac+" has successed");
				System.out.println("---------------------------------");
				out.println("success");
				out.flush();
			} else
			{
				out.println("fail");
				out.flush();
			}
		} else if (command.equals("DEPOSIT"))
		{
			String ac = in.next();
			Double amount = Double.parseDouble(in.next());
			if (checkExisting(ac))
			{
				BankServer.Account.put(ac, BankServer.Account.get(ac)+amount);
				System.out.println("Deposit to account "+ac+" has successed");
				System.out.println("---------------------------------");
				out.println("success");
				out.flush();
			} else
			{
				out.println("fail");
				out.flush();
			}
			return;
		} else if (command.equals("WITHDRAW"))
		{
			String ac = in.next();
			Double amount = Double.parseDouble(in.next());
			if (checkExisting(ac)&&checkNegative(ac, amount))
			{
				BankServer.Account.put(ac, BankServer.Account.get(ac)-amount);
				System.out.println("Withdraw from account "+ac+" has successed");
				System.out.println("---------------------------------");
				out.println("success");
				out.flush();
			} else
			{
				out.println("fail");
				out.flush();
			}
			return;
		} else if (command.equals("TRANSFER"))
		{
			String src = in.next();
			String dest = in.next();
			Double amount = Double.parseDouble(in.next());
			if (checkExisting(src)&&checkExisting(dest)&&checkNegative(src,amount))
			{
				BankServer.Account.put(src, BankServer.Account.get(src)-amount);
				BankServer.Account.put(dest, BankServer.Account.get(dest)+amount);
				System.out.println("Transfer from account "+src+" to account "+dest+" has successed");
				System.out.println("---------------------------------");
				out.println("success");
				out.flush();
			} else
			{
				out.println("fail");
				out.flush();
			}
			return;
		}
	}
	public boolean checkExisting(String ac)
	{
		return BankServer.Account.containsKey(ac);
	}
	public boolean checkNegative(String ac, Double amount)
	{
		return (amount <= BankServer.Account.get(ac));
	}
}
