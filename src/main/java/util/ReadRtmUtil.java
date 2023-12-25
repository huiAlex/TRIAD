package util;


import document.SimilarityMatrix;
import document.SingleLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadRtmUtil {
    public static SimilarityMatrix createSimilarityMatrix(String path) {
        SimilarityMatrix sims = new SimilarityMatrix();
        if (!path.endsWith(".txt"))
            throw new IllegalArgumentException("not a txt file");

        String contents = FileIOUtil.readFile(path);
        String[] lines = contents.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String[] tokens = lines[i].split(" ");
            SingleLink link = new SingleLink(tokens[0].trim(), tokens[1].trim(), 1.0);
            sims.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
        }
        return sims;
    }

    public static Map<String, List<String>> readMatricMap(String path) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> lines = FileIOUtil.readFileByLine(path);
        for (int i = 0; i < lines.size(); i++) {
            String[] tokens = lines.get(i).split(" ");
            String source = tokens[0].trim();
            String target = tokens[1].trim();
            if (!map.keySet().contains(source))
                map.put(source, new ArrayList<String>());
            map.get(source).add(target);
        }
        return map;
    }

    public static Integer getAvgRtm (String path) {
        Map<String, List<String>> map1 = readMatricMap(path);
        Map<String, Integer> map2 = new HashMap<>();
        for(String artifact : map1.keySet()) {
            int num = map1.get(artifact).size();
            map2.put(artifact, num);
        }
        Map<String, Integer> map3 = SetMapUtil.sortMapByValues(map2);
        int count = 0, index = 0;
        for(String artifact : map3.keySet()) {
            if(index!=0 && index!=map3.keySet().size()-1)
                count += map3.get(artifact);
            index++;
        }

        return count/(map3.keySet().size() - 2);
    }
}
