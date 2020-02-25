package com.yanzhenxing.smzdmau.spider;

import java.util.List;

/**
 * @author Jason Yan
 * @date 20/04/2019
 */
public class CollectionUtil {

    public static boolean isEmpty(List<? extends Object> list){
        return list == null || list.size() == 0;
    }
}
