package com.crimsoncrips.alexsmobsinteraction;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;

import java.util.UUID;

public class AMICommonProxy {

    public void clientInit() {
    }

    public void init(IEventBus modEventBus) {
    }

    public void removeBossBarRender(UUID bossBar) {
    }

    public void setBossBarRender(UUID bossBar, int renderType) {
    }

    public Player getClientSidePlayer() {
        return null;
    }
}
