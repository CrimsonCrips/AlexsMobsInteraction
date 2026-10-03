package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.cockroach;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityCockroach.class)
public abstract class AMICockroach extends Mob {

    private static final int SERVANT_GLOW_HOLD = 20;

    private static final int SERVANT_GLOW_FADE = 40;

    protected AMICockroach(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean canBeLeashed() {
        return super.canBeLeashed() && !this.getData(AMIAttachments.IS_GOD) && AMIUtils.getWorshiping(this) == null;
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return super.isCurrentlyGlowing() || (this.level().isClientSide && alexsMobsInteraction$servantGlow() > 0);
    }

    @Override
    public int getTeamColor() {
        float glow = alexsMobsInteraction$servantGlow();
        if (glow > 0) {
            int color = ChatFormatting.YELLOW.getColor();
            return FastColor.ARGB32.color(0, (int) (FastColor.ARGB32.red(color) * glow), (int) (FastColor.ARGB32.green(color) * glow), (int) (FastColor.ARGB32.blue(color) * glow));
        }
        return super.getTeamColor();
    }

    private float alexsMobsInteraction$servantGlow() {
        long start = this.getData(AMIAttachments.SERVANT_GLOW_START);
        if (start <= 0)
            return 0;
        long elapsed = this.level().getGameTime() - start;
        if (elapsed < 0 || elapsed >= SERVANT_GLOW_HOLD + SERVANT_GLOW_FADE)
            return 0;
        return elapsed < SERVANT_GLOW_HOLD ? 1F : 1F - (elapsed - SERVANT_GLOW_HOLD) / (float) SERVANT_GLOW_FADE;
    }
}
