package engine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Flipper {
	private static final long serialVersionUID = 1L;
	private int s;
	private int i = -1;
	
	/**
	 * @param seed
	 */
	public Flipper(int seed) {
		this.s = seed;
		for(int i = 0; i < 3; i++){
			nextS();
		}
	}
	
	/**
	 * 
	 */
	private int nextS() {
		return this.s = this.s * 22695477 + 1;
	}
	
	/**
	 * 
	 */
	private int nextX() {
		return mod((nextS() / 65536), 16384);
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private int mod(int x, int y) {
		int r = x % y;
	    if(r < 0){
	        return r + y - 1;
	    }
	    return r;
	}
	
	/**
	 * @param n
	 * @return
	 */
	public int randomInt(int n) {
		this.i++;
		return Math.round(mod(nextX(), n));
	}
}
