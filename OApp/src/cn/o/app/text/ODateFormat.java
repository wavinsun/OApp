package cn.o.app.text;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class ODateFormat {

	public static Date parse(String text, String pattern) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat();
		Date d = null;
		while (!pattern.isEmpty()) {
			try {
				df.applyPattern(pattern);
				return df.parse(text);
			} catch (Exception e) {
				try {
					String s = df.format(d != null ? d : new Date());
					if (s.length() <= text.length()) {// 尝试解析格式化字符串对应长度
						return df.parse(text.substring(0, s.length()));
					}
				} catch (Exception ex) {

				}
				pattern = getSuggestPattern(pattern);// 缩短格式化字符串
			}
		}
		throw new Exception();
	}

	public static String format(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	// 获取兼容格式
	// yyyy-MM-dd'T'HH:mm:ss.SSS'Z' --> yyyy-MM-dd'T'HH:mm:ss.SSS
	protected static String getSuggestPattern(String pattern) {
		int suggestIndex = 0;
		char lastChar = 0;
		boolean hasQuotes = false;
		for (int i = pattern.length() - 1; i >= 0; i--) {
			char c = pattern.charAt(i);
			if (i == pattern.length() - 1) {
				if (c == '\'') {
					hasQuotes = true;
				}
			} else {
				suggestIndex = i;
				if (hasQuotes) {
					if (c == '\'') {
						break;
					}
				} else {
					if (c != lastChar) {
						suggestIndex++;
						break;
					}
				}
			}
			lastChar = c;
		}
		return pattern.substring(0, suggestIndex);
	}
}
