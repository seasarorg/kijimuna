package org.seasar.kijimuna.core.util;





public final class ClassUtil {
	
    /**
     * クラス名の要素を結合します。
     * 
     * @param s1
     * @param s2
     * @return 結合された名前
     */
    public static String concatName(String s1, String s2) {
        if (StringUtils.isEmpty(s1) && StringUtils.isEmpty(s2)) {
            return null;
        }
        if (!StringUtils.isEmpty(s1) && StringUtils.isEmpty(s2)) {
            return s1;
        }
        if (StringUtils.isEmpty(s1) && !StringUtils.isEmpty(s2)) {
            return s2;
        }
        return s1 + '.' + s2;
    }

}
