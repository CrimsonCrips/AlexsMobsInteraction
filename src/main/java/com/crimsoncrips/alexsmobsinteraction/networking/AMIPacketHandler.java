package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.message.UrsaUpdateBossBarMessage;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AMIPacketHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(AlterPacket.TYPE, AlterPacket.STREAM_CODEC, AlterPacket::handle);
        registrar.playToClient(StabilizedPacket.TYPE, StabilizedPacket.STREAM_CODEC, StabilizedPacket::handle);
        registrar.playToClient(UrsaUpdateBossBarMessage.TYPE, UrsaUpdateBossBarMessage.STREAM_CODEC, UrsaUpdateBossBarMessage::handle);
        registrar.playToClient(ToastPacket.TYPE, ToastPacket.STREAM_CODEC, ToastPacket::handle);
        registrar.playToServer(EagleBombPacket.TYPE, EagleBombPacket.STREAM_CODEC, EagleBombPacket::handle);
        registrar.playToClient(MimicChatPacket.TYPE, MimicChatPacket.STREAM_CODEC, MimicChatPacket::handle);
        registrar.playToClient(WelcomeToastPacket.TYPE, WelcomeToastPacket.STREAM_CODEC, WelcomeToastPacket::handle);
    }
}
