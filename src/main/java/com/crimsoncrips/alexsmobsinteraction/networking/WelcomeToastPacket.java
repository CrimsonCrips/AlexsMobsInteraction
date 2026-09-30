package com.crimsoncrips.alexsmobsinteraction.networking;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIToastManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WelcomeToastPacket() implements CustomPacketPayload {

    public static final Type<WelcomeToastPacket> TYPE = new Type<>(AlexsMobsInteraction.prefix("welcome_toast_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WelcomeToastPacket> STREAM_CODEC = StreamCodec.unit(new WelcomeToastPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WelcomeToastPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            int seconds = AlexsMobsInteraction.CLIENT_CONFIG.WELCOME_TOAST_SECONDS.get();
            if (seconds >= 1) {
                AMIToastManager.addToast(Component.translatable("misc.alexsmobsinteraction.welcome_toast_message"), seconds * 1000L);
            }

            if (AlexsMobsInteraction.CLIENT_CONFIG.WARNING_ENABLED.get() && AlexsMobsInteraction.CLIENT_CONFIG.PHOTOSENSITIVITY_ENABLED.get()) {
                AMIToastManager.addToast(Component.translatable("misc.alexsmobsinteraction.warning_message"), 5 * 1000L);
            }
        });
    }
}