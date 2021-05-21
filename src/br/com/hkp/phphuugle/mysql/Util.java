package br.com.hkp.phphuugle.mysql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/******************************************************************************
 * A classe fornece metodos utilitarios para bancos de dados mysql.
 * 
 * @since 14 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public final class Util {
    
    /*[01]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    private static final Pattern TIMESTAMP =  Pattern.compile (
        "(\\d{2})\\D+?" + "([JFMAJSOND][a-z\u00e7]+)" +
        "\\D+?(\\d{4})\\D+?(\\d{2}:\\d{2}:\\d{2})"    
    );
    /**
     * Obtem o timestamp de uma data/hora por extenso escrita no formato
     * DD de Nomedomes de AAAA, hh:mm:ss.
     * 
     * @param datetime A String da data/hora
     * 
     * @return O timestamp da data
     * 
     * @throws IllegalArgumentException
     * Se a data nao puder ser convertida para timestamp
     */
    public static String toTimestamp(final String datetime) {
        
        Matcher m = TIMESTAMP.matcher(datetime);
                                 
        if (m.find()) { 
                
            String month = "";
            
            switch (m.group(2)) {
                
                case "Janeiro": 
                    month = "01";
                    break;
                case "Fevereiro": 
                    month = "02";
                    break;     
                case "Mar\u00e7o": 
                    month = "03";
                    break;   
                case "Abril": 
                    month = "04";
                    break;
                case "Maio": 
                    month = "05";
                    break;     
                case "Junho": 
                    month = "06";
                    break;   
                case "Julho": 
                    month = "07";
                    break;
                case "Agosto": 
                    month = "08";
                    break;     
                case "Setembro": 
                    month = "09";
                    break;   
                case "Outubro": 
                    month = "10";
                    break;
                case "Novembro": 
                    month = "11";
                    break;     
                case "Dezembro": 
                    month = "12";
               
            }//switch
            
            if (!month.isEmpty()) return 
                m.group(3) + "-" + month + "-" + m.group(1) + " " + m.group(4);
      
        }//if
        
        throw 
            new IllegalArgumentException("Can't get timestamp for " + datetime);
        
    }//GetTimestamp() 
    
    public static void main(String[] args) {
        System.out.println(toTimestamp("31 Janeiro 2020 00:21:36"));
    }
    
}//classe Util
