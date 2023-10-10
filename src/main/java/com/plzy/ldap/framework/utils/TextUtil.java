package com.plzy.ldap.framework.utils;

import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 文本工具类
 */
public class TextUtil{

    /**
     * 字符串 ids 到 List<Long> 的转换
     *
     * @param ids
     * @return
     */
    public static List<Long> ids2LongList(String ids){

        if(ids != null){

            ids = ids.replaceFirst("\\[","").replaceFirst("\\]","");

            String[] idArray = ids.split(",");
            List<Long> idList = new ArrayList<Long>();
            for(String id : idArray){
                idList.add(Long.parseLong(id));
            }
            return idList;
        }
        return Arrays.asList();
    }

    /**
     * 字符串 ids 到 List<Integer> 的转换
     *
     * @param ids
     * @return
     */
    public static List<Integer> ids2IntegerList(String ids){

        if(ids != null){

            ids = ids.replaceFirst("\\[","").replaceFirst("\\]","");

            String[] idArray = ids.split(",");
            List<Integer> idList = new ArrayList<Integer>();
            for(String id : idArray){
                idList.add(Integer.parseInt(id));
            }
            return idList;
        }
        return Arrays.asList();
    }

    public static Map<String, Object> camel2Underline(Map<String, Object> in){

        Map<String, Object> result = new HashMap<>();

        Iterator<Map.Entry<String, Object>> it = in.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Object> curr = it.next();
            result.put(camel2Underline(curr.getKey()), curr.getValue());
        }

        return result;
    }

    /**
     * 驼峰转下划线
     *
     * @param camelStr
     * @return
     */
    public static String camel2Underline(String camelStr) {

        if (StringUtils.isEmpty(camelStr)) {

            return null;
        }

        int len = camelStr.length();
        StringBuilder strb = new StringBuilder(len + len >> 1);
        for (int i = 0; i < len; i++) {

            char c = camelStr.charAt(i);
            if (Character.isUpperCase(c)) {

                strb.append("_");
                strb.append(Character.toLowerCase(c));
            } else {

                strb.append(c);
            }
        }
        return strb.toString();
    }

}
