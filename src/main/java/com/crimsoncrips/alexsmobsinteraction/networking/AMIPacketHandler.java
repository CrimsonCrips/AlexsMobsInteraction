package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class AMIPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final String PROTOCOL_VERSION_2 = "2";
    public static final SimpleChannel FARSEER_ALTER = NetworkRegistry.newSimpleChannel(
            AlexsMobsInteraction.prefix("farseer_alter"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        FARSEER_ALTER.registerMessage(0, AlterPacket.class, AlterPacket::encode, AlterPacket::decode, AlterPacket::handle);
    }
}
