package br.com.hkp.phphuugle.makedb;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Classe que fornece metodos e campos comuns apenas para classes do pacote 
 * makedb.
 * 
 * @since 12 de agosto 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
final class Util {
    
    public static final int TOTAL_NUMBER_OF_POSTS = 951943;
    
    private static final String LETTERS = "[a-záâãéêíóôõúüç]";
    
    private static final Pattern WORD_REGEX =
        Pattern.compile(LETTERS + "{4,}");
    
    private static final Pattern INVALID_WORD_REGEX =
        Pattern.compile(
            "a{3,}|b{3,}|c{3,}|d{3,}|e{3,}|f{3,}|g{3,}|h{3,}|i{3,}|j{3,}|" +
            "k{3,}|l{3,}|m{3,}|n{3,}|o{3,}|p{3,}|q{3,}|r{3,}|s{3,}|t{3,}|" +
            "u{3,}|v{3,}|x{3,}|w{3,}|y{3,}|z{3,}|ç{3,}"
        );
    
    private static final HashSet<String> EXCLUDEDS_WORDS = 
        getWordsExcludedsSet();
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/    
    private static HashSet<String> getWordsExcludedsSet() {
        
        HashSet<String> excludeds = new HashSet<>(512);
        
        try {
            
            Scanner scanner = new Scanner(new FileReader("excludeds.dat"));

            while (scanner.hasNextLine()) excludeds.add(scanner.nextLine());
        }
        catch (IOException e) {
            
            System.err.println(e);
            System.err.println("Falha ao ler arquivo excludeds.dat");
            System.exit(1);
            
        }//try-catch
        
        return excludeds;
        
    }//getWordsExcludedsSet()

    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public static HashSet<String> collectWords(final String text) {
        
        HashSet<String> wordSet = new HashSet<>(4000);
        
        Matcher wordMatcher = 
            WORD_REGEX.matcher(text.replaceAll("<.+?>", "").toLowerCase());
        
        while (wordMatcher.find()) {
            
            String word = wordMatcher.group();
     
            if (
                
                (word.length() > 16) ||
                
                (EXCLUDEDS_WORDS.contains(word)) ||
            
                (INVALID_WORD_REGEX.matcher(word).find()) 
                
            ) {}
            else wordSet.add(word);
            
        }//while
        
        return wordSet;
        
    }//collectWords()

    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/    
    public static void main(String[] args) {
        
        String s = 
        "Este é um çtexto4s de teste <testando tag html> proto-humano aaaaahhhh\n"
        + "muito bem escrito com açúcar e afeto. Lista dex palavras quex devem"
        + " se abacaxi  daquele  dele muito acha isso 1245 o&%^"
        + " anteçjasdfghjklmnbvc anti--horário- anti-horário  d'água 10décima10"; 
        
        
        for (String word: collectWords(s)) System.out.println(word);
            
    }
    
}//classe Util
