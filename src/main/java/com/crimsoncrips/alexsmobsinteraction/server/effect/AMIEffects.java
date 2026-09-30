package com.crimsoncrips.alexsmobsinteraction.server.effect;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMIEffects {

    public static final DeferredRegister<MobEffect> EFFECT_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, AlexsMobsInteraction.MODID);
    public static final DeferredRegister<Potion> POTION_REGISTER = DeferredRegister.create(Registries.POTION, AlexsMobsInteraction.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> FARSEER_ICON = EFFECT_REGISTER.register("farseer_icon", AMIFarseerIcon::new);
    public static final DeferredHolder<MobEffect, MobEffect> SKREECHING = EFFECT_REGISTER.register("skreeching", AMISkreeching::new);
    public static final DeferredHolder<MobEffect, MobEffect> BLOODED = EFFECT_REGISTER.register("blooded", AMIBlooded::new);
    public static final DeferredHolder<MobEffect, MobEffect> GUSTING = EFFECT_REGISTER.register("gusting", AMIGusting::new);
    public static final DeferredHolder<Potion, Potion> SKREECHING_POTION = POTION_REGISTER.register("skreeching", () -> new Potion(new MobEffectInstance(SKREECHING, 72000)));
    public static final DeferredHolder<Potion, Potion> GUSTING_POTION = POTION_REGISTER.register("gusting", () -> new Potion(new MobEffectInstance(GUSTING, 800)));
    public static final DeferredHolder<Potion, Potion> LONGER_GUSTING_POTION = POTION_REGISTER.register("long_gusting", () -> new Potion("gusting", new MobEffectInstance(GUSTING, 1600)));
    public static final DeferredHolder<Potion, Potion> HEALTH_BOOST_POTION = POTION_REGISTER.register("health_boost", () -> new Potion(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1600, 1)));

    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();
        builder.addMix(Potions.AWKWARD, AMItemRegistry.SKREECHER_SOUL.get(), SKREECHING_POTION);
        builder.addMix(Potions.AWKWARD, AMItemRegistry.GUSTER_EYE.get(), GUSTING_POTION);
        builder.addMix(GUSTING_POTION, Items.REDSTONE, LONGER_GUSTING_POTION);
        builder.addMix(Potions.AWKWARD, AMItemRegistry.GONGYLIDIA.get(), HEALTH_BOOST_POTION);
    }
}
