package br.com.hkp.phphuugle.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/******************************************************************************
 * Permite ler linha a linha um arquivo texto codificado em UTF8. A leitura eh
 * bufferizada. Se nenhum buffer for especificado, serah utilizado um buffer
 * default de 2048 bytes.
 * 
 * @since 10 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 ******************************************************************************/
final class TextLineReader {
    
    private final BufferedReader bufferedReader;
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Construtor da classe.
     * 
     * @param file O arquivo a ser lido.
     * @param buffer O tamanho em bytes do buffer de leitura.
     * @throws IOException Em caso de erro de IO.
     */
    public TextLineReader(final File file, final int buffer) 
        throws IOException {
        
        bufferedReader =  
            new BufferedReader (
               new FileReader(file, StandardCharsets.UTF_8), buffer
            );
    }//construtor
   
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
     /**
     * Construtor da classe.
     * 
     * @param path O caminho e nome do arquivo a ser lido.
     * @param buffer O tamanho em bytes do buffer de leitura.
     * @throws IOException Em caso de erro de IO.
     */
    public TextLineReader(final String path, final int buffer) 
        throws IOException {
        
        this(new File(path), buffer);
        
    }//construtor
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Construtor da classe. 
     * 
     * @param file O arquivo a ser lido.
     * @throws IOException Em caso de erro de IO.
     */
    public TextLineReader(final File file) throws IOException {
        
        this(file, 2048);
        
    }//construtor
    
    /*[00]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Construtor da classe.
     * 
     * @param path O caminho e nome do arquivo a ser lido.
     * @throws IOException Em caso de erro de IO.
     */
    public TextLineReader(final String path) throws IOException {
        
        this(new File(path));
       
    }//construtor
    
    /*[01]----------------------------------------------------------------------
    
    --------------------------------------------------------------------------*/
    /**
     * Quando chamado pela 1 vez, este metodo retorna a 1a linha do arquivo. 
     * Cada chamada subsequente retorna a linha subsequente a linha 
     * anteriormente retornada. Nao havendo mais linhas a serem lidas o metodo
     * retornarah null e fechara o arquivo.
     * 
     * @return A 1a linha do arquivo quando executado na 1a vez, ou a linha 
     * subsequente a linha anteriormente retornada. Ou null qunado nao houver 
     * mais linhas a serem lidas. Nesse caso o arquivo eh fechado.
     * 
     * @throws IOException Em casso de erro de IO.
     */
    public String readLine() throws IOException {
        
        String line;
        
        if ((line = bufferedReader.readLine()) != null)
            return line;
        else {
            bufferedReader.close();
            return null;
        }//if-else
        
    }//readLine()
   
}//classe TextLineReader
