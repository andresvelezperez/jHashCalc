/*
 * Copyright (C) 2017 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more deta ils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.avpsoft.jhashcalc.crypto;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase encargada de calcular las funciones hash <br>
 *
 * http://www.wadalbertia.org/foro/viewtopic.php?t=3714
 * https://omniumpotentior.wordpress.com/2007/10/01/jhashcalc-implementacion-libre-de-hashcalc/
 *
 * @author Death Master
 * @author Federico Tello Gentile
 * @version 1.2
 * @see gnu.crypto.hash.BaseHash
 * @see gnu.crypto.hash.IMessageDigest
 */
public class Hash {

    // ATRIBUTOS
    /**
     * Tamaño de buffer para lectura de ficheros de 16 Kb
     */
    private static final int BUFFER_SIZE = 16 * 1024;
    /**
     * Set de algoritmos soportados por el programa
     */
    public static final List<String> GNU_HASHES;

    static {
        GNU_HASHES = new ArrayList<>();
        GNU_HASHES.add("Base64");
        GNU_HASHES.add("MD2");
        GNU_HASHES.add("MD4");
        GNU_HASHES.add("MD5");
        GNU_HASHES.add("SHA1");
        GNU_HASHES.add("SHA-256");
        GNU_HASHES.add("SHA-384");
        GNU_HASHES.add("SHA-512");
        GNU_HASHES.add("RIPEMD-128");
        GNU_HASHES.add("RIPEMD-160");
        GNU_HASHES.add("Tiger");
        GNU_HASHES.add("Whirlpool");
    }

    // MÉTODOS
    /**
     * Obtiene los valores de resumen para un fichero
     *
     * @param fichero Fichero cuyos valores de resumen queremos obtener
     * @param algoritmos Lista de algoritmos a calcular
     * @return Mapa de algoritmos y sus resúmenes correspondientes
     * @throws java.io.IOException
     */
    public static Map<String, String> getHash(File fichero, String... algoritmos) throws IOException {

        return getHash(fichero, Arrays.asList(algoritmos));
    }

    /**
     * Obtiene los valores de resumen para un fichero
     *
     * @param fichero Fichero cuyos valores de resumen queremos obtener
     * @param algoritmos Lista de algoritmos a calcular
     * @return Mapa de algoritmos y sus resúmenes correspondientes
     * @throws java.io.IOException
     */
    public static Map<String, String> getHash(File fichero, List<String> algoritmos) throws IOException {

        // Crea un HashMap de algoritmos e instancias de objetos Hash
        final Map<String, IMessageDigest> mds = new HashMap<>();

        // Recorre la lista de algoritmos solicitados
        for (String algoritmo : algoritmos) {
            // Comprueba que el algoritmo esté soportado
            if (GNU_HASHES.contains(algoritmo)) {
                // Genera una instancia dle algoritmo
                if (algoritmo.compareToIgnoreCase("Base64") == 0) {
                    
                } else {
                    mds.put(algoritmo, HashFactory.getInstance(algoritmo));
                }
            }
        }

        // Genera un flujo de entrada
        InputStream is = new FilterInputStream(new BufferedInputStream(
                new FileInputStream(fichero))) {
                    /* 
                     * Anula y reimplementa el método de lectura "read"
                     * Implementación por Federico <federicotg EN gmail PUNTO com>
                     */
                    @Override
                    public int read(byte[] b, int off, int len) throws IOException {
                        int leido = this.in.read(b, off, len);
                        if (leido != -1) {
                            for (IMessageDigest md : mds.values()) {
                                md.update(b, off, leido);
                            }
                        }
                        return leido;
                    }
                };

        // Declara un buffer de tamaño BUFFER_SIZE
        byte[] buffer = new byte[BUFFER_SIZE];

        // Lee el fichero
        while (is.read(buffer) != -1) {
            // No hay que hacer nada, el trabajo se hace en el FilterInputStream
        }

        // Genera un HashMap con los resultados
        Map<String, String> resultados = new HashMap<>();
        // Recorre la lista de algoritmos solicitados
        for (String algoritmo : algoritmos) {
            // Introduce los resultados en la tabla
            resultados.put(algoritmo,
                    getStringFromBytes(mds.get(algoritmo).digest()));
        }

        // Devuelve los resultados
        return resultados;
    }

    /**
     * Devuelve el resumen de la cadena de texto proporcionada
     *
     * @param textoEnClaro Cadena cuyo resumen deseamos calcular
     * @param algoritmo Algoritmo de resumen utilizado
     * @return Resumen de la cadena de texto proporcionada
     */
    public static String getHash(String textoEnClaro, String algoritmo) {
        // Obtiene una instancia del algoritmo
        IMessageDigest md;
        if (algoritmo.compareToIgnoreCase("Base64") == 0) {
            md = new Base64();
        } else {
            md = HashFactory.getInstance(algoritmo);
        }
        // Genera un vector de bytes con el texto
        byte[] textBytes = textoEnClaro.getBytes();

        // Actualiza el proceso de resumen byte a byte
        for (int i = 0; i < textBytes.length; i++) {
            md.update(textBytes[i]);
        }
        // Finaliza el proceso de resumen
        byte[] resumen = md.digest();
        // Devuelve el valor obtenido
        return getStringFromBytes(resumen);
    }

    /**
     * Convierte el vector de bytes en una cadena
     *
     * @param resumen Vector de bytes del resumen
     * @return Cadena de texto con el resumen
     */
    private static String getStringFromBytes(byte[] resumen) {
        // Genera una cadena hexadecimal
        StringBuilder hexString = new StringBuilder();
        // Recorre el vector de bytes
        for (int i = 0; i < resumen.length; i++) {
            // Convierte el valor
            // Añade el caracter
            hexString.append(String.format("%02x", (0xFF & resumen[i])));
        }
        // Devuelve la cadena de texto
        return hexString.toString();
    }

    /**
     * 
     * @param hexValue
     * @return 
     */
    public static String hexToASCII(String hexValue) {
        
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2) {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

}
