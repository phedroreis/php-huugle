package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * 
 * Classe relacionada a tabela postid_file do banco cc e ao arquivo 
 * postid_file.json.  Um objeto desta classe relaciona cada post do forum com
 * o arquivo HTML onde se encontra.
 * 
 * A estrutura da tabela postid_file:
 * 
    CREATE TABLE `postid_file` (
      `postid` INT UNIQUE NOT NULL,
      `file` varchar(22) COLLATE utf8mb4_bin NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
 * 
 * @since 13 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class JsonPostidFile extends JsonObject {
    
    //Chave estrangeira
    private String postid;
    
    //O aqruivo onde se encontra o post
    private String file;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public JsonPostidFile (
        final String path,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(path, mysql, "postid_file");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        insertInto (
            "(" + postid +
            SEP + file +  ");"
        );
        
    }//inputInto()
    
    /*[02]----------------------------------------------------------------------
     Cada registro que eh lido do arquivo json eh atribuido ao seu respectivo
     atributo de um objeto desta classe atraves deste metodo.
    
     Um contador na superclasse jsonObject determina o index do atributo que
     foi lido e faz uma chamada a este metodo. Ao ler o ultimo atrib. o contador
     eh zerado para a leitura do prox. registro.
    
     Quando o ultimo atributo eh lido, o registro eh gravado no banco de dados.
    --------------------------------------------------------------------------*/
    @Override
    protected void put(final String field, final int index) 
    throws SQLException {
                
        switch (index) {
            case 1:
                postid = betweenQuotes(field.replace("msg", ""));
                break;
            case 2:
                file = betweenQuotes(field);
                insertInto();
        }//switch
        
    }//put()
   
    /*[03]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Le o arquivo json e cria e popula a tabela no banco de dados.
     * 
     * @param args n/a
     * 
     * @throws IOException Em caso de erro de IO
     * 
     * @throws SQLException Erro ao atualizar o banco
     */
    public static void main(String[] args) throws IOException, SQLException {
        JsonPostidFile p = 
                new JsonPostidFile (
                    "json/postid_file.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        p.fillDatabaseTable();
        
    }//main()
    
}//classe JsonPostidFile
