package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AlterPacket {

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        // Do nothing
    }

    public static AlterPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new AlterPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            AMIRendering.ALTER_PROGRESS = 1.0F;
        });
        context.setPacketHandled(true);
    }
}
