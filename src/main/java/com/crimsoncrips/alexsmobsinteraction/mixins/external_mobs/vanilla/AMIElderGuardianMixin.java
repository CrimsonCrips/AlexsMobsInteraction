package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFollowNearestGoal;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(ElderGuardian.class)
public abstract class AMIElderGuardianMixin extends Guardian {


    public AMIElderGuardianMixin(EntityType<? extends Guardian> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
    private void alexsMobsInteraction$customServerAiStep(CallbackInfo ci) {
        ci.cancel();
        super.customServerAiStep();
        if ((this.tickCount + this.getId()) % 1200 == 0) {
            MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 6000, 2);
            List<ServerPlayer> list = MobEffectUtil.addEffectToPlayersAround((ServerLevel)this.level(), this, this.position(), 50.0D, mobeffectinstance, 1200);
            list.forEach((p_289459_) -> {
                if (p_289459_.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AMIEnchantmentRegistry.STABILIZER.get()) > 0) {
                    p_289459_.removeEffect(mobeffectinstance.getEffect());
                } else {
                    p_289459_.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, this.isSilent() ? 0.0F : 1.0F));
                }
            });
        }

        if (!this.hasRestriction()) {
            this.restrictTo(this.blockPosition(), 16);
        }
    }

}
