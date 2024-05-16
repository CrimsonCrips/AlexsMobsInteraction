package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(EntityLobster.class)
public class AMILobster extends Mob {

    protected AMILobster(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        if (AMInteractionConfig.lobsternight) {
            return AMEntityRegistry.rollSpawn(AMConfig.lobsterSpawnRolls, this.getRandom(), spawnReasonIn) && level().isNight();
        } else {
            return AMEntityRegistry.rollSpawn(AMConfig.lobsterSpawnRolls, this.getRandom(), spawnReasonIn);
        }
    }
}
