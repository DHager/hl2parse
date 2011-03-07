/**
 * Copyright (C) 2011 Darien Hager
 *
 * This code is part of the "HL2Parse" project, and is licensed under
 * a Creative Commons Attribution-ShareAlike 3.0 Unported License. For
 * either a summary of conditions or the full legal text, please visit:
 *
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Permissions beyond the scope of this license may be available
 * at http://technofovea.com/ .
 */
package com.technofovea.hl2parse.registry;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 *
 * @author Darien Hager
 */
public class BlobRaw extends BlobValue {

    ByteBuffer data;
    WeakReference<byte[]> byteVersion = null;

    public BlobRaw(String name, ByteBuffer realValue) {
        super(name);
        data = realValue.asReadOnlyBuffer();

    }

    @Override
    public DataType getType() {
        return DataType.RAW;
    }

    public ByteBuffer asBuffer() {
        return data;
    }

    public synchronized byte[] getRaw() {
        byte[] cached = byteVersion.get();
        if (cached != null) {
            return cached;
        } else {
            data.clear();
            byte[] raw = new byte[data.remaining()];
            data.get(raw);
            byteVersion = new WeakReference<byte[]>(raw);
            return raw;
        }

    }
}
