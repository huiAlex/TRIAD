package model;


import approach.ALGO;
import document.SimilarityMatrix;
import document.SingleLink;
import document.TextDataset;
import experiment.Result;
import experiment.enums.ArtifactLevelEnum;
import experiment.project.Project;

public class IR {

    public static Result compute(Project project, ArtifactLevelEnum source, ArtifactLevelEnum target, IRModel irModel, ALGO algorithm) {
        Result result = null;
        SimilarityMatrix matrix_improve = new SimilarityMatrix();
        TextDataset textDataset = new TextDataset(project.getArtifactPath(source), project.getArtifactPath(target), project.getMatricPath(source, target));

        try {
            SimilarityMatrix similarityMatrix = irModel.Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            SimilarityMatrix matrix = algorithm.improve(similarityMatrix, textDataset);
            for (SingleLink link : matrix.allLinks()) {
                if (similarityMatrix.isLinkAboveThreshold(link.getSourceArtifactId(), link.getTargetArtifactId())) {
                    matrix_improve.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
                } else {
                    matrix_improve.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), 0.0);
                }
            }

            matrix_improve.setSourceTermMatrix(similarityMatrix.getSourceTermMatrix());
            matrix_improve.setTargetTermMatrix((similarityMatrix.getTargetTermMatrix()));
            result = new Result(matrix_improve, textDataset.getRtm());
            result.setAlgorithmName(algorithm.getAlgorithmName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.setModel(irModel.toString());
        return result;
    }

    public static SimilarityMatrix compute(Project project, ArtifactLevelEnum self, IRModel irModel) {
        TextDataset textDataset = new TextDataset(project.getArtifactPath(self), project.getArtifactPath(self));
        SimilarityMatrix similarityMatrix = new SimilarityMatrix();
        try {
            similarityMatrix = irModel.Compute(textDataset.getSourceCollection(), textDataset.getTargetCollection());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return similarityMatrix;
    }

    public static SimilarityMatrix compute(TextDataset textDataset, IRModel irModel) {
        SimilarityMatrix similarityMatrix = new SimilarityMatrix();
        try {
            similarityMatrix = irModel.Compute(textDataset.getSourceCollection(), textDataset.getTargetCollection());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return similarityMatrix;
    }

    public static SimilarityMatrix compute(Project project, ArtifactLevelEnum source, ArtifactLevelEnum target, IRModel irModel) {
        TextDataset textDataset = new TextDataset(project.getArtifactPath(source), project.getArtifactPath(target));
        SimilarityMatrix similarityMatrix = new SimilarityMatrix();
        try {
            similarityMatrix = irModel.Compute(textDataset.getSourceCollection(), textDataset.getTargetCollection());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return similarityMatrix;
    }

    public static SimilarityMatrix computeUnion(Project project, ArtifactLevelEnum source, ArtifactLevelEnum target) {
        TextDataset textDataset = new TextDataset(project.getArtifactPath(source), project.getArtifactPath(target));
        SimilarityMatrix matrix = new SimilarityMatrix();

        try {
            SimilarityMatrix vsmMatrix1 = new VSM().Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            SimilarityMatrix lsiMatrix2 = new LSI().Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            SimilarityMatrix jsdMatrix3 = new JSD().Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            for (SingleLink link : vsmMatrix1.allLinks()) {
                String sourceId = link.getSourceArtifactId();
                String targetId = link.getTargetArtifactId();
                double vsmScore = link.getScore();
                double lsiScore = lsiMatrix2.getScoreForLink(sourceId, targetId);
                double jsdScore = jsdMatrix3.getScoreForLink(sourceId, targetId);

                double avg = (vsmScore + lsiScore + jsdScore) / 3.0;
                matrix.addLink(sourceId, targetId, avg);
            }

            matrix.setSourceTermMatrix(vsmMatrix1.getSourceTermMatrix());
            matrix.setTargetTermMatrix(vsmMatrix1.getTargetTermMatrix());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return matrix;
    }

    public static SimilarityMatrix computeUnion(TextDataset textDataset) {
        SimilarityMatrix matrix = new SimilarityMatrix();

        try {
            SimilarityMatrix vsmMatrix1 = new VSM().Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            SimilarityMatrix lsiMatrix2 = new LSI().Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            SimilarityMatrix jsdMatrix3 = new JSD().Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            for (SingleLink link : vsmMatrix1.allLinks()) {
                String sourceId = link.getSourceArtifactId();
                String targetId = link.getTargetArtifactId();
                double vsmScore = link.getScore();
                double lsiScore = lsiMatrix2.getScoreForLink(sourceId, targetId);
                double jsdScore = jsdMatrix3.getScoreForLink(sourceId, targetId);

                double avg = (vsmScore + lsiScore + jsdScore) / 3.0;
                matrix.addLink(sourceId, targetId, avg);
            }
            matrix.setSourceTermMatrix(vsmMatrix1.getSourceTermMatrix());
            matrix.setTargetTermMatrix(vsmMatrix1.getTargetTermMatrix());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return matrix;
    }
}
