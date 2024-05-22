package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntitySnowLeopard.class)
public class AMISnowLeopard extends Mob {

    protected AMISnowLeopard(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }
    public void awardKillScore(Entity entity, int score, DamageSource src) {
        if(AMInteractionConfig.SNOW_LUCK_ENABLED){
            if (entity instanceof Goat && random.nextDouble() < 0.5) {
                entity.spawnAtLocation(Items.GOAT_HORN);
                if (random.nextDouble() < 0.05) entity.spawnAtLocation(Items.GOAT_HORN);
            } else if (entity instanceof Turtle && random.nextDouble() < 0.2) {
                entity.spawnAtLocation(Items.SCUTE);
            } else if (entity instanceof EntityFrilledShark && random.nextDouble() < 0.08) {
                entity.spawnAtLocation(AMItemRegistry.SERRATED_SHARK_TOOTH.get());
                if (random.nextDouble() < 0.01) entity.spawnAtLocation(AMItemRegistry.SERRATED_SHARK_TOOTH.get());
            } else if (entity instanceof EntityBananaSlug && random.nextDouble() < 0.2) {
                entity.spawnAtLocation(AMItemRegistry.BANANA_SLUG_SLIME.get());
            } else if (entity instanceof Rabbit) {
                entity.spawnAtLocation(Items.RABBIT_FOOT);
                if (random.nextDouble() < 0.02) entity.spawnAtLocation(Items.RABBIT_FOOT);
            }
        }
        super.awardKillScore(entity, score, src);
    }


}
