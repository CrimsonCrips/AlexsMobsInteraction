package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.FalconBombing;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EagleBombPacket(int eagleId) implements CustomPacketPayload {

    public static final Type<EagleBombPacket> TYPE = new Type<>(AlexsMobsInteraction.prefix("eagle_bomb"));
    public static final StreamCodec<ByteBuf, EagleBombPacket> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(EagleBombPacket::new, EagleBombPacket::eagleId);

    public static void handle(EagleBombPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (AlexsMobsInteraction.COMMON_CONFIG.BIRD_BOMBING_ENABLED.get() && player.level().getEntity(packet.eagleId()) instanceof EntityBaldEagle eagle && eagle.isOwnedBy(player) && eagle.isLaunched()) {
                FalconBombing.dropBomb(eagle, player);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
