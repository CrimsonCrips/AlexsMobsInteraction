package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.message.UrsaUpdateBossBarMessage;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AMIPacketHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(AlterPacket.TYPE, AlterPacket.STREAM_CODEC, AlterPacket::handle);
        registrar.playToClient(UrsaUpdateBossBarMessage.TYPE, UrsaUpdateBossBarMessage.STREAM_CODEC, UrsaUpdateBossBarMessage::handle);
    }
}
