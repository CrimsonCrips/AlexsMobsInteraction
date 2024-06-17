package com.crimsoncrips.alexsmobsinteraction.effect;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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

    public static final RegistryObject<MobEffect> SKREECHING = EFFECT_REGISTER.register("skreeching", AMISkreechingPotion::new);
    public static final RegistryObject<Potion> SKREECHING_POTION = POTION_REGISTER.register("skreeching", ()-> new Potion(new MobEffectInstance(SKREECHING.get(), 100000)));

    public static ItemStack createPotion(RegistryObject<Potion> potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get());
    }

    public static ItemStack createPotion(Potion potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    public static void init(){
        BrewingRecipeRegistry.addRecipe(new AMIBrewingRecipes(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.SKREECHER_SOUL.get()), createPotion(SKREECHING_POTION)));

    }
}
