package com.niz.roll;

public class Tag {
public int hash;
public int value;

	public static int getHash(String s){
		int hash = s.hashCode();
		
		return hash;
	}
}
