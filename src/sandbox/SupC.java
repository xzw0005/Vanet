package sandbox;

public class SupC {
	
	int i;
	
	public SupC() {
		System.out.println("Super Class Constructor!");
		i = 0;
	}
	
	public void md() {
		System.out.println("Super Class Method!");
	}
	
	public void msu() {
		++i;
	}
	
	public void mp() {
		this.msu();
		System.out.println(i);
	}

	
}
