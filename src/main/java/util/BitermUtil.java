package util;


import document.SimilarityMatrix;
import document.TextDataset;
import experiment.enums.ArtifactLevelEnum;
import experiment.project.Project;
import experiment.transitive.Threshold;
import model.IR;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitermUtil {

    public static Map<String, Map<String, Integer>> readBiterm(String bitermDirPath) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        File dir = new File(bitermDirPath);

        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (!f.getName().contains(".txt"))
                    continue;
                String artifact = f.getName().split(".txt")[0];
                Map<String, Integer> numMap = new HashMap<>();
                result.put(artifact, numMap);
                List<String> bitermNumList = FileIOUtil.readFileByLine(f.getPath());
                for (String bitermNumStr : bitermNumList) {
                    String[] ary = bitermNumStr.split(":");
                    String biterm = ary[0];
                    Integer num = Integer.valueOf(ary[1]);
                    numMap.put(biterm, num);
                }
            }
        }
        return result;
    }


    public static void updateBitermMap(Map<String, Map<String, Integer>> extendBitermMap,
                                       Map<String, Map<String, Integer>> middleBitermMap,
                                       ArtifactLevelEnum extendLevel,
                                       ArtifactLevelEnum middleLevel,
                                       Project project) {

        TextDataset textDataset = new TextDataset(project.getArtifactPath(extendLevel), project.getArtifactPath(middleLevel));
        textDataset = textDataset.updateTextDataSet(extendBitermMap, middleBitermMap);
        SimilarityMatrix matrix = IR.computeUnion(textDataset);

        for (String extendArtifactId : extendBitermMap.keySet()) {
            Map<String, Double> sortedMap = SetMapUtil.sortMapByValues(matrix.getLinksForSourceId(extendArtifactId));
            int index = 0;
            double max = 0.0;
            Map<String, Integer> artifactBiterms = extendBitermMap.get(extendArtifactId);
            for (String middleArtifact : sortedMap.keySet()) {
                if (index == 0)
                    max = sortedMap.get(middleArtifact);
                if (sortedMap.get(middleArtifact) >= max * Threshold.m && index < Threshold.outerTop) {
                    Map<String, Integer> middleBiterms = middleBitermMap.get(middleArtifact);
                    if (middleBiterms != null) {
                        for (String biterm : middleBiterms.keySet()) {
                            artifactBiterms.put(biterm, artifactBiterms.getOrDefault(biterm, 0) + 1);
                        }
                    }
                } else {
                    break;
                }
                index++;
            }
        }
    }


    public static Map<String, String> getAddString(Map<String, Map<String, Integer>> map) {
        Map<String, String> res = new HashMap<>();
        for (String artifact : map.keySet()) {
            StringBuilder sb = new StringBuilder();
            Map<String, Integer> tmp = map.get(artifact);
            for (String biterm : tmp.keySet()) {
                Integer cnt = tmp.get(biterm);
                for (int i = 0; i < cnt; i++) {
                    sb.append(biterm).append(" ");
                }
            }
            res.put(artifact, sb.toString().trim());
        }
        return res;
    }

}
