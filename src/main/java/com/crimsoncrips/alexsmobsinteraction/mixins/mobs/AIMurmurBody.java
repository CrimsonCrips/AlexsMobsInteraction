package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.alexsmobs.entity.EntityMurmurHead;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;
import java.util.UUID;


@Mixin(EntityMurmur.class)
public abstract class AIMurmurBody extends Mob {

    private static final EntityDataAccessor<Integer> HEAD_ID;

    private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID;


    protected AIMurmurBody(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    static {
        HEAD_UUID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.OPTIONAL_UUID);
        HEAD_ID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.INT);
    }

    private boolean renderFakeHead = true;

    public void tick() {
        EntityMurmur murmur = (EntityMurmur)(Object)(this);
        super.tick();
        if (this.renderFakeHead) {
            this.renderFakeHead = false;
        }

        this.yBodyRot = this.getYRot();
        this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70.0F, this.yBodyRot + 70.0F);
        if (!this.level().isClientSide) {
            Entity head = murmur.getHead();
            if (head == null && !(AInteractionConfig.murmurdecapitate && AInteractionConfig.aprilfools)) {
                LivingEntity created;
                ReflectionUtil.callMethod(created = this, "createHead", new Class[0], new Object[0]);
                murmur.setHeadUUID(created.getUUID());
                murmur.getEntityData().set(HEAD_ID, created.getId());
            }
        }

    }

    public Entity getHead() {
        if (!this.level().isClientSide) {
            UUID id = this.getHeadUUID();
            return id == null ? null : ((ServerLevel)this.level()).getEntity(id);
        } else {
            int id = (Integer)this.entityData.get(HEAD_ID);
            return id == -1 ? null : this.level().getEntity(id);
        }
    }
    public UUID getHeadUUID() {
        return (UUID)((Optional)this.entityData.get(HEAD_UUID)).orElse((Object)null);
    }




}
