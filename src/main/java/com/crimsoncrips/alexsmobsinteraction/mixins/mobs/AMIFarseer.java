package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.crimsoncrips.alexsmobsinteraction.networking.FarseerPacket;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityFarseer.class)
public class AMIFarseer extends Mob {


    protected AMIFarseer(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Unique
    private int alexsMobsInteraction$loop;


    @Inject(method = "tick", at = @At("TAIL"))
    private void tickFarseer(CallbackInfo ci) {
        if (!AlexsMobsInteraction.COMMON_CONFIG.FARSEER_ALTERING_ENABLED.get())
            return;
        if (this.level().isClientSide())
            return;
        if (!(alexsMobsInteraction$loop >= 0))
            return;

        if (this.getTarget() instanceof Player player) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AMIEnchantmentRegistry.STABILIZER.get()) > 0)
                return;
            alexsMobsInteraction$loop--;
            System.out.println(alexsMobsInteraction$loop);
            Inventory inv = player.getInventory();

            for (int i = 0; i < 9 - 1; i++) {
                ItemStack current = inv.getItem(i);
                int j = this.getRandom().nextInt(i + 1, 9);
                ItemStack to = inv.getItem(j);
                inv.setItem(j, current);
                inv.setItem(i, to);
            }

            if (alexsMobsInteraction$loop == 49) {
                AMIPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new FarseerPacket());

                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
            }

        }
        if (this.getTarget() == null && alexsMobsInteraction$loop <= 0) {
            alexsMobsInteraction$loop = 50;
        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityFarseer farseer = (EntityFarseer)(Object)this;

        farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Raider.class, 3, false, true, null));
        farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Villager.class, 3, false, true, null));
        farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, WanderingTrader.class, 3, false, true, null));

    }


}
