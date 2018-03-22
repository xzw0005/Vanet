package sandbox;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
		
//		String lh = InetAddress.getLocalHost().getHostName();
		InetAddress lh = InetAddress.getLocalHost();
		System.out.println(lh);
//		InetAddress add = InetAddress.getAllByName("pavilion"); //getByAddress("127.0.1.1");
		System.out.println(InetAddress.getByName("pavilion").getAddress());
		
		System.out.println(ifPacketLoss(5));
		
	}

}
