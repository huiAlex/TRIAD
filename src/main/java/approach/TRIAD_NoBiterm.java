package approach;


import document.SimilarityMatrix;
import document.TextDataset;
import experiment.Result;
import experiment.enums.ArtifactLevelEnum;
import experiment.project.Project;
import experiment.transitive.InnerOuterTransitive;
import experiment.transitive.OuterTransitive;
import model.IR;
import model.IRModel;

public class TRIAD_NoBiterm {
    public Result compute(Project project, ArtifactLevelEnum sourceLevel, ArtifactLevelEnum targetLevel, ArtifactLevelEnum middleLevel, IRModel irModel, ALGO algorithm,
                          boolean outerTransitiveFlag, boolean innerTransitiveFlag) {

        TextDataset textDataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(targetLevel), project.getMatricPath(sourceLevel, targetLevel));
        SimilarityMatrix matrix = IR.compute(textDataset, irModel);

        if (innerTransitiveFlag && outerTransitiveFlag) {
            InnerOuterTransitive.innerOuterTransitiveNoBiterm(project, sourceLevel, targetLevel, middleLevel, matrix);
        } else if (outerTransitiveFlag) {
            OuterTransitive.outerTransitiveNoBiterm(project, sourceLevel, targetLevel, middleLevel, matrix);
        }

        Result result = new Result(matrix, textDataset.getRtm());
        result.setAlgorithmName(algorithm.getAlgorithmName());
        result.setModel(irModel.getModelName());

        return result;
    }

}
