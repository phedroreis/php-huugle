package br.com.hkp.phphuugle.mysql;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/******************************************************************************
 * Uma classe para realizar conexoes, atualizacoes e consultas a bancos de
 * dados MySQL. Ao criar um objeto desta classe a conexao eh estabelecida.
 * 
 * @since 29 de abril de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public final class MySQL
{
    private static final String PREFIX_DATABASE_URL = "jdbc:mysql://";
    
    private final Connection connection;
    
    private final Statement statement;
    
    private ResultSet resultSet;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Ao criar o objeto a conexao ao banco de dados eh estabelecida.
     * 
     * @param server Endereco do servidor
     * 
     * @param user Id do usuario
     * 
     * @param pass Senha no servidor
     * 
     * @param database O banco ao qual se conectae
     * 
     * @throws SQLException Em caso de falha na conexao
     */
    public MySQL (
            
        final String server, 
        final String user, 
        final String pass, 
        final String database
    )
        throws SQLException {
        
        String databaseUrl = PREFIX_DATABASE_URL + server + '/' + database;
        connection = DriverManager.getConnection(databaseUrl, user, pass);
        statement = connection.createStatement();
       
    }//construtor
    
    /*[01]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Realiza uma operacao de consulta ao banco de dados.
     * 
     * @param query Uma String com a instrucao MySQL de consulta
     * 
     * @return Um objeto ResultSet com o resultado da consulta
     * 
     * @throws SQLException Em caso de falha ao realizar a consulta
     */
    public ResultSet query(final String query) throws SQLException {
        
        resultSet = statement.executeQuery(query);
        return resultSet;
      
    }//query()
    
    /*[02]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Realiza uma operacao de atualizacao do banco de dados.
     * 
     * @param update A instrucao MySQL de atualizacao
     * 
     * @return O numero de linhas afetadas pelo comando  ou 0 se nao houve
     * atualizacao
     * 
     * @throws SQLException Em caso de falha ao atualizar
     */
    public int update(final String update) throws SQLException {
        
        return statement.executeUpdate(update);
    }//update()
    
    /*[03]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Retorna os metadados da ultima consulta realizada.
     * 
     * @return Um objeto ResultSetMetaData com o metadados da ultima consulta
     * realizada
     * 
     * @throws SQLException Em caso de falha ao consultar o banco
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return resultSet.getMetaData();
    }//getMetaData()
   
    
}//classe MySQL

