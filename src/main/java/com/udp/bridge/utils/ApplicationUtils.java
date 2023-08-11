package com.udp.bridge.utils;

public class ApplicationUtils {
	
	static int correlId = 10000;
	
    public static String byteArrayToString(byte[] bA) {
        int length = bA.length;

        // Find the length of non-null characters
        int nonNullLength = 0;
        while (nonNullLength < length && bA[nonNullLength] != 0) {
            nonNullLength++;
        }

        // Create a new String from the non-null portion of the byte array
        return new String(bA, 0, nonNullLength);
    }
    
    public static byte[] getCorrelationID(String key) {
    	String value = null;
    	if(key != null) {
    		value = key + correlId;
    	} else {
    		value = String.valueOf(correlId);
    	}
    	correlId++;
    	correlId = (correlId % 100000);
    	if(correlId == 0)
    		correlId = 10000;
        return value.getBytes();
    }
}
