package experiment;


import document.LinksList;
import document.SimilarityMatrix;
import document.SingleLink;

import java.util.*;

public class Result {
    public SimilarityMatrix matrix;
    protected SimilarityMatrix oracle;
    private String algorithmName;
    private String log;
    protected String model;
    private List<String> ApAndMapList;
    protected String resultName;
    protected double cutParameter;

    public Result(SimilarityMatrix tarot_matrix, SimilarityMatrix rtm) {
        this.matrix = tarot_matrix;
        this.oracle = rtm;
    }

    public Result() {
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return this.model;
    }

    public SimilarityMatrix getMatrix() {
        return matrix;
    }

    public SimilarityMatrix getOracle() {
        return oracle;
    }

    public void setMatrix(SimilarityMatrix matrix) {
        this.matrix = matrix;
    }

    public void setOracle(SimilarityMatrix oracle) {
        this.oracle = oracle;
    }


    public HashMap<String, Double> getAveragePrecisionByQuery() {
        HashMap<String, Double> sourceAveragePrecision = new LinkedHashMap<>();

        for (String sourceID : oracle.sourceArtifactsIds()) {
            double sumOfPrecisions = 0.0;
            int currentLink = 0;
            int correctSoFar = 0;
            LinksList links = matrix.getLinksAboveThresholdForSourceArtifact(sourceID);
            Collections.sort(links, Collections.reverseOrder());
            for (SingleLink link : links) {
                currentLink++;
                if (oracle.isLinkAboveThreshold(sourceID, link.getTargetArtifactId())) {
                    correctSoFar++;
                    sumOfPrecisions += correctSoFar / (double) currentLink;
                }
            }//for
            sourceAveragePrecision.put(sourceID, sumOfPrecisions / oracle.getCountOfLinksAboveThresholdForSourceArtifact(sourceID));
        }
        return sourceAveragePrecision;
    }

    public double getMeanAveragePrecisionByQuery() {
        HashMap<String, Double> sourceAveragePrecision = getAveragePrecisionByQuery();
        double sum = 0.0;
        for (String sourceArtifact : sourceAveragePrecision.keySet()) {
            sum += sourceAveragePrecision.get(sourceArtifact);
        }
        return (sum / sourceAveragePrecision.size());
    }

    public double getAveragePrecisionByRanklist() {
        double sumOfPrecisions = 0.0;
        int currentLink = 0;
        int correctSoFar = 0;
        LinksList links = matrix.allLinks();
        Collections.sort(links, Collections.reverseOrder());
        for (SingleLink link : links) {
            currentLink++;
            if (oracle.isLinkAboveThreshold(link.getSourceArtifactId(), link.getTargetArtifactId())) {
                correctSoFar++;
                sumOfPrecisions += correctSoFar / (double) currentLink;
            }
        }
        double averagePrecision = sumOfPrecisions / oracle.allLinks().size();
        return averagePrecision;
    }

    public double[] getWilcoxonDataArray_fmeasure() {

        int currentLink = 0;
        int correctSoFar = 0;

        LinksList allLinks = matrix.allLinks();
        Collections.sort(allLinks, Collections.reverseOrder());

        List<Double> f1List = new ArrayList<>();
        for (SingleLink singleLink : allLinks) {
            currentLink++;
            if (oracle.isLinkAboveThreshold(singleLink.getSourceArtifactId(), singleLink.getTargetArtifactId())) {
                correctSoFar++;
                double precision = 1.0 * correctSoFar / currentLink;
                double recall = 1.0 * correctSoFar / oracle.count();
                double f1Measure = computeF1Measure(precision, recall);
                f1List.add(f1Measure);
            }
        }

        double[] result = new double[f1List.size()];
        for (int i = 0; i < f1List.size(); i++) {
            result[i] = f1List.get(i);
        }

        return result;
    }


    private double computeF1Measure(double precision, double recall) {
        return 2.0 * (precision * recall) / (precision + recall);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }


}


