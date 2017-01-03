package com.example.spellmaus.kanyeipsum;

/**
 * Created by spellmaus on 1/3/2017.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.renderscript.ScriptGroup;
import android.widget.Toast;

import java.text.*;
import java.util.*;
import java.io.*;

public class TextGenerator {
    private String source;
    private BufferedReader br;
    private BreakIterator iterator;
    private int index;
    private int numSentences;
    private int numPara;
    private static boolean debug;
    private InputStream ip;

    private ArrayList<String> sentences;
    private ArrayList<Integer> paragraphs;
    private ArrayList<Integer> usedIndex;

    public TextGenerator(){
        //change in Android, put in assets
    }

    public void debugMode(){
        debug = true;
        for(int i = 0; i<paragraphs.size(); i++){
            System.out.print(paragraphs.get(i) + " ");
        }

        System.out.println("\nNumber sentences: " + getNumSent());
        System.out.println("Number paragraphs: " + getNumPara());
        System.out.println();

    }

    public int getNumPara(){
        return numPara;
    }

    public int getNumSent(){
        return numSentences;
    }

    public String getSentences(int n , boolean caps, boolean pTags){
        String s = "";
        int index = 0;
        String op = "";

        for(int i = 0; i<n; i++){
            do{
                index = (int)(Math.random()*numSentences);
            }while(usedIndex.contains(index));
            //finds an unused index

            s+=sentences.get(index)+" ";
            usedIndex.add(index);
            if(usedIndex.size()==numSentences) //used up all sentences
            {
                usedIndex.clear();
                //giving the most flexibility with no repeats with large n
            }

        }

        usedIndex.clear(); //clear at the end

        if(caps){
            op = (s.trim()).toUpperCase();
        }

        else{
            op = s.trim();
        }

        if(pTags){
            return "<p>" + op + "</p>";
        }
        else{
            return op;
        }
    }

    public String getParagraphs(int n, boolean caps, boolean pTags){
        String s = "";
        int index = 0;
        int nextIndex = 0;
        int rand = 0;

        for(int i = 0; i<n; i++){
            do{
                rand = (int)(Math.random()*numPara);
                index = paragraphs.get(rand);
            }while(usedIndex.contains(index));
            //finds an unused index

            usedIndex.add(index);
            int a = rand+1;
            if(a==paragraphs.size()) //dealing with out of bounds
            {
                nextIndex = sentences.size();
            }
            else{
                nextIndex = paragraphs.get(a); //sentences.get(nextIndex-1) is the last sentence of cur paragraph
            }
            if(pTags)
                s+="<p>";
            for(int j = index; j<nextIndex; j++){
                s+=sentences.get(j)+" ";
            }
            s=s.trim();
            if(pTags)
                s+="</p>";
            s+="\n\n";

            if(usedIndex.size()==numPara) //used up all paragraphs
            {
                usedIndex.clear();
                //giving the most flexibility with no repeats with large n
            }

        }

        usedIndex.clear(); //clear at the end

        if(caps){
            return s.trim().toUpperCase();
        }

        return s.trim();



    }

    public static String censor(String s){ //Strings are immutable
        if(debug){ System.out.println("\nShowing censored words:"); }
        String m = s;
        String origWord = "";
        String[] sb = m.split("\\s+");
        for(String l : sb){
            origWord = l; //capitalization safe
            l = l.toLowerCase();

            if(l.startsWith("fuc")||l.startsWith("nigg")||l.startsWith("bitc")||l.startsWith("shit")){
                //any non-word characters at the beginning or start would affect the word boundary replacing whole words
                if(!Character.isLetter(l.charAt(l.length()-1))){
                    origWord = origWord.substring(0, origWord.length()-1);
                }
                else if(!Character.isLetter(l.charAt(0))){
                    origWord = origWord.substring(1, origWord.length());
                }

                m = m.replaceAll("\\b"+origWord+"\\b", "¡UH!"); //word boundary

                if(debug){
                    System.out.println(">> " + origWord);
                }
            }
        }

        if(debug){  }
        return m;
    }

//    public static void main(String[] args){
//        TextGenerator t = new TextGenerator();
//        t.debugMode();
//        //structure of function: num, caps, para tags
//        String c = t.getSentences(5, false, false);
//        System.out.println(c);
//        System.out.println("\n"+t.getParagraphs(4, false, true));
//
//        System.out.println("\n"+censor("Have you ever asked your bitch for other bitches? Fuck looking cool."));
//        System.out.println(censor(c));
//        System.out.println(censor("Bougie girl, grab my hand, fuck that bitch, she don’t wanna dance."));
//    }
}
