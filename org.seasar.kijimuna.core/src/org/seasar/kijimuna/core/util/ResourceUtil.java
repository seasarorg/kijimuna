package org.seasar.kijimuna.core.util;

/**
 * リソース用のユーティリティクラスです。
 * 
 */
public final class ResourceUtil {

	/**
	 * 拡張子を返します。
	 * 
	 * @param path
	 * @return 拡張子
	 */
	public static String getExtension(String path) {
		int extPos = path.lastIndexOf(".");
		if (extPos >= 0) {
			return path.substring(extPos + 1);
		}
		return null;
	}

	/**
	 * 拡張子を取り除きます。
	 * 
	 * @param path
	 * @return 取り除いた後の結果
	 */
	public static String removeExtension(String path) {
		int extPos = path.lastIndexOf(".");
		if (extPos >= 0) {
			return path.substring(0, extPos);
		}
		return path;
	}

}
