package com.crimsoncrips.alexsmobsinteraction.event;

import biomesoplenty.api.block.BOPBlocks;
import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.compat.SoulFiredCompat;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.goal.*;
import com.crimsoncrips.alexsmobsinteraction.misc.CrimsonAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.inventory.CurioSlot;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import static com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry.*;
import static com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig.ELEPHANT_TRAMPLE_ENABLED;
import static com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber.FUNGUS;
import static net.minecraft.world.level.block.SculkShriekerBlock.CAN_SUMMON;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AMInteractionEvents {

    @SubscribeEvent
    public void onEntityFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        final var entity = event.getEntity();

        if (entity instanceof EntityCrimsonMosquito crimsonMosquito){
            if (AMInteractionConfig.BLOODED_ENABLED && crimsonMosquito.getRandom().nextDouble() < 0.2){
                crimsonMosquito.setBloodLevel(crimsonMosquito.getBloodLevel() + 1);
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


        if (AMInteractionConfig.BLOODED_EFFECT_ENABLED){
            if (ModList.get().isLoaded("biomesoplenty") && livingEntity.getFeetBlockState().is(BOPBlocks.BLOOD)) {
                livingEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED.get(), 140, 0));
            }
        }

        if (livingEntity instanceof EntityCrimsonMosquito crimsonMosquito){
            Entity attach = crimsonMosquito.getVehicle();

            if ((AMInteractionConfig.GOOFY_MODE_ENABLED && AMInteractionConfig.GOOFY_CRIMSON_MULTIPLY_ENABLED &&  attach != null && crimsonMosquito.getBloodLevel() > 1)) {
                if (!(attach instanceof Player)){
                    AMEntityRegistry.GUST.get().spawn((ServerLevel) crimsonMosquito.level(), BlockPos.containing(crimsonMosquito.getX(), crimsonMosquito.getY() + 0.2, crimsonMosquito.getZ()), MobSpawnType.MOB_SUMMONED);
                    attach.remove(Entity.RemovalReason.DISCARDED);
                }

            }
        }


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
                        LivingEntity entity = var4.next();
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
            if (centipede.getLastHurtByMob() == target)
                return;
            if (target.isHolding(Ingredient.of(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR)) || target instanceof Player player && curiosLight(player)) {
                centipede.setTarget(null);
            }
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

            if(living instanceof EntityCrimsonMosquito crimsonMosquito){
                if (AMInteractionConfig.CRIMSON_TRANSFORM_ENABLED) {
                    if (itemStack.getItem() == AMItemRegistry.WARPED_MUSCLE.get() && crimsonMosquito.hasEffect(MobEffects.WEAKNESS)) {
                        if (!player.isCreative()) {
                            itemStack.shrink(1);
                        }
                        crimsonMosquito.gameEvent(GameEvent.ENTITY_INTERACT);
                        crimsonMosquito.gameEvent(GameEvent.EAT);
                        crimsonMosquito.playSound(SoundEvents.GENERIC_EAT, 1, crimsonMosquito.getVoicePitch());
                        crimsonMosquito.setSick(true);
                    }
                }
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
                if (itemStack.getItem() == Items.SUGAR || itemStack.getItem() == Items.SUGAR_CANE) {
                    if (!player.isCreative()) itemStack.shrink(1);
                    sugarGlider.gameEvent(GameEvent.EAT);
                    sugarGlider.playSound(SoundEvents.FOX_EAT, 1, sugarGlider.getVoicePitch());
                    sugarGlider.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 1));
                }
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
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CompoundTag playerData = event.getEntity().getPersistentData();
        CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        if (!data.getBoolean("ami_book") && AMInteractionConfig.CRIMSON_WIKI_ENABLED) {
            System.out.println("testing");
            CrimsonAdvancementTriggerRegistry.AMI_BOOK.trigger((ServerPlayer) player);
            data.putBoolean("ami_book", true);
            playerData.put(Player.PERSISTED_NBT_TAG, data);
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
        if(attackEvent.getSource().getDirectEntity() instanceof EntitySoulVulture soulVulture){
            soulVulture.setSoulLevel(soulVulture.getSoulLevel() + 1);
        }

    }

    @SubscribeEvent
    public void mobHurt(LivingHurtEvent hurtEvent){


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
        if (lightningEvent.getEntity() instanceof EntityTusklin tusklin && AMInteractionConfig.TUSKLIN_STRUCK_ENABLED){
            Level level = tusklin.level();
            if (!level.isClientSide) {
                LivingEntity entityToSpawn = EntityType.ZOGLIN.spawn((ServerLevel) level, BlockPos.containing(tusklin.getX() + 0.5, tusklin.getY() + 1.0, tusklin.getZ() + 0.5), MobSpawnType.TRIGGERED);
                if (tusklin.isBaby() && entityToSpawn instanceof Zoglin zoglin) zoglin.setBaby(true);
                tusklin.discard();
            }

        }
    }



    @SubscribeEvent
    public void mobSpawn(MobSpawnEvent.PositionCheck spawnPlacementCheck){
        EntityType<?> entityType = spawnPlacementCheck.getEntity().getType();
        Holder<Biome> biome = spawnPlacementCheck.getLevel().getBiome(spawnPlacementCheck.getEntity().blockPosition());
        long time = spawnPlacementCheck.getLevel().dayTime();

        if(entityType == AMEntityRegistry.LOBSTER.get()){
            if (!AMInteractionConfig.LOBSTER_NIGHT_ENABLED)
                return;
            if (!(time > 13000 && time < 23460)) {
                spawnPlacementCheck.setResult(Event.Result.DENY);
            }
        }
    }

    public boolean curiosLight(Player player){
        if (ModList.get().isLoaded("curiouslanterns")) {
            ICuriosItemHandler handler = CuriosApi.getCuriosInventory(player).orElseThrow(() -> new IllegalStateException("Player " + player.getName() + " has no curios inventory!"));
            return handler.getStacksHandler("belt").orElseThrow().getStacks().getStackInSlot(0).is(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR);
        } else return false;
    }




}
