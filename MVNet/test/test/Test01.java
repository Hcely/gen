package test;

import java.util.stream.Stream;

public class Test01 {

	public static void main(String[] args) throws InterruptedException {
		String[] aaa=new String[100];
		System.out.println(aaa.hashCode());
		Stream<String> a=Stream.of(aaa);
		System.out.println(a.toArray().hashCode());
	}

}
