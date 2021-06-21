package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import static br.com.hkp.phphuugle.mysql.Util.toTimestamp;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * 
 * Classe relacionada a tabela topics do banco cc e ao arquivo topics.json.
 * Um objeto desta classe armazena todos os dados de um topico do forum CC.
 * 
 * Estrutura da tabela topics :
 * 
 * 
    CREATE TABLE `topics` (
      `id` smallint unsigned NOT NULL,
      `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
      `authorid` varchar(26) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
      `subsectionid` tinyint unsigned NOT NULL,
      `postid` int NOT NULL,
      `creationdate` timestamp NOT NULL,
      `views` int NOT NULL,
      `pages` smallint unsigned NOT NULL,
      `pollid` smallint unsigned DEFAULT NULL,
      `ord` smallint unsigned NOT NULL,
      `lastpostid` int NOT NULL,
      `lastpostdate` timestamp NOT NULL,
      `lockedtopic` tinyint(1) NOT NULL,
      `stickedtopic` tinyint(1) NOT NULL,
      PRIMARY KEY (`id`),
      KEY `authorid` (`authorid`),
      KEY `subsectionid` (`subsectionid`),
      KEY `topics_ibfk_3` (`lastpostid`),
      CONSTRAINT `topics_ibfk_1` FOREIGN KEY (`authorid`) 
      REFERENCES `users` (`id`),
      CONSTRAINT `topics_ibfk_2` FOREIGN KEY (`subsectionid`) 
      REFERENCES `subsections` (`id`),
      CONSTRAINT `topics_ibfk_3` FOREIGN KEY (`lastpostid`)
      REFERENCES `posts` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

 * 
 * @since 13 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class JsonTopic extends JsonObject {
    
    //Chave prim. 
    private String id;
    
    //O titulo do topico
    private String title;
    
    //Chave estr. para users 
    private String authorid;
      
    //Chave estr. para subsections
    private String subsectionid;
    
    //Chave estr. para posts
    private String postid;
    
    //Data e hora por extenso da criacao do topico
    private String creationdate;
    
    //Visualizacoes do topico
    private String views;
    
    //Num. de pags. no topico
    private String pages;
    
    //Se o topico inclui enquete
    private String pollid;
    
    //A ordem desse topico na sequencia de criacao dos topicos    
    private String ord;   
    
    
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Cria um objeto para acessar a tabela sections
     * 
     * @param pathName O nome do arquivo json a ser lido com os dados para a 
     * tabela
     * 
     * @param mysql Um objeto de conexao com o banco de dados
     * 
     * @throws IOException Em caso de erro de IO
     */
    public JsonTopic (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(pathName, mysql, "topics");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        sqlCommand (
            
            "(" + id +
            SEP + title + 
            SEP + authorid +  
            SEP + subsectionid +      
            SEP + postid +  
            SEP + creationdate +  
            SEP + views +  
            SEP + pages +  
            SEP + pollid + 
            SEP + ord + ");"
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
                id = betweenQuotes(field.replace("topic=", ""));
                break;
            case 2:
                title = betweenQuotes(field);
                break;
            case 3:
                authorid = betweenQuotes(field);
                break;
            //case 4:consome o campo "sectionid" do registro json
            case 5:
                subsectionid = betweenQuotes(field.replace("board=", ""));
                break;                
            case 6:
                postid = betweenQuotes(field.replace("msg", ""));
                break;
            case 7:
                creationdate = betweenQuotes(toTimestamp(field));
                break;
            case 8:
                views = betweenQuotes(field);
                break;
            case 9:
                pages = betweenQuotes(field);
                break;
            case 10:
                pollid = betweenQuotes(field.replace("poll=", ""));
                break;
            case 11:
                ord = betweenQuotes(field);               
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
 
        JsonTopic t = 
                new JsonTopic (
                    "json/topics.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        t.fillDatabaseTable(1);
        
    }//main()
    
}//classe JsonSection
