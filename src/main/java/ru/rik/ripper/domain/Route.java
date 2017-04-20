package ru.rik.ripper.domain;

import java.io.Serializable;

public class Route implements Serializable {
	private static final long serialVersionUID = 1L;
	private final long fromd;
	private final long tod;
	private final Oper oper;
	public final static Route NULL_ROUTE = new Route(0, 0, Oper.UNKNOWN);

	
	public Route(long fromd, long tod, Oper oper) {
		super();
		this.fromd = fromd;
		this.tod = tod;
		this.oper = oper;
	}
	
	public  Route(String[] a) {
		this(Long.valueOf(a[0]), 
				Long.valueOf(a[1]), 
				Oper.findByMnc(Integer.valueOf(a[4])));
	}
	
	public boolean isIn(Long number) {
		return ((fromd <= number) && (number <= tod));
	}

	public long getFromd() {
		return fromd;
	}

	public long getTod() {
		return tod;
	}

	public Oper getOper() {
		return oper;
	}
}
