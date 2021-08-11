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
    
    private static final Pattern EMPHASIZED_WORD_REGEX =
        Pattern.compile(
            "(<i>|<b>|<strong>|<em>)\\s*([a-záàâãéêíóôõúç]{4,16})" +
            "\\s*(<\\/i>|<\\/b>|<\\/strong>|<\\/em>)"
        );
    
    private final MySQL postsQuery;
    
    private final MySQL topicsQuery;
    
    private static final int TOTAL_NUMBER_OF_POSTS = 30;//951943;
    
    private static final int STEP = 10;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public Collect() throws SQLException {
        
        postsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
        topicsQuery = new MySQL("localhost", "root", "eratostenes", "cc");
        
    }//construtor
    
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
        
    }
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    private void updateDatabases(
        final int postid,
        final HashMap<String, Integer> wordsRankMap
    ) 
    {
        
        for (String key: wordsRankMap.keySet()) {
            System.out.println (
                key + " : " + wordsRankMap.get(key) + " : post = " + postid
            );
        }
        
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
            
            System.out.printf("%d --- %s\n%s\n", postid, topicid, post);

            titleWordsSet = buildTitleWordsSet(topicid);
            
            emphasizedsWordsSet = buildEmphasizedsWordsSet(post);
            
            wordsRankMap = 
                buildWordsRankMap(post, titleWordsSet, emphasizedsWordsSet);
            
            updateDatabases(postid, wordsRankMap);
 
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
