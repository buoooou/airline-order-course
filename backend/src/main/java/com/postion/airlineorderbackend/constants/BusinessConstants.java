package com.postion.airlineorderbackend.constants;

public interface BusinessConstants {
	// Response status
	public static String SUCCESS = "Success";
	
	// The lock is released after 55s at most
	public static String LOOK_AT_MOST_FOR = "55s";
	// The lock is held for at least 10s
	public static String LOOK_AT_LAST_FOR = "10s";
}
