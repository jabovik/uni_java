class MyFirstClass {
	static void main(String[] s) {
		MySecondClass o = new MySecondClass(0, 0);
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				o.setFirst(i);
				o.setSecond(j);
				System.out.println(o.max());
				System.out.println(" ");
			}
			System.out.println();
		}
	}
}

class MySecondClass {
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
