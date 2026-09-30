package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.misc.FalconBombing;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.BOPCompat;
import com.crimsoncrips.alexsmobsinteraction.compat.CuriosCompat;
import com.crimsoncrips.alexsmobsinteraction.compat.SoulFiredCompat;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBasicInterfaces;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.crimsoncrips.alexsmobsinteraction.networking.AlterPacket;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import com.crimsoncrips.alexsmobsinteraction.networking.WelcomeToastPacket;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.stream.Stream;

import static com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber.FUNGUS;
import static net.minecraft.world.level.block.SculkShriekerBlock.CAN_SUMMON;

public class AMIServerEvents {

    @SubscribeEvent
    public void onEntityFinalizeSpawn(FinalizeSpawnEvent event) {
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
        if (villagerTradesEvent.getType() == VillagerProfession.FISHERMAN && AlexsMobsInteraction.COMMON_CONFIG.DEVILS_FISHING_INDUSTRY.get()) {
            VillagerTrades.ItemListing pupfishTrade = new EmeraldsForItemsTrade(AMItemRegistry.DEVILS_HOLE_PUPFISH_BUCKET.get(), 24, 2, 5);
            final var list = villagerTradesEvent.getTrades().get(5);
            list.add(pupfishTrade);
            villagerTradesEvent.getTrades().put(5, list);
        }
    }

    Stream<BlockPos> getNearbySirens(ServerLevel world, int range,EntityBaldEagle baldEagle) {
        return FalconBombing.getNearbySirens(world,range,baldEagle);
    }

    @SubscribeEvent
    public void mobTickEvents(EntityTickEvent.Pre entityTickEvent){
        if (!(entityTickEvent.getEntity() instanceof LivingEntity livingEntity))
            return;
        Level level = livingEntity.level();

        if (livingEntity instanceof EntityCosmaw cosmaw){
            LivingEntity owner = cosmaw.getOwner();
            if (owner != null && cosmaw.hasPassenger(owner) && owner.getArmorValue() > 8 && cosmaw.getRandom().nextDouble() < 0.3){
                AMIUtils.addParticlesAroundSelf(ParticleTypes.SPLASH,cosmaw,1,0.2);
            }
        }

        if ((livingEntity instanceof Frog || livingEntity instanceof EntityRainFrog) && !level.isClientSide && AMIUtils.tickTransforming(livingEntity)){
            LivingEntity entityToSpawn = AMEntityRegistry.WARPED_TOAD.get().spawn((ServerLevel) level, livingEntity.blockPosition(), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
                livingEntity.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);
                livingEntity.remove(Entity.RemovalReason.DISCARDED);
            }
        }

        if (livingEntity instanceof EntityFly fly && !level.isClientSide && AMIUtils.tickTransforming(fly)) {
            LivingEntity entityToSpawn = AMEntityRegistry.CRIMSON_MOSQUITO.get().spawn((ServerLevel) level, fly.blockPosition(), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn instanceof EntityCrimsonMosquito crimsonMosquito) {
                crimsonMosquito.onSpawnFromFly();
                fly.remove(Entity.RemovalReason.DISCARDED);
            }
        }

        if (livingEntity instanceof EntityBaldEagle baldEagle){
            List<String> nukes = List.of(new String[]{"alexscaves:nuclear_bomb","alexscavesexemplified:gamma_nuclear_bomb"});


            if (nukes.contains(baldEagle.getItemInHand(InteractionHand.MAIN_HAND).getItem().toString()) && level instanceof ServerLevel serverLevel){
                for (BlockPos sirenPos : getNearbySirens(serverLevel, 130,baldEagle).toList()){
                    FalconBombing.activateSiren(sirenPos,baldEagle);
                }
            }
        }




        if (AlexsMobsInteraction.COMMON_CONFIG.HEMOGENICISM_ENABLED.get()){
            if (ModList.get().isLoaded("biomesoplenty") && livingEntity.getInBlockState().is(BOPCompat.getBOPBlock())) {
                livingEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED, 400, 0));
            }

            MobEffectInstance blooded = livingEntity.getEffect(AMIEffects.BLOODED);
            if (livingEntity.isInWaterRainOrBubble() && blooded != null){
                livingEntity.removeEffect(AMIEffects.BLOODED);
                livingEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED, blooded.getDuration() - 300, blooded.getAmplifier()));
            }

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(1.2))) {
                if (entity != livingEntity && livingEntity.getRandom().nextDouble() < 0.01 && livingEntity.hasEffect(AMEffectRegistry.EXSANGUINATION)) {
                    livingEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED, 300, 1));
                }
            }
        }

        if (livingEntity instanceof EntityCombJelly entityCombJelly && entityCombJelly.getHealth() < entityCombJelly.getMaxHealth()){
            entityCombJelly.heal(0.05F);
        }



        if(livingEntity instanceof Player player){

            BlockState feetBlockstate = player.getBlockStateOn();

            if (AlexsMobsInteraction.COMMON_CONFIG.COMBUSTIBLE_ENABLED.get() && player.hasEffect(AMEffectRegistry.OILED)){
                if (feetBlockstate.is(Blocks.MAGMA_BLOCK) || feetBlockstate.is(Blocks.CAMPFIRE)) {
                    player.igniteForSeconds(20);
                    AMIUtils.awardAdvancement(player,"combustible","combust");
                }

                if (feetBlockstate.is(Blocks.SOUL_CAMPFIRE)){
                    if (ModList.get().isLoaded("soul_fire_d")) {
                        SoulFiredCompat.setOnFire(player,20);
                    } else player.igniteForSeconds(20);
                    AMIUtils.awardAdvancement(player,"combustible","combust");
                }

            }




            if(AlexsMobsInteraction.COMMON_CONFIG.JUDGEMENTAL_RETURNS_ENABLED.get()){

                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(7, 4, 7))) {
                    if (entity.getType().is(EntityTypeTags.UNDEAD) && !entity.isInWater()) {
                        if (player.hasEffect(AMEffectRegistry.SUNBIRD_BLESSING)) {
                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                            entity.igniteForSeconds(3);
                        }
                        if (player.hasEffect(AMEffectRegistry.SUNBIRD_CURSE)) {
                            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 350, 2));
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0));
                        }
                    }
                }
            }

//            if (getClosestLookingAtEntityFor(level,player,32D) != null){
//                System.out.println("looking at blobfish");
//                player.addEffect(new MobEffectInstance(MobEffects. INVISIBILITY, 30, 0));
//            }


        }



    }

    @SubscribeEvent
    public void mobTickPostEvents(EntityTickEvent.Post entityTickEvent){
        Entity tickingEntity = entityTickEvent.getEntity();
        Level level = tickingEntity.level();

        if (tickingEntity instanceof EntityTendonSegment tendonSegment && AlexsMobsInteraction.COMMON_CONFIG.TENDON_GRAB_ENABLED.get()) {
            if (tendonSegment.getCreatorEntity() instanceof Player player && AMIEnchantmentRegistry.getLevel(level, player.getMainHandItem(), AMIEnchantmentRegistry.STRETCHY_ACCUMULATION) > 0) {
                Vec3 creatorPos = player.position();
                for (Entity entity : level.getEntitiesOfClass(Entity.class, tendonSegment.getBoundingBox().inflate(2, 2, 2))) {
                    if (entity instanceof ExperienceOrb || entity instanceof ItemEntity) {
                        entity.setPos(creatorPos);
                        if (entity instanceof ItemEntity item) {
                            item.setPickUpDelay(0);
                        }
                        AMIUtils.awardAdvancement(player, "stretchy_accumulation", "stretch");
                    }
                }
            }
        }

        if (!(tickingEntity instanceof LivingEntity livingEntity))
            return;

        if (livingEntity instanceof EntityElephant elephant && AlexsMobsInteraction.COMMON_CONFIG.ELEPHANT_TRAMPLE_ENABLED.get()) {
            if (elephant.isTame() && elephant.getFirstPassenger() instanceof Player) {
                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, elephant.getBoundingBox().expandTowards(0.25, -2, 0.25))) {
                    if (entity != elephant && entity != elephant.getControllingPassenger() && entity.getBbHeight() <= 2.0F) {
                        entity.hurt(elephant.damageSources().mobAttack(elephant), 3);
                        AMIUtils.awardAdvancement(elephant.getFirstPassenger(), "elephant_trample", "trample");
                    }
                }
            }
        }

        if (livingEntity instanceof EntityCentipedeHead centipede && AlexsMobsInteraction.COMMON_CONFIG.LIGHT_FEAR_ENABLED.get()) {
            if (centipede.getTarget() instanceof LivingEntity target && CuriosCompat.hasLight(target) && centipede.getLastHurtByMob() != target) {
                centipede.setTarget(null);
                AMIUtils.awardAdvancement(target, "light_warding", "lighted");
            }
        }

        if (livingEntity instanceof EntityStraddler straddler && AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get() != 0) {
            if (straddler.getData(AMIAttachments.SHOOT_SHOTS) <= 0) {
                straddler.setData(AMIAttachments.SHOOT_COOLDOWN, straddler.getData(AMIAttachments.SHOOT_COOLDOWN) - 1);
            }
            if (straddler.getData(AMIAttachments.SHOOT_COOLDOWN) <= 0 && straddler.getData(AMIAttachments.SHOOT_SHOTS) <= 0) {
                straddler.setData(AMIAttachments.SHOOT_SHOTS, AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get());
                straddler.setData(AMIAttachments.SHOOT_COOLDOWN, 100);
            }
        }

        if (livingEntity instanceof EntityTerrapin terrapin) {
            if (!level.isClientSide && !terrapin.isInWater() && !terrapin.hasRetreated() || !terrapin.isSpinning()) {
                for (Player player : level.getEntitiesOfClass(Player.class, terrapin.getBoundingBox().inflate(0, 0.15F, 0))) {
                    if ((player.jumping || !player.onGround()) && player.getY() > terrapin.getEyeY() && AlexsMobsInteraction.COMMON_CONFIG.TERRAPIN_STOMP_ENABLED.get()) {
                        AMIUtils.awardAdvancement(player, "stomp", "stomp");
                        terrapin.hurt(player.damageSources().generic(), 2);
                    }
                }
            }
        }

        if (livingEntity instanceof EntityGrizzlyBear grizzlyBear) {
            if (grizzlyBear.isHoneyed()) {
                grizzlyBear.setData(AMIAttachments.NO_HONEY, 0);
            } else {
                grizzlyBear.setData(AMIAttachments.NO_HONEY, grizzlyBear.getData(AMIAttachments.NO_HONEY) + 1);
            }
        }

        if (livingEntity instanceof EntityAlligatorSnappingTurtle snappingTurtle) {
            if (!level.isClientSide) {
                boolean awake = level.isNight() || level.isRaining() || level.isThundering() || snappingTurtle.getTarget() != null || snappingTurtle.isInLove();
                snappingTurtle.setData(AMIAttachments.DAY_SLEEPING, !awake);
            }
            if (AlexsMobsInteraction.COMMON_CONFIG.MOSS_PROPOGATION_ENABLED.get()) {
                if ((level.isRaining() || level.isThundering() || snappingTurtle.isInWater()) && snappingTurtle.getRandom().nextDouble() < 0.0001) {
                    snappingTurtle.setMoss(Math.min(10, snappingTurtle.getMoss() + 1));
                }
            }
        }

        if (livingEntity instanceof EntityLeafcutterAnt leafcutterAnt && !AlexsMobsInteraction.COMMON_CONFIG.ANT_WAR_ENABLED.get() && leafcutterAnt.getData(AMIAttachments.VARIANT) == 2) {
            leafcutterAnt.setData(AMIAttachments.VARIANT, 1);
        }
    }

    @SubscribeEvent
    public void playerTickEvents(PlayerTickEvent.Pre playerTickEvent){
        if (!(playerTickEvent.getEntity() instanceof ServerPlayer player))
            return;
        RandomSource random = player.getRandom();

        int alterTime = player.getData(AMIAttachments.ALTER_TIME);
        int stalkDelay = player.getData(AMIAttachments.STALK_DELAY);
        float stalkTime = player.getData(AMIAttachments.STALK_TIME);

        if (AlexsMobsInteraction.COMMON_CONFIG.FARSEER_ALTERING_ENABLED.get()) {
            if (!(AMIEnchantmentRegistry.getLevel(player.level(), player.getItemBySlot(EquipmentSlot.HEAD), AMIEnchantmentRegistry.STABILIZER) > 0) && stalkTime >= 1.5 && alterTime <= 0) {
                stalkDelay = -500;
                alterTime = 100;
                PacketDistributor.sendToPlayer(player, new AlterPacket());
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                AMIUtils.awardAdvancement(player,"altered","alter");
            }

            if (alterTime > 0){
                if (random.nextDouble() < 0.05) {
                    //Thanks ItemmStack for the help
                    Inventory inv = player.getInventory();
                    for (int i = 0; i < 9; i++) {
                        int j = random.nextInt(0, 9);

                        ItemStack a = inv.getItem(i).copy();
                        ItemStack b = inv.getItem(j).copy();
                        inv.setItem(j, a);
                        inv.setItem(i, b);
                    }
                }
                alterTime--;
            }
        }

        if (stalkDelay == 0) {
            if (stalkTime > 0){
                stalkTime -= 0.01F;
            } else {
                stalkTime = 0;
            }
        } else {
            if (stalkDelay < 0){
                stalkDelay++;
                if (stalkTime > 0 && alterTime <= 0) {
                    stalkTime -= 0.05F;
                } else if (alterTime <= 0) {
                    stalkTime = 0;
                }
            } else {
                stalkDelay--;
            }
        }

        AMIAttachments.setIfChanged(player, AMIAttachments.ALTER_TIME, alterTime);
        AMIAttachments.setIfChanged(player, AMIAttachments.STALK_DELAY, stalkDelay);
        AMIAttachments.setIfChanged(player, AMIAttachments.STALK_TIME, Math.max(stalkTime, 0F));

        if (AlexsMobsInteraction.COMMON_CONFIG.SNAPPING_DORMANCY_ENABLED.get()){
            if (player.getUseItem().getItem() instanceof SpyglassItem) {
                Entity lookAt = AMIUtils.getClosestLookingAtEntityFor(player);
                if (lookAt instanceof EntityAlligatorSnappingTurtle snappingTurtle && snappingTurtle.getData(AMIAttachments.DAY_SLEEPING)) {
                    AMIUtils.awardAdvancement(player, "observe_dormancy", "observe");
                }
            }
        }
    }


    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new WelcomeToastPacket());


        }
    }
    @SubscribeEvent
    public void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        InteractionHand interactionHand = player.getUsedItemHand();
        Entity entity = event.getTarget();



        if (AlexsMobsInteraction.COMMON_CONFIG.TRANSFORMATION_ENABLED.get() && entity instanceof LivingEntity livingEntity && (livingEntity instanceof EntityRainFrog || livingEntity instanceof Frog)) {
            if (itemStack.getItem() == Items.WARPED_FUNGUS && livingEntity.hasEffect(MobEffects.WEAKNESS) ){
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                AMIUtils.awardAdvancement(player,"mutate_frog","mutate");
                player.swing(interactionHand,true);
                livingEntity.gameEvent(GameEvent.EAT);
                livingEntity.playSound(SoundEvents.GENERIC_EAT, 1, livingEntity.getVoicePitch());
                livingEntity.setData(AMIAttachments.TRANSFORMING_TIME, AMIUtils.TRANSFORM_DURATION);
            }
        }

        if (AlexsMobsInteraction.COMMON_CONFIG.BANANA_SHEAR_ENABLED.get() && itemStack.getItem() instanceof ShearsItem && entity instanceof EntityBananaSlug slug) {
            if (!player.isCreative()) {
                itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(interactionHand));
            }
            player.swing(interactionHand,true);
            AMIUtils.spawnLoot(AMILootTables.BANANA_SHEAR,slug,player,0);
            slug.playSound(SoundEvents.SHEEP_SHEAR, 1, slug.getVoicePitch());
            slug.discard();
            AMIUtils.awardAdvancement(player,"banana_shear","banana");
        }


        if (entity instanceof EntityBaldEagle eagle && eagle.isOwnedBy(player) && AlexsMobsInteraction.COMMON_CONFIG.BIRD_BOMBING_ENABLED.get()) {
            ItemStack itemStack1 = eagle.getItemInHand(InteractionHand.MAIN_HAND);

            if (FalconBombing.bombs.contains(itemStack.getItem().toString()) && itemStack1.isEmpty()){
                eagle.setItemInHand(InteractionHand.MAIN_HAND,itemStack.copyWithCount(1));
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
            }

            if (itemStack.is(Items.SHEARS) && !itemStack1.isEmpty()) {
                eagle.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, eagle.getVoicePitch());
                if (!eagle.level().isClientSide && player instanceof ServerPlayer) {
                    itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(interactionHand));
                }

                eagle.spawnAtLocation(eagle.getItemInHand(InteractionHand.MAIN_HAND));
                eagle.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
            }
        }



    }

    @SubscribeEvent
    public void mobInteractEvents(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        InteractionHand hand = event.getHand();
        Entity entity = event.getTarget();
        Level level = event.getLevel();

        if (!entity.isAlive())
            return;

        if (entity instanceof EntityFly fly && AlexsMobsInteraction.COMMON_CONFIG.TRANSFORMATION_ENABLED.get()) {
            if (itemStack.getItem() == AMItemRegistry.BLOOD_SAC.get() && fly.hasEffect(MobEffects.WEAKNESS)){
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                fly.gameEvent(GameEvent.ENTITY_INTERACT);
                fly.gameEvent(GameEvent.EAT);
                fly.playSound(SoundEvents.GENERIC_EAT, 1.0F, fly.getVoicePitch());
                fly.setData(AMIAttachments.TRANSFORMING_TIME, AMIUtils.TRANSFORM_DURATION);
                AMIUtils.awardAdvancement(player, "fly_transform", "fly");
            }
        }

        if (entity instanceof EntityCrimsonMosquito crimsonMosquito && AlexsMobsInteraction.COMMON_CONFIG.TRANSFORMATION_ENABLED.get()) {
            if (itemStack.getItem() == AMItemRegistry.WARPED_MUSCLE.get() && crimsonMosquito.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                crimsonMosquito.gameEvent(GameEvent.ENTITY_INTERACT);
                crimsonMosquito.playSound(SoundEvents.GENERIC_EAT, 1, crimsonMosquito.getVoicePitch());
                crimsonMosquito.setSick(true);
                player.swing(hand,true);
                AMIUtils.awardAdvancement(player, "mutate_mosquito", "mutate");
            }
        }

        //Only when the laviathan wouldn't mount the player instead
        if (entity instanceof EntityLaviathan laviathan && player.getMainHandItem().getItem() instanceof PickaxeItem && laviathan.isObsidian() && (!laviathan.hasBodyGear() || laviathan.isBaby())) {
            laviathan.setData(AMIAttachments.RELAVA, true);
            laviathan.setObsidian(false);
            laviathan.playSound(SoundEvents.WITHER_BREAK_BLOCK, 2, laviathan.getVoicePitch());
            AMIUtils.spawnLoot(AMILootTables.OBSIDIAN_EXTRACT,laviathan,player,0);
            player.getMainHandItem().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            laviathan.hurt(laviathan.damageSources().generic(),10);
            AMIUtils.awardAdvancement(player,"obsidian_extract","extract");
        }

        if (entity instanceof EntityGrizzlyBear grizzlyBear) {
            //Owners riding/commanding their bear take priority over brushing
            boolean ownerInteraction = grizzlyBear.isTame() && grizzlyBear.isOwnedBy(player);
            if (AlexsMobsInteraction.COMMON_CONFIG.BRUSHED_ENABLED.get() && itemStack.getItem() instanceof BrushItem && !level.isClientSide && !ownerInteraction && grizzlyBear.isHoneyed() && !grizzlyBear.getData(AMIAttachments.URSA)) {
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(15, player, LivingEntity.getSlotForHand(hand));
                }
                AMIUtils.spawnLoot(AMILootTables.GRIZZLY_BRUSH,grizzlyBear,player,0);
                grizzlyBear.playSound(SoundEvents.BRUSH_GENERIC, 1, grizzlyBear.getVoicePitch());
                AMIUtils.awardAdvancement(player,"brushed","brushed");
            }

            if (AlexsMobsInteraction.COMMON_CONFIG.FREDDYABLE_ENABLED.get() && grizzlyBear.getName().getString().equals("Freddy Fazbear")) {
                grizzlyBear.setAprilFoolsFlag(2);
                grizzlyBear.setTame(false, true);
                grizzlyBear.setOwnerUUID(null);
            }
        }

        if (entity instanceof EntityFlutter flutter && itemStack.getItem() == Items.SHEARS && AlexsMobsInteraction.COMMON_CONFIG.FLUTTER_SHEAR_ENABLED.get() && !flutter.isTame() && level instanceof ServerLevel) {
            AMIUtils.spawnLoot(AMILootTables.FLUTTER_SHEAR,flutter,player,1);
            if (flutter.isPotted()){
                player.drop(Items.FLOWER_POT.getDefaultInstance(), false);
            }
            player.swing(hand,true);
            if (!player.isCreative()) itemStack.hurtAndBreak(3, player, LivingEntity.getSlotForHand(hand));
            flutter.playSound(SoundEvents.SHEEP_SHEAR, 1, flutter.getVoicePitch());
            flutter.discard();
            AMIUtils.awardAdvancement(player,"flutter_shear","flutter");
        }

        if (entity instanceof EntityCrocodile crocodile && ((AMIBasicInterfaces) crocodile).isWally()) {
            AMIUtils.awardAdvancement(player, "wally", "wally");
        }

        if (entity instanceof EntityAlligatorSnappingTurtle snappingTurtle && AlexsMobsInteraction.COMMON_CONFIG.MOSS_PROPOGATION_ENABLED.get() && itemStack.getItem() instanceof BoneMealItem && snappingTurtle.isInWater()) {
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            snappingTurtle.gameEvent(GameEvent.ENTITY_INTERACT);
            snappingTurtle.playSound(SoundEvents.BONE_MEAL_USE, 1, 1);
            AMIUtils.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER,snappingTurtle,8,1);
            if (snappingTurtle.getRandom().nextDouble() < 0.05) {
                snappingTurtle.setMoss(snappingTurtle.getMoss() + 1);
            }
            AMIUtils.awardAdvancement(player,"moss_propagation","propagate");
        }

        if (entity instanceof EntityCockroach cockroach && itemStack.is(AMIItemRegistry.ASMON_CROWN.get()) && AlexsMobsInteraction.COMMON_CONFIG.ASMONGOLD_ENABLED.get()) {
            if (!cockroach.isDancing() && !cockroach.hasMaracas() && !cockroach.getData(AMIAttachments.IS_GOD) && ((AsmonRoach) cockroach).getWorshiping() == null) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                if (!level.isClientSide){
                    cockroach.setData(AMIAttachments.IS_GOD, true);
                    cockroach.refreshDimensions();
                    AMIUtils.awardAdvancement(player, "asmongold", "asmon");

                    cockroach.setCustomName(Component.nullToEmpty("Asmongold"));
                    cockroach.getAttribute(Attributes.ARMOR).setBaseValue(10F);
                    cockroach.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100F);
                    cockroach.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.10F);
                    cockroach.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(3F);
                    cockroach.setHealth(100);
                    cockroach.setAge(0);
                    cockroach.setPersistenceRequired();

                    int vasalAmount = 0;
                    for (EntityCockroach nearRoaches : level.getEntitiesOfClass(EntityCockroach.class, cockroach.getBoundingBox().inflate(10))) {
                        if (nearRoaches != cockroach && !nearRoaches.getData(AMIAttachments.IS_GOD) && ((AsmonRoach) nearRoaches).getWorshiping() == null) {
                            AMIAttachments.setUUID(nearRoaches, AMIAttachments.WORSHIPING_UUID, cockroach.getUUID());
                            nearRoaches.setCustomName(Component.nullToEmpty("Servant"));
                            vasalAmount++;
                        }
                    }
                    if (vasalAmount >= 20){
                        AMIUtils.awardAdvancement(player, "vassalized", "vassalize");
                    }
                }
                player.swing(hand);
            }
        }
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();
        Player player = event.getEntity();
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();

        if (AlexsMobsInteraction.COMMON_CONFIG.ENDERBOOSTING_ENABLED.get() && itemStack.is(AMItemRegistry.CHORUS_ON_A_STICK.get()) && player.getControlledVehicle() instanceof EntityEndergrade entityEndergrade && !player.getCooldowns().isOnCooldown(itemStack.getItem())){
            if (level.isClientSide){
                player.swing(hand);
            }
            AMIUtils.awardAdvancement(player,"ender_boost","ender_boost");
            player.getCooldowns().addCooldown(itemStack.getItem(), 500);
            ((AMIBasicInterfaces)entityEndergrade).boost();
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
                AMIUtils.addParticlesAroundBlock(ParticleTypes.SCULK_SOUL,pos,worldIn,100);
                AMIUtils.awardAdvancement(livingEntity, "acclimate", "acclimate");
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
                AMIUtils.spawnLoot(deadEntity.getLootTable(),deadEntity,deadEntity,random.nextInt(1,2));
            }
        }

        if (deadEntity instanceof EntityStraddler straddler && straddler.getData(AMIAttachments.SHOOT_SHOTS) == 0){
            AMIUtils.awardAdvancement(livingDeathEvent.getSource().getEntity(), "rearming_kill", "rearm");
        }
    }

    @SubscribeEvent
    public void mobAttack(LivingIncomingDamageEvent attackEvent){
        if(attackEvent.getSource().getDirectEntity() instanceof EntitySoulVulture soulVulture && AlexsMobsInteraction.COMMON_CONFIG.SOUL_STEAL_ENABLED.get()){
            soulVulture.setSoulLevel(soulVulture.getSoulLevel() + 1);
        }

        if (attackEvent.getEntity() instanceof EntityAlligatorSnappingTurtle && attackEvent.getSource().getEntity() instanceof Player player){
            AMIUtils.awardAdvancement(player,"interrupt_dormancy","interrupt");
        }

    }

    @SubscribeEvent
    public void mobDamaged(LivingDamageEvent.Post damageEvent){
        if (damageEvent.getEntity() instanceof EntityCatfish catfish && damageEvent.getSource().getDirectEntity() instanceof LivingEntity living && AlexsMobsInteraction.COMMON_CONFIG.CAT_VENOM_ENABLED.get() && catfish.getRandom().nextDouble() < 0.4){
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 100 * catfish.getCatfishSize()));
            AMIUtils.awardAdvancement(living,"venomous_cat","venom");
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












}
