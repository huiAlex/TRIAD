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

public class OuterTransitive {

    public static void outerTransitive(Project project, ArtifactLevelEnum sourceLevel,
                                       ArtifactLevelEnum targetLevel, ArtifactLevelEnum middleLevel,
                                       SimilarityMatrix matrix,
                                       Map<String, Map<String, Integer>> sourceBitermNumMap,
                                       Map<String, Map<String, Integer>> targetBitermNumMap,
                                       Map<String, Map<String, Integer>> middleBitermNumMap) {

        TextDataset mtTextDataset = new TextDataset(project.getArtifactPath(middleLevel), project.getArtifactPath(targetLevel));
        mtTextDataset = mtTextDataset.updateTextDataSet(middleBitermNumMap, targetBitermNumMap);
        TextDataset smTextDataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(middleLevel));
        smTextDataset = smTextDataset.updateTextDataSet(sourceBitermNumMap, middleBitermNumMap);

        SimilarityMatrix smMatrix = IR.computeUnion(smTextDataset);
        SimilarityMatrix mtMatrix = IR.computeUnion(mtTextDataset);

        for (String sourceArtifact : smMatrix.getSourceTermMatrix().getDocIndex()) {
            Map<String, Double> smMap = smMatrix.getLinksForSourceId(sourceArtifact);
            Set<String> middleArtifactOfTopSmSet = SetMapUtil.getOverThresholdsKeySet(smMap, Threshold.m, Threshold.outerTop);
            for (String middleArtifact : middleArtifactOfTopSmSet) {
                double smScore = smMap.get(middleArtifact);
                Map<String, Double> mtMap = mtMatrix.getLinksForSourceId(middleArtifact);
                Set<String> targetArtifactOfTopSmSet = SetMapUtil.getOverThresholdsKeySet(mtMap, Threshold.m + 0.1, Threshold.outerTop - 1);
                for (String targetArtifact : targetArtifactOfTopSmSet) {
                    double mtScore = mtMap.get(targetArtifact);
                    double bonus = smScore * mtScore;
                    double newScore = matrix.getScoreForLink(sourceArtifact, targetArtifact) * (1 + bonus);
                    matrix.setScoreForLink(sourceArtifact, targetArtifact, newScore);
                }
            }
        }
    }

    public static void outerTransitiveNoBiterm(Project project, ArtifactLevelEnum sourceLevel,
                                       ArtifactLevelEnum targetLevel, ArtifactLevelEnum middleLevel,
                                       SimilarityMatrix matrix) {

        TextDataset mtTextDataset = new TextDataset(project.getArtifactPath(middleLevel), project.getArtifactPath(targetLevel));
        TextDataset smTextDataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(middleLevel));
        SimilarityMatrix smMatrix = IR.computeUnion(smTextDataset);
        SimilarityMatrix mtMatrix = IR.computeUnion(mtTextDataset);

        for (String sourceArtifact : smMatrix.getSourceTermMatrix().getDocIndex()) {
            Map<String, Double> smMap = smMatrix.getLinksForSourceId(sourceArtifact);
            Set<String> middleArtifactOfTopSmSet = SetMapUtil.getOverThresholdsKeySet(smMap, Threshold.m, Threshold.outerTop);
            for (String middleArtifact : middleArtifactOfTopSmSet) {
                double smScore = smMap.get(middleArtifact);
                Map<String, Double> mtMap = mtMatrix.getLinksForSourceId(middleArtifact);
                Set<String> targetArtifactOfTopSmSet = SetMapUtil.getOverThresholdsKeySet(mtMap, Threshold.m + 0.1, Threshold.outerTop - 1);
                for (String targetArtifact : targetArtifactOfTopSmSet) {
                    double mtScore = mtMap.get(targetArtifact);
                    double bonus = smScore * mtScore;
                    double newScore = matrix.getScoreForLink(sourceArtifact, targetArtifact) * (1 + bonus);
                    matrix.setScoreForLink(sourceArtifact, targetArtifact, newScore);
                }
            }
        }
    }

}
