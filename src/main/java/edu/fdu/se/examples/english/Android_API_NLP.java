package edu.fdu.se.examples.english;

/**
 * Created by Administrator on 2018-01-12.
 */
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.util.*;

public class Android_API_NLP {
    List<String> verbList = new ArrayList<String>();
    List<String> nounList = new ArrayList<String>();
    public static void main(String[] args)throws IOException{
        Android_API_NLP android_api_nlp=new Android_API_NLP();
        String text = " I touch, press down and move the screen when I release my finger, the data will be refreshed";
        Annotation document = android_api_nlp.runAllAnnotators(text);
        List<String> phrases = android_api_nlp.parserOutput(document);
        for(String phrase:phrases){
            System.out.println(phrase);
        }
        for(String verb:android_api_nlp.verbList){
            System.out.print(verb+" ");
        }
        System.out.println();
        for(String noun:android_api_nlp.nounList){
            System.out.print(noun+" ");
        }
        System.out.println();
        //example.runAllAnnotators();
        String s1="set data";
        String s2="refresh data";
        Similar sm= new Similar(s1, s2);
        System.out.println(sm.getSimilarity());
    }

    public Annotation runAllAnnotators(String text){
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        //String text = "I want to implement this function that when I slide the screen, the page will be switched"; // Add your text here!
        //String text="Interface definition for a callback to be invoked when a touch event is dispatched to this view.";

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);


        // run all Annotators on this text
        pipeline.annotate(document);
        return document;

        //parserOutput(document);
    }

    public String Lemmatization(String str){
        String lemSentence = "";
        Annotation document = runAllAnnotators(str);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence:sentences){
            for(CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)){
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                lemSentence += lemma;
                lemSentence += " ";
            }
        }
        return lemSentence.trim();
    }

    public List<String> parserOutput(Annotation document){
        List<String> phrases = new ArrayList<String>();
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
            //System.out.println(dependencies.toString(SemanticGraph.OutputFormat.LIST));
            for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
                //compound(Return-2, getExitTransition-1) amod(Returns-2, isCancelled-1)
                if (edge.getRelation().toString().equals("dobj")  ) {
                    String dobjPhrase = edge.getSource().value()+" "+edge.getTarget().value();
                    phrases.add(this.Lemmatization(dobjPhrase));
                    //System.out.println(dobjPhrase);
                }
                if(edge.getRelation().toString().equals("nsubjpass")){
                    String nsubjpassPhrase = edge.getSource().value()+" "+edge.getTarget().value();
                    phrases.add(this.Lemmatization(nsubjpassPhrase));
                    //System.out.println(nsubjpassPhrase);
                }
            }
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                //String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if((pos.equals("VB") || pos.equals("VBD") || pos.equals("VBN") || pos.equals("VBG")
                        || pos.equals("VBP") || pos.equals("VBZ")) && !lemma.equals("be")){
                    verbList.add(lemma);
                }
                if(pos.equals("NN") || pos.equals("NNS") || pos.equals("NNP") || pos.equals("NNPS")){
                    nounList.add(lemma);
                }
                //System.out.println(word+" "+lemma+" "+pos);
            }

            // this is the parse tree of the current sentence
            //Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            //System.out.println("语法树：");
            //System.out.println(tree.toString());

            // this is the Stanford dependency graph of the current sentence
           // SemanticGraph dependencies2 = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            //System.out.println("依存句法：");
            //System.out.println(dependencies2.toString(SemanticGraph.OutputFormat.LIST));
        }

        // This is the coreference link graph
        // Each chain stores a set of mentions that link to each other,
        // along with a method for getting the most representative mention
        // Both sentence and token offsets start at 1!
        Map<Integer, CorefChain> graph =
                document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        return phrases;
    }
}
