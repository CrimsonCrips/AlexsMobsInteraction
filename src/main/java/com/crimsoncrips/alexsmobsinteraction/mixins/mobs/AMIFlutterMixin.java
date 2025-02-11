package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.crimsoncrips.alexsmobsinteraction.networking.FarseerPacket;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFlutterBonemeal;
import com.github.alexmodguy.alexscaves.server.entity.ai.CorrodentAttackGoal;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import com.github.alexthe666.alexsmobs.entity.EntityPollenBall;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityFlutter.class)
public abstract class AMIFlutterMixin extends TamableAnimal {


    protected AMIFlutterMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

}
