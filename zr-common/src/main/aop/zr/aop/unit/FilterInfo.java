package zr.aop.unit;

import zr.aop.AopFilter;

public class FilterInfo {
	protected final AopFilter filter;
	protected final String[] authoritys;
	protected Object flag;

	public FilterInfo(AopFilter filter, String[] authoritys) {
		this.filter = filter;
		this.authoritys = authoritys;
	}

	@SuppressWarnings("unchecked")
	public <T> T getFlag() {
		return (T) flag;
	}

	public void setFlag(Object flag) {
		this.flag = flag;
	}

	public AopFilter getFilter() {
		return filter;
	}

	public String[] getAuthoritys() {
		return authoritys;
	}

}
