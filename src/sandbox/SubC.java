package sandbox;

public class SubC extends SupC {
	
		public SubC() {
			super();
			System.out.println("Subclass Constructor!");
		}
		
		public void md() {
			super.md();
			System.out.println("Subclass Method!");
		}
		
		public void msu() {
			--i;
		}
		
}
