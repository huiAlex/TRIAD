package experiment.preprocess.warc;

import edu.stanford.nlp.util.CoreMap;
import experiment.enums.ArtifactLevelEnum;
import experiment.preprocess.util.StanfordNlpUtil;
import experiment.project.WARC;
import util.FileIOUtil;

import java.io.File;
import java.util.*;

public class ExtractWarcBiterm {
    private static StanfordNlpUtil stanfordNlpUtil = new StanfordNlpUtil();

    private static WARC warc;

    private static Map<String, Map<String, Integer>> initLevel1BitermsMap;
    private static Map<String, Map<String, Integer>> initLevel2BitermsMap;
    private static Map<String, Map<String, Integer>> initLevel3BitermsMap;

    private static Map<String, Map<String, Integer>> level1BitermsMap;
    private static Map<String, Map<String, Integer>> level2BitermsMap;
    private static Map<String, Map<String, Integer>> level3BitermsMap;

    private static Set<String> level1And2SharedBitermSet;
    private static Set<String> level2And3SharedBitermSet;


    public static void main(String[] args) {
        ExtractWarcBiterm extractWarcBiterm = new ExtractWarcBiterm();
        extractWarcBiterm.initBitermDirectory();
        initLevel1BitermsMap = extractWarcBiterm.getInitBitermMap(warc.getUnprocessedLevel1ArtifactPath());
        initLevel2BitermsMap = extractWarcBiterm.getInitBitermMap(warc.getUnprocessedLevel2ArtifactPath());
        initLevel3BitermsMap = extractWarcBiterm.getInitBitermMap(warc.getUnprocessedLevel3ArtifactPath());

        level1And2SharedBitermSet = extractWarcBiterm.getSharedBiterms(initLevel1BitermsMap, initLevel2BitermsMap);
        level2And3SharedBitermSet = extractWarcBiterm.getSharedBiterms(initLevel3BitermsMap, initLevel2BitermsMap);
        level1BitermsMap = extractWarcBiterm.getFinalBiterm(initLevel1BitermsMap, level1And2SharedBitermSet);
        level3BitermsMap = extractWarcBiterm.getFinalBiterm(initLevel3BitermsMap, level2And3SharedBitermSet);
        Set<String> set = new HashSet<>();
        set.addAll(level1And2SharedBitermSet);
        set.addAll(level2And3SharedBitermSet);
        level2BitermsMap = extractWarcBiterm.getFinalBiterm(initLevel2BitermsMap, set);

        extractWarcBiterm.outputBiterms(level1BitermsMap, warc.getLevel1BitermPath());
        extractWarcBiterm.outputBiterms(level2BitermsMap, warc.getLevel2BitermPath());
        extractWarcBiterm.outputBiterms(level3BitermsMap, warc.getLevel3BitermPath());

    }

    public ExtractWarcBiterm() {
        this.warc = new WARC();
        this.level1BitermsMap = new HashMap<>();
        this.level2BitermsMap = new HashMap<>();
        this.level3BitermsMap = new HashMap<>();

        this.level1And2SharedBitermSet = new HashSet<>();
        this.level2And3SharedBitermSet = new HashSet<>();
    }

    private Set<String> getSharedBiterms(Map<String, Map<String, Integer>> firstBitermsMap,
                                         Map<String, Map<String, Integer>> secondBitermsMap) {
        Set<String> firstBitermSet = new HashSet<>();
        for (String artifact : firstBitermsMap.keySet()) {
            firstBitermSet.addAll(firstBitermsMap.get(artifact).keySet());
        }

        Set<String> secondBitermSet = new HashSet<>();
        for (String artifact : secondBitermsMap.keySet()) {
            secondBitermSet.addAll(secondBitermsMap.get(artifact).keySet());
        }
        firstBitermSet.retainAll(secondBitermSet);

        return firstBitermSet;
    }

    private Map<String, Map<String, Integer>> getFinalBiterm(Map<String, Map<String, Integer>> bitermMap, Set<String> sharedSet) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (String artifact : bitermMap.keySet()) {
            result.put(artifact, new HashMap<>());
            Map<String, Integer> map = bitermMap.get(artifact);
            for (String biterm : map.keySet()) {
                if (sharedSet.contains(biterm)) {
                    result.get(artifact).put(biterm, map.get(biterm));
                }
            }
        }
        return result;
    }

    private void initBitermDirectory() {
        FileIOUtil.initDirectory(warc.getLevel1BitermPath());
        FileIOUtil.initDirectory(warc.getLevel2BitermPath());
        FileIOUtil.initDirectory(warc.getLevel3BitermPath());
    }

    private void outputBiterms(Map<String, Map<String, Integer>> bitermsMap, String outputDir) {
        for (String artifact : bitermsMap.keySet()) {
            Map<String, Integer> map = bitermsMap.get(artifact);
            StringBuilder sb = new StringBuilder();
            for (String biterm : map.keySet()) {
                sb.append(biterm + ":" + map.get(biterm) + "\n");
            }
            FileIOUtil.writeFile(sb.toString(), outputDir + File.separator + artifact + ".txt");
        }
    }

    private Map<String, Map<String, Integer>> getInitBitermMap(String artifactDirPath) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        File artifactDir = new File(artifactDirPath);

        if (artifactDir.isDirectory()) {
            for (File f : artifactDir.listFiles()) {
                if (!f.getName().contains(".txt"))
                    continue;
                String artifact = f.getName().split(".txt")[0];
                Map<String, Integer> numMap = new HashMap<>();
                map.put(artifact, numMap);
                List<String> issueTxtLineList = FileIOUtil.readFileByLine(f.getPath());
                for (int i = 0; i < issueTxtLineList.size(); i++) {
                    String currentLine = issueTxtLineList.get(i);
                    if (!currentLine.equals("")) {
                        List<CoreMap> sentenceList = stanfordNlpUtil.splitSentence(currentLine);
                        sentenceList.stream().forEach(s -> {
                            List<String> tpList = stanfordNlpUtil.getTermPairList(s.toString());
                            for (String tp : tpList) {
                                if (!tp.equals("")) {
                                    String biterm = tp.split(":")[0];
                                    String term1 = biterm.split(" ")[0];
                                    String term2 = biterm.split(" ")[1];

                                    List<String> list1 = new LinkedList<>();
                                    list1.add(term1);
                                    list1.add(term2);
                                    String biterm1 = getNewTerm(list1);
                                    numMap.put(biterm1, numMap.getOrDefault(biterm1, 0) + 1);

                                    List<String> list2 = new LinkedList<>();
                                    list2.add(term2);
                                    list2.add(term1);
                                    String biterm2 = getNewTerm(list2);
                                    numMap.put(biterm2, numMap.getOrDefault(biterm2, 0) + 1);
                                }
                            }
                        });
                    }
                }
            }
        }
        return map;
    }

    private static String getNewTerm(List<String> pair) {
        StringBuilder sb = new StringBuilder();
        sb.append(pair.get(0));
        for (int i = 1; i < pair.size(); i++) {
            char[] chs = pair.get(i).toCharArray();
            chs[0] = (char) (chs[0] - 32);
            sb.append(new String(chs));
        }
        return sb.toString();
    }

}
