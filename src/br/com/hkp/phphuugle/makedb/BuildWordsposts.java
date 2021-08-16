package br.com.hkp.phphuugle.makedb;

import static br.com.hkp.phphuugle.makedb.Util.TOTAL_NUMBER_OF_POSTS;
import static br.com.hkp.phphuugle.makedb.Util.collectWords;
import br.com.hkp.phphuugle.mysql.MySQL;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Classe que coleta todas as palavras em posts e popula a tabela wordsposts.
 * 
   CREATE TABLE `wordsposts` (
      `wordid` mediumint unsigned NOT NULL,
      `postid` int NOT NULL,
      `ranking` smallint unsigned NOT NULL,
      KEY `postid` (`postid`),
      KEY `wordid` (`wordid`),
      CONSTRAINT `wordsposts_ibfk_1` FOREIGN KEY (`postid`) 
      REFERENCES `posts` (`id`),
      CONSTRAINT `wordsposts_ibfk_2` FOREIGN KEY (`wordid`) 
      REFERENCES `wordsindex` (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
 * 
 * @since 10 de agosto 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public final class BuildWordsposts {
    
    private static final Pattern EMPHASIZED_WORD_REGEX =
        Pattern.compile(
            "(<i>|<b>|<strong>|<em>)\\s*([a-záàâãéêíóôõúç]{4,16})" +
            "\\s*(<\\/i>|<\\/b>|<\\/strong>|<\\/em>)"
        );
    
    private final MySQL postsQuery;
    
    private final MySQL topicsQuery;
    
    private final MySQL wordspostsUpdate;
    
    private final MySQL wordsindexQuery;
    
    private static final int STEP = 200;
    
    private int countRowsInPostsTable;
    
    private int countRowsInWordspostsTable;
        
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public BuildWordsposts() throws SQLException {
        
        postsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
        topicsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
        wordsindexQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
        wordspostsUpdate = new MySQL("localhost", "root", "eratostenes", "cc");
        
        countRowsInPostsTable = 0;
       
        countRowsInWordspostsTable = 0;

    }//construtor
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public ResultSet readPosts(final int start) throws SQLException {
        
        return postsQuery.query(
            "SELECT id, topicid, post FROM posts LIMIT " + 
            start + ", " + STEP + ";"
        );
        
    }//readPosts()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/      
    public ResultSet readTopic(final int topicid)throws SQLException {
                 
        return topicsQuery.query(
            "SELECT title FROM topics WHERE id = " + topicid + ";"
        );
        
    }//readTopic()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/   
    private HashSet<String> buildTitleWordsSet(final int topicid) 
        throws SQLException {
 
        ResultSet resultSet = readTopic(topicid);
        
        String title;
        
        if ((resultSet != null) && (resultSet.next()))
            title = (String)resultSet.getObject("title");
        else
            throw new SQLException("Topic id not found");
  
        return collectWords(title);
        
    }//buildTitleWordsSet()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/     
    private HashSet<String> buildEmphasizedsWordsSet(final String post) {
        
        HashSet<String> emphasizedsWordsSet = new HashSet<>();
        
        Matcher matcher = EMPHASIZED_WORD_REGEX.matcher(post);
        
        while (matcher.find()) emphasizedsWordsSet.add(matcher.group(2));
        
        return emphasizedsWordsSet;
        
    }//buildEmphasizedsWordsSet()

    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/       
    private HashMap<String, Integer> buildWordsRankMap(
        final String post,
        final HashSet<String> titleWordsSet,
        final HashSet<String> emphasizedsWordsSet
    )
        
    {
        HashMap<String, Integer> hashMap = new HashMap<>();
        
        HashSet<String> wordsSet = collectWords(post);
    
        int rank; 
        
        for (String word: wordsSet) {
            
            boolean wordPresentInTopicTitle = titleWordsSet.contains(word);
            
            boolean wordEmphasized = emphasizedsWordsSet.contains(word); 
            
            if (wordPresentInTopicTitle && wordEmphasized)
                rank = 9;
            else if (wordPresentInTopicTitle || wordEmphasized)
                rank = 3;
            else
                rank = 1;
   
            if (hashMap.containsKey(word)) rank += hashMap.get(word);
            
            hashMap.put(word, rank);            
            
        }//for
         
        return hashMap;
        
    }//buildWordsRankMap()

    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    private void updateDatabase(
        final int postid,
        final HashMap<String, Integer> wordsRankMap
    ) 
        throws SQLException {
        
        int numberOfWordsOnThisPost = wordsRankMap.size();
        
        if (numberOfWordsOnThisPost == 0) return;
        
        countRowsInWordspostsTable += numberOfWordsOnThisPost;
        
        String update = 
            "INSERT INTO wordsposts (wordid, postid, ranking) VALUES";
        
        int wordId;
        
        for (String word: wordsRankMap.keySet()) {
            
            ResultSet resultSet = 
                wordsindexQuery.query(
                    "SELECT id FROM wordsindex WHERE word = '" + word + "';"
                );
            
            if ((resultSet != null) && (resultSet.next())) 
                wordId = (Integer)resultSet.getObject("id");
            else
                throw new SQLException("Word id not found");
            
            update += "\n('" + wordId + "', '" + postid + "', '" + 
                wordsRankMap.get(word) + "'),";
        }
        
        update = (update + "*").replace(",*", ";");
        
        wordspostsUpdate.update(update);
     
    }//updateDatabases()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public void processResultSet(final ResultSet resultSet) 
        throws SQLException {
        
        String post;
        int postid;
        int topicid;
        HashSet<String> titleWordsSet;
        HashSet<String> emphasizedsWordsSet;
        HashMap<String, Integer> wordsRankMap;
        
               
        while (resultSet.next()) {
            
            countRowsInPostsTable++;
      
            post = (String)resultSet.getObject("post");
            postid = (Integer)resultSet.getObject("id");
            topicid = (Integer)resultSet.getObject("topicid");
       
            titleWordsSet = buildTitleWordsSet(topicid);
            
            emphasizedsWordsSet = buildEmphasizedsWordsSet(post);
            
            wordsRankMap = 
                buildWordsRankMap(post, titleWordsSet, emphasizedsWordsSet);
            
            updateDatabase(postid, wordsRankMap);
  
        }//while
        
        System.out.println(countRowsInPostsTable);
  
    }//processResultSet()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public int getWordsPerPostAverage() {
        
        return countRowsInWordspostsTable / TOTAL_NUMBER_OF_POSTS;
    }//getWordsPerPostAverage()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public static void main(String[] args) 
        throws SQLException {
        
        BuildWordsposts buildWordsposts = new BuildWordsposts();

        for (int jump = 0; jump < TOTAL_NUMBER_OF_POSTS; jump += STEP) {
            
            buildWordsposts.processResultSet(buildWordsposts.readPosts(jump));

        }//for
        
        System.out.println(
            "\n" + buildWordsposts.getWordsPerPostAverage() + 
            " diferentes palavras por post (média)"
        );
        
    }//main()
    

}//classe BuildWordsposts
