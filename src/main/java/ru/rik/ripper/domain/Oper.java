package ru.rik.ripper.domain;

public enum Oper {
	RED(0, 1), GREEN(1, 2), YELLOW(2, 99), UNKNOWN(3, 0);
	private final int id;
	private final int mnc;
	
	Oper(int v, int mnc) {
		this.id = v;
		this.mnc = mnc;
	}
	
	public static Oper getInstance(int id) {
		switch(id) {
			case 0: return Oper.RED;
			case 1: return Oper.GREEN;
			case 2: return Oper.YELLOW;
			default: return Oper.UNKNOWN;				
		}
	}
	
	public static Oper findByMnc(int mnc) {
		switch(mnc) {
			case 1: return Oper.RED;
			case 2: return Oper.GREEN;
			case 99: return Oper.YELLOW;
			default: return Oper.UNKNOWN;				
		}
	}
	
	public int getId () {return this.id;}
	public int getMnc() {return mnc;}
}
