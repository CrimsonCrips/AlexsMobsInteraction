package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AlterPacket() implements CustomPacketPayload {

    public static final Type<AlterPacket> TYPE = new Type<>(AlexsMobsInteraction.prefix("farseer_alter"));
    public static final StreamCodec<ByteBuf, AlterPacket> STREAM_CODEC = StreamCodec.unit(new AlterPacket());

    public static void handle(AlterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> AMIRendering.ALTER_PROGRESS = 1.0F);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
