package experiment.transitive;

import document.SimilarityMatrix;
import document.TextDataset;
import experiment.enums.ArtifactLevelEnum;
import experiment.project.Project;
import model.IR;
import util.ReadRtmUtil;
import util.SetMapUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class InnerOuterTransitive {


    public static void innerOuterTransitive(Project project, ArtifactLevelEnum sourceLevel,
                                            ArtifactLevelEnum targetLevel, ArtifactLevelEnum middleLevel,
                                            SimilarityMatrix matrix,
                                            Map<String, Map<String, Integer>> sourceBitermNumMap,
                                            Map<String, Map<String, Integer>> targetBitermNumMap,
                                            Map<String, Map<String, Integer>> middleBitermNumMap) {

        TextDataset smTextdataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(middleLevel));
        smTextdataset = smTextdataset.updateTextDataSet(sourceBitermNumMap, middleBitermNumMap);
        TextDataset mtTextdataset = new TextDataset(project.getArtifactPath(middleLevel), project.getArtifactPath(targetLevel));
        mtTextdataset = mtTextdataset.updateTextDataSet(middleBitermNumMap, targetBitermNumMap);
        TextDataset mmTextdataset = new TextDataset(project.getArtifactPath(middleLevel), project.getArtifactPath(middleLevel));
        mmTextdataset = mmTextdataset.updateTextDataSet(middleBitermNumMap, middleBitermNumMap);
        TextDataset ssTextdataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(sourceLevel));
        ssTextdataset = ssTextdataset.updateTextDataSet(sourceBitermNumMap, sourceBitermNumMap);

        SimilarityMatrix smMatrix = IR.computeUnion(smTextdataset);
        SimilarityMatrix mtMatrix = IR.computeUnion(mtTextdataset);
        SimilarityMatrix ssMatrix = IR.computeUnion(ssTextdataset);
        SimilarityMatrix mmMatrix = IR.computeUnion(mmTextdataset);

        for (String sourceArtifact : smMatrix.getSourceTermMatrix().getDocIndex()) {
            // outer first
            Map<String, Double> smMap1 = smMatrix.getLinksForSourceId(sourceArtifact);
            Set<String> middleArtifactOfTopSmSet = SetMapUtil.getOverThresholdsKeySet(smMap1, Threshold.m, Threshold.outerTop);
            for (String middleArtifact : middleArtifactOfTopSmSet) {
                double smScore = smMap1.get(middleArtifact);
                Map<String, Double> mtMap = mtMatrix.getLinksForSourceId(middleArtifact);
                Set<String> targetArtifactOfTopSmSet = SetMapUtil.getOverThresholdsKeySet(mtMap, Threshold.m + 0.1, Threshold.outerTop - 1);
                for (String targetArtifact : targetArtifactOfTopSmSet) {
                    double mtScore = mtMap.get(targetArtifact);
                    double bonus = smScore * mtScore;
                    double newScore = matrix.getScoreForLink(sourceArtifact, targetArtifact) * (1 + bonus);
                    matrix.setScoreForLink(sourceArtifact, targetArtifact, newScore);
                }
                Map<String, Double> mmMap = mmMatrix.getLinksForSourceId(middleArtifact);
                mmMap.remove(middleArtifact, mmMap.get(middleArtifact)); // delete m:m
                Set<String> topMmSet = SetMapUtil.getOverThresholdsKeySet(mmMap, Threshold.m + 0.1, Threshold.innerTop - 1);
                for (String innerMiddleArtifact : topMmSet) {
                    double mmScore = mmMap.get(innerMiddleArtifact);
                    Map<String, Double> mtMap2 = mtMatrix.getLinksForSourceId(middleArtifact);
                    Set<String> topMtSet2 = SetMapUtil.getOverThresholdsKeySet(mtMap2, Threshold.m + 0.2, Threshold.outerTop - 2);
                    for (String targetArtifact : topMtSet2) {
                        double mtScore = mtMap.get(targetArtifact);
                        double bonus2 = smScore * mmScore * mtScore;
                        double newScore = matrix.getScoreForLink(sourceArtifact, targetArtifact) * (1 + bonus2);
                        matrix.setScoreForLink(sourceArtifact, targetArtifact, newScore);
                    }
                }
            }

            Map<String, Double> ssMap = ssMatrix.getLinksForSourceId(sourceArtifact);
            ssMap.remove(sourceArtifact, ssMap.get(sourceArtifact)); // delete s:s
            Set<String> topSsSet = SetMapUtil.getOverThresholdsKeySet(ssMap, Threshold.m, Threshold.innerTop);
            for (String innerSourceArtifact : topSsSet) { // ss inner-transitive
                double ssScore = ssMap.get(innerSourceArtifact);
                Map<String, Double> smMap2 = smMatrix.getLinksForSourceId(sourceArtifact);
                Set<String> topSmSet = SetMapUtil.getOverThresholdsKeySet(smMap2, Threshold.m + 0.1, Threshold.outerTop - 1);
                for (String middleArtifact : topSmSet) { // s:m
                    double innerSmScore = smMatrix.getScoreForLink(innerSourceArtifact, middleArtifact);
                    Map<String, Double> mtMap = mtMatrix.getLinksForSourceId(middleArtifact);
                    Set<String> topMtSet = SetMapUtil.getOverThresholdsKeySet(mtMap, Threshold.m + 0.2, Threshold.outerTop - 2);
                    for (String targetArtifact : topMtSet) { //m:t
                        double mtScore = mtMap.get(targetArtifact);
                        double bonus1 = ssScore * innerSmScore * mtScore;
                        double newScore = matrix.getScoreForLink(sourceArtifact, targetArtifact) * (1 + bonus1);
                        matrix.setScoreForLink(sourceArtifact, targetArtifact, newScore);
                    }
                }
            }
        }
    }


    public static void innerOuterTransitiveNoBiterm(Project project, ArtifactLevelEnum sourceLevel,
                                            ArtifactLevelEnum targetLevel, ArtifactLevelEnum middleLevel,
                                            SimilarityMatrix matrix) {

        TextDataset smTextdataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(middleLevel));
        TextDataset mtTextdataset = new TextDataset(project.getArtifactPath(middleLevel), project.getArtifactPath(targetLevel));
        TextDataset mmTextdataset = new TextDataset(project.getArtifactPath(middleLevel), project.getArtifactPath(middleLevel));
        TextDataset ssTextdataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(sourceLevel));

        SimilarityMatrix smMatrix = IR.computeUnion(smTextdataset);
        SimilarityMatrix mtMatrix = IR.computeUnion(mtTextdataset);
        SimilarityMatrix ssMatrix = IR.computeUnion(ssTextdataset);
        SimilarityMatrix mmMatrix = IR.computeUnion(mmTextdataset);

        for (String sourceArtifact : smMatrix.getSourceTermMatrix().getDocIndex()) {
            // outer first
            Map<String, Double> smMap1 = smMatrix.getLinksForSourceId(sourceArtifact);
            Set<String> middleArtifactOfTopSmSet = SetMapUtil.getOverThresholdsKeySet(smMap1, Threshold.m, Threshold.outerTop);
            for (String middleArtifact : middleArtifactOfTopSmSet) {
                double smScore = smMap1.get(middleArtifact);
                Map<String, Double> mtMap = mtMatrix.getLinksForSourceId(middleArtifact);
                Set<String> targetArtifactOfTopSmSet = SetMapUtil.getOverThresholdsKeySet(mtMap, Threshold.m + 0.1, Threshold.outerTop - 1);
                for (String targetArtifact : targetArtifactOfTopSmSet) {
                    double mtScore = mtMap.get(targetArtifact);
                    double bonus = smScore * mtScore;
                    double newScore = matrix.getScoreForLink(sourceArtifact, targetArtifact) * (1 + bonus);
                    matrix.setScoreForLink(sourceArtifact, targetArtifact, newScore);
                }
                Map<String, Double> mmMap = mmMatrix.getLinksForSourceId(middleArtifact);
                mmMap.remove(middleArtifact, mmMap.get(middleArtifact)); // delete m:m
                Set<String> topMmSet = SetMapUtil.getOverThresholdsKeySet(mmMap, Threshold.m + 0.1, Threshold.innerTop - 1);
                for (String innerMiddleArtifact : topMmSet) {
                    double mmScore = mmMap.get(innerMiddleArtifact);
                    Map<String, Double> mtMap2 = mtMatrix.getLinksForSourceId(middleArtifact);
                    Set<String> topMtSet2 = SetMapUtil.getOverThresholdsKeySet(mtMap2, Threshold.m + 0.2, Threshold.outerTop - 2);
                    for (String targetArtifact : topMtSet2) {
                        double mtScore = mtMap.get(targetArtifact);
                        double bonus2 = smScore * mmScore * mtScore;
                        double newScore = matrix.getScoreForLink(sourceArtifact, targetArtifact) * (1 + bonus2);
                        matrix.setScoreForLink(sourceArtifact, targetArtifact, newScore);
                    }
                }
            }

            Map<String, Double> ssMap = ssMatrix.getLinksForSourceId(sourceArtifact);
            ssMap.remove(sourceArtifact, ssMap.get(sourceArtifact)); // delete s:s
            Set<String> topSsSet = SetMapUtil.getOverThresholdsKeySet(ssMap, Threshold.m, Threshold.innerTop);
            for (String innerSourceArtifact : topSsSet) { // ss inner-transitive
                double ssScore = ssMap.get(innerSourceArtifact);
                Map<String, Double> smMap2 = smMatrix.getLinksForSourceId(sourceArtifact);
                Set<String> topSmSet = SetMapUtil.getOverThresholdsKeySet(smMap2, Threshold.m + 0.1, Threshold.outerTop - 1);
                for (String middleArtifact : topSmSet) { // s:m
                    double innerSmScore = smMatrix.getScoreForLink(innerSourceArtifact, middleArtifact);
                    Map<String, Double> mtMap = mtMatrix.getLinksForSourceId(middleArtifact);
                    Set<String> topMtSet = SetMapUtil.getOverThresholdsKeySet(mtMap, Threshold.m + 0.2, Threshold.outerTop - 2);
                    for (String targetArtifact : topMtSet) { //m:t
                        double mtScore = mtMap.get(targetArtifact);
                        double bonus1 = ssScore * innerSmScore * mtScore;
                        double newScore = matrix.getScoreForLink(sourceArtifact, targetArtifact) * (1 + bonus1);
                        matrix.setScoreForLink(sourceArtifact, targetArtifact, newScore);
                    }
                }
            }
        }
    }

}