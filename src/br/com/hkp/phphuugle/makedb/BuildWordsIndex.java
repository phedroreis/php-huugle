package br.com.hkp.phphuugle.makedb;

import static br.com.hkp.phphuugle.makedb.Util.TOTAL_NUMBER_OF_POSTS;
import static br.com.hkp.phphuugle.makedb.Util.collectWords;
import br.com.hkp.phphuugle.mysql.MySQL;
import java.util.HashSet;
import java.sql.SQLException;
import java.sql.ResultSet;

/******************************************************************************
 * Classe que coleta todas as diferentes palavras catalogadas nos  posts e cria 
 * uma tabela no banco de dados.
 * 
 * CREATE TABLE `wordsindex` (
      `id` mediumint unsigned NOT NULL AUTO_INCREMENT,
      `word` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `word` (`word`)
  ) ENGINE=InnoDB AUTO_INCREMENT=676358 DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_bin
 * 
 * @since 12 de agosto 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
final class BuildWordsIndex {
       
    private final MySQL ccDatabase;
   
    private static final int STEP = 200;
    
    private final HashSet<String> wordsSet;
 
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public BuildWordsIndex() throws SQLException {
        
        ccDatabase = new MySQL("localhost", "root", "eratostenes", "cc");
        
        wordsSet = new HashSet<>(4500000);

    }//construtor
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public ResultSet readPosts(final int start) throws SQLException {
        
        return ccDatabase.query(
            "SELECT post FROM posts LIMIT " + start + ", " + STEP + ";"
        );
        
    }//readPosts()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/    
    public int getNumberOfWordsCollected() {
        
        return wordsSet.size();
        
    }//getNumberOfWordsCollected()
 
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/    
    private static final String INSERT_INTO_PREFIX = 
        "INSERT INTO wordsindex (word) VALUES\n";
    
    public void updateDatabase() throws SQLException {
        
        int numberOfWordsCollected = getNumberOfWordsCollected();
        int count = 0;
        String update = INSERT_INTO_PREFIX;
        
        for (String word: wordsSet) {
            
           count++;
           
           boolean updateNow =
               (((count % 150) == 0) || (count == numberOfWordsCollected));
           
           update += "('" + word + (updateNow ? "');" : "'),\n");
           
           if  (updateNow) {
               
               System.out.printf("\n%s\n--- %d ---", update, count);

               ccDatabase.update(update);
               
               update = INSERT_INTO_PREFIX;
               
           }//if
           
        }//for
        
    }//updateDatabase()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public void processResultSet(final ResultSet resultSet) 
        throws SQLException {
        
        String post;
              
        while (resultSet.next()) {
            
            post = ((String)resultSet.getObject("post"));
            
            wordsSet.addAll(collectWords(post));
  
        }//while
  
    }//processResultSet()
   
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public static void main(String[] args) throws SQLException {
        
        BuildWordsIndex buildWordsIndex = new BuildWordsIndex();
   
        for (int jump = 0; jump < TOTAL_NUMBER_OF_POSTS; jump += STEP) {
                           
            buildWordsIndex.processResultSet(buildWordsIndex.readPosts(jump));
            
            int processed = jump + STEP;
            
            if ((processed % 10000) == 0) {
                
                System.out.printf(
                    
                    "%d palavras catalogadas em %d posts processados\n",
                    buildWordsIndex.getNumberOfWordsCollected(), processed 
                );
            }

        }//for
        
        buildWordsIndex.updateDatabase();

    }//main()
    
    
}//classe BuildWordsIndex
