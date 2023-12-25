package approach;


import document.SimilarityMatrix;
import document.SingleLink;
import document.TextDataset;
import experiment.Result;
import experiment.enums.ArtifactLevelEnum;
import experiment.project.Project;
import experiment.transitive.InnerOuterTransitive;
import experiment.transitive.OuterTransitive;
import model.IR;
import model.IRModel;
import util.BitermUtil;

import java.util.Map;

public class TRIAD {

    public Result compute(Project project, ArtifactLevelEnum sourceLevel, ArtifactLevelEnum targetLevel, ArtifactLevelEnum middleLevel, IRModel irModel, ALGO algorithm,
                          boolean outerTransitiveFlag, boolean innerTransitiveFlag) {
        TextDataset textDataset1 = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(targetLevel), project.getMatricPath(sourceLevel, targetLevel));
        TextDataset textDataset2 = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(targetLevel), project.getMatricPath(sourceLevel, targetLevel));

        Map<String, Map<String, Integer>> extendSourceBitermNumMap = BitermUtil.readBiterm(project.getLevel1BitermPath());
        Map<String, Map<String, Integer>> sourceBitermNumMap = BitermUtil.readBiterm(project.getLevel1BitermPath());
        Map<String, Map<String, Integer>> extendTargetBitermNumMap = BitermUtil.readBiterm(project.getLevel3BitermPath());
        Map<String, Map<String, Integer>> targetBitermNumMap = BitermUtil.readBiterm(project.getLevel3BitermPath());
        Map<String, Map<String, Integer>> middleBitermNumMap = BitermUtil.readBiterm(project.getLevel2BitermPath());

        BitermUtil.updateBitermMap(extendSourceBitermNumMap, middleBitermNumMap, sourceLevel, middleLevel, project);
        BitermUtil.updateBitermMap(extendTargetBitermNumMap, middleBitermNumMap, targetLevel, middleLevel, project);

        textDataset1 = textDataset1.updateTextDataSet(extendSourceBitermNumMap, targetBitermNumMap);
        textDataset2 = textDataset2.updateTextDataSet(sourceBitermNumMap, extendTargetBitermNumMap);

        SimilarityMatrix extendSourceMatrix = IR.compute(textDataset1, irModel);
        SimilarityMatrix extendTargetMatrix = IR.compute(textDataset2, irModel);

        SimilarityMatrix matrix = new SimilarityMatrix();
        matrix.setSourceTermMatrix(extendSourceMatrix.getSourceTermMatrix());
        matrix.setTargetTermMatrix(extendSourceMatrix.getTargetTermMatrix());
        for (SingleLink link : extendSourceMatrix.allLinks()) {
            String sourceId = link.getSourceArtifactId();
            String targetId = link.getTargetArtifactId();
            double score1 = link.getScore();
            double score2 = extendTargetMatrix.getLinksForSourceId(sourceId).get(targetId);
            double score = 0.5 * (score1 + score2);
            matrix.addLink(sourceId, targetId, score);
        }

        if (innerTransitiveFlag && outerTransitiveFlag) {
            InnerOuterTransitive.innerOuterTransitive(project, sourceLevel, targetLevel, middleLevel, matrix, sourceBitermNumMap, targetBitermNumMap, middleBitermNumMap);
        } else if (outerTransitiveFlag) {
            OuterTransitive.outerTransitive(project, sourceLevel, targetLevel, middleLevel, matrix, sourceBitermNumMap, targetBitermNumMap, middleBitermNumMap);
        }

        Result result = new Result(matrix, textDataset1.getRtm());
        result.setAlgorithmName(algorithm.getAlgorithmName());
        result.setModel(irModel.getModelName());

        return result;
    }

}
