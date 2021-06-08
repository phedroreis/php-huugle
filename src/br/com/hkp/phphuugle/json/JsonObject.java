package br.com.hkp.phphuugle.json;


import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * Super classe abstrata das classes relacionadas com os arquivos json que devem
 * ser convertidos em tabelas do banco de dados ccraw.
 * 
    CREATE DATABASE `cc` 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_bin 
    DEFAULT ENCRYPTION='N';
 * 
 * 
 * 
 * @since 10 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public abstract class JsonObject {
      
    /**
     * String para campo nao atribuido no arquivo json
     */
    protected static final String NA = "N/A";
    
    /**
     * String que separa os dados em uma instrucao INSERT INTO
     */
    protected static final String SEP = ", ";
    
    /**A subclasse usarah esta variavel para contar quantos campos jah foram 
    lidos em um registro. Quando todos os campos forem lidos devera ser setada 
    para 0, aguardando a leitura do proximo registro.
    */
    private int fieldIndex;
    
    /**Um obj. para ler linha a linha o arquivo json*/
    private final TextLineReader textLineReader;
    
    /*O banco de dados onde os dados serao inseridos*/
    private final MySQL mysql;
    
    /**
    Uma String com a porcao iniicial da instrucao INSERT INTO que serah 
    executada por um objeto dessa classe Ex:"INSERT INTO sections (id, title)"
    */
    private final String insertIntoPrefix;
    
    /** Conta registros lidos e gravados*/
    private int count = 0;
    
    /**Comeca a gravar registros a partir de startIndex*/
    private int startIndex;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Recebe o caminho e nome de um arquivo json e cria um ojbeto manipular
     * uma tabela no banco de dados a partir dos dados lidos neste arquivo json.
     * A manipulacao pode ser referente a insercao dos registros lidos no 
     * arquivo na tabela ou alteracao de registros jah existentes na tabela.
     * 
     * @param mysql A conexao com o banco de dados. A conexao eh feita ao se 
     * criar o objeto
     * 
     * @param pathName O nome e caminho do arquivo json.
     * 
     * @param command O parte inicial do comando SQL que sera executado para 
     * cada registro lido no arquivo json
     * 
     * @throws IOException Em caso de erro de IO.
     */
    protected JsonObject (
        
        final MySQL mysql,
        final String pathName,
        final String command
    )
    throws IOException {
        
        //Um obj. para ler linha a linha o arquivo json
        textLineReader = new TextLineReader(pathName);
        
        //Consome a 1a linha do arquivo. Esta linha e o caractere '{'
        textLineReader.readLine(); 
        
        //O indice do campo que foi lido no registro
        fieldIndex = 0;
        
        this.mysql = mysql;
        
        //O prefixo de toda instrucao de insercao no banco de dados
        insertIntoPrefix = command;
     
    }//construtor
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Recebe o caminho e nome de um arquivo json e cria um ojbeto para gravar
     * seus registros em uma tabela de banco de dados.
     * 
     * @param pathName O nome e caminho do arquivo json.
     * 
     * @param mysql A conexao com o banco de dados. A conexao eh feita ao se 
     * criar o objeto
     * 
     * @param tableName O nome da tabela
     * 
     * @throws IOException Em caso de erro de IO.
     */
    protected JsonObject (
            
        final String pathName,
        final MySQL mysql,
        final String tableName
    )
    throws IOException {
        
        this(mysql, pathName, "INSERT INTO " + tableName + " VALUES ");
      
    }//construtor
    
    /*[01]----------------------------------------------------------------------
               Executa a instrucao de insercao de uma linha no BD ou
               de atualizacao de uma linha
    --------------------------------------------------------------------------*/
    protected void sqlCommand(final String values) throws SQLException {
        
        if (++count < startIndex) return;
        
        String update = insertIntoPrefix + values;
        
        System.out.println (count + " - " + update);
        
        /*Este metodo soh deve alterar uma unica linha do BD a cada chamada*/
        if (mysql.update(update) != 1) 
            throw new SQLException("Error updating database");
        
    }//sqlCommand()
    
    /*[02]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Atribui field a um campo de um objeto pertencente a uma subclasse desta
     * classe jsonObject.
     * 
     * @param field Um campo lido de um registro em um arquivo json
     * 
     * @param fieldIndex
     * 
     * @throws SQLException Erro ao atualizar banco
     */
    protected abstract void put(final String field, final int fieldIndex)
        throws SQLException;
    
       
    /*[03]----------------------------------------------------------------------
     Recebe uma linha lida de um arquivo json e retorna o valor do campo ou 
     null no caso de chave de fechamento (indicando que o registro foi lido)
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
    /**
     * Formata uma String entre aspas simples se o dado for diferente de null.
     * 
     * @param value A String
     * 
     * @return A String formatada entre aspas simples ou a String NULL
     */
    protected String betweenQuotes(final String value) {
        
        if (value.equals(NA)) 
            return "NULL";
        else
            return "'" + value + "'";
        
    }//format()
    
    /*[05]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Preenche a tabela do banco com os registros lidos do arquivo json. Inicia
     * o preenchimento a partir do enesimo registro lido no arquivo. Se n = 10,
     * serah gravado o decimo regisro lido no arquivo e os posteriores.
     * 
     * @param n Comeca a gravar registros na tabela a partir do enesimo registro
     * lido no arquivo json
     * 
     * @throws IOException Em caso de erro de IO
     * 
     * @throws SQLException Falha no acesso ao banco
     */
    public final void fillDatabaseTable(final int n) 
    throws IOException, SQLException {
        
        this.startIndex = n;
        
        while(true) {
            
            /*Consome a chave de abertura de um registro json. */            
            String field = textLineReader.readLine();
           
            /*
            Mas se em vez de chave de abertura for de fechamento eh porque foi
            alcancado o fim do arquivo.
            */
            if (field.trim().charAt(0) == '}') {
                
                //Le mais uma linha para forcar fechamento o arquivo
                textLineReader.readLine();
                break;
                
            }//if
            
            /*Le cada campo do registro json e o atribui a um campo do objeto*/
            do {

                field = retrieveField(textLineReader.readLine());
                
                /*
                Se foi lido algo diferente de uma chave de fechamento entao eh
                um campo json do registro e que deve ser atribuido a um campo
                do objeto. Se foi encontrada uma chave de fechamento foi
                retornado valor null em field
                */
                if (field != null) {
                    
                   //escapa aspas simples na String
                   field = field.replace("'", "''"); 
                    
                    put(field, ++fieldIndex);
                }

            } while (field != null);
            
            /*
            Todos os campos do registro foram lidos. Zera contador para iniciar
            leitura do proximo registro na proxima iteracao do loop while
            */
            fieldIndex = 0;
                       
        }//while
        
    }//fillDatabaseTable()
    
    
}//classe JsonObject
