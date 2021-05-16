package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * 
 * Classe relacionada a tabela sections do banco cc e ao arquivo sections.json.
 * Um objeto desta classe armazena os dados de uma secao do forum CC.
 * 
 * Estrutura da tabela sections :
 * 
    CREATE TABLE `sections` (
      `id` tinyint unsigned NOT NULL,
      `title` varchar(11) COLLATE utf8mb4_bin DEFAULT NULL,
       PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
 * 
 * @since 10 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class JsonSection extends JsonObject {
    
    //Chave prim. Existiam 3 secoes : #c3, #c4, #c5
    private String id;
    
    //O titulo da secao. Pode ser Informacoes, Discussoes ou Diversao
    private String title;
    
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
    public JsonSection (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(pathName, mysql, "sections");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        insertInto (
            "(" + id +
            SEP + title +  ");"
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
                id = betweenQuotes(field.replace("#c", ""));
                break;
            case 2:
                title = betweenQuotes(field);
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
        JsonSection j = 
                new JsonSection (
                    "json/sections.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        j.fillDatabaseTable(1);
        
    }//main()
    
}//classe JsonSection
