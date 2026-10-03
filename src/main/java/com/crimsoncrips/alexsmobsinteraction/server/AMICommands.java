package com.crimsoncrips.alexsmobsinteraction.server;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.phys.AABB;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.function.Consumer;

public class AMICommands {

    private static final double VOID_WORM_RADIUS = 64.0D;
    private static final Map<String, Integer> VOID_WORM_ABILITIES = new LinkedHashMap<>();

    static {
        VOID_WORM_ABILITIES.put("portal_charge", AMIVoidWormBoss.MODE_PORTAL);
        VOID_WORM_ABILITIES.put("geyser", AMIVoidWormBoss.MODE_GEYSER);
        VOID_WORM_ABILITIES.put("encirclement", AMIVoidWormBoss.MODE_SURROUND);
        VOID_WORM_ABILITIES.put("barrage", AMIVoidWormBoss.MODE_BARRAGE);
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("alexsmobsinteraction")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("ursa")
                        .executes(context -> summonUrsa(context, context.getSource().getPosition()))
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .executes(context -> summonUrsa(context, Vec3Argument.getVec3(context, "pos")))))
                .then(Commands.literal("spinning_seal")
                        .executes(context -> summonSpinningSeal(context, context.getSource().getPosition()))
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .executes(context -> summonSpinningSeal(context, Vec3Argument.getVec3(context, "pos")))))
                .then(Commands.literal("void_worm")
                        .then(Commands.argument("ability", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(VOID_WORM_ABILITIES.keySet(), builder))
                                .executes(context -> forceVoidWormAbility(context, StringArgumentType.getString(context, "ability"), VOID_WORM_RADIUS))
                                .then(Commands.argument("radius", DoubleArgumentType.doubleArg(1.0D, 512.0D))
                                        .executes(context -> forceVoidWormAbility(context, StringArgumentType.getString(context, "ability"), DoubleArgumentType.getDouble(context, "radius")))))));
    }

    private static int summonUrsa(CommandContext<CommandSourceStack> context, Vec3 pos) {
        EntityGrizzlyBear grizzlyBear = summon(context, AMEntityRegistry.GRIZZLY_BEAR.get(), pos, bear -> bear.setData(AMIAttachments.URSA, true));
        if (grizzlyBear == null)
            return 0;
        context.getSource().sendSuccess(() -> Component.translatable("commands.alexsmobsinteraction.ursa.success"), true);
        return 1;
    }

    private static int summonSpinningSeal(CommandContext<CommandSourceStack> context, Vec3 pos) {
        EntitySeal seal = summon(context, AMEntityRegistry.SEAL.get(), pos, spinningSeal -> {
            spinningSeal.setData(AMIAttachments.SPINNING_SEAL, true);
            spinningSeal.setNoAi(true);
            spinningSeal.setInvulnerable(true);
            spinningSeal.setPersistenceRequired();
        });
        if (seal == null)
            return 0;
        context.getSource().sendSuccess(() -> Component.translatable("commands.alexsmobsinteraction.spinning_seal.success"), true);
        return 1;
    }

    private static int forceVoidWormAbility(CommandContext<CommandSourceStack> context, String ability, double radius) {
        Integer mode = VOID_WORM_ABILITIES.get(ability);
        if (mode == null) {
            context.getSource().sendFailure(Component.translatable("commands.alexsmobsinteraction.void_worm.unknown", ability));
            return 0;
        }
        if (!AMIVoidWormBoss.enabled()) {
            context.getSource().sendFailure(Component.translatable("commands.alexsmobsinteraction.void_worm.disabled"));
            return 0;
        }
        Vec3 pos = context.getSource().getPosition();
        List<EntityVoidWorm> worms = context.getSource().getLevel().getEntitiesOfClass(EntityVoidWorm.class, AABB.ofSize(pos, radius * 2.0D, radius * 2.0D, radius * 2.0D),
                worm -> !worm.isSplitter() && worm.isAlive() && !worm.isNoAi() && worm.position().distanceToSqr(pos) <= radius * radius
                        && worm.getTarget() != null && worm.getTarget().isAlive());
        int count = 0;
        for (EntityVoidWorm worm : worms) {
            int current = AMIVoidWormBoss.getMode(worm);
            if (current == AMIVoidWormBoss.MODE_BARRAGE || current == AMIVoidWormBoss.MODE_RECOMBINE)
                continue;
            if (mode == AMIVoidWormBoss.MODE_BARRAGE && AMIVoidWormBoss.getChain(worm).size() < AMIVoidWormBoss.BARRAGE_MIN_SEGMENTS)
                continue;
            worm.setData(AMIAttachments.VOID_WORM_FORCED_ATTACK, mode);
            count++;
        }
        if (count == 0) {
            context.getSource().sendFailure(Component.translatable("commands.alexsmobsinteraction.void_worm.none"));
            return 0;
        }
        int performing = count;
        context.getSource().sendSuccess(() -> Component.translatable("commands.alexsmobsinteraction.void_worm.success", performing, ability), true);
        return count;
    }

    private static <T extends Mob> T summon(CommandContext<CommandSourceStack> context, EntityType<T> type, Vec3 pos, Consumer<T> setup) {
        ServerLevel level = context.getSource().getLevel();
        T mob = type.create(level);
        if (mob == null) {
            context.getSource().sendFailure(Component.translatable("commands.summon.failed"));
            return null;
        }
        mob.moveTo(pos.x, pos.y, pos.z, context.getSource().getRotation().y, 0.0F);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(pos)), MobSpawnType.COMMAND, null);
        setup.accept(mob);
        level.addFreshEntityWithPassengers(mob);
        return mob;
    }
}
