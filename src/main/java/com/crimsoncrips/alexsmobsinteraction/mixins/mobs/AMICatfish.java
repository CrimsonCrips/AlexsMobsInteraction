package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(EntityCatfish.class)
public abstract class AMICatfish extends WaterAnimal {


    @Shadow protected abstract void registerGoals();

    @Shadow public abstract int getCatfishSize();

    @Shadow public SimpleContainer catfishInventory;

    protected AMICatfish(EntityType<? extends WaterAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyReturnValue(method = "isFood", at = @At("RETURN"),remap = false)
    private boolean alexsMobsInteraction$isFood(boolean original,@Local Entity entity) {
        if (AlexsMobsInteraction.TARGETS_CONFIG.CANNIBALISM_ENABLED.get()) {
            if (this.getCatfishSize() == 2) {
                return !entity.getType().is(AMTagRegistry.CATFISH_IGNORE_EATING) && entity instanceof Mob && !(entity instanceof EntityCatfish catfish && catfish.getCatfishSize() == 2 ) && entity.getBbHeight() <= 1.0F;
            } else {
                return entity instanceof ItemEntity && ((ItemEntity)entity).getAge() > 35;
            }
        }
        return original;
    }

}
