package approach;

import document.*;
import experiment.Result;
import experiment.enums.ArtifactLevelEnum;
import experiment.project.Project;
import model.IRModel;
import util.BitermUtil;
import util.IRUtil;
import util.WeightUtil;

import java.util.Collections;
import java.util.Map;


public class TAROT {


    public Result compute(Project project, ArtifactLevelEnum sourceLevel, ArtifactLevelEnum targetLevel, ArtifactLevelEnum middleLevel, IRModel irModel, ALGO algorithm,
                          boolean lambdaFlag, boolean thetaFlag) {
        SimilarityMatrix similarityMatrix;

        TextDataset textDataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(targetLevel), project.getMatricPath(sourceLevel, targetLevel));

        Map<String, Map<String, Integer>> sourceBitermNumMap = BitermUtil.readBiterm(project.getLevel1BitermPath());
        Map<String, Map<String, Integer>> targetBitermNumMap = BitermUtil.readBiterm(project.getLevel3BitermPath());

        textDataset = textDataset.updateTextDataSet(sourceBitermNumMap, targetBitermNumMap);

        ArtifactsCollection sourceCollection = textDataset.getSourceCollection();
        ArtifactsCollection targetCollection = textDataset.getTargetCollection();
        ArtifactsCollection bothArtifacts = new ArtifactsCollection();
        bothArtifacts.putAll(sourceCollection);
        bothArtifacts.putAll(targetCollection);
        TermDocumentMatrix sourceTermMarix = new TermDocumentMatrix(sourceCollection);
        TermDocumentMatrix targetTermMarix = new TermDocumentMatrix(targetCollection);
        TermDocumentMatrix sourceTargetTermMarix = new TermDocumentMatrix(bothArtifacts);

        TermDocumentMatrix TF = IRUtil.ComputeTF(sourceTargetTermMarix);
        double[] IDF = IRUtil.ComputeIDF(IRUtil.ComputeDF(sourceTargetTermMarix), sourceTargetTermMarix.NumDocs());

        similarityMatrix = irModel.Compute(textDataset.getSourceCollection(), textDataset.getTargetCollection());

        SimilarityMatrix matrix_improve = algorithm.improve(similarityMatrix, textDataset);
        SimilarityMatrix tarot_matrix = new SimilarityMatrix();

        for (int i = 0; i < sourceTermMarix.NumDocs(); i++) {
            LinksList links = new LinksList();
            String sourceArtifact = sourceTermMarix.getDocumentName(i);
            for (int j = 0; j < targetTermMarix.NumDocs(); j++) {
                String targetArtifact = targetTermMarix.getDocumentName(j);
                double lambda = 0.0, theta = 0.0;
                if (lambdaFlag) {
                    if (sourceBitermNumMap.containsKey(sourceArtifact) && targetBitermNumMap.containsKey(targetArtifact)) {
                        lambda = WeightUtil.getLambda(sourceBitermNumMap.get(sourceArtifact).keySet(), targetBitermNumMap.get(targetArtifact).keySet(), TF, IDF);
                        if (sourceArtifact.equals("RE-671")) {
                            if (targetArtifact.equals("AFInfoBox") || targetArtifact.equals("AFEmergencyComponent")) {
                                System.out.println(sourceArtifact + " " + targetArtifact + ": lamda = " + lambda);
                                Map<String, Integer> map1 = sourceBitermNumMap.get(sourceArtifact);
                                System.out.println("   source biterms:");
                                for (String b1 : map1.keySet())
                                    System.out.println(b1 + ":" + map1.get(b1));
                                Map<String, Integer> map2 = targetBitermNumMap.get(targetArtifact);
                                for (String b2 : map2.keySet())
                                    System.out.println(b2 + ":" + map2.get(b2));
                            }
                        }
                    }
                }

                double newScore = 0.0;
                double orginalScore = matrix_improve.getScoreForLink(sourceArtifact, targetArtifact);
                if (lambda > 0) {
                    newScore = orginalScore * (1 + lambda + theta);
                    newScore = Math.min(0.999999, newScore);
                } else {
                    newScore = orginalScore * 0.9;
                }
                links.add(new SingleLink(sourceArtifact, targetArtifact, newScore));
            }
            links.sort(Collections.reverseOrder());
            for (SingleLink link : links) {
                if (matrix_improve.isLinkAboveThreshold(link.getSourceArtifactId(), link.getTargetArtifactId())) {
                    tarot_matrix.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
                } else {
                    tarot_matrix.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), 0.0);
                }
            }
        }

        Result result = new Result(tarot_matrix, textDataset.getRtm());
        result.setAlgorithmName(algorithm.getAlgorithmName());
        result.setModel(irModel.getModelName());

        return result;
    }

//    public static SimilarityMatrix compute(Project project, ArtifactLevelEnum self, ArtifactLevelEnum targetLevel, IRModel irModel) {
//        TextDataset textDataset = new TextDataset(project.getArtifactPath(self), project.getArtifactPath(self));
//        Map<String, Map<String, Integer>> bitermNumMap = BitermUtil.readBiterm(project.getCoreLevelBitermPath(targetLevel, self));
//        textDataset = textDataset.updateTextDataSet(bitermNumMap);
//        SimilarityMatrix similarityMatrix = new SimilarityMatrix();
//        try {
//            similarityMatrix = irModel.Compute(textDataset.getSourceCollection(),
//                    textDataset.getTargetCollection());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return similarityMatrix;
//    }

    public static SimilarityMatrix compute(Project project, ArtifactLevelEnum sourceLevel, ArtifactLevelEnum targetLevel, ArtifactLevelEnum coreLevel, IRModel irModel) {
        TextDataset textDataset = new TextDataset(project.getArtifactPath(sourceLevel), project.getArtifactPath(targetLevel));
        Map<String, Map<String, Integer>> sourceBitermNumMap = BitermUtil.readBiterm(project.getLevel1BitermPath());
        Map<String, Map<String, Integer>> targetBitermNumMap = BitermUtil.readBiterm(project.getLevel3BitermPath());
        textDataset = textDataset.updateTextDataSet(sourceBitermNumMap, targetBitermNumMap);
        ArtifactsCollection sourceCollection = textDataset.getSourceCollection();
        ArtifactsCollection targetCollection = textDataset.getTargetCollection();
        ArtifactsCollection bothArtifacts = new ArtifactsCollection();
        bothArtifacts.putAll(sourceCollection);
        bothArtifacts.putAll(targetCollection);
        TermDocumentMatrix sourceTermMarix = new TermDocumentMatrix(sourceCollection);
        TermDocumentMatrix targetTermMarix = new TermDocumentMatrix(targetCollection);
        TermDocumentMatrix sourceTargetTermMarix = new TermDocumentMatrix(bothArtifacts);
        TermDocumentMatrix TF = IRUtil.ComputeTF(sourceTargetTermMarix);
        double[] IDF = IRUtil.ComputeIDF(IRUtil.ComputeDF(sourceTargetTermMarix), sourceTargetTermMarix.NumDocs());
        SimilarityMatrix similarityMatrix = irModel.Compute(textDataset.getSourceCollection(), textDataset.getTargetCollection());
        SimilarityMatrix matrix_improve = new IR_Only().improve(similarityMatrix, textDataset);
        SimilarityMatrix tarot_matrix = new SimilarityMatrix();

        for (int i = 0; i < sourceTermMarix.NumDocs(); i++) {
            LinksList links = new LinksList();
            String sourceArtifact = sourceTermMarix.getDocumentName(i);
            for (int j = 0; j < targetTermMarix.NumDocs(); j++) {
                String targetArtifact = targetTermMarix.getDocumentName(j);
                double lambda = 0.0, theta = 0.0;
                if (sourceBitermNumMap.containsKey(sourceArtifact) && targetBitermNumMap.containsKey(targetArtifact)) {
                    lambda = WeightUtil.getLambda(sourceBitermNumMap.get(sourceArtifact).keySet(), targetBitermNumMap.get(targetArtifact).keySet(), TF, IDF);
                }
                double newScore = 0.0;
                double orginalScore = matrix_improve.getScoreForLink(sourceArtifact, targetArtifact);
                if (lambda > 0) {
                    newScore = orginalScore * (1 + lambda + theta);
                    if (newScore > 1) newScore = 0.999;
                } else {
                    newScore = orginalScore * 0.9;
                }
                links.add(new SingleLink(sourceArtifact, targetArtifact, newScore));
            }
            links.sort(Collections.reverseOrder());
            for (SingleLink link : links) {
                if (matrix_improve.isLinkAboveThreshold(link.getSourceArtifactId(), link.getTargetArtifactId())) {
                    tarot_matrix.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
                } else {
                    tarot_matrix.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), 0.0);
                }
            }
            tarot_matrix.setSourceTermMatrix(matrix_improve.getSourceTermMatrix());
            tarot_matrix.setTargetTermMatrix(matrix_improve.getTargetTermMatrix());
        }
        return tarot_matrix;
    }

    public static SimilarityMatrix compute(TextDataset updateTextDataSet,
                                           Map<String, Map<String, Integer>> sourceBitermNumMap,
                                           Map<String, Map<String, Integer>> targetBitermNumMap,
                                           IRModel irModel) {
        ArtifactsCollection sourceCollection = updateTextDataSet.getSourceCollection();
        ArtifactsCollection targetCollection = updateTextDataSet.getTargetCollection();
        ArtifactsCollection bothArtifacts = new ArtifactsCollection();
        bothArtifacts.putAll(sourceCollection);
        bothArtifacts.putAll(targetCollection);
        TermDocumentMatrix sourceTermMarix = new TermDocumentMatrix(sourceCollection);
        TermDocumentMatrix targetTermMarix = new TermDocumentMatrix(targetCollection);
        TermDocumentMatrix sourceTargetTermMarix = new TermDocumentMatrix(bothArtifacts);

        TermDocumentMatrix TF = IRUtil.ComputeTF(sourceTargetTermMarix);
        double[] IDF = IRUtil.ComputeIDF(IRUtil.ComputeDF(sourceTargetTermMarix), sourceTargetTermMarix.NumDocs());
        SimilarityMatrix similarityMatrix = irModel.Compute(updateTextDataSet.getSourceCollection(), updateTextDataSet.getTargetCollection());
        SimilarityMatrix matrix_improve = new IR_Only().improve(similarityMatrix, updateTextDataSet);
        SimilarityMatrix tarot_matrix = new SimilarityMatrix();

        for (int i = 0; i < sourceTermMarix.NumDocs(); i++) {
            LinksList links = new LinksList();
            String sourceArtifact = sourceTermMarix.getDocumentName(i);
            for (int j = 0; j < targetTermMarix.NumDocs(); j++) {
                String targetArtifact = targetTermMarix.getDocumentName(j);
                double lambda = 0.0, theta = 0.0;
                if (sourceBitermNumMap.containsKey(sourceArtifact) && targetBitermNumMap.containsKey(targetArtifact)) {
                    lambda = WeightUtil.getLambda(sourceBitermNumMap.get(sourceArtifact).keySet(), targetBitermNumMap.get(targetArtifact).keySet(), TF, IDF);
                }
                double newScore = 0.0;
                double orginalScore = matrix_improve.getScoreForLink(sourceArtifact, targetArtifact);
                if (lambda > 0) {
                    newScore = orginalScore * (1 + lambda + theta);
                    if (newScore > 1) newScore = 0.999;
                } else {
                    newScore = orginalScore * 0.9;
                }
                links.add(new SingleLink(sourceArtifact, targetArtifact, newScore));
            }
            links.sort(Collections.reverseOrder());
            for (SingleLink link : links) {
                if (matrix_improve.isLinkAboveThreshold(link.getSourceArtifactId(), link.getTargetArtifactId())) {
                    tarot_matrix.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
                } else {
                    tarot_matrix.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), 0.0);
                }
            }
            tarot_matrix.setSourceTermMatrix(matrix_improve.getSourceTermMatrix());
            tarot_matrix.setTargetTermMatrix(matrix_improve.getTargetTermMatrix());
        }
        return tarot_matrix;
    }
}
