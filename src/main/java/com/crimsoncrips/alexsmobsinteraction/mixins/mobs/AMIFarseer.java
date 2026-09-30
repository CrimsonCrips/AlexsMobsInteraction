package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityFarseer.class)
public abstract class AMIFarseer extends Mob {


    @Shadow protected abstract boolean canUseLaser();

    protected AMIFarseer(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }



    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (getTarget() instanceof Player player && this.canUseLaser()){
            if (AMIEnchantmentRegistry.getLevel(player.level(), player.getItemBySlot(EquipmentSlot.HEAD), AMIEnchantmentRegistry.STABILIZER) > 0){
                AMIUtils.awardAdvancement(player,"repel","repel");
            } else if (player.getData(AMIAttachments.STALK_DELAY) >= 0) {
                float stalkTime = player.getData(AMIAttachments.STALK_TIME);
                AMIAttachments.setIfChanged(player, AMIAttachments.STALK_DELAY, 100);
                AMIAttachments.setIfChanged(player, AMIAttachments.STALK_TIME, stalkTime <= 1.5 ? stalkTime + 0.005F : stalkTime);
            }
        }
    }


}
