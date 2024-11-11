package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.goal.AvoidBlockGoal;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;


@Mixin(EntityCentipedeHead.class)
public abstract class AMICentipede extends Monster {


    protected AMICentipede(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityCentipedeHead centipede = (EntityCentipedeHead)(Object)this;
        if (AMInteractionConfig.LIGHT_FEAR_ENABLED) {
            centipede.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(centipede, Player.class, 50, true, false, livingEntity -> {
                return !livingEntity.isHolding(Ingredient.of(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR)) && !(livingEntity instanceof Player player && curiosLight(player));
            }));

            centipede.goalSelector.addGoal(1, new AvoidEntityGoal<>(centipede, LivingEntity.class, 4.0F, 1.5, 2, (livingEntity) -> {
                return centipede.getLastAttacker() != livingEntity && (livingEntity.isHolding(Ingredient.of(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR)) || (livingEntity instanceof Player player && curiosLight(player))) ;
            }));
            centipede.goalSelector.addGoal(1, new AvoidBlockGoal(centipede, 4,1,1.2,(pos) -> {
                BlockState state = centipede.level().getBlockState(pos);
                return state.is(AMInteractionTagRegistry.CENTIPEDE_BLOCK_FEAR);
            }));
        }
        centipede.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(centipede, LivingEntity.class, 55, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CAVE_CENTIPEDE_KILL)));


    }

    public boolean curiosLight(Player player){
        if (ModList.get().isLoaded("curiouslanterns")) {
            ICuriosItemHandler handler = CuriosApi.getCuriosInventory(player).orElseThrow(() -> new IllegalStateException("Player " + player.getName() + " has no curios inventory!"));
            return handler.getStacksHandler("belt").orElseThrow().getStacks().getStackInSlot(0).is(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR);
        } else return false;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 7))
    private boolean nearestAttack(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.LIGHT_FEAR_ENABLED;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 8))
    private boolean nearestAttack2(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.LIGHT_FEAR_ENABLED;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 9))
    private boolean nearestAttack3(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.LIGHT_FEAR_ENABLED;
    }



}
