package br.com.hkp.phphuugle.makedb;

import static br.com.hkp.phphuugle.makedb.Util.TOTAL_NUMBER_OF_POSTS;
import static br.com.hkp.phphuugle.makedb.Util.WORD_REGEX;
import static br.com.hkp.phphuugle.makedb.Util.getWordsExcludedsSet;
import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.regex.Matcher;

/******************************************************************************
 * Classe que coleta todas as diferentes palavras catalogadas nos  posts e cria 
 * uma tabela no banco de dados.
 * 
 * @since 12 de agosto 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public final class CollectWords {
       
    private final MySQL postsQuery;
   
    private static final int STEP = 200;
    
    private final HashSet<String> wordsSet;
    
    private final HashSet<String> excludedsWordsSet;
    
 
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public CollectWords() throws SQLException, FileNotFoundException {
        
        postsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
        wordsSet = new HashSet<>(3000000);

        excludedsWordsSet = getWordsExcludedsSet();

    }//construtor
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public ResultSet readPosts(final int start) throws SQLException {
        
        return postsQuery.query(
            "SELECT post FROM posts LIMIT " + start + ", " + STEP + ";"
        );
        
    }//readPosts()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/       
    private void updateWordsSet(final String post) {
        
        Matcher matcher = WORD_REGEX.matcher(post);
        
        String word;
        
        while (matcher.find()) {
            
            word = matcher.group(); 
            
            if (excludedsWordsSet.contains(word)) continue;

            wordsSet.add(word);
            
        }//while
        
        
    }//buildWordsSet()

    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/    
    public void updateDatabase() {
        
    }//updateDatabase()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public void processResultSet(final ResultSet resultSet) 
        throws SQLException {
        
        String post;
              
        while (resultSet.next()) {
            
            post = 
                ((String)resultSet.getObject("post")).
                    replaceAll("<.+?>", "").
                    toLowerCase();
            
            updateWordsSet(post);
  
        }//while
  
    }//processResultSet()

    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/    
    public int getNumberOfWordsCollected() {
        
        return wordsSet.size();
        
    }//getNumberOfWordsCollected()
        
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public static void main(String[] args) 
        throws SQLException, FileNotFoundException {
        
        CollectWords collectWords = new CollectWords();

        for (int jump = 0; jump < TOTAL_NUMBER_OF_POSTS; jump += STEP) {
                           
            collectWords.processResultSet(collectWords.readPosts(jump));
            
            int processed = jump + STEP;
            
            if ((processed % 10000) == 0) {
                
                System.out.printf(
                    
                    "%d palavras catalogadas em %d posts processados.\n",
                    collectWords.getNumberOfWordsCollected(), processed 
                );
            }

        }//for
        
        collectWords.updateDatabase();

    }//main()
    
    
}//classe CollectWords
