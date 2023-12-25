package experiment.preprocess.easyclinic;

import edu.stanford.nlp.util.CoreMap;
import experiment.enums.ArtifactLevelEnum;
import experiment.preprocess.util.StanfordNlpUtil;
import experiment.preprocess.util.TextPreprocess;
import experiment.project.EasyClinic;
import util.FileIOUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ExtractEasyClinicBiterm {
    private static EasyClinic easyClinic;
    private static StanfordNlpUtil stanfordNlpUtil = new StanfordNlpUtil();

    private static Map<String, Map<String, Integer>> initLevel1BitermsMap;
    private static Map<String, Map<String, Integer>> initLevel2BitermsMap;
    private static Map<String, Map<String, Integer>> initLevel3BitermsMap;

    private static Map<String, Map<String, Integer>> level1BitermsMap;
    private static Map<String, Map<String, Integer>> level2BitermsMap;
    private static Map<String, Map<String, Integer>> level3BitermsMap;

    private static Set<String> level2And3SharedBitermSet;
    private static Set<String> level1And2SharedBitermSet;

    public static void main(String[] args) {

        ExtractEasyClinicBiterm extractEasyClinicBiterm = new ExtractEasyClinicBiterm();
        extractEasyClinicBiterm.initBitermDirectory();
        initLevel1BitermsMap = extractEasyClinicBiterm.getInitUcBitermMap(easyClinic.getUnprocessedLevel1ArtifactPath());
        initLevel2BitermsMap = extractEasyClinicBiterm.getInitIdBitermMap(easyClinic.getUnprocessedLevel2ArtifactPath());
        initLevel3BitermsMap = extractEasyClinicBiterm.getInitCcBitermMap(easyClinic.getUnprocessedLevel3ArtifactPath());

        level1And2SharedBitermSet = extractEasyClinicBiterm.getSharedBiterms(initLevel1BitermsMap, initLevel2BitermsMap);
        level2And3SharedBitermSet = extractEasyClinicBiterm.getSharedBiterms(initLevel3BitermsMap, initLevel2BitermsMap);
        level1BitermsMap = extractEasyClinicBiterm.getFinalBiterm(initLevel1BitermsMap, level1And2SharedBitermSet);
        level3BitermsMap = extractEasyClinicBiterm.getFinalBiterm(initLevel3BitermsMap, level2And3SharedBitermSet);
        Set<String> set = new HashSet<>();
        set.addAll(level1And2SharedBitermSet);
        set.addAll(level2And3SharedBitermSet);
        level2BitermsMap = extractEasyClinicBiterm.getFinalBiterm(initLevel2BitermsMap, set);

        extractEasyClinicBiterm.outputBiterms(level1BitermsMap, easyClinic.getLevel1BitermPath());
        extractEasyClinicBiterm.outputBiterms(level2BitermsMap, easyClinic.getLevel2BitermPath());
        extractEasyClinicBiterm.outputBiterms(level3BitermsMap, easyClinic.getLevel3BitermPath());
    }

    public ExtractEasyClinicBiterm() {
        this.easyClinic = new EasyClinic();
        this.level1BitermsMap = new HashMap<>();
        this.level2BitermsMap = new HashMap<>();
        this.level3BitermsMap = new HashMap<>();

        this.level1And2SharedBitermSet = new HashSet<>();
        this.level2And3SharedBitermSet = new HashSet<>();
    }

    private void initBitermDirectory() {
        FileIOUtil.initDirectory(easyClinic.getLevel1BitermPath());
        FileIOUtil.initDirectory(easyClinic.getLevel2BitermPath());
        FileIOUtil.initDirectory(easyClinic.getLevel3BitermPath());
    }

    private Map<String, Map<String, Integer>> getInitUcBitermMap(String ucDirPath) {
        Map<String, Map<String, Integer>> map = new HashMap<>();

        System.out.println("extract biterm from UC");
        File ucDirectory = new File(ucDirPath);

        if (ucDirectory.isDirectory()) {
            for (File f : ucDirectory.listFiles()) {
                if (!f.getName().contains(".txt"))
                    continue;
                String artifact = f.getName().split(".txt")[0];
                List<String> ucTxtLineList = FileIOUtil.readFileByLine(f.getPath());
                Map<String, Integer> lineNumMap = getLineOfUC(ucTxtLineList);
                Map<String, Integer> bitermNumMap = new HashMap<>();
                map.put(artifact, bitermNumMap);

                for (int i = 0; i < ucTxtLineList.size(); i++) {
                    String currentLine = ucTxtLineList.get(i);
                    if (i > lineNumMap.get("title") && i < lineNumMap.get("description")) {
                        extractFromSentence(currentLine, bitermNumMap);
                    } else if (i > lineNumMap.get("description") && i < lineNumMap.get("precon")) {
                        extractFromSentence(currentLine, bitermNumMap);
                    } else if (i > lineNumMap.get("precon") && i < lineNumMap.get("postcon")) {
                        extractFromSentence(currentLine, bitermNumMap);
                    } else if (i > lineNumMap.get("postcon") && i < lineNumMap.get("subflow")) {
                        extractFromSentence(currentLine, bitermNumMap);
                    } else if (i > lineNumMap.get("subflow") && i < lineNumMap.get("alterflow")) {
                        extractFromSentence(currentLine, bitermNumMap);
                    } else if (i > lineNumMap.get("alterflow")) {
                        extractFromSentence(currentLine, bitermNumMap);
                    }
                }
            }
        }
        return map;
    }

    private Map<String, Map<String, Integer>> getInitIdBitermMap(String idDirPath) {
        System.out.println("extract biterm from ID");
        Map<String, Map<String, Integer>> map = new HashMap<>();
        File ucDirectory = new File(idDirPath);

        if (ucDirectory.isDirectory()) {
            for (File f : ucDirectory.listFiles()) {
                if (!f.getName().contains(".txt"))
                    continue;
                String artifact = f.getName().split(".txt")[0];
                List<String> ucTxtLineList = FileIOUtil.readFileByLine(f.getPath());
                Map<String, Integer> bitermNumMap = new HashMap<>();
                map.put(artifact, bitermNumMap);

                for (int i = 0; i < ucTxtLineList.size(); i++) {
                    String currentLine = ucTxtLineList.get(i);
                    extractFromSentence(currentLine, bitermNumMap);
                }
            }
        }
        return map;
    }

    private Map<String, Map<String, Integer>> getInitCcBitermMap(String ccDirPath) {
        System.out.println("extract biterm from CC");
        Map<String, Map<String, Integer>> map = new HashMap<>();
        File artifactDir = new File(ccDirPath);

        if (artifactDir.isDirectory()) {
            for (File f : artifactDir.listFiles()) {
                if (!f.getName().contains(".txt"))
                    continue;
                String artifact = f.getName().split(".txt")[0];
                Map<String, Integer> numMap = new HashMap<>();
                map.put(artifact, numMap);
                List<String> ccTxtLineList = FileIOUtil.readFileByLine(f.getPath());
                Map<String, Integer> lineNumMap = getLineOfCC(ccTxtLineList);

                Map<String, Integer> bitermNumMap = new HashMap<>();
                map.put(artifact, bitermNumMap);

                for (int i = 0; i < ccTxtLineList.size(); i++) {
                    String currentLine = ccTxtLineList.get(i);
                    if (!currentLine.equals("")) {
                        if (i == lineNumMap.get("class")) {
                            String clsName = currentLine.split(":")[1];
                            extractFromIdentifier(clsName, bitermNumMap, 2);
                        } else if (i == lineNumMap.get("description")) {
                            String clsDescription = currentLine.split(":")[1];
                            extractFromSentence(clsDescription, bitermNumMap);
                        } else if (i > lineNumMap.get("attribute") && i < lineNumMap.get("method")) {
                            String[] attributeAry = currentLine.split(";");
                            String attrName = attributeAry[0];
                            extractFromIdentifier(attrName, bitermNumMap, 1);
                            String attrDescription = attributeAry[2];
                            extractFromSentence(attrDescription, bitermNumMap);
                        } else if (i > lineNumMap.get("method")) {
                            String[] methodAry = currentLine.split(";");
                            String methodName = methodAry[0];
                            extractFromIdentifier(methodName, bitermNumMap, 2);
                            String methodDescription = methodAry[2];
                            extractFromSentence(methodDescription, bitermNumMap);
                        }
                    }
                }
            }
        }
        return map;
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

    private static void extractFromIdentifier(String identifier, Map<String, Integer> bitermNumMap, int addNum) {
        TextPreprocess textPreprocess = new TextPreprocess(identifier);
        List<String> termList = Arrays.stream(textPreprocess.doJavaFileProcess().split(" ")).collect(Collectors.toList());
        List<List<String>> bitermList = combineTwoTerm(termList);
        bitermList.stream().forEach(tp -> {
            String biterm = getNewTerm(tp);
            bitermNumMap.put(biterm, bitermNumMap.getOrDefault(biterm, 0) + addNum);
        });
    }

    private static void extractFromSentence(String currentLine, Map<String, Integer> bitermNumMap) {
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
                    bitermNumMap.put(biterm1, bitermNumMap.getOrDefault(biterm1, 0) + 1);

                    List<String> list2 = new LinkedList<>();
                    list2.add(term2);
                    list2.add(term1);
                    String biterm2 = getNewTerm(list2);
                    bitermNumMap.put(biterm2, bitermNumMap.getOrDefault(biterm2, 0) + 1);
                }
            }
        });
    }


    private static List<List<String>> combineTwoTerm(List<String> termList) {
        List<List<String>> result = new ArrayList<>();
        if (termList.size() > 1) {
            for (int i = 0; i < termList.size(); i++) {
                for (int j = i + 1; j < termList.size(); j++) {
                    List<String> list = new LinkedList<String>();
                    if (termList.get(i).equals(termList.get(j)))
                        continue;
                    list.add(termList.get(i));
                    list.add(termList.get(j));
                    result.add(list);
                }
            }
        }
        return result;
    }

    private static Map<String, Integer> getLineOfCC(List<String> ccTxtLineList) {
        Map<String, Integer> lineNumMap = new HashMap();
        for (int i = 0; i < ccTxtLineList.size(); i++) {
            String line = ccTxtLineList.get(i);
            if (line.contains("Class:")) {
                lineNumMap.put("class", i);
            } else if (line.contains("Date:")) {
                lineNumMap.put("date", i);
            } else if (line.contains("Version:")) {
                lineNumMap.put("version", i);
            } else if (line.contains("Description:")) {
                lineNumMap.put("description", i);
            } else if (line.contains("Attributes:")) {
                lineNumMap.put("attribute", i);
            } else if (line.contains("Methods:")) {
                lineNumMap.put("method", i);
                return lineNumMap;
            }
        }
        return null;
    }

    private static Map<String, Integer> getLineOfUC(List<String> ucTxtLineList) {
        Map<String, Integer> lineNumMap = new HashMap();
        for (int i = 0; i < ucTxtLineList.size(); i++) {
            String line = ucTxtLineList.get(i);
            if (line.contains("Use case:")) {
                lineNumMap.put("title", i);
            } else if (line.contains("Description:")) {
                lineNumMap.put("description", i);
            } else if (line.contains("Preconditions:")) {
                lineNumMap.put("precon", i);
            } else if (line.contains("Postconditions:")) {
                lineNumMap.put("postcon", i);
            } else if (line.contains("Sub-flows:")) {
                lineNumMap.put("subflow", i);
            } else if (line.contains("Alternative flows:")) {
                lineNumMap.put("alterflow", i);
                return lineNumMap;
            }
        }
        return null;
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