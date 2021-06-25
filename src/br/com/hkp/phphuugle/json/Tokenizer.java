package br.com.hkp.phphuugle.json;

final class Tokenizer {
    
    private final String tokenList;
    
    private int begin;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public Tokenizer(final String tokenList) {
        
        this.tokenList = tokenList + "<&>";
        begin = 0; 
        
    }//construtor

    /*[01]----------------------------------------------------------------------

    --------------------------------------------------------------------------*/
    public String getToken() {

        int end = tokenList.indexOf("<&>", begin);

        if (end == -1) return null;
        
        int startPosition = begin;
        
        begin = end + 3;

        return tokenList.substring(startPosition, end);

    }//getTokenList()

}//classe Tokenizer
