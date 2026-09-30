package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.leafcutter;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityLeafcutterAnt.class)
public abstract class AMILeafcutterAntMixin extends Animal {

    protected AMILeafcutterAntMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }







    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void alexsMobsInteraction$finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, SpawnGroupData spawnDataIn, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (AlexsMobsInteraction.COMMON_CONFIG.ANT_WAR_ENABLED.get()) {
            this.setData(AMIAttachments.VARIANT, random.nextBoolean() ? 1 : 2);
        } else this.setData(AMIAttachments.VARIANT, 1);
    }


}
