package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon.HURT_OR_BABY;


@Mixin(EntityKomodoDragon.class)
public class AIKomodoDragon extends Mob {

    private static final Ingredient TEMPTATION_ITEMS;

    protected AIKomodoDragon(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }
    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void BlobFishGoals(CallbackInfo ci){
        EntityKomodoDragon komodoDragon = (EntityKomodoDragon)(Object)this;
        ci.cancel();
        this.goalSelector.addGoal(0, new FloatGoal(komodoDragon));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(komodoDragon));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(komodoDragon, 2D, false));
        this.goalSelector.addGoal(3, new TameableAIFollowOwner(komodoDragon, 1.2D, 6.0F, 3.0F, false));
        this.goalSelector.addGoal(4, new KomodoDragonAIJostle(komodoDragon));
        this.goalSelector.addGoal(5, new TameableAITempt(komodoDragon, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(5, new AnimalAIFleeAdult(komodoDragon, 1.25D, 32));
        this.goalSelector.addGoal(6, new KomodoDragonAIBreed(komodoDragon, 1.0D));
        this.goalSelector.addGoal(6, new RandomStrollGoal(komodoDragon, 1D, 50));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(komodoDragon, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(komodoDragon));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(komodoDragon));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(komodoDragon));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(komodoDragon));
        this.targetSelector.addGoal(4, new CreatureAITargetItems(komodoDragon, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(komodoDragon, EntityKomodoDragon.class, 50, true, false, HURT_OR_BABY));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(komodoDragon, Player.class, 150, true, true, null){
            public boolean canUse() {
                if (AInteractionConfig.weakened) {
                    return !komodoDragon.isTame() && !isBaby() && !(getHealth() <= 0.15F * getMaxHealth()) && !komodoDragon.isInLove() && super.canUse();
                } else
                {
                    return !komodoDragon.isTame() && !isBaby() && !komodoDragon.isInLove() && super.canUse();
                }
            }
        });
        targetSelector.addGoal(4, new EntityAINearestTarget3D<>(komodoDragon, LivingEntity.class, 55, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.KOMODODRAGON_KILL)){
            protected AABB getTargetSearchArea(double targetDistance) {
                return komodoDragon.getBoundingBox().inflate(10D, 1D, 10D);
            }


            public boolean canUse() {
                return super.canUse() && level().isNight() && !komodoDragon.isTame();
            }
        });
    }
    static {
        TEMPTATION_ITEMS = Ingredient.of(new ItemLike[]{Items.ROTTEN_FLESH});
    }

}
