package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;


public class AMIAddTargets {

    private static final Set<Mob> APPLIED = Collections.newSetFromMap(new WeakHashMap<>());

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob) || !APPLIED.add(mob))
            return;

        AMIAddTargetsConfig targets = AlexsMobsInteraction.TARGETS_CONFIG;
        AMIServerConfig common = AlexsMobsInteraction.COMMON_CONFIG;
        boolean cannibalism = targets.CANNIBALISM_ENABLED.get();

        if (mob instanceof Spider spider && targets.SPIDER_ENABLED.get()) {
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityCockroach.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Silverfish.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Bee.class, 2, true, false, null));
        }

        if (mob instanceof EntityAnaconda anaconda) {
            if (cannibalism) {
                AMIGoalSuppressions.remove(anaconda.targetSelector, 3, HurtByTargetGoal.class);
                anaconda.targetSelector.addGoal(3, new HurtByTargetGoal(anaconda, EntityAnaconda.class));
                anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 2500, true, false, livingEntity -> {
                    return livingEntity instanceof EntityAnaconda entityAnaconda && !entityAnaconda.isBaby() && (entityAnaconda.getHealth() <= 0.10F * entityAnaconda.getMaxHealth() || (entityAnaconda.isBaby() && entityAnaconda.getData(AMIAttachments.ORIGIN_ID) != anaconda.getId()));
                }));
            }
            if (targets.ANACONDA_ENABLED.get()) {
                addTagTarget(anaconda, 3, 800, true, true, AMIEntityTagGenerator.ANACONDA_KILL);
            }
        }

        if (mob instanceof EntityBaldEagle baldEagle && cannibalism) {
            baldEagle.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(baldEagle, EntityBaldEagle.class, 1000, true, false, livingEntity -> {
                return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
            }) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !baldEagle.isTame();
                }
            });
        }

        if (mob instanceof EntityBoneSerpent boneSerpent && targets.BONE_SERPENT_ENABLED.get()) {
            addTagTarget(boneSerpent, 3, 55, true, true, AMIEntityTagGenerator.NETHER_KILL);
        }

        if (mob instanceof EntityCachalotWhale cachalotWhale && targets.CACHALOT_ENABLED.get()) {
            addTagTarget(cachalotWhale, 2, 300, true, false, AMIEntityTagGenerator.KILL_FISHES);
        }

        if (mob instanceof EntityCapuchinMonkey capuchinMonkey && targets.CAPUCHIN_ENABLED.get()) {
            capuchinMonkey.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(capuchinMonkey, LivingEntity.class, 400, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.INSECTS)) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !capuchinMonkey.isTame();
                }
            });
        }

        if (mob instanceof EntityCentipedeHead centipede && targets.CAVE_CENTIPEDE_ENABLED.get()) {
            addTagTarget(centipede, 4, 55, true, false, AMIEntityTagGenerator.CENTIPEDE_KILL);
        }

        if (mob instanceof EntityCrow crow) {
            if (targets.CROW_ENABLED.get()) {
                crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.CROW_KILL)) {
                    @Override
                    public boolean canUse() {
                        return super.canUse() && !crow.isTame() && !crow.isBaby();
                    }
                });
            }
            if (cannibalism) {
                crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, EntityCrow.class, 500, true, true, livingEntity -> {
                    return livingEntity.getHealth() <= 0.10F * livingEntity.getMaxHealth();
                }) {
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !crow.isTame() && !crow.isBaby();
                    }
                });
            }
        }

        if (mob instanceof EntityDropBear dropBear && targets.DROPBEAR_ENABLED.get()) {
            dropBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(dropBear, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.NETHER_KILL)) {
                @Override
                protected AABB getTargetSearchArea(double targetDistance) {
                    AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
                    return new AABB(bb.minX, 0.0, bb.minZ, bb.maxX, 256.0, bb.maxZ);
                }
            });
        }

        if (mob instanceof EntityEmu emu && targets.EMU_ENABLED.get()) {
            emu.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.INSECTS)) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !emu.isBaby();
                }
            });
        }

        if (mob instanceof EntityFarseer farseer && targets.FARSEER_ENABLED.get()) {
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Raider.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Villager.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, WanderingTrader.class, 3, false, true, null));
        }

        if (mob instanceof EntityFrilledShark frilledShark) {
            if (targets.FRILLED_SHARK_ENABLED.get()) {
                addTagTarget(frilledShark, 2, 20, false, true, AMIEntityTagGenerator.KILL_FISHES);
            }
        }

        if (mob instanceof EntityGeladaMonkey geladaMonkey && targets.GELADA_MONKEY_ENABLED.get()) {
            addTagTarget(geladaMonkey, 2, 1, true, false, AMIEntityTagGenerator.INSECTS);
        }

        if (mob instanceof EntityGorilla gorilla && targets.GORILLA_ENABLED.get()) {
            addTagTarget(gorilla, 2, 1, true, false, AMIEntityTagGenerator.INSECTS);
        }

        if (mob instanceof EntityGrizzlyBear grizzlyBear && common.HONEYLESS_HUNTING_ENABLED.get() && targets.GRIZZLY_BEAR_ENABLED.get()) {
            grizzlyBear.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(grizzlyBear, LivingEntity.class, 10, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.GRIZZLY_BEAR_KILL)) {
                @Override
                public boolean canUse() {
                    return super.canUse() && (!grizzlyBear.isTame() || !AlexsMobsInteraction.COMMON_CONFIG.TAMED_FRIENDLIES_ENABLED.get()) && !grizzlyBear.isEating() && !grizzlyBear.isHoneyed() && grizzlyBear.getData(AMIAttachments.NO_HONEY) >= 10000;
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && canUse();
                }
            });
        }

        if (mob instanceof EntityHammerheadShark hammerheadShark && targets.HAMMERHEAD_ENABLED.get()) {
            addTagTarget(hammerheadShark, 2, 0, true, false, AMIEntityTagGenerator.HAMMERHEAD_KILL);
        }

        if (mob instanceof EntityMantisShrimp mantisShrimp && cannibalism) {
            mantisShrimp.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(mantisShrimp, EntityMantisShrimp.class, 200, true, false, livingEntity -> {
                return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
            }) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !mantisShrimp.isTame();
                }
            });
        }

        if (mob instanceof EntityMudskipper mudskipper && targets.MUDSKIPPER_ENABLED.get()) {
            addTagTarget(mudskipper, 2, 200, true, false, AMIEntityTagGenerator.MUDSKIPPER_KILL);
        }

        if (mob instanceof EntityOrca orca && targets.ORCA_ENABLED.get()) {
            addTagTarget(orca, 3, 200, true, false, AMIEntityTagGenerator.ORCA_KILL);
        }

        if (mob instanceof EntityPotoo potoo && targets.POTOO_ENABLED.get()) {
            potoo.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(potoo, EntityFly.class, 600, true, false, LivingEntity::isAlive));
        }

        if (mob instanceof EntityRaccoon raccoon && targets.RACCOON_ENABLED.get()) {
            raccoon.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(raccoon, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.INSECTS)) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !raccoon.isTame() && raccoon.level().isNight();
                }
            });
        }

        if (mob instanceof EntityRattlesnake rattlesnake) {
            if (targets.RATTLESNAKE_ENABLED.get()) {
                addTagTarget(rattlesnake, 2, 300, true, true, AMIEntityTagGenerator.WEAK_PREY);
            }
            if (cannibalism) {
                rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, EntityRattlesnake.class, 1500, true, true, livingEntity -> {
                    return livingEntity.getHealth() <= 0.60F * livingEntity.getMaxHealth() || livingEntity.isBaby();
                }));
            }
        }

        if (mob instanceof EntityRoadrunner roadrunner && targets.ROADRUNNER_ENABLED.get()) {
            roadrunner.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(roadrunner, LivingEntity.class, 200, true, true, livingEntity -> {
                return livingEntity.getType().is(AMIEntityTagGenerator.INSECTS) || livingEntity instanceof EntityRattlesnake;
            }));
        }

        if (mob instanceof EntityShoebill shoebill && targets.SHOEBILL_ENABLED.get()) {
            shoebill.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(shoebill, LivingEntity.class, 400, true, true, livingEntity -> {
                return livingEntity.getType().is(AMIEntityTagGenerator.SHOEBILL_BABY_KILL) && livingEntity.isBaby() || livingEntity.getType().is(AMIEntityTagGenerator.INSECTS);
            }));
        }

        if (mob instanceof EntityStraddler straddler && targets.STRADDLER_ENABLED.get()) {
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityBoneSerpent.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityCrimsonMosquito.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityWarpedMosco.class, true));
        }

        if (mob instanceof EntityTasmanianDevil tasmanianDevil && targets.TASMANIAN_DEVIL_ENABLED.get()) {
            tasmanianDevil.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(tasmanianDevil, LivingEntity.class, 200, false, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.WEAK_PREY)));
        }

        if (mob instanceof EntityWarpedMosco warpedMosco && cannibalism) {
            warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityCrimsonMosquito.class, 1000, true, true, null));
            warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityWarpedMosco.class, 100, true, true, livingEntity -> {
                return livingEntity.getHealth() <= 0.05F * livingEntity.getMaxHealth();
            }));
        }

    }

    private static void addTagTarget(Mob mob, int priority, int chance, boolean checkVisibility, boolean onlyNearby, TagKey<EntityType<?>> tag) {
        mob.targetSelector.addGoal(priority, new EntityAINearestTarget3D<>(mob, LivingEntity.class, chance, checkVisibility, onlyNearby, AMEntityRegistry.buildPredicateFromTag(tag)));
    }
}
