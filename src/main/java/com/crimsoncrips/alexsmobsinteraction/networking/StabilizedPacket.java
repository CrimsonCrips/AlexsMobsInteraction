package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StabilizedPacket() implements CustomPacketPayload {

    public static final Type<StabilizedPacket> TYPE = new Type<>(AlexsMobsInteraction.prefix("stabilized"));
    public static final StreamCodec<ByteBuf, StabilizedPacket> STREAM_CODEC = StreamCodec.unit(new StabilizedPacket());

    public static void handle(StabilizedPacket packet, IPayloadContext context) {
        context.enqueueWork(AMIRendering::flashStabilizedVignette);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
