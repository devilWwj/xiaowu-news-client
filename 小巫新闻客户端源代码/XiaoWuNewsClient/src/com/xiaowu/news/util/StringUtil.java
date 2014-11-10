package com.xiaowu.news.util;

public class StringUtil {
	/**
	 * 从字符串转化为整形
	 * @param str	
	 * @return
	 */
	public static int string2Int(String str) {
		try {
			int value = Integer.valueOf(str);
			return value;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
	}
}	
