/*
 * Copyright (C) 2017 andres.velez
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.avpsoft.jhashcalc.crypto;

import gnu.crypto.hash.BaseHash;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author andres.velez
 */
public class Base64 extends BaseHash {

    private static final int BLOCK_SIZE = 1;

    private static final int HASH_SIZE = Integer.MAX_VALUE;

    private static final String NAME = "Base64";

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public Base64() {
        super(NAME, HASH_SIZE, BLOCK_SIZE);
    }

    @Override
    public Object clone() {
        return new Base64();
    }

    @Override
    public boolean selfTest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected byte[] padBuffer() {

        int n = (int) (count % blockSize);
        int partLen = n > 0 ? blockSize - n : 0;
        byte pad[] = new byte[partLen];

        return pad;
    }

    @Override
    protected byte[] getResult() {

        return org.apache.commons.codec.binary.Base64.encodeBase64(byteArrayOutputStream.toByteArray());
    }

    @Override
    protected void resetContext() {
        if (byteArrayOutputStream != null) {
            byteArrayOutputStream.reset();
        }
    }

    @Override
    protected void transform(byte[] bytes, int i) {

        byteArrayOutputStream.write(bytes, i, blockSize());
    }

}
