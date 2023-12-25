package experiment.preprocess.util;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class StanfordNlpUtil {

    private static Properties props;
    private static StanfordCoreNLP pipeline;

    public static void main(String[] args) {
        StanfordNlpUtil stanfordNlpUtil = new StanfordNlpUtil();
        System.out.println(stanfordNlpUtil.getTermPair("Operations shall include RTL takeoff hover-in-place and resend command"));
    }

    public StanfordNlpUtil() {
        this.props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");    // Annotators
        this.pipeline = new StanfordCoreNLP(props);
    }

    public String getTermPair(String input) {
        StringBuilder sb = new StringBuilder();
        Annotation document = new Annotation(input);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            Map<String, String> termMap = new HashMap<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String term = token.get(CoreAnnotations.TextAnnotation.class);
                if (term.matches("^[a-zA-Z]+$")) {
                    String processedTerm = doTextProcess(term).trim();
                    if (!processedTerm.equals(""))
                        termMap.put(term, processedTerm);
                }
            }
            // term dependency graph
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
                String relation = edge.getRelation().toString().split(":")[0];

                String[] ary1 = edge.getSource().toString().split("/");
                String[] ary2 = edge.getTarget().toString().split("/");
                String term1 = ary1[0];
                String pos1 = ary1[1];
                String term2 = ary2[0];
                String pos2 = ary2[1];

                if (isLegalPOS(pos1, pos2)) {
                    String processedTerm1 = termMap.get(term1);
                    String processedTerm2 = termMap.get(term2);

                    if (termMap.containsKey(term1) && termMap.containsKey(term2)) {
                        if (processedTerm1.equals(processedTerm2))
                            continue;
                        String tp = "";
                        tp = processedTerm1 + " " + processedTerm2 + ":" + relation;
                        sb.append(tp + "\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    public List<String> getTermPairList(String input) {
        List<String> tpList = new ArrayList<>();
        Annotation document = new Annotation(input);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            Map<String, String> termMap = new HashMap<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String term = token.get(CoreAnnotations.TextAnnotation.class);
                if (term.matches("^[a-zA-Z]+$")) {
                    TextPreprocess textPreprocess = new TextPreprocess(term);
                    String processedTerm = doTextProcess(term).trim();
                    if (!processedTerm.equals(""))
                        termMap.put(term, processedTerm);
                }
            }
            // term dependency graph
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
                String relation = edge.getRelation().toString().split(":")[0];

                String[] ary1 = edge.getSource().toString().split("/");
                String[] ary2 = edge.getTarget().toString().split("/");
                String term1 = ary1[0];
                String pos1 = ary1[1];
                String term2 = ary2[0];
                String pos2 = ary2[1];

                if (isLegalPOS(pos1, pos2)) {
                    String processedTerm1 = termMap.get(term1);
                    String processedTerm2 = termMap.get(term2);

                    if (termMap.containsKey(term1) && termMap.containsKey(term2)) {
                        if (processedTerm1.equals(processedTerm2))
                            continue;
                        String tp = "";
                        tp = processedTerm1 + " " + processedTerm2 + ":" + relation;
                        tpList.add(tp);
                    }
                }
            }
        }
        return tpList;
    }

    private boolean isLegalPOS(String pos1, String pos2) {
        boolean isLegal = false;
        if (pos1.contains("NN") || pos1.contains("JJ") || pos1.contains("VB")) {
            if (pos2.contains("NN") || pos2.contains("JJ") || pos2.contains("VB")) {
                isLegal = true;
            }
        }
        return isLegal;
    }

    public List<CoreMap> splitSentence(String input) {
        Annotation document = new Annotation(input);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        return sentences;
    }

    public String lemmaAnnotation(String tokens) {
        StringBuilder sb = new StringBuilder();
        Annotation document = new Annotation(tokens);
        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                sb.append(lemma + " ");
            }
        }
        return sb.toString();
    }

    public String posSentence(String input) {
        StringBuilder sb = new StringBuilder();

        Annotation document = new Annotation(input);
        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (lemma.matches("^[a-zA-Z]+$")) {
                    if (pos.contains("NN") || pos.contains("VB") || pos.contains("JJ")) {
                        TextPreprocess textPreprocess = new TextPreprocess(lemma);
                        word = textPreprocess.doNlpFileProcess().trim();
                        if (word.length() > 0 && !word.contains(" ")) // UC32: currentDate -> current date
                            sb.append(word.toLowerCase() + ":" + pos + " ");
                    }
                }
            }
        }
        return sb.toString();
    }


    public static String doTextProcess(String str) {
        str = CleanUp.chararctorClean(str);
        str = CamelCase.split(str);
        StanfordNlpUtil stanfordNlpUtil = new StanfordNlpUtil();
        str = stanfordNlpUtil.lemmaAnnotation(str);
        str = CleanUp.lengthFilter(str, 3);
        str = CleanUp.tolowerCase(str);
        str = Stopwords.remover(str, "src/main/resources/stopwords.txt");
        str = Snowball.stemming(str);
        if (str.equals("sent"))
            str = "send";

        return str;
    }

}
