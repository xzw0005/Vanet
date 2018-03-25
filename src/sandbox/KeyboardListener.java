package sandbox;

import java.util.Scanner;

public class KeyboardListener extends Thread {
	
	private Scanner sc;
	private boolean listening;
	
	public KeyboardListener() {
		listening = false;
	}
	
	@Override
	public void run() {
		while (true) {
			System.out.println("@@ listening... ");
			sc = new Scanner(System.in);
			String s = sc.next();
			System.out.println("You typed " + s + " from terminal");
			
		}
	}
	
}
