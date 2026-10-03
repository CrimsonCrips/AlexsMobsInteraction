package com.crimsoncrips.alexsmobsinteraction.server.item;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AMIDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DEF_REG = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AlexsMobsInteraction.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> MIMICKED = DEF_REG.register("mimicked",
            () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAGGOT_BAITED = DEF_REG.register("maggot_baited",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

}
