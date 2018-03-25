package sandbox;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import edu.auburn.comp6360.application.Node;
import edu.auburn.comp6360.application.RbaCache.CacheContent;

public class UnitTests {
	
	public static boolean ifPacketLoss(double distance) {
		double jitter = (95 + Math.random() * 10) / 100.;
		double possibility = 90.158730 - (0.00873 * distance * distance) + (0.571428 * distance);
		double succRate = jitter * possibility / 100.; 
		System.out.println(succRate);
		double toss = succRate * ((50 + Math.random() * 50) / 100.);
		if (toss < 0.5)
			return true;
		return false;
	}

	public static void main(String[] args) throws UnknownHostException {
		
		long clock = System.currentTimeMillis();
		System.out.println(clock);
		try {
			Thread.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long t = System.currentTimeMillis();
		System.out.println(t - clock);
		
////		String lh = InetAddress.getLocalHost().getHostName();
//		InetAddress lh = InetAddress.getLocalHost();
//		System.out.println(lh);
////		InetAddress add = InetAddress.getAllByName("pavilion"); //getByAddress("127.0.1.1");
//		System.out.println(InetAddress.getByName("pavilion").getAddress());
		
		Scanner sc;
		String line1 = "Node 2 tux055, 10011 80 120 links 1 3 4";
		String line = "Node 1 pavilion, 10121 34.6 0.0 links ";
		String le = "";
		
		sc = new Scanner(line);
		String s0 = sc.next();
		System.out.println(s0);
		
		String s1 = sc.next();
		System.out.println(s1);
		
		String s2 = sc.next();
		System.out.println(s2);

		String s3 = sc.next();
		System.out.println(s3);
		
		
//		String[] lineArray = line1.split(" ");
//		System.out.println("@@@@Line Length: " + lineArray.length);
//		if (lineArray.length >= 7) {
//			int nodeID = Integer.parseInt(lineArray[1]);
//			String host = lineArray[2].trim();
//			System.out.println(host);
//			host = host.substring(0, host.indexOf(","));
//			int port = Integer.parseInt(lineArray[3]);
//			double x = Double.parseDouble(lineArray[4]);
//			double y = Double.parseDouble(lineArray[5]);
//			if (lineArray.length > 7) {
//				for (int i = 7; i < line.length()-1; i++) 
//					System.out.println(Integer.parseInt(lineArray[i]));			
//			}
//		}		
		
	}

}
