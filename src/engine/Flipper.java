package engine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Flipper {
	private static final long serialVersionUID = 1L;
	private static final int xs = 1000;
	private final int[] s = new int[xs + 4];
	private final double[] x = new double[xs];
	private int i = -1;
	
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
	 * @param x
	 * @param y
	 * @return
	 */
	private double mod(double x, double y) {//TODO try without
		double r = x % y;
	    if(r < 0){
	        r += y;
	    }
	    return r;
	}
	
	/**
	 * @param n
	 * @return
	 */
	public int randomInt(int n) {
		this.i++;
		return (int) Math.round(mod(this.x[this.i], n));
	}
}
