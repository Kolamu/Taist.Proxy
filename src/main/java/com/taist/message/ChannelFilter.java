package com.taist.message;

import java.util.ArrayList;

public class ChannelFilter {
	private static ArrayList<String> filterList = new ArrayList<String>();
	
	/**
	 * 匹配过滤规则，当给定文档匹配到过滤列表中的某项规则时，该文档将被过滤掉
	 * @param content
	 * @return 文档匹配任一规则时返回TRUE,否则返回FALSE
	 */
	public static boolean filter(String content) {
		if(content == null) {
			return false;
		}
		for(String filter : filterList) {
			if(content.contains(filter) || content.matches(filter)) {
				return true;
			}
		}
		return false;
	}
	
	public static void add(String filter) {
		if(!filterList.contains(filter)) {
			filterList.add(filter);
		}
	}
	
	public static void remove(String filter) {
		filterList.remove(filter);
	}
	
	public static void clear() {
		filterList.clear();
	}
}
