package com.perfect.freshair.Common;

import java.util.UUID;

public class CommonEnumeration {

    //REQUEST
    public static final int REQUEST_ENABLE_BT = 100;
    public static final int REQUEST_ACTIVITY_SCAN = 101;
    //UUID
    public final static UUID UUID_SERVICE = UUID.fromString("00002220-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_RECEIVE = UUID.fromString("00002221-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SEND = UUID.fromString("00002222-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CLIENT_CONFIGURATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static int UPDATE_MAIN_DUST_VALUE = 1;
    public static String dataUpdateAction = "com.perfect.freshair.action.DO_UPDATE";
    public static String workerTag = "com.perfect.freshair.action.my_worker";
}
