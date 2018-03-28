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
	
	public void msu(int j) {
		++j;
	}
	
	public void mp() {
		this.msu(i);
		System.out.println(i);
	}

	private class nestedC {
		
		private int i = 5;
		
		private int nm(int i) {
			msu(i);
			return i;
		}
		
		
	}
}
