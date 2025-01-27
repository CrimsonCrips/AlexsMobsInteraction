package com.crimsoncrips.alexsmobsinteraction.server.entity;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.base.Predicates;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMIEntityRegistry {

    public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AlexsMobs.MODID);

    public static final RegistryObject<EntityType<EntityLeafcutterPupa>> LEAFCUTTER_PUPA = DEF_REG.register("leafcutter_ant_pupa", () -> registerEntity(EntityType.Builder.of(EntityLeafcutterPupa::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityLeafcutterPupa::new), "leafcutter_ant_pupa"));

    private static EntityType registerEntity(EntityType.Builder builder, String entityName) {
        return builder.build(entityName);
    }


    public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> entityTag){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag);
        }
    }

}
