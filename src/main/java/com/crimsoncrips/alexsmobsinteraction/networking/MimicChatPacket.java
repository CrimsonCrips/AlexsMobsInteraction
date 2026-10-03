package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.client.particle.MimicChatParticle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MimicChatPacket(int entityId, Component message) implements CustomPacketPayload {

    public static final Type<MimicChatPacket> TYPE = new Type<>(AlexsMobsInteraction.prefix("mimic_chat_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MimicChatPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MimicChatPacket::entityId,
            ComponentSerialization.STREAM_CODEC, MimicChatPacket::message,
            MimicChatPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MimicChatPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> MimicChatParticle.spawnAbove(message.entityId(), message.message()));
    }
}
