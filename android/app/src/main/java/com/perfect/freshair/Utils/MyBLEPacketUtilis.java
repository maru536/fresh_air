package com.perfect.freshair.Utils;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MyBLEPacketUtilis {
    // this utility is for cc41-a ble module's ibeacon mode

    static final int ADVERTISEMENT_DATA_TYPE_FLAG = 0x1;
    static final int ADVERTISEMENT_DATA_TYPE_LOCAL_NAME = 0x09;
    static final int ADVERTISEMENT_DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;

    public static byte[] getMajorMinor(byte[] data)
    {
        /* example
        {2 1 6} {26 255 76 0 2 21 (116 39 139 218) (182 68 69 32) (143 12 114 14) (175 5 153 53) (255 224) (255 225) 197} {8 9 (100 117 115 116 66 76 69)}
                                       IBE UUID0      IBE1 UUID1	 IBE2 UUID2	    IBE3 UUID3       MAJ      MINO  MEA(POWER) 	    device name
        */
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<data.length; i++)
        {
            int temp = data[i] & 0xff;
            builder.append(temp+" ");
        }
        Log.i("bytes", builder.toString());
        byte[] bytes = null;
        int idx = 0;
        int length = data[idx++];
        int type = 0;
        while(length > 0 && idx < data.length)
        {
            type = 0;
            type |= ((0xff & data[idx]));
            if(type == ADVERTISEMENT_DATA_TYPE_MANUFACTURER_SPECIFIC_DATA)
            {
                idx+=21;
                if((idx+4)<data.length)
                {
                    bytes = Arrays.copyOfRange(data, idx,idx+4);
                    break;
                }else
                {
                    break;
                }
            }else
            {
                idx += (length);
                length = 0;
                length |= ((0xff & data[idx++]));
            }
        }

        return bytes;
    }

    public static int getMajor(byte[] data)
    {
        int ret = 0;
        if(data == null)
            return ret;
        ret |= (0xff & data[1]);
        ret |= ((0xff & data[0])<<8);
        return ret;
    }

    public static int getMinor(byte[] data)
    {
        int ret = 0;
        if(data == null)
            return ret;
        ret |= (0xff & data[3]);
        ret |= ((0xff & data[2])<<8);
        return ret;
    }
}
