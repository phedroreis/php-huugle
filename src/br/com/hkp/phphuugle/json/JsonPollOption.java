package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * 
 * Classe relacionada a tabela polloptions. Insere os registros na tabela
 * polloptions.
 * 
 * Estrutura da tabela polloptions :
 * 
    CREATE TABLE `polloptions` (
      `pollid` smallint unsigned NOT NULL,
      `optionindex` tinyint unsigned NOT NULL,
      `option` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
      KEY `pollid` (`pollid`),
      CONSTRAINT `polloptions_ibfk_1` FOREIGN KEY (`pollid`) 
      REFERENCES `polls` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

 * 
 * @since 25 de junho de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public final class JsonPollOption extends JsonObject {
  
    //Chave estrangeira para tabela polls
    private int pollid;
    
   //As opcoes de resposta de enquete
    private String options;
    
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
     /**
     * Cria um objeto para acessar a tabela polloptions.
     * 
     * @param pathName O nome do arquivo json a ser lido com os dados para a 
     * tabela
     * 
     * @param mysql Um objeto de conexao com o banco de dados
     * 
     * @throws IOException Em caso de erro de IO
     */
    public JsonPollOption (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(pathName, mysql, "polloptions");
        
        pollid = 0;
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        Tokenizer tokenizer = new Tokenizer(options);
        
        String token;
        
        int optionindex = 0;
        
        while ((token = tokenizer.getToken()) != null) {
            
            sqlCommand (
                "(" + pollid +
                SEP + ++optionindex +
                SEP + betweenQuotes(token) + ");"
            );
            
        }//while

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
            case 5:
                ++pollid;
                if (field.isEmpty()) break;
                options = field;
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
        JsonPollOption o = 
                new JsonPollOption (
                    "json/polls.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        o.fillDatabaseTable(1);
        
    }//main()

    
}//classe JsonPollOption
