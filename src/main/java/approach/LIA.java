package approach;


import document.SimilarityMatrix;
import document.TextDataset;
import experiment.Result;
import experiment.enums.ProjectEnum;
import experiment.project.Project;
import util.FileIOUtil;
import util.SetMapUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LIA {
    private static Project project;
    private static String resultDirPath;
    private static TextDataset textDataset;
    private static Map<String, Result> allResultMap;
    private static Map<String, Result> allVsmResultMap;
    private static Map<String, Result> allLsiResultMap;
    private static Map<String, Result> allMixedResultMap;
    private static Map<String, Double> allResultMAPMap;
    private static Map<String, Double> allResultAPMap;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class projectClass = Class.forName(ProjectEnum.LIBEST.getName()); // select project
        Project project = (Project) projectClass.newInstance();

        LIA lia = new LIA(project);
        System.out.println(project.getProjectName());

        Result bestVsmResult = lia.getBestResult("VSM");
        System.out.println(bestVsmResult.getAlgorithmName() + "| AP: " + bestVsmResult.getAveragePrecisionByRanklist() + " MAP:" + bestVsmResult.getMeanAveragePrecisionByQuery());
        Result recomVsmAPMAP = lia.getRecommendResult("VSM");
        System.out.println("RECOMM-VSM| AP:" + recomVsmAPMAP.getAveragePrecisionByRanklist() + " MAP:" + recomVsmAPMAP.getMeanAveragePrecisionByQuery());

        Result bestLsiResult = lia.getBestResult("LSI");
        System.out.println(bestVsmResult.getAlgorithmName() + "| AP: " + bestLsiResult.getAveragePrecisionByRanklist() + " MAP:" + bestLsiResult.getMeanAveragePrecisionByQuery());
        Result recomLsiAPMAP = lia.getRecommendResult("LSI");
        System.out.println("RECOMM-LSI| AP:" + recomLsiAPMAP.getAveragePrecisionByRanklist() + " MAP:" + recomLsiAPMAP.getMeanAveragePrecisionByQuery());
    }


    public LIA(Project project) {
        this.project = project;
        this.resultDirPath = "result/baseline_result/lia/" + project.getProjectName();
        this.textDataset = new TextDataset(project.getProcessedLevel1ArtifactPath(), project.getProcessedLevel3ArtifactPath(), project.getLevel1ToLevel3MatricPath());
        this.allResultMap = new HashMap<>();
        this.allVsmResultMap = new HashMap<>();
        this.allLsiResultMap = new HashMap<>();
        this.allMixedResultMap = new HashMap<>();
        this.allResultMAPMap = new HashMap<>();
        this.allResultAPMap = new HashMap<>();

        readAllResult();
    }

    public void readAllResult() {
        File resultDir = new File(resultDirPath);
        for (File resultFile : resultDir.listFiles()) {
            List<String> traceList = FileIOUtil.readFile2List(resultFile);
            SimilarityMatrix matrix = new SimilarityMatrix();
            for (String trace : traceList) {
                String[] ary = trace.split(" ");
                String source = ary[0].replace(".txt", "");
                String target = ary[1].replace(".txt", "");
                double score = Double.valueOf(ary[2]);
                matrix.addLink(source, target, score);
            }
            Result result = new Result(matrix, textDataset.getRtm());
            String resultName = resultFile.getName().split("\\.txt")[0];
            result.setAlgorithmName(resultName);
            allResultMap.put(resultName, result);
            allResultMAPMap.put(resultName, result.getMeanAveragePrecisionByQuery());
            allResultAPMap.put(resultName, result.getAveragePrecisionByRanklist());
            readResultBasedIrModel(resultName, result);
        }
    }

    public void readResultBasedIrModel(String resultName, Result result) {
        if (resultName.startsWith("(o")) { // hybrid
            if (resultName.contains("VSM")) {
                String[] ary = resultName.split("VSM");
                if (ary.length == 4) {
                    allVsmResultMap.put(resultName, result);
                } else {
                    allMixedResultMap.put(resultName, result);
                }
            } else {
                String[] ary = resultName.split("LSI");
                if (ary.length == 4) {
                    allLsiResultMap.put(resultName, result);
                } else {

                }
            }

        } else if (resultName.startsWith("(x")) { // transitive
            if (resultName.contains("VSM")) {
                allVsmResultMap.put(resultName, result);
            } else {
                allLsiResultMap.put(resultName, result);
            }
        }
    }

    private Map<String, Result> getTaggedResultMap(String tag) {
        Map<String, Result> resultMap = new HashMap<>();
        switch (tag) {
            case "VSM":
                resultMap = allVsmResultMap;
                break;
            case "LSI":
                resultMap = allLsiResultMap;
                break;
            case "ALL":
                resultMap = allResultMap;
                break;
            case "MIX":
                resultMap = allMixedResultMap;
                break;
        }
        return resultMap;
    }

    private Map<String, Double> getSortedMapApMap(Map<String, Result> resultMap, Map<String, Double> apOrMapMap) {
        Map<String, Double> sortedMap = new HashMap<>();
        for (String resultName : resultMap.keySet()) {
            sortedMap.put(resultName, apOrMapMap.get(resultName));
        }
        sortedMap = SetMapUtil.sortMapByValues(sortedMap);
        return sortedMap;
    }

    public Result getBestResult(String tag) {
        Map<String, Result> resultMap = getTaggedResultMap(tag);

        Result bestResult = new Result();
        Map<String, Double> sortedMap = getSortedMapApMap(resultMap, allResultMAPMap);
        for (String resultName : sortedMap.keySet()) {
            bestResult = allResultMap.get(resultName);
            bestResult.setAlgorithmName("LIA-BEST");
            break;
        }
        return bestResult;
    }

    public Result getRecommendResult(String tag) {
        Map<String, Result> resultMap = getTaggedResultMap(tag);

        Result bestResult = new Result();
        Map<String, Double> sortedMap = getSortedMapApMap(resultMap, allResultMAPMap);
        for (String resultName : sortedMap.keySet()) {
            if (resultName.contains("(SUM)") && resultName.contains("(SUM GLOBAL)") && resultName.contains("(" + tag + " NT)")) {
                bestResult = allResultMap.get(resultName);
                bestResult.setAlgorithmName("LIA-RECOMMEND");
                break;
            }
        }
        return bestResult;
    }

}