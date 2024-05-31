package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.crimsoncrips.alexsmobsinteraction.networking.FarseerPacket;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.alexthe666.alexsmobs.client.event.ClientEvents.renderStaticScreenFor;


@Mixin(EntityFarseer.class)
public class AMIFarseer extends Mob {


    @Unique
    private int alexsMobsInteraction$loop;


    protected AMIFarseer(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {

        if (!AMInteractionConfig.FARSEER_ALTERING_ENABLED)
            return;
        if (level().isClientSide())
            return;
        if (!(alexsMobsInteraction$loop >= 0))
            return;

        if (this.getTarget() instanceof Player player) {
            alexsMobsInteraction$loop--;
            renderStaticScreenFor = 20;
            Inventory inv = player.getInventory();
            if (player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AMIEnchantmentRegistry.STABILIZER.get()) > 0)
                return;


            for (int i = 0; i < 9 - 1; i++) {
                ItemStack current = inv.getItem(i);
                int j = random.nextInt(i + 1, 9);

                // swap
                ItemStack to = inv.getItem(j);
                inv.setItem(j, current);
                inv.setItem(i, to);
            }

            if (alexsMobsInteraction$loop != 9)
                return;

            AMIPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new FarseerPacket());
            int something = getRandom().nextInt(6);
            switch (something) {
                case 0:
                    player.sendSystemMessage(Component.translatable("alexsmobsinteraction.farseerspeech0"));
                    break;
                case 1:
                    player.sendSystemMessage(Component.translatable("alexsmobsinteraction.farseerspeech1"));
                    break;
                case 2:
                    player.sendSystemMessage(Component.translatable("alexsmobsinteraction.farseerspeech2"));
                    break;
                case 3:
                    player.sendSystemMessage(Component.translatable("alexsmobsinteraction.farseerspeech3"));
                    break;
                case 4:
                    player.sendSystemMessage(Component.translatable("alexsmobsinteraction.farseerspeech4"));
                    break;
                case 5:
                    player.sendSystemMessage(Component.translatable("alexsmobsinteraction.farseerspeech5"));
                    break;
            }
        }
        if (this.getTarget() == null && alexsMobsInteraction$loop <= 0) {
            alexsMobsInteraction$loop = 10;
        }

        }
    }
