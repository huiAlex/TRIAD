package experiment.preprocess.dronology;

import edu.stanford.nlp.util.CoreMap;
import experiment.enums.ArtifactLevelEnum;
import experiment.enums.CodeEnum;
import experiment.enums.LayerEnum;
import experiment.preprocess.util.StanfordNlpUtil;
import experiment.project.Dronology;
import util.FileIOUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


public class ExtractDronoBiterm {
    private static StanfordNlpUtil stanfordNlpUtil = new StanfordNlpUtil();

    private static Dronology dronology;

    private static Map<String, Map<CodeEnum, Set<String>>> level3CodeLayerBiterms;
    private static Map<String, Map<CodeEnum, Map<String, Integer>>> level3CodeLayerBitermsNumMap;
    private static Map<String, Map<String, Integer>> level3CodeBitermsNumMap;

    private static Set<String> initLevel2BitermsSet;


    private static Map<String, Map<String, Integer>> level1BitermsMap;
    private static Map<String, Map<String, Integer>> level2BitermsMap;
    private static Map<String, Map<String, Integer>> level3BitermsMap;

    public static void main(String[] args) {
        ExtractDronoBiterm extractDronoBiterm = new ExtractDronoBiterm();
        extractDronoBiterm.initBitermDirectory();
        extractDronoBiterm.extractBiterms();
        extractDronoBiterm.outputBiterms(level1BitermsMap, dronology.getLevel1BitermPath());
        extractDronoBiterm.outputBiterms(level2BitermsMap, dronology.getLevel2BitermPath());
        extractDronoBiterm.outputBiterms(level3BitermsMap, dronology.getLevel3BitermPath());
    }

    private void initBitermDirectory() {
        FileIOUtil.initDirectory(dronology.getLevel1BitermPath());
        FileIOUtil.initDirectory(dronology.getLevel2BitermPath());
        FileIOUtil.initDirectory(dronology.getLevel3BitermPath());
    }

    private void extractBiterms() {
        level2BitermsMap = getTextTypeBitermMap(dronology.getUnprocessedLevel2ArtifactPath(), true, initLevel2BitermsSet);
        initLevel2BitermsSet = getBitermSet(level2BitermsMap);

        getBitermFromClsMap(level3CodeLayerBitermsNumMap, level3CodeLayerBiterms);
        level3BitermsMap = getFinalClassBitermByeCoreMap(level3CodeLayerBitermsNumMap, initLevel2BitermsSet);

        level1BitermsMap = getTextTypeBitermMap(dronology.getUnprocessedLevel1ArtifactPath(), false, initLevel2BitermsSet);

        level2BitermsMap = getFinalCoreBitermMap(level2BitermsMap, level1BitermsMap, level3BitermsMap);
    }

    public ExtractDronoBiterm() {
        this.dronology = new Dronology();
        this.level3CodeLayerBiterms = new HashMap<>();
        this.level3CodeLayerBitermsNumMap = new HashMap<>();
        this.level3CodeBitermsNumMap = new HashMap<>();
        this.initLevel2BitermsSet = new HashSet<>();

        this.level1BitermsMap = new HashMap<>();
        this.level2BitermsMap = new HashMap<>();
        this.level3BitermsMap = new HashMap<>();
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

    private static Map<String, Map<String, Integer>> getFinalCoreBitermMap(Map<String, Map<String, Integer>> initialCoreMap,
                                                                           Map<String, Map<String, Integer>> otherMap1,
                                                                           Map<String, Map<String, Integer>> otherMap2) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        for (String source : otherMap1.keySet()) {
            set.addAll(otherMap1.get(source).keySet());
        }
        for (String source : otherMap2.keySet()) {
            set.addAll(otherMap2.get(source).keySet());
        }
        for (String issue : initialCoreMap.keySet()) {
            map.put(issue, new HashMap<>());
            Map<String, Integer> numMap = initialCoreMap.get(issue);
            for (String biterm : numMap.keySet()) {
                if (set.contains(biterm)) {
                    map.get(issue).put(biterm, numMap.get(biterm));
                }
            }
        }
        return map;
    }


    private static Map<String, Map<String, Integer>> getFinalClassBitermByeCoreMap(
            Map<String, Map<CodeEnum, Map<String, Integer>>> codeLayerBitermNumMap,
            Set<String> coreBitermSet) {
        Map<String, Map<String, Integer>> codeBitermsNumMap = new HashMap<>();

        for (String clazz : codeLayerBitermNumMap.keySet()) {
            Map<String, Integer> clsNameBitermMap = new HashMap<>();
            Map<String, Integer> methodNameBitermMap = new HashMap<>();
            Map<String, Integer> invokeNameBitermMap = new HashMap<>();
            Map<String, Integer> commentNameBitermMap = new HashMap<>();
            Map<String, Integer> fieldNameBitermMap = new HashMap<>();
            Map<String, Integer> fieldTypeBitermMap = new HashMap<>();
            Map<String, Integer> paramNameBitermMap = new HashMap<>();
            Map<String, Integer> paramTypeBitermMap = new HashMap<>();

            Map<String, Integer> tmpMap = codeLayerBitermNumMap.get(clazz).get(CodeEnum.CLASS_NAME);
            for (String biterm : tmpMap.keySet()) {
                if (coreBitermSet.contains(biterm))
                    clsNameBitermMap.put(biterm, tmpMap.get(biterm));
            }
            tmpMap = codeLayerBitermNumMap.get(clazz).get(CodeEnum.METHOD_NAME);
            for (String biterm : tmpMap.keySet()) {
                if (coreBitermSet.contains(biterm))
                    methodNameBitermMap.put(biterm, tmpMap.get(biterm));
            }
            tmpMap = codeLayerBitermNumMap.get(clazz).get(CodeEnum.INVOKED_METHOD);
            for (String biterm : tmpMap.keySet()) {
                if (coreBitermSet.contains(biterm))
                    invokeNameBitermMap.put(biterm, tmpMap.get(biterm));
            }
            tmpMap = codeLayerBitermNumMap.get(clazz).get(CodeEnum.COMMENT);
            for (String biterm : tmpMap.keySet()) {
                if (coreBitermSet.contains(biterm))
                    commentNameBitermMap.put(biterm, tmpMap.get(biterm));
            }
            tmpMap = codeLayerBitermNumMap.get(clazz).get(CodeEnum.FIELD_NAME);
            for (String biterm : tmpMap.keySet()) {
                if (coreBitermSet.contains(biterm))
                    fieldNameBitermMap.put(biterm, tmpMap.get(biterm));
            }
            tmpMap = codeLayerBitermNumMap.get(clazz).get(CodeEnum.FIELD_TYPE);
            for (String biterm : tmpMap.keySet()) {
                if (coreBitermSet.contains(biterm))
                    fieldTypeBitermMap.put(biterm, tmpMap.get(biterm));
            }
            tmpMap = codeLayerBitermNumMap.get(clazz).get(CodeEnum.PARAM_NAME);
            for (String biterm : tmpMap.keySet()) {
                if (coreBitermSet.contains(biterm))
                    paramNameBitermMap.put(biterm, tmpMap.get(biterm));
            }
            tmpMap = codeLayerBitermNumMap.get(clazz).get(CodeEnum.PARAM_TYPE);
            for (String biterm : tmpMap.keySet()) {
                if (coreBitermSet.contains(biterm))
                    paramTypeBitermMap.put(biterm, tmpMap.get(biterm));
            }

            Map<String, Integer> temp1 = mergeMap(clsNameBitermMap, methodNameBitermMap);
            Map<String, Integer> temp2 = mergeMap(invokeNameBitermMap, commentNameBitermMap);
            Map<String, Integer> temp3 = mergeMap(fieldNameBitermMap, fieldTypeBitermMap);
            Map<String, Integer> temp4 = mergeMap(paramNameBitermMap, paramTypeBitermMap);

            Map<String, Integer> tmp = mergeMap(temp1, temp2);
            tmp = mergeMap(tmp, mergeMap(temp3, temp4));
            System.out.println(clazz + " " + tmp.size());
            codeBitermsNumMap.put(clazz, tmp);
        }
        return codeBitermsNumMap;
    }

    private static Map<String, Integer> mergeMap(Map<String, Integer> map1, Map<String, Integer> map2) {
        for (String biterm : map2.keySet()) {
            map1.merge(biterm, map2.get(biterm), Integer::sum);
        }
        return map1;
    }


    private static Map<String, Map<String, Integer>> getTextTypeBitermMap(String issueDirPath, Boolean isLevel2, Set<String> initLevel2BitermsSet) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        File issueDir = new File(issueDirPath);

        if (issueDir.isDirectory()) {
            for (File f : issueDir.listFiles()) {
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
                                    if (isLevel2) {
                                        numMap.put(biterm1, numMap.getOrDefault(biterm1, 0) + 1);

                                    } else {
                                        if (initLevel2BitermsSet.contains(biterm1)) {
                                            numMap.put(biterm1, numMap.getOrDefault(biterm1, 0) + 1);
                                        }

                                        List<String> list2 = new LinkedList<>();
                                        list2.add(term2);
                                        list2.add(term1);
                                        String biterm2 = getNewTerm(list2);
                                        if (initLevel2BitermsSet.contains(biterm2)) {
                                            numMap.put(biterm2, numMap.getOrDefault(biterm2, 0) + 1);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
        for (String s : map.keySet()) {
            System.out.println(s + " " + map.get(s).size());
        }
        return map;
    }

    private static Map<String, Integer> getLineOfIssue(List<String> ucTxtLineList) {
        Map<String, Integer> lineNumMap = new HashMap();
        for (int i = 0; i < ucTxtLineList.size(); i++) {
            String line = ucTxtLineList.get(i);
            if (line.contains("SUMMARY:")) {
                lineNumMap.put("summary", i);
            } else if (line.contains("DESCRIPTION:")) {
                lineNumMap.put("description", i);
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

    private static Set<String> getBitermSet(Map<String, Map<String, Integer>> initCoreBitermsMap) {
        Set<String> set = new HashSet<>();
        for (String key : initCoreBitermsMap.keySet()) {
            set.addAll(initCoreBitermsMap.get(key).keySet());
        }
        return set;
    }

    private static Map<String, List<List<String>>> getBitermFromClsMap(Map<String, Map<CodeEnum, Map<String, Integer>>> codeLayerBitermsNumMap,
                                                                       Map<String, Map<CodeEnum, Set<String>>> codeLayerBiterms) {
        Map<String, List<List<String>>> res = new HashMap<>();

        File dir = new File(dronology.getClsNameDirPath());
        for (File file : dir.listFiles()) {
            if (!file.getName().contains(".txt")) continue;
            String cls = file.getName().split(".txt")[0];
            Map<CodeEnum, Map<String, Integer>> m = genCodeLayerBitermNumMap();
            codeLayerBitermsNumMap.put(cls, m);
            Map<CodeEnum, Set<String>> layerBitermMap = genCodeLayerBitermMap();
            codeLayerBiterms.put(cls, layerBitermMap);
        }

        File[] clsNameFiles = new File(dronology.getClsNameDirPath()).listFiles();
        for (File file : clsNameFiles) {
            if (!file.getName().contains(".txt")) continue;
            String cls = file.getName().split(".txt")[0];
            List<List<String>> list = new ArrayList<>();
            Set<String> layerBitermList = codeLayerBiterms.get(cls).get(CodeEnum.CLASS_NAME);
            Map<String, Integer> bitermNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.CLASS_NAME);
            List<String> lineList = FileIOUtil.readFile2List(file);
            for (String line : lineList) {
                List<String> termList = Arrays.stream(line.split(" ")).collect(Collectors.toList());
                List<List<String>> bitermList = combineTwoTerm(termList);
                list.addAll(bitermList);
                bitermList.stream().forEach(tp -> {
                    String biterm = getNewTerm(tp);
                    layerBitermList.add(biterm);
                    bitermNumMap.put(biterm, bitermNumMap.getOrDefault(biterm, 0) + 2);
                });
            }
            res.put(cls, list);
        }

        File[] funcNameFiles = new File(dronology.getMethodNameDirPath()).listFiles();
        for (File file : funcNameFiles) {
            if (!file.getName().contains(".txt")) continue;
            String cls = file.getName().split(".txt")[0];
            List<List<String>> list = new ArrayList<>();
            Set<String> layerBitermSet = codeLayerBiterms.get(cls).get(CodeEnum.METHOD_NAME);
            Map<String, Integer> bitermNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.METHOD_NAME);
            List<String> lineList = FileIOUtil.readFile2List(file);
            for (String line : lineList) {
                List<String> termList = Arrays.stream(line.split(" ")).collect(Collectors.toList());
                List<List<String>> bitermList = combineTwoTerm(termList);
                list.addAll(bitermList);
                bitermList.stream().forEach(bl -> {
                    String biterm = getNewTerm(bl);
                    layerBitermSet.add(biterm);
                    bitermNumMap.put(biterm, bitermNumMap.getOrDefault(biterm, 0) + 2);
                });
            }
            res.get(cls).addAll(list);
        }
        res = expandBiterm(res, codeLayerBiterms, codeLayerBitermsNumMap);
        return res;
    }

    private static Map<CodeEnum, Map<String, Integer>> genCodeLayerBitermNumMap() {
        Map<CodeEnum, Map<String, Integer>> map = new HashMap<>();
        map.put(CodeEnum.CLASS_NAME, new HashMap<String, Integer>());
        map.put(CodeEnum.METHOD_NAME, new HashMap<String, Integer>());
        map.put(CodeEnum.INVOKED_METHOD, new HashMap<String, Integer>());
        map.put(CodeEnum.COMMENT, new HashMap<String, Integer>());
        map.put(CodeEnum.FIELD_NAME, new HashMap<String, Integer>());
        map.put(CodeEnum.FIELD_TYPE, new HashMap<String, Integer>());
        map.put(CodeEnum.PARAM_NAME, new HashMap<String, Integer>());
        map.put(CodeEnum.PARAM_TYPE, new HashMap<String, Integer>());
        return map;
    }

    private static Map<CodeEnum, Set<String>> genCodeLayerBitermMap() {
        Map<CodeEnum, Set<String>> map = new HashMap<>();
        map.put(CodeEnum.CLASS_NAME, new HashSet<>());
        map.put(CodeEnum.COMMENT, new HashSet<>());
        map.put(CodeEnum.INVOKED_METHOD, new HashSet<>());
        map.put(CodeEnum.METHOD_NAME, new HashSet<>());
        map.put(CodeEnum.FIELD_NAME, new HashSet<>());
        map.put(CodeEnum.FIELD_TYPE, new HashSet<>());
        map.put(CodeEnum.PARAM_NAME, new HashSet<>());
        map.put(CodeEnum.PARAM_TYPE, new HashSet<>());
        return map;
    }

    private static Map<String, List<List<String>>> expandBiterm(Map<String, List<List<String>>> map,
                                                                Map<String, Map<CodeEnum, Set<String>>> codeLayerBiterms,
                                                                Map<String, Map<CodeEnum, Map<String, Integer>>> codeLayerBitermsNumMap) {
        File[] invokeFiles = new File(dronology.getInvokeMethodDirPath()).listFiles();
        Map<String, List<String>> clsCallMap = new HashMap<>();
        for (File f : invokeFiles) {
            String cls = f.getName().split("\\.")[0];
            clsCallMap.put(cls, FileIOUtil.readFile2List(f));
        }

        File[] fieldNameFiles = new File(dronology.getFieldNameDirPath()).listFiles();
        Map<String, List<String>> clazzFieldNameMap = new HashMap<>();
        for (File f : fieldNameFiles) {
            String clazz = f.getName().split("\\.")[0];
            clazzFieldNameMap.put(clazz, FileIOUtil.readFile2List(f));
        }

        File[] fieldTypeFiles = new File(dronology.getFieldTypeDirPath()).listFiles();
        Map<String, List<String>> clazzFieldTypeMap = new HashMap<>();
        for (File f : fieldTypeFiles) {
            String clazz = f.getName().split("\\.")[0];
            clazzFieldTypeMap.put(clazz, FileIOUtil.readFile2List(f));
        }

        File[] paramNameFiles = new File(dronology.getParamNameDirPath()).listFiles();
        Map<String, List<String>> clazzParamNameMap = new HashMap<>();
        for (File f : paramNameFiles) {
            String clazz = f.getName().split("\\.")[0];
            clazzParamNameMap.put(clazz, FileIOUtil.readFile2List(f));
        }

        File[] paramTypeFiles = new File(dronology.getParamTypeDirPath()).listFiles();
        Map<String, List<String>> clazzParamTypeMap = new HashMap<>();
        for (File f : paramTypeFiles) {
            String clazz = f.getName().split("\\.")[0];
            clazzParamTypeMap.put(clazz, FileIOUtil.readFile2List(f));
        }

        Map<String, List<List<String>>> commentBitermMap = new HashMap<>();
        File dir = new File(dronology.getCommentDirPath());
        for (File file : dir.listFiles()) {
            if (!file.getName().contains(".txt")) continue;
            String clazz = file.getName().split("\\.txt")[0];
            List<List<String>> l = new ArrayList<>();
            commentBitermMap.put(clazz, l);
            List<String> bitermList = FileIOUtil.readFile2List(file);
            if (bitermList.size() > 0) {
                for (String tp : bitermList) {
                    List<String> list = new LinkedList<>();
                    String relation = tp.split(":")[1];
                    tp = tp.split(":")[0];
                    if (tp.split(" ").length > 2) continue;
                    list.add(tp.split(" ")[0]);
                    list.add(tp.split(" ")[1]);
                    l.add(list);
                }
            }
        }

        int addNum = 1;
        for (String clazz : map.keySet()) {
            List<List<String>> list = map.get(clazz);

            // extract from invoked method name
            List<String> callList = clsCallMap.get(clazz);
            Map<String, Integer> invokeNumMap = codeLayerBitermsNumMap.get(clazz).get(CodeEnum.INVOKED_METHOD);
            Set<String> invokeMethodBitermSet = codeLayerBiterms.get(clazz).get(CodeEnum.INVOKED_METHOD);
            if (callList != null && callList.size() != 0) {
                for (String call : callList) {
                    List<String> termList = Arrays.stream(call.split(" ")).collect(Collectors.toList());
                    List<List<String>> bitermList = combineTwoTerm(termList);
                    list.addAll(bitermList);
                    bitermList.stream().forEach(tp -> {
                        String biterm = getNewTerm(tp);
                        invokeMethodBitermSet.add(biterm);
                        invokeNumMap.put(biterm, invokeNumMap.getOrDefault(biterm, addNum));
                    });
                }
            }

            // extract from field name
            List<String> fieldNameList = clazzFieldNameMap.get(clazz);
            Map<String, Integer> fieldNameNumMap = codeLayerBitermsNumMap.get(clazz).get(CodeEnum.FIELD_NAME);
            Set<String> fieldNameBitermSet = codeLayerBiterms.get(clazz).get(CodeEnum.FIELD_NAME);
            if (fieldNameList != null && fieldNameList.size() != 0) {
                for (String fieldName : fieldNameList) {
                    List<String> termList = Arrays.stream(fieldName.split(" ")).collect(Collectors.toList());
                    List<List<String>> bitermList = combineTwoTerm(termList);
                    list.addAll(bitermList);
                    bitermList.stream().forEach(tp -> {
                        String biterm = getNewTerm(tp);
                        fieldNameBitermSet.add(biterm);
                        fieldNameNumMap.put(biterm, fieldNameNumMap.getOrDefault(biterm, addNum));
                    });
                }
            }

            // extract from field type
            List<String> fieldTypeList = clazzFieldTypeMap.get(clazz);
            Map<String, Integer> fieldTypeNumMap = codeLayerBitermsNumMap.get(clazz).get(CodeEnum.FIELD_TYPE);
            Set<String> fieldTypeBitermSet = codeLayerBiterms.get(clazz).get(CodeEnum.FIELD_TYPE);
            if (fieldTypeList != null && fieldTypeList.size() != 0) {
                for (String fieldType : fieldTypeList) {
                    List<String> termList = Arrays.stream(fieldType.split(" ")).collect(Collectors.toList());
                    List<List<String>> bitermList = combineTwoTerm(termList);
                    list.addAll(bitermList);
                    bitermList.stream().forEach(tp -> {
                        String biterm = getNewTerm(tp);
                        fieldTypeBitermSet.add(biterm);
                        fieldTypeNumMap.put(biterm, fieldTypeNumMap.getOrDefault(biterm, addNum));
                    });
                }
            }

            // extract from param name
            List<String> paramNameList = clazzParamNameMap.get(clazz);
            Map<String, Integer> paramNameNumMap = codeLayerBitermsNumMap.get(clazz).get(CodeEnum.PARAM_NAME);
            Set<String> paramNameTpSet = codeLayerBiterms.get(clazz).get(CodeEnum.PARAM_NAME);
            if (paramNameList != null && paramNameList.size() != 0) {
                for (String paramName : paramNameList) {
                    List<String> termList = Arrays.stream(paramName.split(" ")).collect(Collectors.toList());
                    List<List<String>> bitermList = combineTwoTerm(termList);
                    list.addAll(bitermList);
                    bitermList.stream().forEach(tp -> {
                        String biterm = getNewTerm(tp);
                        paramNameTpSet.add(biterm);
                        paramNameNumMap.put(biterm, paramNameNumMap.getOrDefault(biterm, addNum));
                    });
                }
            }

            // extract from param type
            List<String> paramTypeList = clazzParamTypeMap.get(clazz);
            Map<String, Integer> paramTypeNumMap = codeLayerBitermsNumMap.get(clazz).get(CodeEnum.PARAM_TYPE);
            Set<String> paramTypeBitermSet = codeLayerBiterms.get(clazz).get(CodeEnum.PARAM_TYPE);
            if (paramTypeList != null && paramTypeList.size() != 0) {
                for (String paramType : paramTypeList) {
                    List<String> termList = Arrays.stream(paramType.split(" ")).collect(Collectors.toList());
                    List<List<String>> bitermList = combineTwoTerm(termList);
                    list.addAll(bitermList);
                    bitermList.stream().forEach(tp -> {
                        String biterm = getNewTerm(tp);
                        paramTypeBitermSet.add(biterm);
                        paramTypeNumMap.put(biterm, paramTypeNumMap.getOrDefault(biterm, addNum));
                    });
                }
            }

            // extract from comment
            Map<String, Integer> commentNumMap = codeLayerBitermsNumMap.get(clazz).get(CodeEnum.COMMENT);
            Set<String> commentBitermSet = codeLayerBiterms.get(clazz).get(CodeEnum.COMMENT);
            if (commentBitermMap.get(clazz) != null) {
                List<List<String>> bitermList = commentBitermMap.get(clazz);
                list.addAll(bitermList);
                bitermList.stream().forEach(tp -> {
                    String biterm = getNewTerm(tp);
                    commentBitermSet.add(biterm);
                    commentNumMap.put(biterm, commentNumMap.getOrDefault(biterm, 0) + 1);
                });
            }
        }
        return map;
    }

}
