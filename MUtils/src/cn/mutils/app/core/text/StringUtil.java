package cn.mutils.app.core.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.mozilla.universalchardet.UniversalDetector;

import android.text.Html;
import cn.mutils.app.core.io.IOUtil;
import cn.mutils.app.core.json.JsonUtil;
import cn.mutils.app.core.xml.XmlUtil;

/**
 * String utility of framework
 */
public class StringUtil {

	protected static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	public static byte[] md5(byte[] bytes) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			return md5.digest();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * MD5 of string
	 * 
	 * @param text
	 * @return Small letter of MD5
	 */
	public static String md5(String text) {
		try {
			return toHex(md5(text.getBytes("UTF-8")));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * MD5 of string
	 * 
	 * @param text
	 * @return Capital letter of MD5
	 */
	public static String MD5(String text) {
		return md5(text).toUpperCase(Locale.getDefault());
	}

	/**
	 * MD5 of file
	 * 
	 * @param file
	 * @return Small letter of MD5
	 */
	public static String md5(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int bufferIndex = -1;
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			while ((bufferIndex = fis.read(buffer)) != -1) {
				md5.update(buffer, 0, bufferIndex);
			}
			return toHex(md5.digest());
		} catch (Exception e) {
			return "";
		} finally {
			IOUtil.close(fis);
		}
	}

	/**
	 * MD5 of file
	 * 
	 * @param text
	 * @return Capital letter of MD5
	 */
	public static String MD5(File file) {
		return md5(file).toUpperCase(Locale.getDefault());
	}

	/**
	 * Convert byte data to hex string
	 * 
	 * @param data
	 * @return
	 */
	public static String toHex(byte[] data) {
		char[] str = new char[data.length + data.length];
		for (int i = data.length - 1, strIndex = str.length - 1; i >= 0; i--) {
			byte byte4i = data[i];
			str[strIndex--] = hexDigits[byte4i & 0xF];
			str[strIndex--] = hexDigits[byte4i >>> 4 & 0xF];
		}
		return new String(str);
	}

	/**
	 * Convert hex string to byte data
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] toBytes(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0, size = bytes.length; i < size; i++) {
			int index = 2 * i;
			bytes[i] = Integer.valueOf(hex.substring(index, index + 2), 16).byteValue();
		}
		return bytes;
	}

	/**
	 * Convert byte array to string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String get(byte[] bytes) {
		try {
			return new String(bytes, StringUtil.getCharset(bytes));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Convert text file to string
	 * 
	 * @param file
	 * @return
	 */
	public static String get(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return StringUtil.get(fis);
		} catch (Exception e) {
			return null;
		} finally {
			IOUtil.close(fis);
		}
	}

	/**
	 * Put string to text file
	 * 
	 * @param file
	 * @param str
	 * @return
	 */
	public static boolean put(File file, String str) {
		try {
			byte[] bytes = str.getBytes("UTF-8");
			return IOUtil.putBytes(file, bytes);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Convert text stream to string
	 * 
	 * @param is
	 * @return
	 */
	public static String get(InputStream is) {
		return get(IOUtil.getBytes(is));
	}

	/**
	 * Put string to text stream
	 * 
	 * @param os
	 * @param str
	 * @return
	 */
	public static boolean put(OutputStream os, String str) {
		try {
			os.write(str.getBytes("UTF-8"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Detect character set of byte array
	 * 
	 * @param bytes
	 * @return
	 */
	public static String getCharset(byte[] bytes) {
		if (bytes == null) {
			return "UTF-8";
		}
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		String dc = detector.getDetectedCharset();
		String[] charsets = new String[] { "UTF-8", "UTF-16LE", "UTF-16BE", "UTF-32LE", "UTF-32BE", "GBK" };
		for (String c : charsets) {
			if (c.equals(dc)) {
				return dc;
			}
		}
		return "GBK";
	}

	/**
	 * Whether version is stable
	 * 
	 * @param version
	 * @return Return true if last one is even
	 */
	public static boolean isVersionStable(String version) {
		if (version == null) {
			return false;
		}
		String lastCode = version;
		int dotIndex = version.lastIndexOf('.');
		if (dotIndex != -1) {
			lastCode = version.substring(dotIndex + 1);
		}
		try {
			return Integer.parseInt(lastCode) % 2 == 0;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Compare double versions
	 * 
	 * @param v1
	 * @param v2
	 * @return Zero for v1==v2, positive number for v1>v2, negative number for
	 *         v1<v2
	 */
	public static int compareVersion(String v1, String v2) {
		int diff = 0;
		if (v1 == null || v2 == null) {
			return diff;
		}
		String[] vs1 = v1.split("\\.");
		String[] vs2 = v2.split("\\.");
		int size = vs1.length < vs2.length ? vs1.length : vs2.length;
		for (int i = 0; i < size; i++) {
			String vs1i = vs1[i];
			String vs2i = vs2[i];
			if ((diff = vs1i.length() - vs2i.length()) != 0) {
				break;
			}
			if ((diff = vs1i.compareTo(vs2i)) != 0) {
				break;
			}
		}
		// return if it has result,otherwise who has subversion is bigger.
		return diff != 0 ? diff : (vs1.length - vs2.length);
	}

	/**
	 * Print stack trace
	 * 
	 * @param e
	 * @return
	 */
	public static String printStackTrace(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		pw.close();
		return sw.toString();
	}

	/**
	 * UUID
	 * 
	 * @return Small letter of UUID
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * UUID
	 * 
	 * @param text
	 * @return Capital letter of UUID
	 */
	public static String UUID() {
		return uuid().toUpperCase(Locale.getDefault());
	}

	/**
	 * Get Locale
	 * 
	 * @param locale
	 * @return
	 */
	public static Locale getLocale(String locale) {
		if (locale == null) {
			return null;
		}
		String[] parts = locale.split("_");
		if (parts.length == 1) {
			return new Locale(parts[0]);
		} else if (parts.length == 2 || (parts.length == 3 && parts[2].startsWith("#"))) {
			return new Locale(parts[0], parts[1]);
		} else {
			return new Locale(parts[0], parts[1], parts[2]);
		}
	}

	/**
	 * Object toString
	 * 
	 * @param object
	 * @return
	 */
	public static String toString(Object object) {
		return object == null ? null : object.toString();
	}

	/**
	 * Get tag of stack trace for logging
	 * 
	 * @param e
	 * @return
	 */
	public static String getTag(StackTraceElement e) {
		StringBuilder sb = new StringBuilder();
		String name = e.getClassName();
		name = name.substring(name.lastIndexOf(".") + 1);
		sb.append(name);
		sb.append(".");
		sb.append(e.getMethodName());
		sb.append("(L:");
		sb.append(e.getLineNumber());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Get text content of HTML
	 * 
	 * @param html
	 * @return
	 */
	public static String htmlText(String html) {
		try {
			return Html.fromHtml(html).toString();
		} catch (Throwable e) {
			// android.text.Html not found in Java standard environment
			return html;
		}
	}

	/**
	 * Convert object to JSON
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJSON(Object obj) {
		try {
			return JsonUtil.convert(obj);
		} catch (Exception e) {
			return obj.toString();
		}
	}

	/**
	 * Convert object to XML
	 * 
	 * @param obj
	 * @return
	 */
	public static String toXML(Object obj) {
		try {
			return XmlUtil.toString(XmlUtil.convertToDoc(obj), true);
		} catch (Exception e) {
			return obj.toString();
		}
	}

	/**
	 * Convert class to lower case id<br>
	 * "StringUtil"->"string.util"
	 * 
	 * @param cls
	 * @return
	 */
	public static String toLowerCaseId(Class<?> cls) {
		return toLowerCaseId(cls.getName());
	}

	/**
	 * Convert source string to lower case id<br>
	 * "StringUtil"->"string.util"
	 * 
	 * @param src
	 * @return
	 */
	public static String toLowerCaseId(String src) {
		StringBuilder sb = new StringBuilder();
		String lower = src.toLowerCase(Locale.getDefault());
		for (int i = 0, size = lower.length(); i < size; i++) {
			char lowerChar = lower.charAt(i);
			char srcChar = src.charAt(i);
			if (lowerChar != srcChar && i != 0 && i != size - 1) {
				char lowerCharBefore = lower.charAt(i - 1);
				if (lowerCharBefore != '.') {
					if (lowerCharBefore == src.charAt(i - 1) || lower.charAt(i + 1) == src.charAt(i + 1)) {
						sb.append('.');
					}
				}
			}
			sb.append(lowerChar);
		}
		return sb.toString();
	}

	public static boolean isEmpty(String str) {
		return str == null ? true : str.isEmpty();
	}

	public static String zeroPadding(String str, int length) {
		if (str == null) {
			return str;
		}
		int paddingLength = length - str.length();
		if (paddingLength <= 0) {
			return str;
		}
		char[] padding = new char[paddingLength];
		Arrays.fill(padding, '0');
		StringBuilder sb = new StringBuilder();
		sb.append(padding);
		sb.append(str);
		return sb.toString();
	}

}
