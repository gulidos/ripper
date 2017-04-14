package ru.rik.ripper.domain;



public class Pair {
	private final String n1; 
	private final String n2;
	private final Direction d;
	public Pair(String n1, String n2, Direction d) {
		super();
		this.n1 = n1;
		this.n2 = n2;
		this.d = d;
	}
	
	public String getN1() {
		return n1;
	}
	
	public String getPartition() {
		return n1.substring(n1.length() - 1);
	}
	
	public String getN2() {
		return n2;
	}
	public Direction getD() {
		return d;
	}
	
	public enum Direction {FROM_ME, TO_ME}; //dsts - from_me, srcs - to_me
}
