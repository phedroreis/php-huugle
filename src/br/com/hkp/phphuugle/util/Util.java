package br.com.hkp.phphuugle.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/******************************************************************************
 * A classe fornece metodos utilitarios.
 * 
 * @since 10 de maio de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public final class Util {
    
    /*[01]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Le um arquivo no sistema de arquivos para um objeto String e a retorna.
     * Este arquivo deve estar codificado em UTF-8.
     * 
     * @param file O arquivo a ser lido.
     * 
     * @return Uma String com o conteudo do arquivo texto.
     * 
     * @throws IOException Em caso de erro de IO.
     */
    public static String readTextFile(final File file) throws IOException {
        return 
            new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
       
    }//readTextFile()
    
    /*[02]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Le um arquivo no sistema de arquivos para um objeto String e a retorna.
     * Este arquivo deve estar codificado em UTF-8.
     * 
     * @param filename O nome do arquivo a ser lido
     * 
     * @return Uma String com o conteudo do arquivo texto
     * 
     * @throws IOException Em caso de erro de IO
     */
    public static String readTextFile(final String filename) throws IOException {
        
        return readTextFile(new File(filename));
        
    }//readTextFile()
    
    /*[03]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Escreve o conteudo de uma String em um arquivo texto. Se o arquivo jah
     * existir seu conteudo serah substituido por esta String, e se nao existir
     * serah criado. A String deve ser UTF0.
     * 
     * @param file O arquivo
     * 
     * @param content A String codificada em UTF-8
     * 
     * @throws IOException Em caso de erro de IO
     */
    public static void writeTextFile(final File file, final String content)
    throws IOException {
        FileWriter  fw = new FileWriter(file, StandardCharsets.UTF_8);
               
        fw.write(content);
        
        fw.close();
  
    }//writeTextFile()
    
    /*[04]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Escreve o conteudo de uma String em um arquivo texto. Se o arquivo jah
     * existir seu conteudo serah substituido por esta String, e se nao existir
     * o arquivo serah criado. A String deve ser UTF8.
     * 
     * @param filename O arquivo
     * 
     * @param content A String codificada em UTF-8
     * 
     * @throws IOException Em caso de erro de IO
     */
    public static void writeTextFile(final String filename, final String content)
    throws IOException {
        
        writeTextFile(new File(filename), content);
        
    }//writeTextFile()
     
    /*[05]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Le um arquivo txt empacotado no jar.
     * 
     * @param filename O nome do arquivo empacotado no jar.
     * 
     * @return O conteudo do arquivo.
     * 
     */
    public static String getTxtResource(final String filename) {
        
        StringBuilder sb = new StringBuilder();
        
        try {    
            InputStream in = 
                new Util().getClass().getResourceAsStream(filename); 
            
            BufferedReader reader = 
                new BufferedReader (
                        
                        new InputStreamReader(in, StandardCharsets.UTF_8)
                );
            
            String line;
            
            while ((line = reader.readLine()) != null) 
                sb.append(line).append("\n");
            
            sb.append("\n");
        }//try
        catch (IOException e) {}
        
        return sb.toString();
        
    }//getTxtResource()
    
    
}//classe Util


