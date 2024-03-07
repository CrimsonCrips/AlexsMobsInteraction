package com.crimsoncrips.alexsmobsinteraction.event;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionEvents {

    @SubscribeEvent
    public void onEntityFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        final var entity = event.getEntity();
        try {
            if (AInteractionConfig.frogeatflies && entity instanceof final Frog frog) {
                frog.targetSelector.addGoal(4,
                        new NearestAttackableTargetGoal<>(frog, EntityFly.class, 1, true, false, null));
            } else if (AInteractionConfig.spidereats && entity instanceof final Spider spider) {
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityCockroach.class, 2, true, false, null));
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Silverfish.class, 2, true, false, null));
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Bee.class, 2, true, false, null));
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityFly.class, 2, true, false, null));

            }
        } finally {

        }
    }

}
