package com.crimsoncrips.alexsmobsinteraction.server.effect;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMIEffects {


    public static final DeferredRegister<MobEffect> EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AlexsMobsInteraction.MODID);
    public static final DeferredRegister<Potion> POTION_REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, AlexsMobsInteraction.MODID);

    public static final RegistryObject<MobEffect> SKREECHING = EFFECT_REGISTER.register("skreeching", AMISkreeching::new);
    public static final RegistryObject<MobEffect> BLOODED = EFFECT_REGISTER.register("blooded", AMIBlooded::new);
    public static final RegistryObject<Potion> SKREECHING_POTION = POTION_REGISTER.register("skreeching", ()-> new Potion(new MobEffectInstance(SKREECHING.get(), 72000)));
    public static final RegistryObject<MobEffect> GUSTING = EFFECT_REGISTER.register("gusting", AMIGusting::new);
    public static final RegistryObject<Potion> GUSTING_POTION = POTION_REGISTER.register("gusting", ()-> new Potion(new MobEffectInstance(GUSTING.get(), 800)));
    public static final RegistryObject<Potion> LONGER_GUSTING_POTION = POTION_REGISTER.register("long_gusting", ()-> new Potion(new MobEffectInstance(GUSTING.get(), 1600)));
    public static final RegistryObject<Potion> HEALTH_BOOST_POTION = POTION_REGISTER.register("health_boost",() -> new Potion(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1600,1)));


    public static ItemStack createPotion(Potion potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }




    public static void init(){
        BrewingRecipeRegistry.addRecipe(new AMIBrewingRecipes(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.SKREECHER_SOUL.get()), createPotion(SKREECHING_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new AMIBrewingRecipes(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.GUSTER_EYE.get()), createPotion(GUSTING_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new AMIBrewingRecipes(Ingredient.of(createPotion(AMIEffects.GUSTING_POTION.get())), Ingredient.of(Items.REDSTONE), createPotion(LONGER_GUSTING_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new AMIBrewingRecipes(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.GONGYLIDIA.get()), createPotion(HEALTH_BOOST_POTION.get())));
    }
}
