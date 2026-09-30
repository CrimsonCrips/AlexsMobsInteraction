package com.crimsoncrips.alexsmobsinteraction.client;

import com.crimsoncrips.alexsmobsinteraction.networking.EagleBombPacket;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;


public class AMIKeyMappings {

    public static final KeyMapping DROP_BOMB = new KeyMapping(
            "key.alexsmobsinteraction.drop_bomb",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_I,
            "key.categories.alexsmobsinteraction"
    );


    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DROP_BOMB);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        while (DROP_BOMB.consumeClick()) {
            if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle eagle) {
                PacketDistributor.sendToServer(new EagleBombPacket(eagle.getId()));
            }
        }

    }

}
