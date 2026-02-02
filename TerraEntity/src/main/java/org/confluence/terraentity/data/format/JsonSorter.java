package org.confluence.terraentity.data.format;

import java.util.*;

public class JsonSorter {

    /**
     * 按照指定顺序重新排列Map中的键
     * @param map 原始Map
     * @param keyOrder 键的顺序列表
     * @return 重新排序后的LinkedHashMap
     */
    public static <K, V> Map<K, V> reorderMap(Map<K, V> map, List<K> keyOrder) {
        Map<K, V> orderedMap = new LinkedHashMap<>();

        // 首先添加指定顺序的键
        for (K key : keyOrder) {
            if (map.containsKey(key)) {
                orderedMap.put(key, map.get(key));
            }
        }

        // 然后添加其他键（按原始顺序）
        for (K key : map.keySet()) {
            if (!orderedMap.containsKey(key)) {
                orderedMap.put(key, map.get(key));
            }
        }

        return orderedMap;
    }

    /**
     * 按自然顺序排序Map中的键
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortMapByKey(Map<K, V> map) {
        Map<K, V> sortedMap = new TreeMap<>(map);
        return sortedMap;
    }
    /**
     * 按自定义比较器排序Map中的键
     */
    public static <K, V> Map<K, V> sortMapByKey(Map<K, V> map, Comparator<K> comparator) {
        Map<K, V> sortedMap = new TreeMap<>(comparator);
        sortedMap.putAll(map);
        return sortedMap;
    }
}
