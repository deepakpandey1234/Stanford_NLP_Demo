package com.sentiments.analyzers;

import com.common.StatementResult;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.util.*;

public class SentimentsCompute {

    public List<StatementResult> getSentimentsScore(String stringToCompute, String link, String searchKeyword) throws IOException {
        Map<String, Integer> polarityCount = new LinkedHashMap<>();
        List<StatementResult> statementResults = new ArrayList<>();

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(stringToCompute);

        pipeline.annotate(annotation);

        // An Annotation is a Map and you can get and use the various analyses individually.
        // For instance, this gets the parse tree of the first sentence in the text.
        int sentiment = -1;
        List<CoreMap> sentencess = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        //removing first sentence since it might be vague details
        sentencess.remove(0);
        if (sentencess != null && sentencess.size() > 0) {
            StatementResult result = new StatementResult();
            result.setSearchKeyword(searchKeyword);
            result.setValidLinks(link);
            List<String> sentiments = new ArrayList<>();
            Map<String, List<String>> wordPosMap = new LinkedHashMap<>();
            List<String> wordPosList = new ArrayList<>();
            List<String> semanticGraph = new ArrayList<>();
            for (CoreMap sentence : sentencess) {
                Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                sentiments.add(getText(sentiment) + "<-Sentiments : : Text-> " + partText);
                System.out.println(getText(sentiment) + partText);

                if (polarityCount.containsKey(getText(sentiment))) {
                    int temp = polarityCount.get(getText(sentiment));
                    polarityCount.put(getText(sentiment), ++temp);
                } else {
                    polarityCount.put(getText(sentiment), 1);
                }

                //Extracting words and PartOfSpeech
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    wordPosList.add("word: " + word + " pos: " + pos);
                    //System.out.println("word: " + word + " pos: " + pos);
                }
                wordPosMap.put(partText, wordPosList);

                SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
                System.out.println("dependency graph:\n" + dependencies);
                semanticGraph.add(String.valueOf(dependencies));
            }
            result.setWordAndPos(wordPosMap);
            result.setSemanticGraph(semanticGraph);
            result.setSentiment(sentiments);
            statementResults.add(result);
        }
        Comparator comparator2 = new ValueComparator<String, Integer>((HashMap<String, Integer>) polarityCount);
        TreeMap<String, Integer> result2 = new TreeMap<String, Integer>(comparator2);
        result2.putAll(polarityCount);
        System.out.println("--------"+result2.firstEntry().getKey());
        statementResults.get(0).setHighestPolarityOfPage(result2.firstEntry().getKey());
        return statementResults;
    }

    private static String getText(int sentiment) {
        String sen = "";
        if (sentiment > 4 && sentiment < 0) {
            return "Not in range !";
        } else if (sentiment == 1 || sentiment == 0) {
            sen = "NEGATIVE";
        } else if (sentiment == 2) {
            sen = "NEUTRAL";
        } else if (sentiment == 3) {
            sen = "GOOD";
        } else {
            sen = "POSITIVE";
        }
        return sen;
    }

    public static void main(String[] args) throws IOException {
        new SentimentsCompute().getSentimentsScore("hello. I am a happy person. killing in india 1991. This is Oracle.", "dummyURL", "searchKeyWord");
    }
}


// a comparator using generic type
class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {
    HashMap<K, V> map = new HashMap<K, V>();

    public ValueComparator(HashMap<K, V> map) {
        this.map.putAll(map);
    }

    @Override
    public int compare(K s1, K s2) {
        return -map.get(s1).compareTo(map.get(s2));//descending order
    }
}