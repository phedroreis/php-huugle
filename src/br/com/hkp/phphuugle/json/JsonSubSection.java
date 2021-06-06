package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * 
 * Classe relacionada a tabela subsections do banco cc e ao arquivo
 * subsections.json. Um objeto desta classe armazena todos os dados de uma 
 * subsecao do forum CC. (uma subsecao contem uma lista de topicos)
 * 
 * Estrutura da tabela subsections :
 * 
    CREATE TABLE `subsections` (
      `id` tinyint unsigned NOT NULL,
      `title` varchar(46) COLLATE utf8mb4_bin NOT NULL,
      `sectionid` tinyint unsigned NOT NULL,
      `topiccount` smallint unsigned NOT NULL,
      PRIMARY KEY (`id`),
      KEY `sectionid` (`sectionid`),
      CONSTRAINT `subsections_ibfk_1` FOREIGN KEY (`sectionid`) 
      REFERENCES `sections` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
 * 
 * @since 13 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class JsonSubSection extends JsonObject {
    
    //Chave prim. 
    private String id;
    
    //O titulo da board
    private String title;
    
    //Chave estrangeira para a tabela sections
    private String sectionid;
    
    //Num. de topicos em uma board
    private String topiccount;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
     /**
     * Cria um objeto para acessar a tabela subsections
     * 
     * @param pathName O nome do arquivo json a ser lido com os dados para a 
     * tabela
     * 
     * @param mysql Um objeto de conexao com o banco de dados
     * 
     * @throws IOException Em caso de erro de IO
     */
    public JsonSubSection (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(pathName, mysql, "subsections");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        sqlCommand (
            "(" + id +
            SEP + title + 
            SEP + sectionid + 
            SEP + topiccount + ");"
        );
        
    }//insertInto()
    
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
                id = betweenQuotes(field.replace("board=", ""));
                break;
            case 2:
                title = betweenQuotes(field);
                break;
            case 3:
                sectionid = betweenQuotes(field.replace("#c", ""));
                break;
            case 4:
                topiccount = betweenQuotes(field);
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
        JsonSubSection s = 
                new JsonSubSection (
                    "json/subsections.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        s.fillDatabaseTable(1);
        
    }//main()
    
}//classe JsonSubSection
