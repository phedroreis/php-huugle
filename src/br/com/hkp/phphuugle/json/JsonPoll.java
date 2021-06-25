package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import static br.com.hkp.phphuugle.mysql.Util.toTimestamp;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * 
 * Classe relacionada a tabela polls. Insere os registros na tabela polls.
 * 
 * Estrutura da tabela polls :
 * 
   CREATE TABLE `polls` (
  `topicid` smallint unsigned NOT NULL,
  `pollquestion` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `totalvotes` smallint unsigned NOT NULL,
  `finished` timestamp NULL DEFAULT NULL,
  UNIQUE KEY `topicid` (`topicid`),
  CONSTRAINT `polls_ibfk_1` FOREIGN KEY (`topicid`) REFERENCES `topics` (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

 * 
 * @since 21 de junho de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public final class JsonPoll extends JsonObject {
    
    //Chave primaria
    private int id;
  
    //Chave estrangeira para tabela topics
    private String topicid;
    
   //A pergunta de uma enquete
    private String pollquestion;
    
    //Totalizacao dos votos dados
    private String correctTotalVotes;
    
    //Data de encerramento da enquete ou null se nao foi encerrada
    private String finished;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
     /**
     * Cria um objeto para acessar a tabela polls.
     * 
     * @param pathName O nome do arquivo json a ser lido com os dados para a 
     * tabela
     * 
     * @param mysql Um objeto de conexao com o banco de dados
     * 
     * @throws IOException Em caso de erro de IO
     */
    public JsonPoll (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(pathName, mysql, "polls");
        id = 0;
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        sqlCommand (
            "(" + ++id +
            SEP +topicid +
            SEP + pollquestion + 
            SEP + correctTotalVotes +     
            SEP + finished + ");"
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
            case 2:
                topicid = betweenQuotes(field.replace("topic=", ""));
                break;
            case 4:
                pollquestion = betweenQuotes(field);
                break;
            case 8:
                correctTotalVotes = betweenQuotes(field);
                break;
            case 9:  
                if (field.equals(NA)) 
                    finished = "NULL";
                else
                    finished = betweenQuotes(toTimestamp(field));
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
        JsonPoll p = 
                new JsonPoll (
                    "json/polls.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        p.fillDatabaseTable(1);
        
    }//main()
    
}//classe JsonPoll
