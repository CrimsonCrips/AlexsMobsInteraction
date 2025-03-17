package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.client.AMISoundRegistry;
import com.crimsoncrips.alexsmobsinteraction.compat.BOPCompat;
import com.crimsoncrips.alexsmobsinteraction.compat.CuriosCompat;
import com.crimsoncrips.alexsmobsinteraction.compat.SoulFiredCompat;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIBlockTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIDamageTypes;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
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

import java.util.Iterator;

import static com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber.FUNGUS;
import static net.minecraft.world.level.block.SculkShriekerBlock.CAN_SUMMON;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AMInteractionEvents {

    @SubscribeEvent
    public void onEntityFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        final var entity = event.getEntity();

        if (entity instanceof EntityCrimsonMosquito crimsonMosquito){
            if (crimsonMosquito.getRandom().nextDouble() < AlexsMobsInteraction.COMMON_CONFIG.BLOODED_CHANCE.get()){
                crimsonMosquito.setBloodLevel(crimsonMosquito.getBloodLevel() + 1);
            }
        }

        if (entity instanceof EntityEnderiophage enderiophage && AlexsMobsInteraction.COMMON_CONFIG.ENDERIOPHAGE_ADAPTION_ENABLED.get()){
            enderiophage.setSkinForDimension();
        }

    }

    @SubscribeEvent
    public void tradeEvents(VillagerTradesEvent villagerTradesEvent){
        if (villagerTradesEvent.getType() == VillagerProfession.FISHERMAN && AlexsMobsInteraction.COMMON_CONFIG.DEVILS_TRADE_ENABLED.get()) {
            VillagerTrades.ItemListing pupfishTrade = new EmeraldsForItemsTrade(AMItemRegistry.DEVILS_HOLE_PUPFISH_BUCKET.get(), 24, 2, 5);
            final var list = villagerTradesEvent.getTrades().get(5);
            list.add(pupfishTrade);
            villagerTradesEvent.getTrades().put(5, list);
        }
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent){
        if (AlexsMobsInteraction.CLIENT_CONFIG.EFFECTS_REMINDER_ENABLED.get() && AlexsMobsInteraction.CLIENT_CONFIG.FARSEER_EFFECTS_ENABLED.get()) {
           playerLoggedInEvent.getEntity().displayClientMessage(Component.nullToEmpty("BEWARE! BEWARE! BEWARE! BEWARE!"),false);
           playerLoggedInEvent.getEntity().displayClientMessage(Component.nullToEmpty("THERE IS PHOTOSENSITIVE EFFECTS THE FARSEER DOES WHEN TARGETTING A PLAYER,IF YOU ARE PHOTOSENSITIVE PLEASE EITHER DISABLE THE ENTIRE FEATURE IN THE COMMON CONFIG, OR DISABLE THE PHOTOSENSITIVE EFFECTS IN THE CLIENT CONFIG SPECIFICALLY"),false);
           playerLoggedInEvent.getEntity().displayClientMessage(Component.nullToEmpty("IF YOU DONT WISH TO SEE THIS MESSAGE ANYMORE DURING LOGIN, DISABLE IT IN CLIENT CONFIG"),false);

        }
    }

    @SubscribeEvent
    public void mobTickEvents(LivingEvent.LivingTickEvent livingTickEvent){
        LivingEntity livingEntity = livingTickEvent.getEntity();
        Level level = livingTickEvent.getEntity().level();




        if (AlexsMobsInteraction.COMMON_CONFIG.BLOODED_EFFECT_ENABLED.get()){
            if (ModList.get().isLoaded("biomesoplenty") && livingEntity.getFeetBlockState().is(BOPCompat.getBOPBlock())) {
                livingEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED.get(), 140, 0));
            }

            MobEffectInstance blooded = livingEntity.getEffect(AMIEffects.BLOODED.get());
            if (livingEntity.isInWaterRainOrBubble() && blooded != null){
                livingEntity.removeEffect(AMIEffects.BLOODED.get());
                livingEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED.get(), blooded.getDuration() - 300, blooded.getAmplifier()));
            }

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(1.2))) {
                if (entity != livingEntity && livingEntity.getRandom().nextDouble() < 0.01 && livingEntity.hasEffect(AMEffectRegistry.EXSANGUINATION.get())) {
                    livingEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED.get(), 300, 1));
                }
            }
        }

        if (livingEntity instanceof EntityCombJelly entityCombJelly && entityCombJelly.getHealth() < entityCombJelly.getMaxHealth()){
            entityCombJelly.heal(0.05F);
        }

        if (livingEntity instanceof EntityCrimsonMosquito crimsonMosquito){
            Entity attach = crimsonMosquito.getVehicle();

            if (AlexsMobsInteraction.COMMON_CONFIG.GOOFY_CRIMSON_MULTIPLY_ENABLED.get() && attach != null && crimsonMosquito.getBloodLevel() > 1) {
                if (!(attach instanceof Player) && attach.isAlive()){
                    AMEntityRegistry.CRIMSON_MOSQUITO.get().spawn((ServerLevel) level, BlockPos.containing(crimsonMosquito.getX(), crimsonMosquito.getY() + 0.2, crimsonMosquito.getZ()), MobSpawnType.MOB_SUMMONED);
                    attach.remove(Entity.RemovalReason.DISCARDED);
                }

            }
        }


        if (livingEntity instanceof EntityGrizzlyBear grizzlyBear){
            if(AlexsMobsInteraction.COMMON_CONFIG.FREDDYABLE_ENABLED.get()){
                String freddy = "Freddy Fazbear";
                if (grizzlyBear.getName().getString().equals(freddy)) {
                    grizzlyBear.setAprilFoolsFlag(2);
                    grizzlyBear.setTame(false);
                    grizzlyBear.setOwnerUUID(null);
                }
            }
        }

        if (livingEntity instanceof EntityElephant elephant){
            if(AlexsMobsInteraction.COMMON_CONFIG.ELEPHANT_TRAMPLE_ENABLED.get()){
                Iterator<LivingEntity> var4 = level.getEntitiesOfClass(LivingEntity.class, elephant.getBoundingBox().expandTowards(0.25, -2, 0.25)).iterator();
                if (elephant.isVehicle() && elephant.isTame()) {
                    while (var4.hasNext()) {
                        LivingEntity entity = var4.next();
                        if (entity != elephant && entity != elephant.getControllingPassenger() && entity.getBbHeight() <= 2.0F) {
                            entity.hurt(elephant.damageSources().mobAttack((LivingEntity) elephant), 3);
                            AMIUtils.awardAdvancement(elephant.getFirstPassenger(), "elephant_trample", "trample");
                        }
                    }
                }
            }
        }



        if(livingEntity instanceof EntityBananaSlug bananaSlug && AlexsMobsInteraction.COMMON_CONFIG.GOOFY_BANANA_SLIP_ENABLED.get()){
            for (LivingEntity livingEntitys : level.getEntitiesOfClass(LivingEntity.class, bananaSlug.getBoundingBox().expandTowards(0.5, 0.2, 0.5))) {
                if (livingEntitys instanceof Player player && !player.isCreative() && player.isAlive()) {
                    player.hurt(AMIDamageTypes.causeBananaSlip(level.registryAccess()),100);
                    player.playSound(AMISoundRegistry.BANANA_SLIP.get());
                }
            }
        }

        if(livingEntity instanceof EntityAlligatorSnappingTurtle snappingturtle && AlexsMobsInteraction.COMMON_CONFIG.MOSS_PROPOGATION_ENABLED.get()){
            if ((level.isRaining() || level.isThundering() || snappingturtle.isInWater()) && snappingturtle.getRandom().nextDouble() < 0.0001){
                snappingturtle.setMoss(Math.min(10, snappingturtle.getMoss() + 1));
            }
        }



        if(livingEntity instanceof Player player){

            BlockState feetBlockstate = player.getBlockStateOn();

            if (AlexsMobsInteraction.COMMON_CONFIG.COMBUSTABLE_ENABLED.get() && player.hasEffect(AMEffectRegistry.OILED.get())){
                if (feetBlockstate.is(Blocks.MAGMA_BLOCK) || feetBlockstate.is(Blocks.CAMPFIRE)) {
                    player.setSecondsOnFire(20);
                    AMIUtils.awardAdvancement(player,"combustable","combust");
                }

                if (feetBlockstate.is(Blocks.SOUL_CAMPFIRE)){
                    if (ModList.get().isLoaded("soulfired")) {
                        SoulFiredCompat.setOnFire(player,20);
                    } else player.setSecondsOnFire(20);
                    AMIUtils.awardAdvancement(player,"combustable","combust");
                }

            }



            if(AlexsMobsInteraction.COMMON_CONFIG.GOOFY_BANANA_SLIP_ENABLED.get()){
                if (feetBlockstate.is(AMBlockRegistry.BANANA_PEEL.get()) && !player.isCreative() && player.isAlive()){
                    player.hurt(AMIDamageTypes.causeBananaSlip(level.registryAccess()),100);
                    player.playSound(AMISoundRegistry.BANANA_SLIP.get());
                }
            }

            if(AlexsMobsInteraction.COMMON_CONFIG.SUNBIRD_UPGRADE_ENABLED.get()){

                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(7, 4, 7))) {
                    MobType entityType = entity.getMobType();
                    if (entityType == MobType.UNDEAD && !entity.isInWater()) {
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

            if (AlexsMobsInteraction.COMMON_CONFIG.TIGER_STEALTH_ENABLED.get() && player.hasEffect(AMEffectRegistry.TIGERS_BLESSING.get()) && !player.isSprinting() && !player.isSwimming()){
                player.addEffect(new MobEffectInstance(MobEffects. INVISIBILITY, 60, 0,false,false));
            }

//            if (getClosestLookingAtEntityFor(level,player,32D) != null){
//                System.out.println("looking at blobfish");
//                player.addEffect(new MobEffectInstance(MobEffects. INVISIBILITY, 30, 0));
//            }


        }



    }

    @SubscribeEvent
    public void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        RandomSource random = player.getRandom();
        Entity entity = event.getTarget();
        Level level = entity.level();


        if (entity instanceof LivingEntity living) {
            if (itemStack.getItem() == AMItemRegistry.LAVA_BOTTLE.get() && AlexsMobsInteraction.COMMON_CONFIG.MOLTEN_BATH_ENABLED.get()) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                    player.addItem(Items.GLASS_BOTTLE.getDefaultInstance());
                }
                living.setSecondsOnFire(10);
                player.playSound(SoundEvents.LAVA_POP, 1F, 1F);
                player.swing(event.getHand());
                AMIUtils.awardAdvancement(player,"molten_bath","molten");
            }
            if (itemStack.getItem() == AMItemRegistry.POISON_BOTTLE.get() && AlexsMobsInteraction.COMMON_CONFIG.POISONOUS_BATH_ENABLED.get()) {
                if (!player.isCreative()) itemStack.shrink(1);
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 150, 1));
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                    player.addItem(Items.GLASS_BOTTLE.getDefaultInstance());
                }
                player.playSound(SoundEvents.FIRE_EXTINGUISH, 1F, 1F);
                player.swing(event.getHand());
                AMIUtils.awardAdvancement(player,"poison_bath","poison");
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


        if (AlexsMobsInteraction.COMMON_CONFIG.SKREECHER_WARD_ENABLED.get()){
            if (event.getItemStack().is(AMItemRegistry.SKREECHER_SOUL.get()) && blockState.is(Blocks.SCULK_SHRIEKER) && !blockState.getValue(CAN_SUMMON)) {
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
                AMIUtils.awardAdvancement(livingEntity, "acclamate", "acclamate");
            }

        }


        if (AlexsMobsInteraction.COMMON_CONFIG.COCKROACH_CHAMBER_ENABLED.get() && blockState.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()) && !worldIn.isClientSide){
            if (blockState.getValue(FUNGUS) == 5 && livingEntity.getRandom().nextDouble() < 0.7) {
                Entity entityToSpawn = (AMEntityRegistry.COCKROACH.get()).spawn((ServerLevel) worldIn, BlockPos.containing(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof EntityCockroach cockroach && worldIn.getRandom().nextDouble() < 0.07)
                    cockroach.setBaby(true);
                AMIUtils.awardAdvancement(livingEntity,"uncover_roach","uncover");
            }
        }
    }


    @SubscribeEvent
    public void mobDeath(LivingDeathEvent livingDeathEvent){
        LivingEntity deadEntity = livingDeathEvent.getEntity();
        LivingEntity murdererEntity = deadEntity.getLastAttacker();

        if(AlexsMobsInteraction.COMMON_CONFIG.SNOW_LUCK_ENABLED.get()){
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
    public void talkEvent(ServerChatEvent serverChatEvent){
        Player player = serverChatEvent.getPlayer();

        for (EntityMimicube entity : player.level().getEntitiesOfClass(EntityMimicube.class, player.getBoundingBox().inflate(9))) {
            if (entity != null && entity.getRandom().nextDouble() < 0.8 && entity.getTarget() == player && AlexsMobsInteraction.COMMON_CONFIG.MIMICKRY_ENABLED.get()) {
                String message = serverChatEvent.getMessage().getString();
                for (int i = 0;i < 2; i++){
                    char randomLetter = (char) ('a' + player.getRandom().nextInt(26));
                    message = message.replaceAll(String.valueOf(randomLetter), "§k" + randomLetter + randomLetter + "§r");
                }

                AMIUtils.awardAdvancement(player,"mimickry","mimic");
                player.sendSystemMessage(Component.nullToEmpty("<" + player.getDisplayName().getString() + "?> " + message));
            }
        }
    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent breakEvent){
        BlockState blockState = breakEvent.getState();
        Level level = (Level) breakEvent.getLevel();
        if (AlexsMobsInteraction.COMMON_CONFIG.COCKROACH_CHAMBER_ENABLED.get()) {
            if (blockState.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()) && breakEvent.getLevel().getRandom().nextDouble() < 0.1) {
                 if (blockState.getValue(FUNGUS) < 3)
                     return;
                Entity entityToSpawn = (AMEntityRegistry.COCKROACH.get()).spawn((ServerLevel) level, BlockPos.containing(breakEvent.getPos().getX() + 0.5, breakEvent.getPos().getY() + 1.0, breakEvent.getPos().getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof EntityCockroach cockroach && breakEvent.getLevel().getRandom().nextDouble() < 0.8)
                    cockroach.setBaby(true);
                AMIUtils.awardAdvancement(breakEvent.getPlayer(),"uncover_roach","uncover");
            }
        }

    }

    @SubscribeEvent
    public void struckLightning(EntityStruckByLightningEvent lightningEvent){
        if (lightningEvent.getEntity() instanceof EntityTusklin tusklin && AlexsMobsInteraction.COMMON_CONFIG.ZOGLINNED_ENABLED.get()){
            Level level = tusklin.level();
            if (!level.isClientSide) {
                LivingEntity entityToSpawn = EntityType.ZOGLIN.spawn((ServerLevel) level, BlockPos.containing(tusklin.getX() + 0.5, tusklin.getY() + 1.0, tusklin.getZ() + 0.5), MobSpawnType.TRIGGERED);
                if (tusklin.isBaby() && entityToSpawn instanceof Zoglin zoglin) zoglin.setBaby(true);
                tusklin.discard();
            }
            AMIUtils.awardAdvancement(lightningEvent.getLightning().getCause(), "zoglinned", "zoglinned");

        }
    }



    @SubscribeEvent
    public void mobSpawn(MobSpawnEvent.PositionCheck spawnPlacementCheck){
        EntityType<?> entityType = spawnPlacementCheck.getEntity().getType();
        Holder<Biome> biome = spawnPlacementCheck.getLevel().getBiome(spawnPlacementCheck.getEntity().blockPosition());
        long time = spawnPlacementCheck.getLevel().dayTime();

        if(entityType == AMEntityRegistry.LOBSTER.get()){
            if (!AlexsMobsInteraction.COMMON_CONFIG.LOBSTER_NIGHT_ENABLED.get())
                return;
            if (!(time > 13000 && time < 23460)) {
                spawnPlacementCheck.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void livingDamage(LivingDamageEvent livingDamageEvent) {
        Entity damager = livingDamageEvent.getSource().getEntity();
        LivingEntity damaged = livingDamageEvent.getEntity();

        if(AlexsMobsInteraction.COMMON_CONFIG.ACIDIC_LEAFCUTTER_ENABLED.get() && damager instanceof EntityLeafcutterAnt leafcutterAnt){
            damaged.addEffect(new MobEffectInstance(MobEffects.POISON, 100 * (leafcutterAnt.isQueen() ? 4 : 2), leafcutterAnt.isQueen() ? 2 : 0));
        }
    }








}
