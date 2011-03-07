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

import com.technofovea.hl2parse.ParseUtil;
import com.technofovea.hl2parse.registry.BlobFolder.FolderItemType;
import com.technofovea.hl2parse.registry.BlobValue.DataType;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class RegParser {

    private static final Logger logger = LoggerFactory.getLogger(RegParser.class);

    static final int HEADER_VAL = 0x5001;
    static final int HEADER_COMPRESSED = 0x4301;
    static final Charset charset = Charset.forName("UTF-8");

    

    public static ByteBuffer readSlice(ByteBuffer source) {
        return readSlice(source, source.remaining());
    }

    public static ByteBuffer readSlice(ByteBuffer source, int length) {
        int originalLimit = source.limit();

        source.limit(source.position() + length);
        ByteBuffer sub = source.slice();
        source.position(source.limit());
        source.limit(originalLimit);
        sub.order(ByteOrder.LITTLE_ENDIAN);
        return sub;
    }

    //TODO fold this into ParseUtil somehow
    public static String getText(ByteBuffer source) {
        int pos = source.position();
        int end = source.limit();
        while (source.remaining() > 0) {
            if (source.get() == ParseUtil.NULL_TERMINATOR) {
                end = source.position() - 1;
                break;
            }
        }
        source.position(pos);
        source.limit(end);

        return charset.decode(source).toString();
    }

    public static CellCollection parseCell(ByteBuffer buf) throws BlobParseFailure {
        try {
            CellCollection ret = new CellCollection();

            ByteBuffer mybuf = readSlice(buf);

            int magic = mybuf.getShort();


            if (magic != HEADER_VAL) {
                if (magic == HEADER_COMPRESSED) {
                    throw new BlobParseFailure("The blob given appears to be compressed, but no compressed blob was expected.");
                } else {
                    throw new BlobParseFailure("Expected magic value " + Integer.toHexString(HEADER_VAL) + " but got " + Integer.toHexString(magic));
                }
            }

            int childrenLen = mybuf.getInt() - 10; // Minus ten because it includes magic(2) len (4) padding (4)
            int nullPadded = mybuf.getInt();
            if (magic != HEADER_VAL) {
                throw new BlobParseFailure("Expected magic value " + Integer.toHexString(HEADER_VAL) + " but got " + Integer.toHexString(magic));
            }
            if (mybuf.remaining() < childrenLen + nullPadded) {
                throw new BlobParseFailure("Content length (" + childrenLen + " and null padding (" + nullPadded + ") are bigger than the total remaining bytes (" + mybuf.remaining() + ")");
            }


            mybuf.limit(mybuf.position() + childrenLen + nullPadded);
            while (mybuf.remaining() > nullPadded) {
                int descriptorLen = mybuf.getShort();
                int payloadLen = mybuf.getInt();
                assert ((payloadLen + descriptorLen) <= mybuf.remaining());

                ByteBuffer desc = readSlice(mybuf, descriptorLen);
                ByteBuffer payload = readSlice(mybuf, payloadLen);
                // Determine if scalar or if subtree
                ret.items.add(new CellItem(desc, payload));
            }
            mybuf.position(mybuf.position() + nullPadded);
            ret.underflow = mybuf.remaining();

            return ret;
        } catch (BufferUnderflowException bue) {
            throw new BlobParseFailure("A buffer-underflow occurred.", bue);
        }



    }

    public static ByteBuffer decompress(ByteBuffer originalBuffer) throws BlobParseFailure {
        logger.debug("Inflating a compressed binary section, initial length (including header) is {}",originalBuffer.remaining());
        int headerSkip = 2;
        Inflater inflater = new Inflater(true);

        try {
            // Make a view so we don't risk altering the parent
            ByteBuffer mybuf = readSlice(originalBuffer);

            int magic = mybuf.getShort();
            if (magic != HEADER_COMPRESSED) {
                if (magic == HEADER_VAL) {
                    throw new BlobParseFailure("The blob given appears to be uncompressed,but a compressed blob was expected.");
                } else {
                    throw new BlobParseFailure("Expected magic value " + Integer.toHexString(HEADER_COMPRESSED) + " but got " + Integer.toHexString(magic));
                }
            }

            // Includes length of magic header etc?
            int wholeLen = mybuf.getInt(); // Includes bytes starting with itself            
            int compressedLen = wholeLen - 20;            
            int x1 = mybuf.getInt();
            int decompressedLen = mybuf.getInt();
            int x2 = mybuf.getInt();
            int compLevel = mybuf.getShort();

            logger.debug("Header claims payload compressed length is {}, deflated length is {}, compression level {}", new Object[]{compressedLen,decompressedLen,compLevel});

            if (mybuf.remaining() < compressedLen) {
                throw new BlobParseFailure("The buffer remainder is too small (" + mybuf.remaining() + ") to contain the amount of data the header specifies (" + compressedLen + ").");
            }

            mybuf.limit(mybuf.position() + compressedLen);

            byte[] compressed = new byte[mybuf.remaining()];
            mybuf.get(compressed);
            inflater.setInput(compressed, headerSkip, compressed.length - headerSkip);
            byte[] decompressed = new byte[decompressedLen];
            try {
                logger.debug("Beginning decompression");
                inflater.inflate(decompressed);
                logger.debug("Decompression successful");
            } catch (DataFormatException ex) {
                throw new BlobParseFailure("An error occurred attempting to decompress the data", ex);
            }

            ByteBuffer newBuf = ByteBuffer.wrap(decompressed);
            newBuf.order(ByteOrder.LITTLE_ENDIAN);

            return newBuf;
        } catch (BufferUnderflowException bue) {
            throw new BlobParseFailure("A buffer-underflow occurred.", bue);
        }
    }

    public static BlobFolder parseClientRegistry(ByteBuffer payload) throws BlobParseFailure {
        logger.debug("Beginning parse of client registry blob data");

        List<BlobFolder> folders = readBox(payload);
        if (folders.size() != 1) {
            throw new BlobParseFailure("Client registry should have had only one starting folder, but found  " + folders.size());
        }
        return folders.get(0);
    }

    static void readFolderItem(CellItem spec, BlobFolder parent) throws BlobParseFailure {
        int typeNum = spec.getMeta().get();
        FolderItemType thisType = FolderItemType.fromNumber(typeNum);
        
        switch (thisType) {
            case FOLDER:
                List<BlobFolder> folders = readBox(spec.getPayload());
                for (BlobFolder f : folders) {
                    if (parent.getFolders().containsKey(f.getName())) {
                        throw new BlobParseFailure("Duplicate folder name encountered.");
                    }
                    parent.folders.put(f.name, f);
                }
                break;
            case FILE:
                List<BlobValue> values = populateValues(spec.getPayload());
                for (BlobValue v : values) {
                    if (parent.getValues().containsKey(v.getName())) {
                        throw new BlobParseFailure("Duplicate item name encountered.");
                    }
                    parent.values.put(v.getName(), v);
                }
                break;

            default:
                throw new BlobParseFailure("Unexpected folder item type: " + typeNum);
        }
    }

    static void populateFolder(ByteBuffer payload, BlobFolder parent) throws BlobParseFailure {
        logger.trace("Populating folder {}",parent.getName());
        CellCollection bd = parseCell(payload);
        for (CellItem is : bd) {
            readFolderItem(is, parent);
        }
    }

    static List<BlobFolder> readBox(ByteBuffer payload) throws BlobParseFailure {
        List<BlobFolder> ret = new ArrayList<BlobFolder>();

        CellCollection bd = parseCell(payload);


        for (CellItem is : bd) {
            String name = getText(is.getMeta());
            BlobFolder current = new BlobFolder(name);
            ret.add(current);

            // Populate folder
            populateFolder(is.getPayload(), current);
        }

        return ret;

    }

    static List<BlobValue> populateValues(ByteBuffer payload) throws BlobParseFailure {
        List<BlobValue> ret = new ArrayList<BlobValue>();
        CellCollection bd = RegParser.parseCell(payload);
        for (CellItem i : bd) {
            String name = getText(i.getMeta());
            BlobValue bv = readScalar(i.getPayload(), name);
            ret.add(bv);
        }

        return ret;
    }

    static BlobValue readScalar(ByteBuffer payload, String name) throws BlobParseFailure {
        CellCollection bd = RegParser.parseCell(payload);
        if (bd.items.size() != 2) {
            throw new BlobParseFailure("A two-item scalar data payload with type and item were expected, but " + bd.items.size() + " children were encountered.");
        }

        DataType type = null;
        ByteBuffer realValue = null;

        for (CellItem is : bd) {
            int kind = is.getMeta().get();
            switch (kind) {
                case 1:
                    int typeNum = is.getPayload().get();
                    if (type != null) {
                        throw new BlobParseFailure("Scalar data-type block encountered twice, should only be seen once.");
                    }
                    type = DataType.fromNumber(typeNum);
                    if (type == null) {
                        throw new BlobParseFailure("Unexpected scalar type. Expected DWORD, RAW, or TEXT, but an unrecognized number was encountered: " + typeNum);
                    }
                    break;
                case 2:
                    if (realValue != null) {
                        throw new BlobParseFailure("Scalar raw-value block encountered twice, should only be seen once.");
                    }
                    realValue = is.getPayload();
                    break;
                default:
                    throw new BlobParseFailure("Expected a two-item payload with scalar type and scalar value, but encountered an unrecognized discriminator: " + kind);

            }
        }

        if (type == null) {
            throw new BlobParseFailure("No data-type block was found for scalar.");
        }
        if (realValue == null) {
            throw new BlobParseFailure("No raw data block was found for scalar.");
        }

        switch (type) {
            case DWORD:
                return new BlobDword(name, realValue);
            case RAW:
                return new BlobRaw(name, realValue);
            case TEXT:
                return new BlobText(name, realValue);
            default:
                // Programmer error
                throw new BlobParseFailure("Not configured to return data type: " + type);

        }



    }
}
