package engine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Flipper {
	private static final long serialVersionUID = 1L;
	private static final int xs = 1000;
	private final int[] s = new int[xs + 4];
	private final int[] x = new int[xs];
	private int i = 0;
	
	/**
	 * @param seed
	 */
	public Flipper(int seed) {
		//s series setup
		this.s[0] = seed;
		for(int i = 1; i < this.s.length; i++){
			this.s[i] = this.s[i - 1] * 22695477 + 1;
		}
		
		//x series setup
		for(int i = 0; i < this.x.length; i++){
			this.x[i] = mod((this.s[i + 4] / 65536), 16384);
		}
	}
	
	/**
	 * @param n
	 * @return
	 */
	public int nextInt(int n) {
		this.i++;
		return mod(this.x[this.i], n);
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private int mod(int x, int y) {
	    int r = x % y;
	    if(r < 0){
	        r += y;
	    }
	    return r;
	}
}
