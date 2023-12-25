package experiment.metric;

import document.LinksList;
import document.SimilarityMatrix;
import document.SingleLink;
import experiment.Result;

import java.util.*;


public class CliffAnalyze {
    public double doCliff(Result result_my, Result result_compareTo,
                          SimilarityMatrix rtm) {
        List<Double> ourMethodFmeasureList = new ArrayList<Double>();
        List<Double> compareToFmeasureList = new ArrayList<Double>();
        getFmeasureList(result_my.getMatrix(), rtm, ourMethodFmeasureList);
        getFmeasureList(result_compareTo.getMatrix(), rtm, compareToFmeasureList);
        double cliff = delta(ourMethodFmeasureList, compareToFmeasureList);
        return cliff;
    }

    private void getFmeasureList(SimilarityMatrix matrix, SimilarityMatrix oracle,
                                 List<Double> f1List) {
        int currentLink = 0;
        int correctSoFar = 0;

        LinksList allLinks = matrix.allLinks();
        Collections.sort(allLinks, Collections.reverseOrder());
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
    }

    private double computeF1Measure(double precision, double recall) {
        if (precision == 0 && recall == 0) {
            return 0;
        } else {
            return 2.0 * precision * recall / (precision + recall);
        }
    }

    private double delta(List<Double> ourMethodPrecisionList, List<Double> udPrecisionList) {
        int tGEc = 0;
        int cGEt = 0;
        for (int i = 0; i < ourMethodPrecisionList.size(); i++) {
            double t = ourMethodPrecisionList.get(i);
            for (int j = 0; j < udPrecisionList.size(); j++) {
                double c = udPrecisionList.get(j);
                if (t > c) {
                    tGEc++;
                }

                if (c > t) {
                    cGEt++;
                }
            }
        }
        double result = Math.abs(1.0 * (tGEc - cGEt) / (1.0 * (ourMethodPrecisionList.size() * udPrecisionList.size())));
        return result;
    }


}
