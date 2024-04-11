package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

import static net.minecraft.world.damagesource.DamageTypes.GENERIC;


@Mixin(EntityBananaSlug.class)
public class AIBananaSlug extends Mob {

    protected AIBananaSlug(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BlobFishGoals(CallbackInfo ci){
        if(AInteractionConfig.preyfear)
            this.goalSelector.addGoal(3, new AvoidEntityGoal((EntityBananaSlug)(Object)this, LivingEntity.class, 2.0F, 1.2, 1.5, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SMALLINSECTFEAR)));
    }
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (stack.getItem() == Items.SHEARS && AInteractionConfig.bananaslugsheared) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, this.getSoundVolume(), this.getVoicePitch());
            this.spawnAtLocation(AMItemRegistry.BANANA.get());
            stack.hurtAndBreak(1, this, (p_233654_0_) -> {
            });
            this.discard();
            return InteractionResult.SUCCESS;
        }
        return type;
    }
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci){
        Iterator var4 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(0.5,0.2,0.5)).iterator();

        if(AInteractionConfig.bananaslip && AInteractionConfig.goofymode){
            while (var4.hasNext()) {
                Entity entity = (Entity) var4.next();
                if (entity instanceof Player) {
                    entity.kill();

                }


            }
        }
        }

}
