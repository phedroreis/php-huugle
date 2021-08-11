package br.com.hkp.phphuugle.makedb;

import br.com.hkp.phphuugle.mysql.MySQL;
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
    
    private static final Pattern WORD_REGEX = 
        Pattern.compile("[a-záàâãéêíóôõúç]{4,16}");
    
    private final MySQL postsQuery;
    
    private final MySQL topicsQuery;
    
    private static final int TOTAL_NUMBER_OF_POSTS = 10;//951943;
    
    private static final int STEP = 10;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public Collect() throws SQLException {
        
        postsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
        topicsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
    }//construtor
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/    
    private String stripTags(final String text) {
        
        return text.replaceAll("<.+?>", "");
        
    }//stripTags()    
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public ResultSet readPosts(final int start) throws SQLException {
        
        return topicsQuery.query(
            "SELECT id, topicid, post FROM posts LIMIT " + 
            start + ", " + STEP + ";"
        );
        
    }//readPosts()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/      
    public ResultSet readTopic(final int topicid)throws SQLException {
                 
        return postsQuery.query(
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
    private HashMap<String, Integer> buildWordsRankMap(
        final String post,
        final HashSet<String> hashSet
    )
        
    {
        HashMap<String, Integer> hashMap = new HashMap<>();
        
        String lowerPost = stripTags(post).toLowerCase();
        
        Matcher matcher = WORD_REGEX.matcher(lowerPost);
        
        String word; int rank; 
        
        while (matcher.find()) {
            
            word = matcher.group();
            
            if (hashSet.contains(word))
                rank = 20;
            else
                rank = 1;
            
            if (hashMap.containsKey(word)) rank += hashMap.get(word);
            
            hashMap.put(word, rank);
            
        }//while
        
        return hashMap;
        
    }
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public void processResultSet(final ResultSet resultSet) 
        throws SQLException {
        
        String post;
        int id;
        int topicid;
        HashSet<String> hashSet;
        HashMap<String, Integer> hashMap;
        
               
        while (resultSet.next()) {
            
            post = (String)resultSet.getObject("post");
            id = (Integer)resultSet.getObject("id");
            topicid = (Integer)resultSet.getObject("topicid");
            System.out.printf("%d --- %s\n%s\n", id, topicid, post);
            
           
            hashSet = buildTitleWordsSet(topicid);
            
            hashMap = buildWordsRankMap(post, hashSet);
            
            for (String key: hashMap.keySet()) {
                System.out.println(key + " : " + hashMap.get(key));
            }
            
            
        }//while
        
    }//processResultSet()
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public static void main(String[] args) throws SQLException {
        
        Collect collect = new Collect();

        for (int jump = 0; jump < TOTAL_NUMBER_OF_POSTS; jump += STEP) {
            
            collect.processResultSet(collect.readPosts(jump));
        }
        
    }//main()
    

}//classe Collect
