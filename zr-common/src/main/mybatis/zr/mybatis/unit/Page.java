package zr.mybatis.unit;

import java.util.List;

public class Page<T> {
	private List<T> list;
	private int sum;

	public Page() {
	}

	public Page(List<T> list, int sum) {
		this.list = list;
		this.sum = sum;
	}

	public List<T> getList() {
		return list;
	}

	public int getSum() {
		return sum;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	@Override
	public String toString() {
		return "Page [list=" + list + ", sum=" + sum + "]";
	}

}
