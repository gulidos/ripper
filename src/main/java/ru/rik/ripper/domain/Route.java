package ru.rik.ripper.domain;

import java.util.Optional;

public class Route {
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
	
	public static Optional<Route> parse(String[] a) {
		try {
			return Optional.of(new Route(Long.valueOf(a[0]), 
					Long.valueOf(a[1]), 
					Oper.findByMnc(Integer.valueOf(a[4]))));
		} catch (Exception e) {
			return Optional.empty();
		}
		
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
