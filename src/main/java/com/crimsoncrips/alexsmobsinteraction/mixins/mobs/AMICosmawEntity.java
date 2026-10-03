package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityCosmaw.class)
public abstract class AMICosmawEntity extends TamableAnimal {

    private static final float WEAKENED_PULSE_SPEED = 0.15F;

    private static final float WEAKENED_MIN_GLOW = 0.3F;

    protected AMICosmawEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (this.level().isClientSide)
            return;
        boolean weakened = AlexsMobsInteraction.COMMON_CONFIG.COSMAW_WEAKENED_ENABLED.get() && this.hasEffect(MobEffects.WEAKNESS);
        if (this.getData(AMIAttachments.COSMAW_WEAKENED) != weakened)
            this.setData(AMIAttachments.COSMAW_WEAKENED, weakened);
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return super.isCurrentlyGlowing() || (this.level().isClientSide && this.getData(AMIAttachments.COSMAW_WEAKENED));
    }

    @Override
    public int getTeamColor() {
        if (this.level().isClientSide && this.getData(AMIAttachments.COSMAW_WEAKENED)) {
            float wave = (Mth.sin(this.tickCount * WEAKENED_PULSE_SPEED * Mth.PI) + 1F) / 2F;
            float glow = Mth.lerp(wave, WEAKENED_MIN_GLOW, 1F);
            int color = ChatFormatting.RED.getColor();
            return FastColor.ARGB32.color(0, (int) (FastColor.ARGB32.red(color) * glow), (int) (FastColor.ARGB32.green(color) * glow), (int) (FastColor.ARGB32.blue(color) * glow));
        }
        return super.getTeamColor();
    }
}
