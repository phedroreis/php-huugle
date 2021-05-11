package br.com.hkp.phphuugle.json;

import java.io.IOException;

/******************************************************************************
 * 
 * @since 10 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class JsonSection extends JsonObject {
    
    private String id;
    
    private String title;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public JsonSection(final String path) throws IOException {
        super(path);
    }//construtor
    
    /*[01]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     *
     */
    @Override
    protected void insertInto() {
        System.out.println(id + " : " + title);
    }//inputInto()
    
    /*[02]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * 
     * @param field 
     * @param index 
     */
    @Override
    protected void put(final String field, final int index) {
                
        switch (index) {
            case 1:
                id = field;
                break;
            case 2:
                title = field;
                insertInto();
        }//switch
        
    }//put()
    
  
    
    public static void main(String[] args) throws IOException
    {
        JsonSection j = new JsonSection("sections.json");
        
        j.fillDatabaseTable();
    }
    
}//classe JsonSection
