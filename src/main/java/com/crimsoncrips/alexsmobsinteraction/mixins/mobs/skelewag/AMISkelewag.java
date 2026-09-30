package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.skelewag;

import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.entity.LivingEntity;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.github.alexthe666.alexsmobs.entity.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(EntitySkelewag.class)
public abstract class AMISkelewag extends Monster {

    protected AMISkelewag(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract int getVariant();

    @Shadow
    public abstract void setVariant(int command);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void alexsMobsInteraction$init(EntityType monster, Level level, CallbackInfo ci) {
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void alexsMobsInteraction$finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get() && level.getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
            this.setVariant(2 + this.random.nextInt(2));
            this.getPassengers().forEach(Entity::discard);
        }
    }

    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return getVariant() == 2 || getVariant() == 3 ? AMILootTables.WITHERED_SKELEWAG : super.getDefaultLootTable();
    }

    @Override
    public boolean fireImmune() {
        return (getVariant() == 3 || getVariant() == 2) && AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get();
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level().getFluidState(pos).is(FluidTags.WATER) || (level().getFluidState(pos).is(FluidTags.LAVA) && AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()) ? 10.0F + level.getLightLevelDependentMagicValue(pos) - 0.5F : super.getWalkTargetValue(pos, level());
    }




    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntitySkelewag;isInWater()Z"))
    private boolean alexsMobsInteraction$travel(EntitySkelewag instance, Operation<Boolean> original){
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()){
            return original.call(instance) || this.isInLava();
        }
        return original.call(instance);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntitySkelewag;isInWaterOrBubble()Z",ordinal = 0))
    private boolean alexsMobsInteraction$tick(boolean original){
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()){
            return original || this.isInLava();
        }
        return original;
    }


    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntitySkelewag;isInWaterOrBubble()Z",ordinal = 1))
    private boolean alexsMobsInteraction$tick1(boolean original){
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()){
            return original || this.isInLava();
        }
        return original;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get() && (itemStack.is(Items.COAL) || itemStack.is(Items.CHARCOAL)) && getVariant() <= 2) {
            if (!pPlayer.isCreative()) {
                itemStack.hurtAndBreak(1, pPlayer, LivingEntity.getSlotForHand(pHand));
            }
            pPlayer.swing(pHand,true);
            pPlayer.playSound(SoundEvents.BRUSH_GENERIC, 3, this.getVoicePitch());
            setVariant(pPlayer.getRandom().nextInt(3,4));
        }

        return super.mobInteract(pPlayer, pHand);

    }


}
