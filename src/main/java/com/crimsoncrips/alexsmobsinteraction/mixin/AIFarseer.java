package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.enchantment.AMEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static com.github.alexthe666.alexsmobs.client.event.ClientEvents.renderStaticScreenFor;


@Mixin(EntityFarseer.class)
public class AIFarseer extends Mob {

    int loop = 10;

    private LivingEntity lastTickTarget;

    private void StaticScreen() {
        renderStaticScreenFor = 20;
    }
    protected AIFarseer(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void FarseerGoals(CallbackInfo ci){
        if (AInteractionConfig.farseerhumanlikeattack) {
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Raider.class, 3, false, true, null));
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Villager.class, 3, false, true, null));
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, WanderingTrader.class, 3, false, true, null));
        }
    }
    public void awardKillScore(Entity entity, int score, DamageSource src) {
        if(entity instanceof LivingEntity living){
            final CompoundTag emptyNbt = new CompoundTag();
            living.addAdditionalSaveData(emptyNbt);
            emptyNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
            living.readAdditionalSaveData(emptyNbt);

        }
        super.awardKillScore(entity, score, src);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.farseeralter){
            LivingEntity currentTarget = getTarget();
            if (currentTarget instanceof Player player && loop >= 0) {
                loop--;
                StaticScreen();
                Inventory inv = player.getInventory();
                if (!(player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AIEnchantmentRegistry.STABILIZER.get()) > 0)) {
                    for (int i = 0; i < 9 - 1; i++) {
                        ItemStack current = inv.getItem(i);
                        int j = random.nextInt(i + 1, 9);

                        // swap
                        ItemStack to = inv.getItem(j);
                        inv.setItem(j, current);
                        inv.setItem(i, to);
                    }
                }
                if (lastTickTarget != currentTarget) {
                    int something = getRandom().nextInt(6);
                    switch (something) {
                        case 0:
                            player.sendSystemMessage(Component.translatable("alexinteraction.farseerspeech0"));
                            break;
                        case 1:
                            player.sendSystemMessage(Component.translatable("alexinteraction.farseerspeech1"));
                            break;
                        case 2:
                            player.sendSystemMessage(Component.translatable("alexinteraction.farseerspeech2"));
                            break;
                        case 3:
                            player.sendSystemMessage(Component.translatable("alexinteraction.farseerspeech3"));
                            break;
                        case 4:
                            player.sendSystemMessage(Component.translatable("alexinteraction.farseerspeech4"));
                            break;
                        case 5:
                            player.sendSystemMessage(Component.translatable("alexinteraction.farseerspeech5"));
                            break;
                    }
                    lastTickTarget = currentTarget;
                }
            }
            if (currentTarget == null && loop <= 0) loop = 10;
        }
    }


}
