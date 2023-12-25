import approach.*;
import document.TextDataset;
import experiment.Result;
import experiment.enums.ArtifactLevelEnum;
import experiment.enums.IREnum;
import experiment.enums.ProjectEnum;
import experiment.metric.CliffAnalyze;
import experiment.project.Project;
import model.IR;
import model.IRModel;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

import java.util.*;

public class RunWithBaseline {
    private static Project project;
    private static ArtifactLevelEnum sourceArtifactEnum = ArtifactLevelEnum.getType("level1");
    private static ArtifactLevelEnum targetArtifactEnum = ArtifactLevelEnum.getType("level3");
    private static ArtifactLevelEnum middleArtifactEnum = ArtifactLevelEnum.getType("level2");

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // evaluated parameter set up
        ProjectEnum projectEnum = ProjectEnum.LIBEST; // select project: EBT, DRONOLOGY, WARC, EASYCLINIC, LIBEST
        IREnum irEnum = IREnum.JSD; // select ir model: VSM, LSI, JSD
        run(projectEnum, irEnum);
    }

    private static void run(ProjectEnum projectEnum, IREnum irEnum) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class projectClass = Class.forName(projectEnum.getName());
        Class irModelClass = Class.forName(irEnum.getModel());

        project = (Project) projectClass.newInstance();
        System.setProperty("project", project.getProjectName());
        IRModel irModel = (IRModel) irModelClass.newInstance();

        Map<String, Result> resultMap = new LinkedHashMap<>();

        // IR-ONLY
        IR ir = new IR();
        Result result_ir = ir.compute(project, sourceArtifactEnum, targetArtifactEnum, irModel, new IR_Only());
        resultMap.put(result_ir.getAlgorithmName(), result_ir);

        // TAROT-λ
        TAROT ir_tarotLamda = new TAROT();
        Result result_tarotLamda = ir_tarotLamda.compute(project, sourceArtifactEnum, targetArtifactEnum, middleArtifactEnum, irModel, new IR_Only(), true, false);
        result_tarotLamda.setAlgorithmName("TAROT");
        resultMap.put(result_tarotLamda.getAlgorithmName(), result_tarotLamda);

        //LIA (only for VSM and LSI)
        if (!irModel.getModelName().equals("JSD")) {
            LIA LIA = new LIA(project);
            Result liaBestResult = LIA.getBestResult(irModel.getModelName()); // best result
            Result liaRecommendResult = LIA.getRecommendResult(irModel.getModelName());
            resultMap.put(liaBestResult.getAlgorithmName(), liaBestResult);
            resultMap.put(liaRecommendResult.getAlgorithmName(), liaRecommendResult);
        }

        //COMET(EBT, LibEST)
        if (project.getProjectName().equals("ebt") || project.getProjectName().equals("libest")) {
            // COMET-MAP
            COMET cometMAP = new COMET(project, "MAP");
            Result cometMAPResult = cometMAP.getResult();
            resultMap.put(cometMAPResult.getAlgorithmName(), cometMAPResult);
            // COMET-NUTS
            COMET cometNUTS = new COMET(project, "NUTS");
            Result cometNUTSResult = cometNUTS.getResult();
            resultMap.put(cometNUTSResult.getAlgorithmName(), cometNUTSResult);
        }

        // TRIAD
        TRIAD triad = new TRIAD();
        Result result_triad = triad.compute(project, sourceArtifactEnum, targetArtifactEnum, middleArtifactEnum, irModel, new IR_Only(), true, true);
        result_triad.setAlgorithmName("TRIAD");
        resultMap.put(result_triad.getAlgorithmName(), result_triad);

        Map<String, List<String>> modelMapData = new HashMap<String, List<String>>();
        retrieveData(result_triad, resultMap, modelMapData);

    }

    private static void retrieveData(Result result_my, Map<String, Result> resultMap, Map<String, List<String>> modelMapData) {
        CliffAnalyze cliffAnalyze = new CliffAnalyze();
        TextDataset textDataset = new TextDataset(project.getProcessedLevel1ArtifactPath(), project.getProcessedLevel3ArtifactPath(), project.getLevel1ToLevel3MatricPath());
        for (String resultName : resultMap.keySet()) {
            if (resultName.equals("TRIAD")) {
                double clusterApValue = result_my.getAveragePrecisionByRanklist();
                String clusterAp = String.format("%.2f", clusterApValue * 100);
                double clusterMapValue = result_my.getMeanAveragePrecisionByQuery();
                String clusterMap = String.format("%.2f", clusterMapValue * 100);
                System.out.println(resultName + "  AP:  " + clusterAp + "    MAP:" + clusterMap);
                String clusterPvalueStr = "_";
                String clusterCliff = "_";
                if (!modelMapData.containsKey(resultName)) {
                    modelMapData.put(resultName, new ArrayList<String>());
                }
                List<String> clusterList = modelMapData.get(resultName);
                clusterList.add(clusterAp);
                clusterList.add(clusterMap);
                clusterList.add(clusterPvalueStr);
                clusterList.add(clusterCliff);
            } else {
                Result result = resultMap.get(resultName);
                double ApValue = result.getAveragePrecisionByRanklist();
                String Ap = String.format("%.2f", ApValue * 100);
                double MapValue = result.getMeanAveragePrecisionByQuery();
                String Map = String.format("%.2f", MapValue * 100);
                double pvalue = getPvalue(result_my, result);
                double cliffValue = cliffAnalyze.doCliff(result_my, result, textDataset.getRtm());
                String cliff = String.format("%.2f", cliffValue);

                System.out.println(resultName + "  AP:  " + Ap + "    MAP:" + Map + "    p-value:" + pvalue + "    cliff:" + cliffValue);

                if (!modelMapData.containsKey(resultName)) {
                    modelMapData.put(resultName, new ArrayList<String>());
                }
                List<String> irList = modelMapData.get(resultName);
                irList.add(Ap);
                irList.add(Map);
                irList.add(Double.toString(pvalue));
                irList.add(cliff);
            }
        }
    }

    private static double getPvalue(Result ours, Result compareTo) {
        MannWhitneyUTest mannWhitneyUTest = new MannWhitneyUTest();
        double pValue_fmeasure = mannWhitneyUTest.mannWhitneyUTest(ours.getWilcoxonDataArray_fmeasure(),
                compareTo.getWilcoxonDataArray_fmeasure());
        return pValue_fmeasure;
    }

}
