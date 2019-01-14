package game;
/**
 * 
 */

/**
 * @author Vali
 *
 */
public class Pair {
	private int first;
	private int second;

	public Pair(int first, int second) {
		this.setFirst(first);
		this.setSecond(second);
	}
	
	public Pair() {
		this.setFirst(0);
		this.setSecond(0);
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (first != other.first)
			return false;
		if (second != other.second)
			return false;
		return true;
	}
	
	
}
