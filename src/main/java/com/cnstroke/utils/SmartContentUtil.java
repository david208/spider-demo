package com.cnstroke.utils;

import org.apache.commons.lang3.StringUtils;

public class SmartContentUtil {

	public static String getContent(String raw) {
		return StringUtils.substringBetween(raw, ">", "<");
	}
	
	

}
