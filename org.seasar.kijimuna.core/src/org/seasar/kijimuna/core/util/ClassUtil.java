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
    
    /**
     * FQCNをパッケージ名とクラス名に分割します。<br/>
     * デフォルトパッケージの場合は
     * @param fqcn　分割対象となるFQCN
     * @return 
     * 	String[0]=パッケージ名(デフォルトパッケージの場合は空文字"")
     * 	String[1]=クラス名(fqcnが不正(パッケージ名であった場合)は"")
     */
    public static String[] splitFQCN(String fqcn){
    	String[] result = new String[2];
    	int idx = fqcn.lastIndexOf('.');
    	if(idx == -1){
    		result[0] = "";
    		result[1] = fqcn;
    	}else{
    		String typename = fqcn.substring(idx + 1);
    		String head = String.valueOf(typename.charAt(0));
    		if(head.equals(head.toUpperCase())){
        		result[0] = fqcn.substring(0, idx);
        		result[1] = typename;
    		}else{
        		result[0] = fqcn;
        		result[1] = "";
    		}
    	}
    	return result;
    }
}
