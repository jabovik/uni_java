package myfirstpackage;
public class MySecondClass {
	private int first;
	private int second;

	public int getFirst() {
		return first;
	}

	public int getSecond() {
		return second;
	}

	public void setFirst(int val) {
		first = val;
	}

	public void setSecond(int val) {
		second = val;
	}

	public MySecondClass(int f, int s) {
		setFirst(f);
		setSecond(s);
	}

	public int max() {
		return first > second ? first : second;
	}
}