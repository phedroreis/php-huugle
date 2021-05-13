package br.com.hkp.phphuugle.json;

import br.com.hkp.phphuugle.mysql.MySQL;
import java.io.IOException;
import java.sql.SQLException;

/******************************************************************************
 * Classe relacionada a tabela users do banco ccraw e ao arquivo users.json.
 * Um objeto desta classe armazena todos os dados de um usuario do forum CC.
 * 
 * Estrutura da tabela users:
 * 
 * create table users
 *(
 *  id varchar(26),
 *  nick varchar(34),
 *  lev varchar(14),
 *  membergroup varchar(22),
 *  postcount varchar(6),
 *  gender varchar(9),
 *  avatar varchar(47),
 *  blurb text,
 *  signature text,
 *  signatureText text,
 *  sites text,
 *  descriptions text,
 *  profil text,
 *  primary key(id)
 *);
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
    
    //O nivel do usuario. max 14 carac.
    private String lev;
    
    //O grupo max. 22 carac.
    private String membergroup;
    
    //Num. de posts. max. 6 carac.
    private String postcount;
    
    //Genero N/A, Masculino ou Feminino
    private String gender;
    
    //Arquivo de avatar max. 47 carac.
    private String avatar;
    
    //String sem limitacao
    private String blurb;
    
    //String sem limitacao
    private String signature;
    
    //Assinatura sem tags HTML - String sem limitacao
    private String signatureText;
    
    //String sem limitacao
    private String sites;
    
    //String sem limitacao
    private String descriptions;
    
    //Perfil do usuario - String sem limitacao
    private String profil;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    public JsonUser (
        final String pathName,
        final MySQL mysql
    ) 
    throws IOException {
        
        super (
            pathName, 
            mysql, 
            "users (id, nick, lev, membergroup, postcount, gender, avatar, " +
            "blurb, signature, signatureText, sites, descriptions, profil)"
        );
    }//construtor
    
    /*[01]----------------------------------------------------------------------
       Quando chamado, este metodo insere os valores correntes dos campos do
       objeto no banco de dados.
    --------------------------------------------------------------------------*/
    private void insertInto() throws SQLException {
        
        insertInto ( 
                
            "('" + id + 
            "', '" + nick +
            "', '" + lev +
            "', '" + membergroup +
            "', '" + postcount +
            "', '" + gender +
            "', '" + avatar +
            "', '" + blurb +
            "', '" + signature +
            "', '" + signatureText +
            "', '" + sites +
            "', '" + descriptions +
            "', '" + profil + "');" 
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
                nick = field;
                break;
            case 3:
                lev = field;
                break;
            case 4:
                membergroup = field;
                break;
            case 5:
                postcount = field;
                break;
            case 6:
                gender = field;
                break;
            case 7:
                avatar = field;
                break;
            case 8:
                blurb = field;
                break;
            case 9:
                signature = field;
                break;
            case 10:
                signatureText = field;
                break;
            case 11:
                sites = field;
                break;
            case 12:
                descriptions = field;
                break;
            case 13:
                profil = field;
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
                    "users.json", 
                    new MySQL("localhost", "root", "eratostenes", "ccraw")
                );
        
        u.fillDatabaseTable();
    }
    
}//classe JsonUser
