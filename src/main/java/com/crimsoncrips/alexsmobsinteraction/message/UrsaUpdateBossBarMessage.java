package com.crimsoncrips.alexsmobsinteraction.message;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UrsaUpdateBossBarMessage {

    private UUID bossBar;
    private int renderType;

    public UrsaUpdateBossBarMessage(UUID bossBar, int renderType) {
        this.bossBar = bossBar;
        this.renderType = renderType;
    }


    public static UrsaUpdateBossBarMessage read(FriendlyByteBuf buf) {
        return new UrsaUpdateBossBarMessage(buf.readUUID(), buf.readInt());
    }

    public static void write(UrsaUpdateBossBarMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.bossBar);
        buf.writeInt(message.renderType);
    }

    public static void handle(UrsaUpdateBossBarMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().setPacketHandled(true);
        Player playerSided = context.get().getSender();
        if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            playerSided = AlexsMobsInteraction.PROXY.getClientSidePlayer();
        }
        if(message.renderType == -1){
            AlexsMobsInteraction.PROXY.removeBossBarRender(message.bossBar);
        }else{
            AlexsMobsInteraction.PROXY.setBossBarRender(message.bossBar, message.renderType);
        }
    }

}
