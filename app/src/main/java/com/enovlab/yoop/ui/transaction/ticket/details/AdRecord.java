package com.enovlab.yoop.ui.transaction.ticket.details;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdRecord {

    public int length;
    public int type;
    public byte[] data;

    public AdRecord(int length, int type, byte[] data) {
        Log.d("DEBUG", "Length: " + length + " Type : " + type + " Data : " + getHexString(data));
        this.length = length;
        this.type = type;
        this.data = data;
    }

    public static List<AdRecord> parseScanRecord(byte[] scanRecord) {
        List<AdRecord> records = new ArrayList<AdRecord>();

        int index = 0;
        while (index < scanRecord.length) {
            int length = scanRecord[index++];
            //Done once we run out of records
            if (length == 0) {
                break;
            }

            int type = scanRecord[index];
            //Done if our record isn't a valid type
            if (type == 0) {
                break;
            }

            byte[] data = Arrays.copyOfRange(scanRecord, index + 1, index + length);

            records.add(new AdRecord(length, type, data));
            //Advance
            index += length;
        }

        return records;
    }

    static final String HEXES = "0123456789ABCDEF";

    public static String getHexString(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static Long getHex(byte[] raw) {
        String hexString = getHexString(raw);
        return Long.parseLong(hexString, 16);
    }
}