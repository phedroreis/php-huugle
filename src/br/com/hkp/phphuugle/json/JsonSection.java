package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * 
 * Classe relacionada a tabela users do banco ccraw e ao arquivo users.json.
 * Um objeto desta classe armazena todos os dados de um usuario do forum CC.
 * 
 * Estrutura da tabela sections :
 * 
 * create table sections
 * (
 *   id char(3),
 *   title varchar(11),
 *   primary key(id)
 * );
 * 
 * @since 10 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class JsonSection extends JsonObject {
    
    //Chave prim. Existiam 3 secoes : #c1, #c2, #c3
    private String id;
    
    //O titulo da secao. Pode ser Informacoes, Discussoes ou Diversao
    private String title;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public JsonSection (
        final String path,
        final MySQL mysql
    ) 
    throws IOException {
        
        super(path, mysql, "sections (id, title)");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        insertInto (
            "('" + id +
            "', '" + title +  "');"
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
                id = field;
                break;
            case 2:
                title = field;
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
                    "sections.json", 
                    new MySQL("localhost", "root", "eratostenes", "ccraw")
                );
        
        j.fillDatabaseTable();
        
    }//main()
    
}//classe JsonSection
