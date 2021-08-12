package br.com.hkp.phphuugle.makedb;

import static br.com.hkp.phphuugle.makedb.Util.TOTAL_NUMBER_OF_POSTS;
import static br.com.hkp.phphuugle.makedb.Util.WORD_REGEX;
import static br.com.hkp.phphuugle.makedb.Util.getWordsExcludedsSet;
import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Classe que coleta todas as palavras em posts.
 * 
 * @since 10 de agosto 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public final class Collect {
    
    private static final Pattern EMPHASIZED_WORD_REGEX =
        Pattern.compile(
            "(<i>|<b>|<strong>|<em>)\\s*([a-záàâãéêíóôõúç]{4,16})" +
            "\\s*(<\\/i>|<\\/b>|<\\/strong>|<\\/em>)"
        );
    
    private final MySQL postsQuery;
    
    private final MySQL topicsQuery;
    
    private final MySQL wordspostsUpdate;
    
    private static final int STEP = 200;
    
    private final HashSet<String> excludedsWordsSet;
    
    private int countRowsInDatabase;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public Collect() throws SQLException, FileNotFoundException {
        
        postsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
        topicsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
        wordspostsUpdate = new MySQL("localhost", "root", "eratostenes", "cc");
        
        excludedsWordsSet = getWordsExcludedsSet();
        
        
        countRowsInDatabase = 0;

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
        
        HashSet<String> hashSet = new HashSet<>();
        
        ResultSet result = readTopic(topicid);
        
        String title = null;
        
        if ((result != null) && (result.next()))
            title = (String)result.getObject("title");
        else
            throw new SQLException("Topicid not found");
        
        title = title.toLowerCase();
        
        Matcher matcher = WORD_REGEX.matcher(title);
        
        while (matcher.find()) hashSet.add(matcher.group());
        
        return hashSet;
        
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
        
        String nonHtmlPost = post.replaceAll("<.+?>", "");
        
        Matcher matcher = WORD_REGEX.matcher(nonHtmlPost);
        
        String word; int rank; 
        
        while (matcher.find()) {
            
            word = matcher.group(); 
            
            if (excludedsWordsSet.contains(word)) continue;
            
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
            
        }//while
        
        return hashMap;
        
    }//buildWordsRankMap()

    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    private void updateDatabases(
        final int postid,
        final HashMap<String, Integer> wordsRankMap
    ) 
        throws SQLException {
        
        int numberOfWordsOnThisPost = wordsRankMap.size();
        
        if (numberOfWordsOnThisPost == 0) return;
        
        countRowsInDatabase += numberOfWordsOnThisPost;
        
        String update = 
            "INSERT INTO wordsposts (word, postid, ranking) VALUES";
        
        for (String key: wordsRankMap.keySet()) {
            
            update += "\n('" + key + "', '" + postid + "', '" + 
                      wordsRankMap.get(key) + "'),";
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
            
            post = ((String)resultSet.getObject("post")).toLowerCase();
            postid = (Integer)resultSet.getObject("id");
            topicid = (Integer)resultSet.getObject("topicid");
       
            titleWordsSet = buildTitleWordsSet(topicid);
            
            emphasizedsWordsSet = buildEmphasizedsWordsSet(post);
            
            wordsRankMap = 
                buildWordsRankMap(post, titleWordsSet, emphasizedsWordsSet);
            
            updateDatabases(postid, wordsRankMap);
  
        }//while
  
    }//processResultSet()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public int getWordsPerPostAverage() {
        
        return countRowsInDatabase / TOTAL_NUMBER_OF_POSTS;
    }//getWordsPerPostAverage()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public static void main(String[] args) 
        throws SQLException, FileNotFoundException {
        
        Collect collect = new Collect();

        for (int jump = 0; jump < TOTAL_NUMBER_OF_POSTS; jump += STEP) {
            
            System.out.println(jump + " posts processados");
            
            collect.processResultSet(collect.readPosts(jump));

        }//for
        
        System.out.println(
            "\n" + collect.getWordsPerPostAverage() + 
            " diferentes palavras por post (média)"
        );
        
    }//main()
    

}//classe Collect
