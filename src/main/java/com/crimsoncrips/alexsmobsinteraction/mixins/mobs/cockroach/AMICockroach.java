package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.cockroach;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.ACCompat;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;


@Mixin(EntityCockroach.class)
public abstract class AMICockroach extends Mob implements AsmonRoach {

    private int conversionTime;

    @Shadow @Final protected static EntityDimensions STAND_SIZE;

    protected AMICockroach(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }



    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        EntityCockroach cockroach = (EntityCockroach)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.COCKROACH_MUTATION_ENABLED.get() && ModList.get().isLoaded("alexscaves")) {
            if (ACCompat.toxicCaves(cockroach)){
                ++conversionTime;
            }
            if (conversionTime > 360 && !this.level().isClientSide) {
                ACCompat.gammaroach().spawn((ServerLevel) this.level(), BlockPos.containing(this.getPosition(1)), MobSpawnType.MOB_SUMMONED);
                this.remove(RemovalReason.DISCARDED);
            }
        }

    }




    @Override
    @Nullable
    public Entity getWorshiping() {
        if (!level().isClientSide) {
            final UUID id = AMIAttachments.getUUID(this, AMIAttachments.WORSHIPING_UUID);
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }





    @Override
    public boolean canBeLeashed() {
        return super.canBeLeashed() && !this.getData(AMIAttachments.IS_GOD) && getWorshiping() == null;
    }
}
