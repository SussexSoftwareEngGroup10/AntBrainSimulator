package engine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Random {
	private static final long serialVersionUID = 1L;
	private int s;
	private int i = -1;
	
	/**
	 * @param seed
	 */
	public Random(int seed) {
		if(seed == 0){
			this.s = (int) (Math.random() * Integer.MAX_VALUE + 1);
		}else{
			this.s = seed;
		}
		
		for(int i = 0; i < 3; i++){
			nextS();
		}
	}
	
	/**
	 * @return
	 */
	private int nextS() {
		return this.s = this.s * 22695477 + 1;
	}
	
	/**
	 * @return
	 */
	private int nextX() {
		return mod((nextS() / 65536), 16384);
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private static int mod(int x, int y) {
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
