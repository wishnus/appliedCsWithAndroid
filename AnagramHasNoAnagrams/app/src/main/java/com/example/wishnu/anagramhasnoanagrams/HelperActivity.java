package com.example.wishnu.anagramhasnoanagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by wishnu on 2/3/18.
 */

public class HelperActivity {

    private static final int MIN_NUM_ANAGRAMS = 2;
    private static int WORD_LENGTH= 3;
    private static final int MAX_WORD_LENGTH = 7;

    public HashSet<String> wordSet= new HashSet<>();
    public HashMap<String, ArrayList<String>> lettersToWords = new HashMap<>();
    public HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();//Hashmap to group with number of letters
    private Random random = new Random();

    public HelperActivity(Reader reader){

        BufferedReader fileReader = new BufferedReader(reader);
        String line;
        try {
            while ((line = fileReader.readLine()) != null) {
                String word = line.trim();
                //Log.d("New word",word);
                wordSet.add(word);

                //create a hashmap with the letters as the key(grouping by letters)
                String key = sortLetter(word);
                //add word under the key(group)(arraylist) if it exits
                if(lettersToWords.containsKey(key)){
                    (lettersToWords.get(key)).add(word);//add to arraylist under the key
                }
                //make an arraylist with the word and add it with the key
                else{
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(word);
                    lettersToWords.put(key, temp);
                }

                //create the hashmap to group by number of letters
                //key = word.length
                if(sizeToWords.containsKey(word.length())){
                    (sizeToWords.get(word.length())).add(word);
                }
                else{
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(word);
                    sizeToWords.put(word.length(),temp);
                }
            }

        }
        catch (IOException e){
            Log.d("error","IOException");
        }

        //create a hashmap with the letters as the key(grouping by letters)

    }

    //sort the letters of a string
    public String sortLetter(String s){
        char charArr[] = s.toCharArray();
        Arrays.sort(charArr);
        String sortedString=new String(charArr);
        return sortedString;
    }

    public boolean isGoodWord(String question, String answer){
        if( (wordSet.contains(answer)==true) && (answer.contains(question)==false) )
            return true;
        else
            return false;
    }

    public List<String> getGoodAnagrams(String targetWord) {
        String key = sortLetter(targetWord);
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> allAnagrams = new ArrayList<>();
        allAnagrams = lettersToWords.get(key);
        if(allAnagrams != null){
            for(String s : allAnagrams){
                if(isGoodWord(targetWord,s))
                    result.add(s);
            }
        }
        return result;
    }

    public List<String> getGoodAnagramsWithOneMoreWord(String targetWord) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> allAnagrams = new ArrayList<>();
        for(char ch = 'a'; ch <= 'z'; ch++){
            String key = sortLetter(targetWord+ch);//add a letter and sort it for key
            allAnagrams = lettersToWords.get(key);
            if(allAnagrams != null){
                for(String s : allAnagrams){
                    if(isGoodWord(targetWord,s))
                        result.add(s);
                }
            }
        }
        return result;
    }

    public List<String> getGoodAnagramsWithTwoMoreWords(String targetWord) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> allAnagrams = new ArrayList<>();
        for(char ch1 = 'a'; ch1 <= 'z'; ch1++){
            for(char ch2 = 'a'; ch2 <= 'z'; ch2++){
                String key = sortLetter(targetWord+ch1+ch2);
                allAnagrams = lettersToWords.get(key);
                if(allAnagrams != null){
                    for(String s : allAnagrams){
                        if(isGoodWord(targetWord,s))
                            result.add(s);
                    }
                }
            }
        }
        return result;
    }
    
    //choose random starter word and increment number of letters each time
    public String pickGoodStarterWord(int mode) {
        ArrayList<String> nLetterWords = new ArrayList<>();
        nLetterWords = sizeToWords.get(WORD_LENGTH);
        //do until a "good" random word is obtained
        while(true) {
            //get a random word from the arraylist
            int index = random.nextInt(nLetterWords.size());
            String randomWord = nLetterWords.get(index);
            ArrayList<String> nMoreLetterWords = new ArrayList<>();
            if (mode == 0)
                nMoreLetterWords = (ArrayList<String>) getGoodAnagrams(randomWord);
            else if (mode == 1)
                nMoreLetterWords = (ArrayList<String>) getGoodAnagramsWithOneMoreWord(randomWord);
            else if (mode == 2)
                nMoreLetterWords = (ArrayList<String>) getGoodAnagramsWithTwoMoreWords(randomWord);
            if (nMoreLetterWords.size() > MIN_NUM_ANAGRAMS) {
                if (WORD_LENGTH < MAX_WORD_LENGTH)//max word length in the word list
                    WORD_LENGTH++;
                return randomWord;
            }
        }
    }






}
