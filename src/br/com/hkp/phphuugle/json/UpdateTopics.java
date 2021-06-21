package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import static br.com.hkp.phphuugle.mysql.Util.toTimestamp;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

/******************************************************************************
 * Classe que insere novos dados na tabela topics.
 * 
 * @since 19 de junho 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class UpdateTopics extends JsonObject {
    
    /**A id do topico*/
    private String topicId;
    
    /**A id do ultimo post publicado no topico*/
    private String lastPostId;
    
    /**A data de publicacao do ultimo post*/
    private String lastPostDate;
    
    /**Indica se o topico estah trancado*/
    private String locked;
    
    /**Indica se o topico estah fixado*/
    private String sticked;

    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Cria um objeto para acessar a tabela topics
     * 
     * @param pathName O arquivo json a ser lido com os dados para a tabela
     *  
     * @param mysql Um objeto de conexao com o banco de dados
     * 
     * @throws IOException Em caso de erro de IO
     */
    public UpdateTopics (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(mysql, pathName, "UPDATE topics SET ");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
         Quando chamado, este metodo atualiza 4 colunas da tabela topics
    --------------------------------------------------------------------------*/
    private void update() {
        
        try {
            sqlCommand("lastpostid = " + lastPostId + " WHERE id = " + topicId);
            sqlCommand("lastpostdate = "+ lastPostDate +" WHERE id = "+topicId);
            sqlCommand("lockedtopic = " + locked + " WHERE id = " + topicId);
            sqlCommand("stickedtopic = " + sticked + " WHERE id = " + topicId);
            
        }
        catch (SQLException e) {
            
            System.err.println("ATUALIZACAO FALHOU para topico: " + topicId);
            System.err.println(e);
            
        }
        finally {
            
            System.out.println("");
            
        }
        
        
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
    protected void put(final String field, final int index) {
                
        switch (index) {
            case 1:
                topicId= betweenQuotes(field.replace("topic=", ""));
                break;
            case 2:
                lastPostId = betweenQuotes(field.replace("msg", ""));
                break;
            case 3:
                lastPostDate = betweenQuotes(toTimestamp(field));
                break;
            case 4:
                locked = field;
                break;
            case 5:
                sticked = field;
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
           
        File sourceDir = new File("json/boards.jsons");
        
        File[] fileList = sourceDir.listFiles();
        
        int count = 0;
        
		System.setErr(new PrintStream("logErr.txt"));
        
        for (File file: fileList) {
            
            String pathname = file.getPath();
            
            UpdateTopics t = 
                new UpdateTopics (
                    pathname, 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
            
            System.out.println(++count + " - " + pathname + "\n");
            
            t.fillDatabaseTable(1); 
            
            System.out.println("");
            
            /*
            Os dados abaixo deveriam constar em algum dos arquivos board=xx.json
            mas nao ha registro com informacoes sobre o ultimo post do topico 
            30700, nem a data deste ultimo post, ou se o topico esta trancado
            ou fixado em uma pagina de subsecao. Portanto estes dados sao 
            inseridos explicitamente com o codigo abaixo, em vez de serem lidos
            de um arquivo pelo programa.
            */
            t.topicId = "'30700'";
            t.lastPostId = "'1009230'";
            t.lastPostDate = "'2020-01-31 17:04:43'";
            t.locked = "false";
            t.sticked = "false";
            t.update();
            
        }//for    
        
    }//main()
    
}//classe UpdateTopics