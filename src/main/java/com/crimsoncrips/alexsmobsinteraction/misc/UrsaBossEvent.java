package com.crimsoncrips.alexsmobsinteraction.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.message.UrsaUpdateBossBarMessage;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.UpdateBossBarMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

public class UrsaBossEvent extends ServerBossEvent {

    private final int renderType;

    public UrsaBossEvent(Component component, int renderType) {
        super(component, BossBarColor.RED, BossBarOverlay.PROGRESS);
        this.renderType = renderType;
    }

    public int getRenderType() {
        return renderType;
    }

    public void addPlayer(ServerPlayer serverPlayer) {
        AlexsMobsInteraction.sendNonLocal(new UrsaUpdateBossBarMessage(this.getId(), renderType), serverPlayer);
        super.addPlayer(serverPlayer);
    }

    public void removePlayer(ServerPlayer serverPlayer) {
        AlexsMobsInteraction.sendNonLocal(new UrsaUpdateBossBarMessage(this.getId(), -1), serverPlayer);
        super.removePlayer(serverPlayer);
    }
}
