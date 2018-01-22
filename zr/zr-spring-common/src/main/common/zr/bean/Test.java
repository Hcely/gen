package zr.bean;

import zr.util.JsonUtil;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = JsonUtil.obj2JsonStr(new T(10));
		System.out.println(JsonUtil.json2Map(str, String.class));
	}

	public static final class T {
		protected String a;
		protected int i;
		protected T t;

		public T() {
		}

		public T(int i) {
			this.a = "aaa-" + i;
			this.i = i;
			this.t = new T();
			t.a = "aadasd" + i;
		}

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i;
		}

		public T getT() {
			return t;
		}

		public void setT(T t) {
			this.t = t;
		}
	}

}
