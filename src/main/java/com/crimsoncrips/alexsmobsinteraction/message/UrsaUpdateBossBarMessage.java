package com.crimsoncrips.alexsmobsinteraction.message;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record UrsaUpdateBossBarMessage(UUID bossBar, int renderType) implements CustomPacketPayload {

    public static final Type<UrsaUpdateBossBarMessage> TYPE = new Type<>(AlexsMobsInteraction.prefix("ursa_update_boss_bar"));
    public static final StreamCodec<ByteBuf, UrsaUpdateBossBarMessage> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, UrsaUpdateBossBarMessage::bossBar,
            ByteBufCodecs.INT, UrsaUpdateBossBarMessage::renderType,
            UrsaUpdateBossBarMessage::new);

    public static void handle(UrsaUpdateBossBarMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (message.renderType == -1) {
                AlexsMobsInteraction.PROXY.removeBossBarRender(message.bossBar);
            } else {
                AlexsMobsInteraction.PROXY.setBossBarRender(message.bossBar, message.renderType);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
