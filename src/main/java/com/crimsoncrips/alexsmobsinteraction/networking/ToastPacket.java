package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIToastManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToastPacket(Component message, long displayTimeMs) implements CustomPacketPayload {

    public static final Type<ToastPacket> TYPE = new Type<>(AlexsMobsInteraction.prefix("toast_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToastPacket> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, ToastPacket::message,
            ByteBufCodecs.VAR_LONG, ToastPacket::displayTimeMs,
            ToastPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ToastPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> AMIToastManager.addToast(message.message(), message.displayTimeMs()));
    }
}
