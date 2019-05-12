package com.perfect.freshair.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MyByteUtils {
    public static int byteToInt(byte[] bytes)
    {
        final int size = Integer.SIZE / 8;
        ByteBuffer buff = ByteBuffer.allocate(size);
        final byte[] newBytes = new byte[size];
        for (int i = 0; i < size; i++) {
            if (i + bytes.length < size) {
                newBytes[i] = (byte) 0x00;
            } else {
                newBytes[i] = bytes[i + bytes.length - size];
            }
        }
        buff = ByteBuffer.wrap(newBytes);
        buff.order(ByteOrder.BIG_ENDIAN); // Endian에 맞게 세팅
        return buff.getInt();
    }

    public static byte[] intToBytes(int integer)
    {
        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
        buff.putInt(integer);
        buff.order(ByteOrder.BIG_ENDIAN);
        //buff.order(ByteOrder.LITTLE_ENDIAN);
        return buff.array();
    }
}
