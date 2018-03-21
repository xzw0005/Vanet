package sandbox;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UnitTests {
	
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
		
		String lh = InetAddress.getLocalHost().getHostAddress();
		System.out.println(lh);
	}

}
