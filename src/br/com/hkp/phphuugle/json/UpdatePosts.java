package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * Classe que insere os dados da coluna filename na tabela posts.
 * 
 * @since 30 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class UpdatePosts extends JsonObject {
    
    //A id do post
    private String postId;
    
    //O nome do arquivo onde esta o post
    private String filename;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Cria um objeto para acessar a tabela posts
     * 
     * @param pathName O nome do arquivo json a ser lido com os dados para a 
     * tabela
     * 
     * @param mysql Um objeto de conexao com o banco de dados
     * 
     * @throws IOException Em caso de erro de IO
     */
    public UpdatePosts (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(mysql, pathName, "UPDATE posts SET filename = ");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere o nome do arquivo na coluna filename
       referente ao post de id postId
    --------------------------------------------------------------------------*/
    private void update() throws SQLException {
        
        sqlCommand (filename + " WHERE id = " + postId);
        
    }//update()
    
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
                postId= betweenQuotes(field.replace("msg", ""));
                break;
            case 2:
                filename = 
                    betweenQuotes(
                        field.replace("topic=", "").replace(".html", "")
                    );
                update();
        }//switch
        
    }//put()
   
    /*[03]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Le o arquivo json e insere o nome do arquivo na respectiva linha da 
     * tabela posts.
     * 
     * @param args n/a
     * 
     * @throws IOException Em caso de erro de IO
     * 
     * @throws SQLException Erro ao atualizar o banco
     */
    public static void main(String[] args) throws IOException, SQLException {
        UpdatePosts u = 
                new UpdatePosts (
                    "json/postid_file.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        u.fillDatabaseTable(1);
        
    }//main()
    
}//classe UpdatePosts
