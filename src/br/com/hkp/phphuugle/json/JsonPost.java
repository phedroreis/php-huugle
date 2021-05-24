package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import static br.com.hkp.phphuugle.mysql.Util.toTimestamp;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * 
 * Classe relacionada a tabela posts do banco cc e ao arquivo posts.json.
 * Um objeto desta classe armazena os dados de um post do forum CC.
 * 
 * Estrutura da tabela posts :
 * 
 
CREATE TABLE `posts` (
  `id` int NOT NULL,
  `topicid` smallint unsigned NOT NULL,
  `authorid` varchar(26) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `creationdate` timestamp NOT NULL,
  `modified` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL,
  `post` mediumtext COLLATE utf8mb4_bin NOT NULL,
  `ord` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `topicid` (`topicid`),
  KEY `authorid` (`authorid`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`topicid`) REFERENCES `topics` (`id`),
  CONSTRAINT `posts_ibfk_2` FOREIGN KEY (`authorid`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

 * 
 * @since 15 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class JsonPost extends JsonObject {
    
    //Chave prim. 
    private String id;
    
    //Chave estr. para topics
    private String topicid;
    
    //Chave estr. para users
    private String authorid;
    
    //Data de criacao do post
    private String creationdate;
    
    //Data e autor da modificacao do post. Se houver
    private String modified;
    
    //O post
    private String post;
    
    //Se foi o enesimo post a ser publicado, ord = n
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
    public JsonPost (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(pathName, mysql, "posts");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        insertInto (
            
            "(" + id + 
            SEP + topicid +
            SEP + authorid +
            SEP + creationdate +
            SEP + modified +
            SEP + post +
            SEP + ord + ");"
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
                id = betweenQuotes(field.replace("msg", ""));
                break;
            case 2:
                topicid = betweenQuotes(field.replace("topic=", ""));
                break;
            case 3:
                authorid = betweenQuotes(field);
                break;
            case 4:
                creationdate = betweenQuotes(toTimestamp(field));
                break;
            case 5:
                modified = betweenQuotes(field);
                break;
            //case 6:Consome o campo "index" do registro json
            case 7: 
                post = betweenQuotes(field.replace("\\", "\\\\"));
                break;
            case 8:
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
        JsonPost p = 
                new JsonPost (
                    "json/posts.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        p.fillDatabaseTable(353750);


        
    }//main()
    
    /*
    Deu erro na data do comando abaixo. Serah bug do MySQL????
    
    INSERT INTO posts VALUES ('981076', '14036', 'u=11506', '2018-11-04 00:48:32', NULL, 'La Abadía del Crimen, jogo baseado no romance &#039;&#039;O Nome da Rosa&#039;&#039; de Umberto Eco. ', '924248');
    
    Para realizar a insercao a hora foi trocada para 01 e funcionou!
    */
    
}//classe JsonPost
