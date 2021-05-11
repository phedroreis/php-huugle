package br.com.hkp.phphuugle.json;


import java.io.IOException;

/******************************************************************************
 * 
 * @since 10 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public abstract class JsonObject {
    
    /*A subclasse usarah esta variavel para contar quantos campos jah foram 
    lidos em um registro. Quando todos os campos forem lidos devera ser setada 
    para 0, aguardando a leitura do proximo registro.
    */
    private int fieldIndex;
    
    /*Um obj. para ler linha a linha o arquivo json*/
    private final TextLineReader textLineReader;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Recebe o caminho e nome de um arquivo json e cria um ojbeto para gravar
     * seus registro em uma tabela de banco de dados.
     * 
     * @param path O nome e caminho do arquivo json.
     * @throws IOException Em caso de erro de IO.
     */
    protected JsonObject(final String path) throws IOException {
        
        //Um obj. para ler linha a linha o arquivo json
        textLineReader = new TextLineReader(path);
        
        textLineReader.readLine(); //consome a 1a linha do arquivo
        
        fieldIndex = 0;
     
    }//construtor
    
    /*[01]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * 
     */
    protected abstract void insertInto();
    
    /*[02]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Atribui field a um campo de um objeto pertencente a uma subclasse desta
     * classe jsonObject.
     * 
     * @param field Um campo lido de um registro em um arquivo json
     * @param index
     */
    protected abstract void put(final String field, final int index);
    
       
    /*[03]----------------------------------------------------------------------
    * Recebe uma linha lida de um arquivo json e retorna o valor do campo ou 
    * null no caso de chave de fechamento (indicando que o registro foi lido)
    --------------------------------------------------------------------------*/
     private String retrieveField(final String jsonLine) {
        
        if (jsonLine.trim().charAt(0) == '}') return null;
  
        int start = jsonLine.indexOf(':') + 2;
        
        int end = 
            jsonLine.charAt(jsonLine.length() - 1) == ',' ? 
                jsonLine.length() - 2 : jsonLine.length() - 1;
           
        return jsonLine.substring(start, end);

    }//retrieveField()
    
    
    /*[04]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public final void fillDatabaseTable() throws IOException {
        
        while(true) {
            
            /*
            Consome a chave de abertura de um registro json. 
            */            
            String field = textLineReader.readLine();
           
            /*
            Mas se em vez de chave de abertura for de fechamento eh porque foi
            alcancado o fim do arquivo.
            */
            if (field.trim().charAt(0) == '}') {
                //Le mais uma linha para fechar o arquivo
                textLineReader.readLine();
                break;
            }//if
            
            /*
            Le cada campo do registro json e o atribui a um campo do objeto.
            */
            do {

                field = retrieveField(textLineReader.readLine());
                
                /*
                Se foi lido algo diferente de uma chave de fechamento entao eh
                um campo json do registro e que deve ser atribuido a um campo
                do objeto.
                */
                if (field != null) put(field, ++fieldIndex);

            } while (field != null);
            
            fieldIndex = 0;
            
                       
        }//while
        
    }//fillDatabaseTable()
 
}//classe JsonObject
