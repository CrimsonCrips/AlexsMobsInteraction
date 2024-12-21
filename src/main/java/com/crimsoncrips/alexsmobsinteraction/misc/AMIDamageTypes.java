package com.crimsoncrips.alexsmobsinteraction.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AMIDamageTypes {

    public static final ResourceKey<DamageType> BANANA_SLIP = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(AlexsMobsInteraction.MODID, "banana_slip"));

    public static DamageSource causeBananaSlip(RegistryAccess registryAccess) {
        return new DamageSourceRandomMessages(registryAccess.registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(BANANA_SLIP), 1);
    }


    private static class DamageSourceRandomMessages extends DamageSource {

        private int messageCount;

        public DamageSourceRandomMessages(Holder.Reference<DamageType> message, int messageCount) {
            super(message);
            this.messageCount = messageCount;
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity attacked) {
            int type = attacked.getRandom().nextInt(this.messageCount);
            String s = "death.attack." + this.getMsgId() + "_" + type;
            Entity entity = this.getDirectEntity() == null ? this.getEntity() : this.getDirectEntity();
            if (entity != null) {
                return Component.translatable(s + ".entity", attacked.getDisplayName(), entity.getDisplayName());
            }else{
                return Component.translatable(s, attacked.getDisplayName());
            }
        }
    }
}
