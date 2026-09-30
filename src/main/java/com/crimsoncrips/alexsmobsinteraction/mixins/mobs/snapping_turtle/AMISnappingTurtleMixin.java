package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.snapping_turtle;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;



@Mixin(EntityAlligatorSnappingTurtle.class)
public abstract class AMISnappingTurtleMixin extends Animal {

    protected AMISnappingTurtleMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;travel(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 alexsMobsInteraction$travel(Vec3 par1) {
        if (this.getData(AMIAttachments.DAY_SLEEPING)){
            return (Vec3.ZERO);
        }
        return par1;
    }








}
