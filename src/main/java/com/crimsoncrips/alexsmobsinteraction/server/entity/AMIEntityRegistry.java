package com.crimsoncrips.alexsmobsinteraction.server.entity;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.base.Predicates;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public class AMIEntityRegistry {

    public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, AlexsMobs.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EntityLeafcutterPupa>> LEAFCUTTER_PUPA = DEF_REG.register("leafcutter_ant_pupa", () -> EntityType.Builder.<EntityLeafcutterPupa>of(EntityLeafcutterPupa::new, MobCategory.MISC).sized(0.5F, 0.5F).build("leafcutter_ant_pupa"));


    public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> entityTag){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag);
        }
    }

}
