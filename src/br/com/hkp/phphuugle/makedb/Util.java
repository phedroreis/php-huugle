package br.com.hkp.phphuugle.makedb;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
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
       
    public static final Pattern WORD_REGEX = 
        Pattern.compile("[a-záàâãéêíóôõúç]{4,16}");
    
    public static final int TOTAL_NUMBER_OF_POSTS = 951943;
    
 
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/    
    public static HashSet<String> getWordsExcludedsSet() 
        throws FileNotFoundException {
        
        HashSet<String> excludeds = new HashSet<>(512);
        
        Scanner scanner = new Scanner(new FileReader("excludeds.dat"));
        
        while (scanner.hasNextLine()) excludeds.add(scanner.nextLine());
        
        return excludeds;
        
    }//getWordsExcludedsSet()
    
}//classe UTil
