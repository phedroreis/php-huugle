package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * Classe relacionada a tabela users do banco cc e ao arquivo users.json.
 * Um objeto desta classe armazena todos os dados de um usuario do forum CC.
 * 
 * Estrutura da tabela users:
 * 
    CREATE TABLE `users` (
      `id` varchar(26) COLLATE utf8mb4_bin NOT NULL,
      `nick` varchar(34) COLLATE utf8mb4_bin NOT NULL,
      `lev` varchar(14) COLLATE utf8mb4_bin DEFAULT NULL,
      `membergroup` varchar(22) COLLATE utf8mb4_bin DEFAULT NULL,
      `postcount` smallint unsigned DEFAULT NULL,
      `gender` enum('M', 'F') DEFAULT NULL,
      `avatar` varchar(47) COLLATE utf8mb4_bin DEFAULT NULL,
      `blurb` text COLLATE utf8mb4_bin DEFAULT NULL,
      `signature` text COLLATE utf8mb4_bin DEFAULT NULL,
      `signatureText` text COLLATE utf8mb4_bin DEFAULT NULL,
      `sites` text COLLATE utf8mb4_bin DEFAULT NULL,
      `descriptions` text COLLATE utf8mb4_bin DEFAULT NULL,
      `profil` text COLLATE utf8mb4_bin DEFAULT NULL,
       PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
 * 
 * @since 10 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
public class JsonUser extends JsonObject {
    
    //chave prim. max. 26 caract. 
    private String id;
    
    //max. 34 caract.
    private String nick;
    
    //O nivel do usuario. max 14 carac. ou null
    private String lev;
    
    //O grupo max. 22 carac. ou null
    private String membergroup;
    
    //Num. de posts. smallint unsigned ou null
    private String postcount;
    
    //Genero null , M ou F
    private String gender;
    
    //Arquivo de avatar max. 47 carac. ou null
    private String avatar;
    
    //text ou null
    private String blurb;
    
    //text ou null
    private String signature;
    
    //Assinatura sem tags HTML - text ou null
    private String signatureText;
    
    //text ou null
    private String sites;
    
    //text ou null
    private String descriptions;
    
    //text ou null
    private String profil;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
     /**
     * Cria um objeto para acessar a tabela users
     * 
     * @param pathName O nome do arquivo json a ser lido com os dados para a 
     * tabela
     * 
     * @param mysql Um objeto de conexao com o banco de dados
     * 
     * @throws IOException Em caso de erro de IO
     */
    public JsonUser (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super (pathName, mysql, "users");
        
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        sqlCommand ( 
                
            "(" + id + 
            SEP + nick + 
            SEP + lev + 
            SEP + membergroup + 
            SEP + postcount + 
            SEP + gender + 
            SEP + avatar + 
            SEP + blurb +
            SEP + signature +
            SEP + signatureText +
            SEP + sites +
            SEP + descriptions +
            SEP + profil + ");" 
            
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
                id = betweenQuotes(field);
                break;
            case 2:
                nick = betweenQuotes(field);
                break;
            case 3:
                lev = betweenQuotes(field);
                break;
            case 4:
                membergroup = betweenQuotes(field);
                break;
            case 5:
                postcount = betweenQuotes(field);
                break;
            case 6:
                if (field.equals(NA))
                    gender = betweenQuotes(field);
                else
                    gender = betweenQuotes("" + field.charAt(0));
                break;
            case 7:
                avatar = betweenQuotes(field);
                break;
            case 8:
                blurb = betweenQuotes(field);
                break;
            case 9:
                signature = betweenQuotes(field);
                break;
            case 10:
                signatureText = betweenQuotes(field);
                break;
            case 11:
                sites = betweenQuotes(field);
                break;
            case 12:
                descriptions = betweenQuotes(field);
                break;
            case 13:
                profil = betweenQuotes(field);
                insertInto();//Grava todos os atributos no banco de dados
       
        }//switch
        
    }//put()
    
  
    /*[02]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Le o arquivo users.json e grava todos seus registros na tabela users do
     * banco de dados. 
     * 
     * @param args n/a
     * 
     * @throws IOException Em caso de erro de IO
     * 
     * @throws SQLException Erro ao atualizar a tabela
     */
    public static void main(String[] args) throws IOException, SQLException {
        JsonUser u = 
                new JsonUser (
                    "json/users.json", 
                    new MySQL("localhost", "root", "eratostenes", "cc")
                );
        
        u.fillDatabaseTable(1);
    }
    
}//classe JsonUser
