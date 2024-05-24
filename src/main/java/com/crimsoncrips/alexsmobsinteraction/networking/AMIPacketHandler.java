package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class AMIPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            AlexsMobsInteraction.prefix("farseer"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(0, FarseerPacket.class, FarseerPacket::encode, FarseerPacket::decode, FarseerPacket::handle);
    }
}
