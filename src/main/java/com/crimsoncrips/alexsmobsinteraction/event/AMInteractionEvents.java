package com.crimsoncrips.alexsmobsinteraction.event;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.compat.SoulFiredCompat;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.goal.*;
import com.crimsoncrips.alexsmobsinteraction.item.AMIItemRegistry;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.crimsoncrips.alexsmobsinteraction.networking.FarseerPacket;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import java.util.Iterator;
import java.util.function.Predicate;
import static com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry.*;
import static com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig.ELEPHANT_TRAMPLE_ENABLED;
import static com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber.FUNGUS;
import static com.github.alexthe666.alexsmobs.client.event.ClientEvents.renderStaticScreenFor;
import static net.minecraft.world.level.block.SculkShriekerBlock.CAN_SUMMON;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AMInteractionEvents {

    @SubscribeEvent
    public void onEntityFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        final var entity = event.getEntity();
        //Vanilla Mobs

        if (AMInteractionConfig.SPIDER_EAT_ENABLED && entity instanceof final Spider spider) {
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityCockroach.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Silverfish.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Bee.class, 2, true, false, null));
        }

        if (AMInteractionConfig.CAVESPIDER_EAT_ENABLED && entity instanceof final CaveSpider cavespider) {
            cavespider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(cavespider, EntityCockroach.class, 2, true, false, null));
            cavespider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(cavespider, Silverfish.class, 2, true, false, null));
            cavespider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(cavespider, Bee.class, 2, true, false, null));
            cavespider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(cavespider, EntityFly.class, 2, true, false, null));
        }



        //AM Mobs
        if (entity instanceof EntityAnaconda anaconda){
            Predicate<LivingEntity> ANACONDA_BABY_TARGETS = AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ANACONDA_BABY_KILL);
            anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, LivingEntity.class, 1000, true, false, (livingEntity) -> {
                return ANACONDA_BABY_TARGETS.test(livingEntity)  && livingEntity.isBaby();
            }));
            if (AMInteractionConfig.ANACONDA_CANNIBALIZE_ENABLED) {
                anaconda.targetSelector.removeAllGoals(goal -> {
                    return goal instanceof HurtByTargetGoal;
                });
                anaconda.targetSelector.addGoal(3, new HurtByTargetGoal(anaconda, EntityAnaconda.class));
                anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 2500, true, false, (livingEntity) -> {
                    return (livingEntity.getHealth() <= 0.10F * livingEntity.getMaxHealth() || livingEntity.isBaby());
                }));
            }
        }

        if (entity instanceof EntityCatfish catfish){
            if (AMInteractionConfig.CATFISH_CANNIBALIZE_ENABLED) {
                catfish.goalSelector.removeAllGoals(goal -> {
                    return goal.getClass().getName().equals("com.github.alexthe666.alexsmobs.entity.EntityCatfish$TargetFoodGoal");
                });
                catfish.goalSelector.addGoal(3, new AMITargetFood(catfish));
            }
        }

        if (entity instanceof final EntityBaldEagle baldEagle) {
            if(AMInteractionConfig.EAGLE_CANNIBALIZE_ENABLED){
                baldEagle.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(baldEagle, EntityBaldEagle.class, 1000, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
                }) {
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !baldEagle.isTame();
                    }
                });
            }
        }

        if(AMInteractionConfig.PREY_FEAR_ENABLED) {
            if (entity instanceof final EntityBananaSlug bananaSlug) {
                bananaSlug.goalSelector.addGoal(3, new AvoidEntityGoal<>(bananaSlug, LivingEntity.class, 2.0F, 1.2, 1.5, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SMALLINSECTFEAR)));
            }
            if (entity instanceof final EntityRaccoon raccoon) {
                raccoon.goalSelector.addGoal(3, new AvoidEntityGoal<>(raccoon, LivingEntity.class, 8.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RACCOON_FEAR)));
            }
            if (entity instanceof final EntityJerboa jerboa) {
                jerboa.goalSelector.addGoal(3, new AvoidEntityGoal<>(jerboa, LivingEntity.class, 7.0F, 1.7D, 1.4,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.JERBOAFEAR)));
            }
            if (entity instanceof final EntityRainFrog rainFrog) {
                rainFrog.goalSelector.addGoal(3, new AvoidEntityGoal<>(rainFrog, LivingEntity.class, 10.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RAINFROG_FEAR)));
            }
            if (entity instanceof final EntityBlobfish blobfish) {
                blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, Player.class, 3.0F, 0.8, 1.2));
                blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, EntityGiantSquid.class, 5.0F, 1, 1.4));
                blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, EntityFrilledShark.class, 2.0F, 0.8, 1));
            }
            if (entity instanceof final EntitySugarGlider sugarGlider) {
                sugarGlider.goalSelector.addGoal(3, new AvoidEntityGoal<>(sugarGlider, LivingEntity.class, 2.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SMALLINSECTFEAR)));
            }
        }

        if (entity instanceof final EntityBoneSerpent boneSerpent ){
            boneSerpent.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(boneSerpent, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.BONESERPENT_KILL)));
            if(AMInteractionConfig.SERPENT_FEAR_ENABLED) {
                boneSerpent.goalSelector.addGoal(7, new AvoidEntityGoal<>(boneSerpent, EntityLaviathan.class, 10.0F, 1.6, 2));
            }
        }

        if (entity instanceof final EntityCaiman caiman){
            if(AMInteractionConfig.CAIMAN_AGGRO_ENABLED) {
                caiman.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(caiman, Player.class, 150, true, true, null){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !caiman.isTame();
                    }
                });
            }
            if (AMInteractionConfig.CAIMAN_EGG_ATTACK_ENABLED) {
                caiman.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(caiman, LivingEntity.class, 0, true, false, (livingEntity) -> {
                    return livingEntity.isHolding(Ingredient.of(AMBlockRegistry.CAIMAN_EGG.get()));
                }){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !caiman.isTame() && !caiman.isBaby();
                    }
                });
            }
        }

        if (entity instanceof final EntityCapuchinMonkey capuchinMonkey) {
            if (!AMInteractionConfig.CAPUCHIN_HUNT_ENABLED)
                return;
            capuchinMonkey.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(capuchinMonkey, LivingEntity.class, 400, true, true, AMEntityRegistry.buildPredicateFromTag(INSECTS)){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !capuchinMonkey.isTame();
                }
            });
        }

        if (entity instanceof final EntityCachalotWhale cachalotWhale && cachalotWhale.isSleeping() && !cachalotWhale.isBeached()){
            cachalotWhale.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(cachalotWhale, LivingEntity.class, 300, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CACHALOT_WHALE_KILL_CHANCE)));
        }

        if (entity instanceof final EntityCentipedeHead centipede){
            if (AMInteractionConfig.LIGHT_FEAR_ENABLED) {
                centipede.goalSelector.addGoal(1, new AvoidEntityGoal<>(centipede, LivingEntity.class, 4.0F, 1.5, 2, (livingEntity) -> {
                    return livingEntity.isHolding(Ingredient.of(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR));
                }));
                centipede.goalSelector.addGoal(1, new AvoidBlockGoal(centipede, 4,1,1.2,(pos) -> {
                    BlockState state = centipede.level().getBlockState(pos);
                    return state.is(AMInteractionTagRegistry.CENTIPEDE_BLOCK_FEAR);
                }));
            }
            centipede.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(centipede, LivingEntity.class, 55, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CAVE_CENTIPEDE_KILL)));

        }

        if (entity instanceof final EntityCrocodile crocodile && !crocodile.isBaby() && !crocodile.isTame()){
            crocodile.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(crocodile, LivingEntity.class, 5000, true, false,AMEntityRegistry.buildPredicateFromTag(CROCODILE_BABY_KILL)));
        }

        if (entity instanceof final EntityCrow crow){
            crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CROW_KILL)));
            if (AMInteractionConfig.CROW_CANNIBALIZE_ENABLED){
                crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, EntityCrow.class, 500, true, true, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.10F * livingEntity.getMaxHealth();
                }){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !crow.isTame() && !crow.isBaby();
                    }
                });
            }
        }

        if (entity instanceof final EntityDropBear dropBear){
            dropBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(dropBear, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.DROPBEAR_KILL)){
                protected AABB getTargetSearchArea(double targetDistance) {
                    AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
                    return new AABB(bb.minX, 0.0, bb.minZ, bb.maxX, 256.0, bb.maxZ);
                }
            });
        }

        if (entity instanceof final EntityElephant elephant){
            if (AMInteractionConfig.ELEPHANT_TERRITORIAL_ENABLED){
                elephant.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(elephant, Player.class, 1000, true, true, (entity1 -> {
                    return entity1.isHolding(Ingredient.of(AMItemRegistry.ACACIA_BLOSSOM.get()));
                })) {
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && elephant.isTusked() && !elephant.isTame();
                    }
                });
            }
        }

        if (entity instanceof final EntityEmu emu){
            if (AMInteractionConfig.EMU_EGG_ATTACK_ENABLED){
                emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 5, true, false, (livingEntity) -> {
                    return livingEntity.isHolding( Ingredient.of(AMItemRegistry.EMU_EGG.get())) || livingEntity.isHolding( Ingredient.of(AMItemRegistry.BOILED_EMU_EGG.get()));
                }){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !emu.isBaby();
                    }
                });
            }
            if (AMInteractionConfig.RANGED_AGGRO_ENABLED){
                emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 70, true, false, (livingEntity) -> {
                    return livingEntity.isHolding(Ingredient.of(AMInteractionTagRegistry.EMU_TRIGGER));
                }){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !emu.isBaby();
                    }
                });
            }
            emu.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.EMU_KILL)){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !emu.isBaby();
                }
            });
        }

        if (entity instanceof final EntityFly fly){
            Predicate<LivingEntity> PESTERTARGET = AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.PESTER_ENTITY);
            if(AMInteractionConfig.CANDLE_REPEL_ENABLED){
                fly.goalSelector.addGoal(3, new AvoidBlockGoal(fly, 4, 1.8, 2.3, (pos) -> {
                    BlockState state = fly.level().getBlockState(pos);
                    if (state.is(BlockTags.CANDLES)){
                        return state.getValue(CandleBlock.LIT);
                    } else return false;
                }));
            }
            if(AMInteractionConfig.FLY_PESTER_ENABLED) {
                fly.goalSelector.addGoal(8, new AMIFollowNearestGoal<>(fly, LivingEntity.class, 1, 0.8, PESTERTARGET));
            }
        }

        if (entity instanceof final EntityEnderiophage enderiophage){
                enderiophage.targetSelector.removeAllGoals(goal -> {
                    return goal instanceof EntityAINearestTarget3D;
                });

                enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, EnderMan.class, 15, true, true, (livingEntity) -> {
                    if (AMInteractionConfig.INFECT_IMMUNITY_ENABLED) {
                        return !(livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE));
                    } else return true;
                }) {
                    public boolean canContinueToUse() {
                        return enderiophage.isMissingEye() && super.canContinueToUse();
                    }
                });

                enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, LivingEntity.class, 15, true, true, (livingEntity) -> {
                    if(AMInteractionConfig.INFECT_WEAK_ENABLED){
                        if (AMInteractionConfig.INFECT_IMMUNITY_ENABLED) {
                            return !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE) && !(livingEntity instanceof EntityEnderiophage) && (livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get()) || livingEntity.getHealth() <= 0.30F * livingEntity.getMaxHealth() || livingEntity instanceof EntityEndergrade);
                        } else return !(livingEntity instanceof EntityEnderiophage) && (livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get())  || livingEntity.getHealth() <= 0.30F * livingEntity.getMaxHealth() || livingEntity instanceof EntityEndergrade);
                    } else {
                        if (AMInteractionConfig.INFECT_IMMUNITY_ENABLED) {
                            return !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE) && !(livingEntity instanceof EntityEnderiophage) && (livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get()) || livingEntity instanceof EntityEndergrade);
                        } else return !(livingEntity instanceof EntityEnderiophage) && (livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get()) || livingEntity instanceof EntityEndergrade);
                    }
                }) {
                    public boolean canUse() {
                        return !enderiophage.isMissingEye() && (int) ReflectionUtil.getField(enderiophage, "fleeAfterStealTime") == 0 && super.canUse();
                    }
                });


        }

        if (entity instanceof EntityFarseer farseer && AMInteractionConfig.FARSEER_HUMANLIKE_ATTACK_ENABLED){
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Raider.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Villager.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, WanderingTrader.class, 3, false, true, null));
        }

        if(entity instanceof EntityFrilledShark frilledShark){
            if (AMInteractionConfig.BLEEDING_HUNGER_ENABLED){
                frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, Player.class, 1, false, true, (mob) -> {
                    return mob.hasEffect(AMEffectRegistry.EXSANGUINATION.get());
                }));
            }
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, LivingEntity.class, 1, false, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.FRILLED_KILL)));
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, EntityGiantSquid.class, 1, false, true,(livingEntity) -> {
                return livingEntity.getHealth() <= 0.25F * livingEntity.getMaxHealth();
            }));
        }

        if(AMInteractionConfig.GELADA_HUNT_ENABLED && entity instanceof EntityGeladaMonkey geladaMonkey){
            geladaMonkey.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(geladaMonkey, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.INSECTS)));
        }

        if(entity instanceof EntityGorilla gorilla){
            gorilla.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(gorilla, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.INSECTS)));
        }

        if(entity instanceof EntityHummingbird hummingbird){
            if(AMInteractionConfig.POLINATE_DAY_ENABLED) {
            hummingbird.goalSelector.removeAllGoals(goal -> {
                return goal instanceof HummingbirdAIPollinate;
            });

            hummingbird.goalSelector.addGoal(4, new HummingbirdAIPollinate(hummingbird){
                public boolean canUse() {
                        return super.canUse() && hummingbird.level().isDay();
                    }
            });
            }
            if(AMInteractionConfig.HUMMING_FOLLOW_ENABLED) {
                hummingbird.goalSelector.addGoal(8, new AMIFollowNearestGoal<>(hummingbird, EntityFlutter.class, 10, 1.2, (livingEntity) -> true) {
                    public boolean canContinueToUse() {
                        return hummingbird.level().isDay();
                    }
                });
            }
        }

        if (entity instanceof EntityKomodoDragon komodoDragon){
            if (!AMInteractionConfig.FRIENDLY_KOMODO_ENABLED)
                return;
            if (komodoDragon.isTame())
                return;

            komodoDragon.targetSelector.removeAllGoals(goal -> {
                return goal instanceof NearestAttackableTargetGoal || goal instanceof EntityAINearestTarget3D;
            });
            komodoDragon.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(komodoDragon, EntityKomodoDragon.class, 50, true, false, (p_213616_0_) -> {
                return p_213616_0_.isBaby() || p_213616_0_.getHealth() <= 0.7F * p_213616_0_.getMaxHealth();
            }));
            komodoDragon.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(komodoDragon, Player.class, 150, true, true, (Predicate)null));
            komodoDragon.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(komodoDragon, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.KOMODO_DRAGON_TARGETS)));



        }

        if (entity instanceof EntityMantisShrimp mantisShrimp && !mantisShrimp.isTame()){
            if(AMInteractionConfig.MANTIS_AGGRO_ENABLED  && !mantisShrimp.isBaby()) {
                mantisShrimp.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mantisShrimp, Player.class, 150, true, true, null));
            }
            if(AMInteractionConfig.MANTIS_CANNIBALIZE_ENABLED) {
                mantisShrimp.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(mantisShrimp, EntityMantisShrimp.class, 200, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
                }));
            }
        }

        if (entity instanceof EntityMudskipper mudskipper && AMInteractionConfig.MUDSKIPPER_HUNT_ENABLED) {
            mudskipper.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(mudskipper, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.MUDSKIPPER_KILL)));
        }

        if(entity instanceof EntityOrca orca && AMInteractionConfig.ORCA_HUNT_ENABLED){
            orca.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(orca, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ORCA_KILL)));
            orca.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(orca, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ORCA_CHANCE_KILL)));
        }

        if(entity instanceof EntityPotoo potoo){
            potoo.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(potoo, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.POTOO_KILL)));
        }

        if (entity instanceof EntityRaccoon raccoon && AMInteractionConfig.RACOON_HUNT_ENABLED) {
            raccoon.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(raccoon, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RACCOON_KILL)) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !raccoon.isTame() && raccoon.level().isNight();
                }

            });
        }

        if (entity instanceof EntityRattlesnake rattlesnake){
            rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, LivingEntity.class, 300, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RATTLESNAKE_KILL)) {
                public void start(){
                    super.start();
                }
            });
            if (AMInteractionConfig.RATTLESNAKE_CANNIBALIZE_ENABLED) {
                rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, EntityRattlesnake.class, 1500, true, true, (livingEntity) -> {
                    return (livingEntity.getHealth() <= 0.60F * livingEntity.getMaxHealth() || livingEntity.isBaby());
                }) {
                    public void start() {
                        super.start();
                    }
                });
            }
        }

        if (entity instanceof EntityRoadrunner roadrunner && AMInteractionConfig.ROADRUNNER_DAY_ENABLED){
            roadrunner.targetSelector.removeAllGoals(goal -> {
                return goal instanceof NearestAttackableTargetGoal;
            });
                roadrunner.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(roadrunner, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ROADRUNNER_KILL)) {
                    public boolean canUse() {
                        return super.canUse();
                    }
                });
        }

        if (entity instanceof EntitySeagull seagull && AMInteractionConfig.SEAGULL_WEAKEN_ENABLED){
            seagull.targetSelector.removeAllGoals(goal -> {
                return goal instanceof SeagullAIStealFromPlayers;
            });
            seagull.targetSelector.addGoal(2, new AMISeagullSteal(seagull){
                public boolean canUse() {
                        return super.canUse() && !(seagull.getHealth() <= 0.40F * seagull.getMaxHealth());
                }
            });
        }

        if (entity instanceof EntityShoebill shoebill){
            shoebill.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(shoebill, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SHOEBILL_BABY_KILL)));
            shoebill.targetSelector.addGoal(7, new EntityAINearestTarget3D<>(shoebill, EntityTerrapin.class, 100, false, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SHOEBILL_KILL)));
        }

        if (entity instanceof EntitySnowLeopard snowLeopard){
            if(AMInteractionConfig.LEOPARD_DESIRES_ENABLED){
                snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, EntityMoose.class, 100, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
                }));
                snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, Player.class, 100, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth() && livingEntity.getItemBySlot(EquipmentSlot.HEAD).is((Item)AMItemRegistry.MOOSE_HEADGEAR.get());
                }));
            }
        }

        if(entity instanceof EntityStraddler straddler && AMInteractionConfig.STRADDLER_VENGEANCE_ENABLED) {
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityBoneSerpent.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityCrimsonMosquito.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityWarpedMosco.class, true));
        }

        if (entity instanceof EntityTasmanianDevil tasmanianDevil){
            tasmanianDevil.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(tasmanianDevil, LivingEntity.class, 10, false, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.TASMANIAN_KILL)));
        }

        if (entity instanceof EntityTusklin tusklin){
            if(AMInteractionConfig.TUSKLIN_FLEE_ENABLED){
                tusklin.goalSelector.addGoal(3, new AvoidBlockGoal(tusklin, 4, 1, 1.2, (pos) -> {
                    BlockState state = tusklin.level().getBlockState(pos);
                    return state.is(BlockTags.HOGLIN_REPELLENTS);
                }));
            }
        }

        if (entity instanceof EntityWarpedMosco warpedMosco){
            if (!AMInteractionConfig.MOSCO_CANNIBALISM_ENABLED)
                return;
            warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityCrimsonMosquito.class, 1000, true, true, null));
            warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityWarpedMosco.class, 100, true, true, (livingEntity) -> {
                        return livingEntity.getHealth() <= 0.05F * livingEntity.getMaxHealth();
                    }));
        }

        if (entity instanceof EntityCrimsonMosquito crimsonMosquito){
            if (AMInteractionConfig.BLOODED_ENABLED && crimsonMosquito.getRandom().nextDouble() < 0.2){
                crimsonMosquito.setBloodLevel(crimsonMosquito.getBloodLevel() + 1);
            }
        }


        if (entity instanceof EntityWarpedToad warpedToad){
            if(!AMInteractionConfig.WARPED_FRIENDLY_ENABLED)
                return;
            warpedToad.targetSelector.removeAllGoals(goal -> {
                return goal instanceof EntityAINearestTarget3D;

            });
            warpedToad.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(warpedToad, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.WARPED_TOAD_TARGETS)){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !warpedToad.isTame();
                }
            });

        }

        if (entity instanceof EntityGrizzlyBear grizzlyBear){
            grizzlyBear.goalSelector.removeAllGoals(goal -> {
                return goal instanceof TameableAITempt;
            });
            grizzlyBear.goalSelector.addGoal(5, new TameableAITempt(grizzlyBear, 1.1D, Ingredient.of(AMInteractionTagRegistry.GRIZZLY_ENTICE), false));
            grizzlyBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(grizzlyBear, LivingEntity.class, 4000, true, false, AMEntityRegistry.buildPredicateFromTag(GRIZZLY_KILL)){
                public boolean canUse() {
                    return  super.canUse() && !grizzlyBear.isTame() && !grizzlyBear.isHoneyed();
                }
            });
            if (AMInteractionConfig.GRIZZLY_FRIENDLY_ENABLED) {
                grizzlyBear.targetSelector.removeAllGoals(goal -> {
                    return goal.getClass().getName().equals("com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear$AttackPlayerGoal") || goal instanceof NearestAttackableTargetGoal<?>;
                });
                grizzlyBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(grizzlyBear, LivingEntity.class, 300, true, true, AMEntityRegistry.buildPredicateFromTag(GRIZZLY_TERRITORIAL)) {
                    public boolean canUse() {
                        return super.canUse() && !grizzlyBear.isTame();
                    }
                });
                grizzlyBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(grizzlyBear, Player.class, 10, true, true,null) {
                    public boolean canUse() {
                        return super.canUse() && !grizzlyBear.isTame();
                    }
                });
            }
        }

        if (entity instanceof EntityAlligatorSnappingTurtle snappingturtle){
            if (AMInteractionConfig.SNAPPING_DORMANCY_ENABLED){
                snappingturtle.goalSelector.removeAllGoals(goal -> {
                    return goal instanceof AnimalAIFindWater || goal instanceof AnimalAILeaveWater || goal instanceof BottomFeederAIWander || goal instanceof RandomLookAroundGoal || goal instanceof LookAtPlayerGoal;
                });
                snappingturtle.goalSelector.addGoal(2, new AnimalAIFindWater(snappingturtle){
                    @Override
                    public boolean canContinueToUse() {
                        Level level = snappingturtle.level();
                        return super.canContinueToUse() && (level.isNight() || level.isRaining() || level.isThundering());
                    }
                });
                snappingturtle.goalSelector.addGoal(2, new AnimalAILeaveWater(snappingturtle){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && (snappingturtle.level().isNight() || snappingturtle.level().isRaining());
                    }
                });
                snappingturtle.goalSelector.addGoal(3, new BottomFeederAIWander(snappingturtle, 1.0, 120, 150, 10){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && (snappingturtle.level().isNight() || snappingturtle.level().isRaining());
                    }
                });
                snappingturtle.goalSelector.addGoal(3, new BreedGoal(snappingturtle, 1.0){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && (snappingturtle.level().isNight() || snappingturtle.level().isRaining());
                    }
                });
                snappingturtle.goalSelector.addGoal(5, new RandomLookAroundGoal(snappingturtle){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && (snappingturtle.level().isNight() || snappingturtle.level().isRaining());
                    }
                });
                snappingturtle.goalSelector.addGoal(6, new LookAtPlayerGoal(snappingturtle, Player.class, 6.0F){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && (snappingturtle.level().isNight() || snappingturtle.level().isRaining());
                    }
                });
                snappingturtle.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(snappingturtle, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SNAPPING_TURTLE_KILL)){
                    public boolean canUse() {
                        return snappingturtle.chaseTime >= 0 && super.canUse() && snappingturtle.level().isNight();
                    }
                    protected AABB getTargetSearchArea(double targetDistance) {
                        return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
                    }});
            } else
                snappingturtle.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(snappingturtle, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SNAPPING_TURTLE_KILL)){
                    public boolean canUse() {
                        return snappingturtle.chaseTime >= 0 && super.canUse();
                    }
                    protected AABB getTargetSearchArea(double targetDistance) {
                        return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
                    }});
        }

        if (entity instanceof EntityCosmaw cosmaw){
            if (AMInteractionConfig.COSMAW_WEAKENED_ENABLED) {
                cosmaw.goalSelector.removeAllGoals(goal -> {
                    return goal.getClass().getName().equals("com.github.alexthe666.alexsmobs.entity.EntityCosmaw$AIPickupOwner");
                });
                cosmaw.goalSelector.addGoal(4, new AMICosmawOwner(cosmaw));
            }
        }

        if (entity instanceof EntityHammerheadShark hammerheadShark){

            if (AMInteractionConfig.HAMMERHEAD_MANTIS_EAT_ENABLED){
                hammerheadShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(hammerheadShark, EntityMantisShrimp.class, 50, true, false, (mob) -> {
                    return mob.getHealth() <= 0.2 * mob.getMaxHealth();
                }));
            }
            hammerheadShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(hammerheadShark, LivingEntity.class, 0, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.HAMMERHEAD_KILL)));
        }

        if (entity instanceof EntitySoulVulture soulVulture){
            soulVulture.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(soulVulture, Hoglin.class, true));
            soulVulture.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(soulVulture, EntityDropBear.class, true));
            soulVulture.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(soulVulture, EntityBoneSerpent.class, 0, true, false, (mob) -> {
                return !entity.isInLava();
            }));
        }

        if (entity instanceof EntityRhinoceros rhinoceros){
            if (AMInteractionConfig.ACCIDENTAL_BETRAYAL_ENABLED){
                rhinoceros.targetSelector.removeAllGoals(goal -> {
                    return goal instanceof AnimalAIHurtByTargetNotBaby;
                });

                rhinoceros.targetSelector.addGoal(1, new HurtByTargetGoal(rhinoceros, new Class[]{EntityRhinoceros.class}));


                rhinoceros.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(rhinoceros, EntityRhinoceros.class, 200, true, true, (mob) -> {
                    System.out.println(mob.getDeltaMovement().horizontalDistance());
                    return mob.isBaby() && mob.getDeltaMovement().horizontalDistance() >= 0.08 && mob.getLastAttacker() != rhinoceros;
                }) {
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !rhinoceros.isBaby() && rhinoceros.level().isDay() && !rhinoceros.isLeashed() && !rhinoceros.isInLove();
                    }

                    protected double getFollowDistance() {
                        return 3.0;
                    }
                });
            }

        }

        if (entity instanceof EntitySkelewag skelewag){
            if (AMInteractionConfig.SKELEWAG_CIRCLE_ENABLED){
                skelewag.goalSelector.removeAllGoals(goal -> {
                    return goal.getClass().getName().equals("com.github.alexthe666.alexsmobs.entity.EntitySkelewag$AttackGoal");
                });
                skelewag.goalSelector.addGoal(1, new AMISkelewagCircleGoal(skelewag,1F));
            }
            if (AMInteractionConfig.MIGHT_UPGRADE_ENABLED){
                skelewag.targetSelector.removeAllGoals(target -> {
                    return target instanceof EntityAINearestTarget3D;
                });
                skelewag.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(skelewag, Player.class, 100, true, false, (livingEntity) -> {
                    return !livingEntity.hasEffect(AMEffectRegistry.ORCAS_MIGHT.get());
                }));
            }
        }

    }

    @SubscribeEvent
    public void tradeEvents(VillagerTradesEvent villagerTradesEvent){
        if (villagerTradesEvent.getType() == VillagerProfession.FISHERMAN && AMInteractionConfig.DEVILS_TRADE_ENABLED) {
            VillagerTrades.ItemListing pupfishTrade = new EmeraldsForItemsTrade(AMItemRegistry.DEVILS_HOLE_PUPFISH_BUCKET.get(), 24, 2, 5);
            final var list = villagerTradesEvent.getTrades().get(5);
            list.add(pupfishTrade);
            villagerTradesEvent.getTrades().put(5, list);
        }
    }


    @SubscribeEvent
    public void mobTickEvents(LivingEvent.LivingTickEvent livingTickEvent){
        LivingEntity livingEntity = livingTickEvent.getEntity();


        if (livingEntity instanceof EntityGrizzlyBear grizzlyBear){
            if(AMInteractionConfig.FREDDYABLE_ENABLED){
                String freddy = "Freddy Fazbear";
                if (grizzlyBear.getName().getString().equals(freddy)) {
                    grizzlyBear.setAprilFoolsFlag(2);
                    if(!AMInteractionConfig.GOOFY_MODE_ENABLED){
                        grizzlyBear.setTame(false);
                        grizzlyBear.setOwnerUUID(null);
                    }
                }
            }
        }

        if (livingEntity instanceof EntityElephant elephant){
            if(ELEPHANT_TRAMPLE_ENABLED){
                Iterator<LivingEntity> var4 = elephant.level().getEntitiesOfClass(LivingEntity.class, elephant.getBoundingBox().expandTowards(0.5, -2, 0.5)).iterator();

                if (elephant.isVehicle() && elephant.isTame()) {
                    while (var4.hasNext()) {
                        Entity entity = (Entity) var4.next();
                        if (entity != elephant && entity != elephant.getControllingPassenger() && entity.getBbHeight() <= 2.5F) {
                            entity.hurt(elephant.damageSources().mobAttack((LivingEntity) elephant), 8.0F + elephant.getRandom().nextFloat() * 2.0F);
                        }
                    }
                }
            }
        }



        if(livingEntity instanceof EntityBananaSlug bananaSlug){
            if (!AMInteractionConfig.GOOFY_MODE_ENABLED)
                return;
            if (!AMInteractionConfig.GOOFY_BANANA_SLIP_ENABLED)
                return;
            for (LivingEntity livingEntitys : bananaSlug.level().getEntitiesOfClass(LivingEntity.class, bananaSlug.getBoundingBox().expandTowards(0.5, 0.2, 0.5))) {
                if (livingEntitys instanceof Player) {
                    ((Entity) livingEntitys).kill();
                }
            }
        }

        if(livingEntity instanceof EntityAlligatorSnappingTurtle snappingturtle){
            if (!AMInteractionConfig.SNAPPING_MOSS_ENABLED)
                return;
            if (!(snappingturtle.getRandom().nextDouble() < 0.0001))
                return;
            if (snappingturtle.level().isRaining() || snappingturtle.level().isThundering() || snappingturtle.isInWater()){
                snappingturtle.setMoss(Math.min(10, snappingturtle.getMoss() + 1));
            }
        }

        if(livingEntity instanceof EntityCentipedeHead centipede){
            if (!AMInteractionConfig.LIGHT_FEAR_ENABLED)
                return;
            LivingEntity target = centipede.getTarget();
            if (target == null)
                return;
            if (!target.isHolding(Ingredient.of(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR)))
                return;
            if (centipede.getLastHurtByMob() == target)
                return;
            centipede.setTarget(null);
        }

        if(livingEntity instanceof Player player){
            BlockState feetBlockstate = player.getBlockStateOn();


            if (AMInteractionConfig.COMBUSTABLE_ENABLED && player.hasEffect(AMEffectRegistry.OILED.get())){
                if (feetBlockstate.is(Blocks.MAGMA_BLOCK) || feetBlockstate.is(Blocks.CAMPFIRE))
                    player.setSecondsOnFire(20);

                if (feetBlockstate.is(Blocks.SOUL_CAMPFIRE)){
                    if (ModList.get().isLoaded("soulfired")) {
                        SoulFiredCompat.setOnFire(player,20);
                    } else player.setSecondsOnFire(20);
                }

            }

            if(AMInteractionConfig.GOOFY_BANANA_SLIP_ENABLED && AMInteractionConfig.GOOFY_MODE_ENABLED){
                if (feetBlockstate.is(AMBlockRegistry.BANANA_PEEL.get())){
                    player.kill();
                }
            }

            if(AMInteractionConfig.SUNBIRD_UPGRADE_ENABLED){

                for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(7, 4, 7))) {
                    EntityType<?> entityType = entity.getType();
                    if (entityType.is(AMInteractionTagRegistry.BURNABLE_DEAD) && !entity.isInWater()) {
                        if (player.hasEffect(AMEffectRegistry.SUNBIRD_BLESSING.get())) {
                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                            entity.setSecondsOnFire(3);
                        }
                        if (player.hasEffect(AMEffectRegistry.SUNBIRD_CURSE.get())) {
                            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 350, 2));
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0));
                        }
                    }
                }
            }



            if (AMInteractionConfig.TIGER_STEALTH_ENABLED && player.hasEffect(AMEffectRegistry.TIGERS_BLESSING.get()) && !player.isSprinting() && !player.isSwimming()){
                player.addEffect(new MobEffectInstance(MobEffects. INVISIBILITY, 30, 0));
            }


        }



    }

    @SubscribeEvent
    public void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        RandomSource random = player.getRandom();
        if (event.getTarget() instanceof LivingEntity living) {
            if (itemStack.getItem() == AMItemRegistry.LAVA_BOTTLE.get() && AMInteractionConfig.MOLTEN_BATH_ENABLED){
                if (!player.isCreative()) itemStack.shrink(1);
                living.setSecondsOnFire(10);
                player.addItem(Items.GLASS_BOTTLE.getDefaultInstance());
            }

            if (itemStack.getItem() == AMItemRegistry.POISON_BOTTLE.get() && AMInteractionConfig.POISONOUS_BATH_ENABLED){
                if (!player.isCreative()) itemStack.shrink(1);
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 150, 1));
                player.addItem(Items.GLASS_BOTTLE.getDefaultInstance());
            }

            if (living instanceof EntityBananaSlug bananaSlug) {
                if (!AMInteractionConfig.BANANA_SHEAR_ENABLED)
                    return;
                if (itemStack.getItem() != Items.SHEARS)
                    return;
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(1, player, (p_233654_0_) -> {
                    });
                }
                bananaSlug.gameEvent(GameEvent.ENTITY_INTERACT);
                bananaSlug.playSound(SoundEvents.SHEEP_SHEAR, 1, bananaSlug.getVoicePitch());
                bananaSlug.spawnAtLocation(AMItemRegistry.BANANA.get());
                bananaSlug.discard();
            }

            if (living instanceof EntityFlutter flutter){
                if (itemStack.getItem() == Items.WITHER_ROSE && AMInteractionConfig.FLUTTER_WITHERED_ENABLED && !flutter.isTame()) {
                    if (!player.isCreative())
                        itemStack.hurtAndBreak(1, flutter, (p_233654_0_) -> {});
                    flutter.addEffect(new MobEffectInstance(MobEffects.WITHER, 900, 0));
                }
                if (itemStack.getItem() == Items.SHEARS && AMInteractionConfig.FLUTTER_SHEAR_ENABLED && !flutter.isTame()) {
                    flutter.playSound(SoundEvents.SHEEP_SHEAR, 1, flutter.getVoicePitch());
                    flutter.spawnAtLocation(Items.SPORE_BLOSSOM);
                    flutter.spawnAtLocation(Items.AZALEA);
                    flutter.spawnAtLocation(Items.AZALEA);
                    if (!player.isCreative()) itemStack.hurtAndBreak(6, flutter, (p_233654_0_) -> {});
                    flutter.discard();
                }
            }

            if (living instanceof EntitySugarGlider sugarGlider) {
                if (!AMInteractionConfig.SUGAR_RUSH_ENABLED)
                    return;
                if (!(itemStack.getItem() == Items.SUGAR || itemStack.getItem() == Items.SUGAR_CANE))
                    return;
                if (!player.isCreative()) itemStack.shrink(1);
                sugarGlider.gameEvent(GameEvent.EAT);
                sugarGlider.playSound(SoundEvents.FOX_EAT, 1, sugarGlider.getVoicePitch());
                sugarGlider.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 1));
            }

            if (living instanceof EntityGrizzlyBear grizzlyBear) {
                if (!AMInteractionConfig.BRUSHED_ENABLED)
                    return;
                if (itemStack.getItem() != Items.BRUSH)
                    return;
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(32, player, (p_233654_0_) -> {
                    });
                }
                grizzlyBear.spawnAtLocation(AMItemRegistry.BEAR_FUR.get());
                if(random.nextDouble() < 0.2) grizzlyBear.spawnAtLocation(AMItemRegistry.BEAR_FUR.get());
                if(random.nextDouble() < 0.0002)grizzlyBear.spawnAtLocation(AMItemRegistry.BEAR_DUST.get());
            }

        }


    }
    @SubscribeEvent
    public void onUseItemOnBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockState blockState = event.getEntity().level().getBlockState(event.getPos());
        BlockPos pos = event.getPos();
        Level worldIn = event.getLevel();
        RandomSource random = event.getEntity().getRandom();
        LivingEntity livingEntity = event.getEntity();


        if (AMInteractionConfig.SKREECHER_WARD_ENABLED){
            if (event.getItemStack().is(AMItemRegistry.SKREECHER_SOUL.get()) && blockState.is(Blocks.SCULK_SHRIEKER) && blockState.getValue(CAN_SUMMON)) {
                for (int x = 0; x < 5; x++) {
                    for (int z = 0; z < 5; z++) {
                        BlockPos sculkPos = new BlockPos(pos.getX() + x - 2, pos.getY() - 1, pos.getZ() + z - 2);
                        BlockState sculkPosState = worldIn.getBlockState(sculkPos);
                        if (random.nextDouble() < 0.7 && sculkPosState.is(BlockTags.SCULK_REPLACEABLE)) {
                            worldIn.setBlock(sculkPos, Blocks.SCULK.defaultBlockState(), 2);
                            worldIn.scheduleTick(sculkPos, sculkPosState.getBlock(), 8);
                            worldIn.playSound((Player) null, sculkPos, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + random.nextFloat() * 0.4F);
                            if (random.nextDouble() < 0.2)
                                worldIn.addParticle(ParticleTypes.SCULK_SOUL, sculkPos.getX() + 0.5, sculkPos.getY() + 1.15, sculkPos.getZ() + 0.5, 0.0, 0.05, 0.0);
                            if (random.nextDouble() < 0.2) for (int i = 0; i < random.nextInt(5); i++)
                                worldIn.addParticle(ParticleTypes.SCULK_CHARGE_POP, sculkPos.getX() + 0.5, sculkPos.getY() + 1.15, sculkPos.getZ() + 0.5, 0 + random.nextGaussian() * 0.02, 0.01 + random.nextGaussian() * 0.02, 0 + random.nextGaussian() * 0.02);
                        }
                    }
                }
                worldIn.playSound(null, pos, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.AMBIENT, 1, 1);
                worldIn.setBlockAndUpdate(pos, blockState.setValue(CAN_SUMMON, true));
                if (livingEntity instanceof Player player && !player.isCreative())
                    event.getItemStack().shrink(1);
                for (int i = 0; i < 100; ++i) {
                    double d0 = random.nextGaussian() * 0.02D;
                    double d1 = random.nextGaussian() * 0.02D;
                    double d2 = random.nextGaussian() * 0.02D;
                    worldIn.addParticle(ParticleTypes.SCULK_SOUL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, d0, d1, d2);
                }
            }



        }

        if (AMInteractionConfig.COCKROACH_CHAMBER_ENABLED){
            if (blockState.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()) && !worldIn.isClientSide){
                if (blockState.getValue(FUNGUS) != 5)
                    return;
                if (!(livingEntity.getRandom().nextDouble() < 0.7))
                    return;
                Entity entityToSpawn = (AMEntityRegistry.COCKROACH.get()).spawn((ServerLevel) worldIn, BlockPos.containing(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof EntityCockroach cockroach && worldIn.getRandom().nextDouble() < 0.07)
                    cockroach.setBaby(true);
            }
        }
    }

    @SubscribeEvent
    public void mobDeath(LivingDeathEvent livingDeathEvent){
        LivingEntity deadEntity = livingDeathEvent.getEntity();
        LivingEntity murdererEntity = deadEntity.getLastAttacker();

        if(AMInteractionConfig.SNOW_LUCK_ENABLED){
            if (murdererEntity instanceof EntitySnowLeopard) {
                RandomSource random = murdererEntity.getRandom();
                if (deadEntity instanceof Goat && random.nextDouble() < 0.5) {
                    murdererEntity.spawnAtLocation(Items.GOAT_HORN);
                    if (random.nextDouble() < 0.05) murdererEntity.spawnAtLocation(Items.GOAT_HORN);
                } else if (deadEntity instanceof Turtle && random.nextDouble() < 0.2) {
                    murdererEntity.spawnAtLocation(Items.SCUTE);
                } else if (deadEntity instanceof EntityFrilledShark && random.nextDouble() < 0.08) {
                    murdererEntity.spawnAtLocation(AMItemRegistry.SERRATED_SHARK_TOOTH.get());
                    if (random.nextDouble() < 0.01)
                        murdererEntity.spawnAtLocation(AMItemRegistry.SERRATED_SHARK_TOOTH.get());
                } else if (deadEntity instanceof EntityBananaSlug && random.nextDouble() < 0.2) {
                    murdererEntity.spawnAtLocation(AMItemRegistry.BANANA_SLUG_SLIME.get());
                } else if (deadEntity instanceof Rabbit) {
                    murdererEntity.spawnAtLocation(Items.RABBIT_FOOT);
                    if (random.nextDouble() < 0.02) deadEntity.spawnAtLocation(Items.RABBIT_FOOT);
                }
            }
        }
    }

    @SubscribeEvent
    public void mobAttack(LivingAttackEvent attackEvent){
        LivingEntity victim = attackEvent.getEntity();
        if(attackEvent.getSource().getDirectEntity() instanceof EntitySoulVulture soulVulture){
            soulVulture.setSoulLevel(soulVulture.getSoulLevel() + 1);
        }

    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent breakEvent){
        BlockState blockState = breakEvent.getState();
        Level level = (Level) breakEvent.getLevel();
        if (AMInteractionConfig.COCKROACH_CHAMBER_ENABLED) {
            if (blockState.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()) && breakEvent.getLevel().getRandom().nextDouble() < 0.1) {
                 if (blockState.getValue(FUNGUS) < 3)
                     return;
                Entity entityToSpawn = (AMEntityRegistry.COCKROACH.get()).spawn((ServerLevel) level, BlockPos.containing(breakEvent.getPos().getX() + 0.5, breakEvent.getPos().getY() + 1.0, breakEvent.getPos().getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof EntityCockroach cockroach && breakEvent.getLevel().getRandom().nextDouble() < 0.8)
                    cockroach.setBaby(true);
            }
        }

    }

    @SubscribeEvent
    public void struckLightning(EntityStruckByLightningEvent lightningEvent){
        if (lightningEvent.getEntity() instanceof EntityTusklin tusklin){
            Level level = tusklin.level();
            if (!level.isClientSide) {
                EntityType.ZOGLIN.spawn((ServerLevel) level, BlockPos.containing(tusklin.getX() + 0.5, tusklin.getY() + 1.0, tusklin.getZ() + 0.5), MobSpawnType.TRIGGERED);
                tusklin.discard();
            }
        }
    }




}
