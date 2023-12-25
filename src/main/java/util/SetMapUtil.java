package util;

import java.util.*;
import java.util.stream.Collectors;

public class SetMapUtil {
    public static Set getIntersect(Set<?> set1, Set<?> set2) {
        Set<Object> set = new HashSet<>();
        set.addAll(set1);
        set.retainAll(set2);
        return set;
    }

    public static Set getUnite(Set<?> set1, Set<?> set2) {
        Set<Object> set = new HashSet<>();
        set.addAll(set1);
        set.addAll(set2);
        return set;
    }

    public static Set getSubtract(Set<?> set1, Set<?> set2) {
        Set<Object> set = new HashSet<>();
        set.addAll(set1);
        set.removeAll(set2);
        return set;
    }

    public static <K extends Comparable, V extends Comparable> Map<K, V> sortMapByValues(Map<K, V> map) {
        HashMap<K, V> finalMap = new LinkedHashMap<K, V>();
        List<Map.Entry<K, V>> list = map.entrySet()
                .stream()
                .sorted((p2, p1) -> p1.getValue().compareTo(p2.getValue()))
                .collect(Collectors.toList());
        list.forEach(ele -> finalMap.put(ele.getKey(), ele.getValue()));
        return finalMap;
    }

    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(
                new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    public static Map<String, List<String>> sortMapListByKey(Map<String, List<String>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, List<String>> sortMap = new TreeMap<String, List<String>>(
                new MapKeyComparator());
        sortMap.putAll(map);
        for (String s : sortMap.keySet())
            Collections.sort(sortMap.get(s));

        return sortMap;
    }

    static class MapKeyComparator implements Comparator<String> {
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }

    public static Set<String> getTopnKeySet(Map<String, Double> map, int topn) {
        int index = 0;
        Set<String> set = new HashSet<>();
        for (String key : map.keySet()) {
            if (index < topn) {
                set.add(key);
            }
            index++;
        }
        return set;
    }

    public static Set<String> getOverThresholdsKeySet(Map<String, Double> map, double scoreThreshold, int numThreshold) {
        Map<String, Double> sortedMap = SetMapUtil.sortMapByValues(map);
        Set<String> set = new HashSet<>();
        double thresholdValue = 0.0;

        int index = 0;
        for (String str : sortedMap.keySet()) {
            index++;
            double value = sortedMap.get(str);
            if (index == 1) {
                set.add(str);
                thresholdValue = value * scoreThreshold;
                continue;
            }
            if (value >= thresholdValue) {
                set.add(str);
            } else {
                break;
            }
            if(index>=numThreshold)
                break;
        }
        return set;
    }

}
