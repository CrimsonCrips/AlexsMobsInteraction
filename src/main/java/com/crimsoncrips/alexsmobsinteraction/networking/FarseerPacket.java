package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FarseerPacket {

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        // Do nothing
    }

    public static FarseerPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new FarseerPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            AMIRendering.ALPHA_PROGRESS = 0.0F;
        });
        context.setPacketHandled(true);
    }
}
