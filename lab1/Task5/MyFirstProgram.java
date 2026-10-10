import myfirstpackage.*;
class MyFirstClass {
	static void main(String[] s) {
		MySecondClass o = new MySecondClass(0, 0);
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				o.setFirst(i);
				o.setSecond(j);
				System.out.print(o.max());
				System.out.println(" ");
			}
			System.out.println();
		}
	}
}
